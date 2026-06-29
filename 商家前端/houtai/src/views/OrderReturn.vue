<template>
  <div class="order-return">
    <el-card>
      <template #header>
        <span>退货管理</span>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="150" />
        <el-table-column prop="customerName" label="客户名称" width="100" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="reason" label="退货原因" min-width="150" />
        <el-table-column prop="amount" label="退款金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'pending' ? 'warning' : 'success'" size="small">
              {{ row.status === 'pending' ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="success" v-if="row.status === 'pending'" @click="handleApprove(row)">
              同意
            </el-button>
            <el-button link type="danger" v-if="row.status === 'pending'" @click="handleReject(row)">
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const tableData = ref([
  {
    id: 1,
    orderNo: 'ORD202401150001',
    customerName: '张三',
    productName: 'iPhone 15 Pro Max',
    reason: '商品损坏',
    amount: 9999,
    status: 'pending'
  },
  {
    id: 2,
    orderNo: 'ORD202401150002',
    customerName: '李四',
    productName: 'MacBook Pro M3',
    reason: '不想要了',
    amount: 15999,
    status: 'approved'
  }
])

const handleView = (_row: any) => {
  ElMessage.info('查看详情')
}

const handleApprove = (_row: any) => {
  ElMessage.success('已同意退货申请')
}

const handleReject = (_row: any) => {
  ElMessage.info('已拒绝退货申请')
}
</script>

<style scoped>
.order-return {
  padding: 0;
}
</style>
