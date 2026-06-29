<template>
  <div class="merchant-register-page">
    <div class="register-container">
      <div class="register-header">
        <el-button text @click="goToLogin">
          <el-icon><ArrowLeft /></el-icon>
          返回登录
        </el-button>
      </div>
      
      <el-card class="register-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <h2>商家入驻申请</h2>
            <p>填写以下信息，开启您的电商之旅</p>
          </div>
        </template>
        
        <el-form 
          :model="form" 
          :rules="rules" 
          ref="formRef" 
          label-width="120px"
          class="register-form"
        >
          <!-- 基本信息区域 -->
          <div class="form-section">
            <h3 class="section-title">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </h3>
            
            <el-form-item label="登录账号" prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入手机号或邮箱作为登录账号"
                maxlength="128"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
              <div class="form-tip">用户名长度为3-50位</div>
            </el-form-item>
            
            <el-form-item label="登录密码" prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入6-20位密码"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="form.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                show-password
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item label="店主姓名" prop="contactName">
              <el-input 
                v-model="form.contactName" 
                placeholder="请输入店主真实姓名"
                maxlength="32"
                clearable
              >
                <template #prefix>
                  <el-icon><UserFilled /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input 
                v-model="form.contactPhone" 
                placeholder="请输入联系电话"
                maxlength="16"
                clearable
              >
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            
            <el-form-item label="电子邮箱" prop="email">
              <el-input 
                v-model="form.email" 
                placeholder="请输入电子邮箱（选填）"
                maxlength="128"
                clearable
              >
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
              <div class="form-tip">用于接收重要通知和找回密码</div>
            </el-form-item>
          </div>
          
          <!-- 店铺信息区域 -->
          <div class="form-section">
            <h3 class="section-title">
              <el-icon><Shop /></el-icon>
              <span>店铺信息</span>
            </h3>
            
            <el-form-item label="店铺名称" prop="merchantName">
              <el-input 
                v-model="form.merchantName" 
                placeholder="请输入店铺名称"
                maxlength="128"
                clearable
              >
                <template #prefix>
                  <el-icon><Shop /></el-icon>
                </template>
              </el-input>
              <div class="form-tip">店铺名称将展示在店铺主页，建议使用品牌名称</div>
            </el-form-item>
            
            <el-form-item label="店铺简介" prop="merchantBrief">
              <el-input 
                v-model="form.merchantBrief" 
                type="textarea" 
                :rows="3"
                placeholder="请输入店铺简介（一句话介绍，选填）"
                maxlength="512"
                show-word-limit
              />
              <div class="form-tip">简短介绍您的店铺特色，吸引更多顾客</div>
            </el-form-item>
            
            <el-form-item label="主营分类" prop="mainCategoryId">
              <el-select 
                v-model="form.mainCategoryId" 
                placeholder="请选择主营分类"
                style="width: 100%;"
              >
                <el-option label="手机数码" :value="1" />
                <el-option label="电脑办公" :value="2" />
                <el-option label="服装鞋帽" :value="3" />
                <el-option label="家用电器" :value="4" />
                <el-option label="美妆护肤" :value="5" />
                <el-option label="食品饮料" :value="6" />
                <el-option label="家居家装" :value="7" />
                <el-option label="母婴玩具" :value="8" />
                <el-option label="图书音像" :value="9" />
                <el-option label="运动户外" :value="10" />
              </el-select>
              <div class="form-tip">选择主营分类有助于精准匹配目标客户</div>
            </el-form-item>
            
            <el-form-item label="店铺Logo" prop="merchantLogo">
              <el-upload
                action="#"
                list-type="picture-card"
                :auto-upload="false"
                :limit="1"
                :on-change="handleLogoChange"
                :on-remove="handleLogoRemove"
                :file-list="logoFileList"
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <div class="form-tip">建议上传 200x200 像素的图片，支持 JPG/PNG 格式（选填）</div>
            </el-form-item>
          </div>
          
          <!-- 协议同意 -->
          <el-form-item prop="agree">
            <el-checkbox v-model="form.agree">
              我已阅读并同意 
            </el-checkbox>
            <span class="agreement-links">
              <span class="agreement-link" @click="handleShowAgreement">《商家服务协议》</span> 
              <span>和</span>
              <span class="agreement-link" @click="handleShowPrivacy">《隐私政策》</span>
            </span>
          </el-form-item>
          
          <!-- 提交按钮 -->
          <el-form-item class="submit-section">
            <el-button 
              type="primary" 
              :loading="loading" 
              :disabled="!form.agree"
              @click="handleRegister"
              class="submit-btn"
            >
              提交申请
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 商家服务协议弹窗 -->
    <el-dialog title="商家服务协议" :visible.sync="showAgreement" width="900px" top="20px">
      <div class="agreement-iframe-wrapper">
        <MerchantAgreement />
      </div>
    </el-dialog>
    
    <!-- 隐私政策弹窗 -->
    <el-dialog title="隐私政策" :visible.sync="showPrivacy" width="900px" top="20px">
      <div class="agreement-iframe-wrapper">
        <MerchantPrivacy />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Lock,
  User,
  UserFilled,
  Phone,
  Message,
  Shop,
  Plus
} from '@element-plus/icons-vue'
import { useMerchantStore } from '@/stores/merchant'
import MerchantAgreement from './MerchantAgreement.vue'
import MerchantPrivacy from './MerchantPrivacy.vue'

