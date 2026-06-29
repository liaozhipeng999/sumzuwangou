<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <div class="header-actions">
            <el-button size="small" @click="handleExport">导出订单</el-button>
            <el-button size="small" type="primary" :loading="loading" @click="loadOrders">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div class="filter-bar">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 260px;"
        />

        <el-input
          v-model="searchQuery"
          placeholder="订单号/客户名称/手机号"
          style="width: 250px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 150px;">
          <el-option label="待付款" value="pending" />
          <el-option label="待发货" value="paid" />
          <el-option label="已发货" value="shipped" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>

        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%; margin-top: 20px;" empty-text="暂无订单">
        <el-table-column prop="orderNo" label="订单号" width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column prop="customerName" label="客户名称" width="110" show-overflow-tooltip />
        <el-table-column prop="customerPhone" label="手机号" width="130" />
        <el-table-column prop="productInfo" label="商品信息" min-width="200" show-overflow-tooltip />
        <el-table-column prop="totalAmount" label="订单金额" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button
              link
              type="success"
              v-if="row.status === 'paid'"
              @click="handleShip(row)"
            >
              发货
            </el-button>
            <el-button
              link
              type="warning"
              v-if="['pending', 'paid'].includes(row.status)"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="(_v) => loadOrders()"
          @current-change="(_v) => loadOrders()"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="720px">
      <div v-if="currentOrder" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="客户名称">{{ currentOrder.customerName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.customerPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)" size="small">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentOrder.payMethod || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物流公司" v-if="currentOrder.express">{{ currentOrder.express.toUpperCase() }}</el-descriptions-item>
          <el-descriptions-item label="物流单号" v-if="currentOrder.trackingNo">{{ currentOrder.trackingNo }}</el-descriptions-item>
          <el-descriptions-item label="备注" v-if="currentOrder.shipRemark" :span="2">{{ currentOrder.shipRemark }}</el-descriptions-item>
          <el-descriptions-item label="订单金额" :span="2">
            <span style="color: #f56c6c; font-weight: bold; font-size: 18px;">
              ¥{{ currentOrder.totalAmount }}
            </span>
          </el-descriptions-item>
        </el-descriptions>

        <el-divider>商品清单</el-divider>

        <el-table :data="currentOrder.items" style="width: 100%;" empty-text="无商品">
          <el-table-column prop="name" label="商品名称" />
          <el-table-column prop="spec" label="规格" width="150" />
          <el-table-column prop="price" label="单价" width="100" align="right">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" align="center" />
          <el-table-column prop="subtotal" label="小计" width="100" align="right">
            <template #default="{ row }">¥{{ row.subtotal }}</template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="shipVisible" title="订单发货" width="480px">
      <el-form :model="shipForm" label-width="90px">
        <el-form-item label="物流公司">
          <el-select v-model="shipForm.express" placeholder="请选择物流公司" style="width: 100%;">
            <el-option label="顺丰速运" value="sf" />
            <el-option label="中通快递" value="zt" />
            <el-option label="圆通速递" value="yt" />
            <el-option label="韵达快递" value="yd" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.trackingNo" placeholder="请输入快递单号" maxlength="64" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="shipForm.remark" type="textarea" :rows="3" placeholder="选填" maxlength="500" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="handleShipConfirm">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getOrderPage, getOrderDetail, shipOrder, cancelOrder, type OrderVO, type OrderDetailVO } from '@/api/order'

const loading = ref(false)
const shipping = ref(false)
const searchQuery = ref('')
const dateRange = ref<[string, string] | null>(null)
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const shipVisible = ref(false)
const tableData = ref<OrderVO[]>([])
const currentOrder = ref<OrderDetailVO | null>(null)
const currentRow = ref<OrderVO | null>(null)

const merchantInfoRaw = localStorage.getItem('merchant_info')
const shopId = merchantInfoRaw ? (() => { try { return JSON.parse(merchantInfoRaw).id } catch { return null } })() : null
const operator = merchantInfoRaw ? (() => { try { return JSON.parse(merchantInfoRaw).username } catch { return '' } })() : ''

const shipForm = reactive({
  express: '',
  trackingNo: '',
  remark: ''
})

const getStatusType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'info',
    paid: 'warning',
    shipped: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待付款',
    paid: '待发货',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const loadOrders = async () => {
  if (!shopId) {
    ElMessage.warning('请先登录')
    return
  }
  loading.value = true
  try {
    const params: any = { shopId, page: currentPage.value, pageSize: pageSize.value }
    if (statusFilter.value) params.status = statusFilter.value
    if (searchQuery.value.trim()) params.keyword = searchQuery.value.trim()
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await getOrderPage(params)
    tableData.value = res.list || []
    total.value = res.total || 0
  } catch (e: any) {
    ElMessage.error(e?.message || '加载订单失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadOrders()
}

const handleReset = () => {
  searchQuery.value = ''
  dateRange.value = null
  statusFilter.value = ''
  currentPage.value = 1
  loadOrders()
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleView = async (row: OrderVO) => {
  try {
    currentOrder.value = await getOrderDetail(row.orderNo, shopId ?? undefined)
    detailVisible.value = true
  } catch (e: any) {
    ElMessage.error(e?.message || '获取订单详情失败')
  }
}

const handleShip = (row: OrderVO) => {
  currentRow.value = row
  shipForm.express = ''
  shipForm.trackingNo = ''
  shipForm.remark = ''
  shipVisible.value = true
}

const handleShipConfirm = async () => {
  if (!shipForm.express || !shipForm.trackingNo) {
    ElMessage.warning('请填写物流公司和快递单号')
    return
  }
  if (!currentRow.value || !shopId) return
  shipping.value = true
  try {
    await shipOrder({
      orderNo: currentRow.value.orderNo,
      express: shipForm.express,
      trackingNo: shipForm.trackingNo,
      remark: shipForm.remark,
      shopId,
      operator
    })
    ElMessage.success('发货成功')
    shipVisible.value = false
    await loadOrders()
  } catch (e: any) {
    ElMessage.error(e?.message || '发货失败')
  } finally {
    shipping.value = false
  }
}

const handleCancel = (row: OrderVO) => {
  if (!shopId) return
  ElMessageBox.confirm('确定要取消该订单吗？取消后不可恢复。', '提示', {
    confirmButtonText: '确定取消',
    cancelButtonText: '再想想',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelOrder({
        orderNo: row.orderNo,
        reason: '商家主动取消',
        shopId,
        operator
      })
      ElMessage.success('订单已取消')
      await loadOrders()
    } catch (e: any) {
      ElMessage.error(e?.message || '取消失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-list { padding: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.header-actions { display: flex; gap: 10px; }
.filter-bar { display: flex; gap: 10px; flex-wrap: wrap; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
.order-detail { padding: 10px; }
</style>
