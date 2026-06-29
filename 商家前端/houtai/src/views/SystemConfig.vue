<template>
  <div class="system-config">
    <el-card>
      <template #header>
        <span>基础配置</span>
      </template>
      
      <el-form :model="form" label-width="120px" style="max-width: 600px;">
        <el-form-item label="网站名称">
          <el-input v-model="form.siteName" placeholder="请输入网站名称" />
        </el-form-item>
        
        <el-form-item label="网站Logo">
          <el-upload action="#" list-type="picture" :auto-upload="false">
            <el-button>选择图片</el-button>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="网站描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        
        <el-form-item label="联系邮箱">
          <el-input v-model="form.email" placeholder="请输入联系邮箱" />
        </el-form-item>
        
        <el-form-item label="版权信息">
          <el-input v-model="form.copyright" placeholder="请输入版权信息" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary">保存</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const form = reactive({
  siteName: '电商后台管理系统',
  description: '专业的电商后台管理系统',
  phone: '400-888-8888',
  email: 'admin@example.com',
  copyright: '© 2024 电商后台管理系统'
})
</script>

<style scoped>
.system-config {
  padding: 0;
}
</style>