const router = useRouter()
const merchantStore = useMerchantStore()
const formRef = ref()
const loading = ref(false)
const showAgreement = ref(false)
const showPrivacy = ref(false)
const logoFileList = ref<any[]>([])

const form = reactive({
  username: 'test_merchant001',
  password: '123456',
  confirmPassword: '123456',
  contactName: '张三',
  contactPhone: '13800138001',
  email: 'test@example.com',
  merchantName: '我的小店',
  merchantBrief: '专业销售各类优质商品',
  mainCategoryId: 1,
  merchantLogo: '',
  agree: true
})

// 判断邮箱（宽松验证）
function isEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
}

// 实时验证规则
const rules = {
  username: [
    { required: true, message: '请输入登录账号', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为3-50位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: any) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  contactName: [
    { required: true, message: '请输入店主姓名', trigger: 'blur' },
    { min: 2, max: 32, message: '姓名长度为2-32位', trigger: 'blur' },
    { 
      pattern: /^[\u4e00-\u9fa5a-zA-Z]+$/, 
      message: '姓名只能包含中文或字母', 
      trigger: 'blur' 
    }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  email: [
    { 
      validator: (_rule: any, value: string, callback: any) => {
        if (value && !isEmail(value)) {
          callback(new Error('请输入正确的邮箱格式'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  merchantName: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 128, message: '店铺名称长度为2-128位', trigger: 'blur' }
  ],
  mainCategoryId: [
    { required: true, message: '请选择主营分类', trigger: 'change' }
  ],
  agree: []
}

const handleLogoChange = (file: any) => {
  form.merchantLogo = file.raw
  logoFileList.value = [file]
}

const handleLogoRemove = () => {
  form.merchantLogo = ''
  logoFileList.value = []
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      
      try {
        const result = await merchantStore.register({
          merchantName: form.merchantName,
          username: form.username,
          password: form.password,
          contactName: form.contactName,
          contactPhone: form.contactPhone,
          email: form.email || undefined,
          merchantBrief: form.merchantBrief || undefined,
          mainCategoryId: form.mainCategoryId || undefined,
          merchantLogo: form.merchantLogo ? 'uploaded' : undefined
        })
        
        if (result.success) {
          ElMessage.success('注册成功！')
          router.push('/merchant/login')
        } else {
          ElMessage.error(result.message)
        }
      } finally {
        loading.value = false
      }
    }
  })
}

const handleShowAgreement = () => {
  showAgreement.value = true
}

const handleShowPrivacy = () => {
  showPrivacy.value = true
}

const goToLogin = () => {
  router.push('/merchant/login')
}
</script>

<style scoped>
.merchant-register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 20px;
  padding-top: 40px;
}

.register-container {
  width: 700px;
  max-width: 100%;
}

.register-header {
  margin-bottom: 20px;
}

.register-header .el-button {
  color: #fff;
  font-size: 14px;
}

.register-card {
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.card-header h2 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.card-header p {
  font-size: 14px;
  color: #666;
}

.register-form {
  padding: 20px 0;
}

.form-section {
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 20px;
  margin-bottom: 20px;
}

.form-section:last-of-type {
  border-bottom: none;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding: 10px 15px;
  background: #f5f5f5;
  border-radius: 8px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.agreement-links {
  margin-left: 8px;
  font-size: 13px;
  color: #606266;
  
  span:not(.agreement-link) {
    margin: 0 5px;
    color: #909399;
  }
}

.agreement-link {
  color: #409eff;
  cursor: pointer;
  
  &:hover {
    color: #66b1ff;
    text-decoration: underline;
  }
}

.submit-section {
  text-align: center;
  margin-top: 30px;
}

.submit-btn {
  width: 200px;
  height: 45px;
  font-size: 16px;
}

.agreement-content {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}

.agreement-content h4 {
  margin: 15px 0 10px;
  font-size: 14px;
  color: #333;
}

.agreement-content p {
  margin: 5px 0;
  font-size: 13px;
  color: #666;
  line-height: 1.8;
}

.agreement-iframe-wrapper {
  max-height: 600px;
  overflow-y: auto;
  padding: 10px;
}

@media (max-width: 768px) {
  .register-container {
    padding: 10px;
  }
  
  .el-form-item__label {
    width: 100px !important;
  }
}
</style>