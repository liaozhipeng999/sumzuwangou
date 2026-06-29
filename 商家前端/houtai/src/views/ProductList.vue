<template>
  <div class="product-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加商品
          </el-button>
        </div>
      </template>
      
      <div class="filter-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索商品名称/编码"
          style="width: 300px;"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="categoryFilter" placeholder="商品分类" clearable style="width: 150px;">
          <el-option label="手机数码" :value="1" />
          <el-option label="电脑办公" :value="2" />
          <el-option label="服装鞋帽" :value="3" />
          <el-option label="家用电器" :value="4" />
          <el-option label="食品生鲜" :value="5" />
          <el-option label="美妆护肤" :value="6" />
        </el-select>
        
        <el-select v-model="statusFilter" placeholder="商品状态" clearable style="width: 150px;">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="mainImage" label="商品图片" width="100">
          <template #default="{ row }">
            <el-image 
              :src="row.mainImage" 
              style="width: 60px; height: 60px;"
              fit="cover"
              :preview-src-list="[row.mainImage]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="160">
          <template #default="{ row }">
            <span class="product-name">{{ row.productName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productCode" label="商品编码" width="130" />
        <el-table-column prop="categoryId" label="分类" width="80">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100" align="right">
          <template #default="{ row }">
            <span class="price">¥{{ row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="70" align="center" />
        <el-table-column prop="sales" label="销量" width="70" align="center" />
        <el-table-column label="标签" width="120">
          <template #default="{ row }">
            <el-tag 
              v-for="(tag, index) in row.tags" 
              :key="index" 
              :style="{ backgroundColor: tag.tagColor, color: getContrastColor(tag.tagColor) }"
              size="small"
            >
              {{ tag.tagName }}
            </el-tag>
            <span v-if="!row.tags || row.tags.length === 0" class="no-tag">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-wrapper">
              <el-button class="btn-edit" @click="handleEdit(row)">编辑</el-button>
              <el-button class="btn-toggle" :class="{ 'btn-off': row.status === 1, 'btn-on': row.status === 0 }" @click="handleToggleStatus(row)">
                {{ row.status === 1 ? '下架' : '上架' }}
              </el-button>
              <el-button class="btn-delete" @click="handleDelete(row)">删除</el-button>
            </div>
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
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑商品" width="600px" :close-on-click-modal="false">
      <el-form :model="editForm" label-width="100px" :rules="editRules" ref="editFormRef">
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="editForm.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="editForm.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option label="手机数码" :value="1" />
            <el-option label="电脑办公" :value="2" />
            <el-option label="服装鞋帽" :value="3" />
            <el-option label="家用电器" :value="4" />
            <el-option label="食品生鲜" :value="5" />
            <el-option label="美妆护肤" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="editForm.price" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="editForm.originalPrice" :min="0" :precision="2" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="editForm.stock" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="主图URL" prop="mainImage">
          <el-input v-model="editForm.mainImage" placeholder="请输入商品主图URL" />
        </el-form-item>
        <el-form-item label="商品简介">
          <el-input v-model="editForm.brief" type="textarea" :rows="3" placeholder="请输入商品简介" />
        </el-form-item>
        <el-form-item label="商品状态">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="商品标签">
          <div class="tags-container">
            <div v-for="(tag, index) in editForm.tags" :key="index" class="tag-item">
              <el-input v-model="tag.tagName" placeholder="标签名称" style="width: 120px;" />
              <el-color-picker v-model="tag.tagColor" />
              <el-button type="danger" :icon="Delete" circle size="small" @click="removeEditTag(index)" />
            </div>
            <el-button type="primary" size="small" @click="addEditTag">添加标签</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEdit" :loading="editLoading">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete } from '@element-plus/icons-vue'
import { getProductList, updateProduct, downProduct, upProduct, deleteProduct } from '@/api/product'
import type { Product, ProductTag } from '@/api/product'

const loading = ref(false)
const searchQuery = ref('')
const categoryFilter = ref<number | string>('')
const statusFilter = ref<number | string>('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const tableData = ref<Product[]>([])

// 编辑弹窗相关
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref()
const editForm = reactive({
  id: 0,
  productName: '',
  categoryId: 0,
  price: 0,
  originalPrice: null as number | null,
  stock: 0,
  mainImage: '',
  brief: '',
  status: 1,
  tags: [] as ProductTag[]
})

const editRules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  mainImage: [{ required: true, message: '请输入主图URL', trigger: 'blur' }]
}

const categoryMap: Record<number, string> = {
  1: '手机数码',
  2: '电脑办公',
  3: '服装鞋帽',
  4: '家用电器',
  5: '食品生鲜',
  6: '美妆护肤'
}

const getCategoryName = (categoryId: number): string => {
  return categoryMap[categoryId] || '未知分类'
}

const getContrastColor = (hexColor: string): string => {
  const r = parseInt(hexColor.slice(1, 3), 16)
  const g = parseInt(hexColor.slice(3, 5), 16)
  const b = parseInt(hexColor.slice(5, 7), 16)
  const brightness = (r * 299 + g * 587 + b * 114) / 1000
  return brightness > 128 ? '#333333' : '#ffffff'
}

const handleSearch = async () => {
  loading.value = true
  try {
    const merchantId = localStorage.getItem('merchant_id') || '24'
    const response = await getProductList(parseInt(merchantId))
    
    if (response.code === 200) {
      let data = response.data
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        data = data.filter(item => 
          item.productName.toLowerCase().includes(query) ||
          item.productCode.toLowerCase().includes(query)
        )
      }
      
      if (categoryFilter.value !== '') {
        data = data.filter(item => item.categoryId === Number(categoryFilter.value))
      }
      
      if (statusFilter.value !== '') {
        data = data.filter(item => item.status === Number(statusFilter.value))
      }
      
      tableData.value = data
      total.value = data.length
    } else {
      ElMessage.error(response.message || '查询失败')
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchQuery.value = ''
  categoryFilter.value = ''
  statusFilter.value = ''
  handleSearch()
}

const handleAdd = () => {
  window.location.href = '/product/add'
}

const handleEdit = (row: Product) => {
  editForm.id = row.id
  editForm.productName = row.productName
  editForm.categoryId = row.categoryId
  editForm.price = row.price
  editForm.originalPrice = row.originalPrice
  editForm.stock = row.stock
  editForm.mainImage = row.mainImage
  editForm.brief = row.brief || ''
  editForm.status = row.status
  editForm.tags = row.tags ? [...row.tags] : []
  editDialogVisible.value = true
}

const addEditTag = () => {
  editForm.tags.push({ tagName: '', tagColor: '#409eff' })
}

const removeEditTag = (index: number) => {
  editForm.tags.splice(index, 1)
}

const submitEdit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    editLoading.value = true
    try {
      const response = await updateProduct({
        id: editForm.id,
        productName: editForm.productName,
        categoryId: editForm.categoryId,
        price: editForm.price,
        stock: editForm.stock,
        mainImage: editForm.mainImage,
        originalPrice: editForm.originalPrice || undefined,
        brief: editForm.brief || undefined,
        status: editForm.status,
        tags: editForm.tags.filter(t => t.tagName)
      })

      if (response.code === 200) {
        ElMessage.success('修改成功')
        editDialogVisible.value = false
        handleSearch()
      } else {
        ElMessage.error(response.message || '修改失败')
      }
    } catch (error) {
      console.error('修改商品失败:', error)
      ElMessage.error('修改商品失败')
    } finally {
      editLoading.value = false
    }
  })
}

const handleToggleStatus = async (row: Product) => {
  const action = row.status === 1 ? '下架' : '上架'
  try {
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = row.status === 1 
      ? await downProduct(row.id) 
      : await upProduct(row.id)

    if (response.code === 200) {
      ElMessage.success(`商品${action}成功`)
      handleSearch()
    } else {
      ElMessage.error(response.message || `${action}失败`)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`${action}商品失败:`, error)
      ElMessage.error(`${action}商品失败`)
    }
  }
}

const handleDelete = async (row: Product) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？删除后不可恢复。', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await deleteProduct(row.id)

    if (response.code === 200) {
      ElMessage.success('商品删除成功')
      handleSearch()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除商品失败:', error)
      ElMessage.error('删除商品失败')
    }
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  handleSearch()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  handleSearch()
}

