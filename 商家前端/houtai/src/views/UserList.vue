<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <span>用户列表</span>
      </template>
      
      <div class="filter-bar">
        <el-input v-model="searchQuery" placeholder="搜索用户名称/手机号" style="width: 250px;" clearable>
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary">搜索</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="avatar" label="头像" width="100">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="50" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="registerTime" label="注册时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link :type="row.status === 'active' ? 'warning' : 'success'" @click="handleToggleStatus(row)">
              {{ row.status === 'active' ? '禁用' : '启用' }}
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
import { Search } from '@element-plus/icons-vue'

const searchQuery = ref('')

const tableData = ref([
  {
    id: 1,
    username: 'user001',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    phone: '13800138000',
    email: 'user001@example.com',
    registerTime: '2024-01-01 10:00:00',
    status: 'active'
  }
])

const handleView = (_row: any) => {
  ElMessage.info('查看用户详情')
}

const handleToggleStatus = (_row: any) => {
  ElMessage.success('状态更新成功')
}
</script>

<style scoped>
.user-list {
  padding: 0;
}

.filter-bar {
  display: flex;
  gap: 10px;
}
</style>
