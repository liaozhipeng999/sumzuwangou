<template>
  <div class="user-address">
    <el-card>
      <template #header>
        <span>收货地址管理</span>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="consignee" label="收货人" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="address" label="详细地址" min-width="200" />
        <el-table-column prop="isDefault" label="默认地址" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="success" size="small">默认</el-tag>
            <el-button v-else link type="primary" size="small" @click="setDefault(row)">设为默认</el-button>
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
    userId: 1,
    consignee: '张三',
    phone: '13800138000',
    address: '北京市朝阳区建国路88号1号楼1001室',
    isDefault: true
  }
])

const setDefault = (_row: any) => {
  ElMessage.success('已设为默认地址')
}
</script>

<style scoped>
.user-address {
  padding: 0;
}
</style>
