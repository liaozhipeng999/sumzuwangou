<template>
  <div class="product-category">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品分类</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="icon" label="图标" width="100">
          <template #default="{ row }">
            <el-icon :size="24"><component :is="row.icon" /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="productCount" label="商品数量" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Goods, Notebook, Van, House } from '@element-plus/icons-vue'

const tableData = ref([
  { id: 1, name: '手机数码', icon: Goods, sort: 1, productCount: 156, status: true },
  { id: 2, name: '电脑办公', icon: Notebook, sort: 2, productCount: 89, status: true },
  { id: 3, name: '服装鞋帽', icon: Van, sort: 3, productCount: 234, status: true },
  { id: 4, name: '家用电器', icon: House, sort: 4, productCount: 67, status: true }
])

const handleAdd = () => {
  ElMessage.info('添加分类功能')
}

const handleEdit = (_row: any) => {
  ElMessage.info('编辑分类功能')
}

const handleDelete = (_row: any) => {
  ElMessage.info('删除分类功能')
}
</script>

<style scoped>
.product-category {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
