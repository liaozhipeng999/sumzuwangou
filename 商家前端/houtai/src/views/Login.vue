<template>
  <div class="login-page">
    <div class="login-container">
      <el-card class="login-card">
        <template #header>
          <h2>电商后台管理系统</h2>
        </template>
        
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large">
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password>
              <template #prefix>
                <el-icon><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" size="large" style="width: 100%;" @click="handleLogin">
              登录
            </el-button>
          </el-form-item>
          
          <div class="login-footer">
            <el-checkbox v-model="form.remember">记住密码</el-checkbox>
            <a href="#">忘记密码？</a>
          </div>
        </el-form>
      </el-card>
      
      <div class="login-tips">
        <p>默认账号: admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  remember: false
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate((valid: boolean) => {
    if (valid) {
      if (form.username === 'admin' && form.password === 'admin123') {
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error('用户名或密码错误')
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  width: 400px;
}

.login-card {
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.login-card h2 {
  text-align: center;
  margin: 0;
  color: #333;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-footer a {
  color: #409eff;
  text-decoration: none;
}

.login-tips {
  margin-top: 20px;
  text-align: center;
  color: #fff;
  font-size: 14px;
}
</style>
