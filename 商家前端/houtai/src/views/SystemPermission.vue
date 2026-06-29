<template>
  <div class="system-permission">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>权限管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加角色
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色编码" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="userCount" label="用户数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handlePermission(row)">权限配置</el-button>
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
import { Plus } from '@element-plus/icons-vue'

const tableData = ref([
  {
    id: 1,
    name: '超级管理员',
    code: 'admin',
    description: '拥有系统所有权限',
    userCount: 2,
    status: true
  },
  {
    id: 2,
    name: '运营管理员',
    code: 'operator',
    description: '负责日常运营管理',
    userCount: 5,
    status: true
  },
  {
    id: 3,
    name: '普通用户',
    code: 'user',
    description: '普通用户角色',
    userCount: 100,
    status: true
  }
])

const handleAdd = () => {
  ElMessage.info('添加角色')
}

const handleEdit = (_row: any) => {
  ElMessage.info('编辑角色')
}

const handlePermission = (_row: any) => {
  ElMessage.info('权限配置')
}

const handleDelete = (_row: any) => {
  ElMessage.info('删除角色')
}
</script>

<style scoped>
.system-permission {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