handleSearch()
</script>

<style scoped>
.product-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 14px;
}

.original-price {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
  text-decoration: line-through;
}

.no-tag {
  color: #909399;
  font-size: 12px;
}

.el-tag {
  margin-right: 4px;
  margin-bottom: 4px;
}

.action-wrapper {
  display: flex;
  gap: 12px;
  justify-content: flex-start;
}

.btn-edit {
  padding: 5px 12px;
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  box-shadow: none !important;
  border-width: 1px;
}

.btn-edit:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
  box-shadow: none !important;
}

.btn-toggle {
  padding: 5px 12px;
  border-radius: 4px;
  font-size: 12px;
  box-shadow: none !important;
  border-width: 1px;
}

.btn-off {
  background-color: #f5a623;
  border-color: #f5a623;
  color: #fff;
}

.btn-off:hover {
  background-color: #f7b84e;
  border-color: #f7b84e;
  box-shadow: none !important;
}

.btn-on {
  background-color: #52c41a;
  border-color: #52c41a;
  color: #fff;
}

.btn-on:hover {
  background-color: #73d13d;
  border-color: #73d13d;
  box-shadow: none !important;
}

.btn-delete {
  padding: 5px 12px;
  background-color: #f56c6c;
  border-color: #f56c6c;
  color: #fff;
  border-radius: 4px;
  font-size: 12px;
  box-shadow: none !important;
  border-width: 1px;
}

.btn-delete:hover {
  background-color: #f78989;
  border-color: #f78989;
  box-shadow: none !important;
}

.product-name {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tags-container {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
