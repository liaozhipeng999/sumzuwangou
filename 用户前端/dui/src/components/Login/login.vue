<template>
  <div class="login-page">
    <!-- 顶部区域 -->
    <div class="header">
      <van-image
        round
        width="80"
        height="80"
        src="https://ts1.tc.mm.bing.net/th/id/OIP-C.p1yrinIljZwXHLyJ5fR--wAAAA?rs=1&pid=ImgDetMain&o=7&rm=3"
        class="logo"
      />
      <h2 class="title">欢迎登录</h2>
      <p class="subtitle">请登录您的账号以继续</p>
    </div>

    <!-- Tab 切换 -->
    <van-tabs v-model:active="activeTab" class="tabs" animated swipeable>
      <van-tab title="登录">
        <div class="form-container">
          <van-form @submit="onLogin">
            <van-cell-group inset>
              <van-field
                v-model="loginForm.username"
                name="username"
                label=""
                placeholder="请输入用户名/手机号/邮箱"
                :rules="[{ required: true, message: '请填写账号' }]"
              >
                <template #left-icon>
                  <van-icon name="user-o" class="field-icon" />
                </template>
              </van-field>
              <van-field
                v-model="loginForm.password"
                type="password"
                name="password"
                label=""
                placeholder="请输入密码"
                :rules="[{ required: true, message: '请填写密码' }]"
              >
                <template #left-icon>
                  <van-icon name="lock" class="field-icon" />
                </template>
              </van-field>
            </van-cell-group>

            <div class="options">
              <van-checkbox v-model="loginForm.remember" shape="square" class="remember">
                记住密码
              </van-checkbox>
              <span class="forgot" @click="onForgot">忘记密码？</span>
            </div>

            <div class="default-account">
              <van-button 
                round 
                block 
                type="default" 
                size="small" 
                @click="fillDefaultAccount"
                class="default-btn"
              >
                使用测试账号登录
              </van-button>
              <p class="default-hint">测试环境可用</p>
            </div>

            <div class="btn-wrap">
              <van-button round block type="primary" native-type="submit" class="submit-btn">
                登 录
              </van-button>
            </div>
          </van-form>
        </div>
      </van-tab>

      <van-tab title="注册">
        <div class="form-container">
          <van-form @submit="onRegister">
            <van-cell-group inset>
              <van-field
                v-model="registerForm.username"
                name="username"
                label=""
                placeholder="请输入用户名"
                :rules="[{ required: true, message: '请填写用户名' }, { pattern: /^.{3,20}$/, message: '用户名长度3-20位' }]"
              >
                <template #left-icon>
                  <van-icon name="user-o" class="field-icon" />
                </template>
              </van-field>
              <van-field
                v-model="registerForm.phone"
                name="phone"
                label=""
                placeholder="请输入手机号"
                :rules="[{ required: true, message: '请填写手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }]"
              >
                <template #left-icon>
                  <van-icon name="phone-o" class="field-icon" />
                </template>
                <template #right-icon>
                  <van-button size="small" type="primary" @click="sendCode">
                    {{ codeText }}
                  </van-button>
                </template>
              </van-field>
              <van-field
                v-model="registerForm.email"
                name="email"
                label=""
                placeholder="请输入邮箱（选填）"
                :rules="[{ pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '邮箱格式不正确' }]"
              >
                <template #left-icon>
                  <van-icon name="mail-o" class="field-icon" />
                </template>
              </van-field>
              <van-field
                v-model="registerForm.nickname"
                name="nickname"
                label=""
                placeholder="请输入昵称（选填）"
              >
                <template #left-icon>
                  <van-icon name="user-circle-o" class="field-icon" />
                </template>
              </van-field>
              <van-field
                v-model="registerForm.password"
                type="password"
                name="password"
                label=""
                placeholder="请设置密码"
                :rules="[{ required: true, message: '请设置密码' }, { pattern: /^.{6,20}$/, message: '密码长度6-20位' }]"
              >
                <template #left-icon>
                  <van-icon name="lock" class="field-icon" />
                </template>
              </van-field>
              <van-field
                v-model="registerForm.confirmPassword"
                type="password"
                name="confirmPassword"
                label=""
                placeholder="请确认密码"
                :rules="[{ required: true, message: '请确认密码' }, { validator: validateConfirmPassword, message: '两次密码不一致' }]"
              >
                <template #left-icon>
                  <van-icon name="lock" class="field-icon" />
                </template>
              </van-field>
              <!-- 图形验证码 -->
              <van-field
                v-model="registerForm.captcha"
                name="captcha"
                label=""
                placeholder="请输入验证码"
                :rules="[{ required: true, message: '请填写验证码' }]"
              >
                <template #button>
                  <van-image
                    v-if="captchaUrl"
                    :src="captchaUrl"
                    class="captcha-img"
                    @click="fetchCaptcha"
                  />
                  <van-button v-else size="small" type="primary" @click="fetchCaptcha">
                    获取验证码
                  </van-button>
                </template>
              </van-field>
            </van-cell-group>

            <div class="agreement">
              <van-checkbox v-model="registerForm.agree" shape="square">
                我已阅读并同意
                <span class="link" @click.stop="showAgreement">《用户协议》</span>
                和
                <span class="link" @click.stop="showPrivacy">《隐私政策》</span>
              </van-checkbox>
            </div>

            <div class="btn-wrap">
              <van-button round block type="primary" native-type="submit" class="submit-btn">
                注 册
              </van-button>
            </div>
          </van-form>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 第三方登录 -->
    <div class="third-party">
      <van-divider>其他登录方式</van-divider>
      <div class="third-icons">
        <van-icon name="wechat" class="third-icon wechat" @click="thirdLogin('wechat')" />
        <van-icon name="alipay" class="third-icon alipay" @click="thirdLogin('alipay')" />
        <van-icon name="qq" class="third-icon qq" @click="thirdLogin('qq')" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { useUserStore } from '@/stores/user'
import { DEFAULT_USERNAME, DEFAULT_PASSWORD } from '@/router'

const router = useRouter()
const route = useRoute()
const activeTab = ref(0)
const userStore = useUserStore()

const autoLogin = async () => {
  try {
    const token = sessionStorage.getItem('token')
    const userInfoStr = localStorage.getItem('userInfo')
    
    if (token && userInfoStr) {
      const loginInfoStr = localStorage.getItem('loginInfo')
      if (loginInfoStr) {
        const loginInfo = JSON.parse(loginInfoStr)
        const now = Date.now()
        
        if (!loginInfo.expireTime || loginInfo.expireTime <= now) {
          localStorage.removeItem('loginInfo')
          sessionStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          return false
        }
      }
      
      try {
        const userInfo = JSON.parse(userInfoStr)
        userStore.setUserInfo(userInfo)
        userStore.setToken(token)
        
        showToast('自动登录成功')
        const redirectPath = route.query.redirect as string || '/home'
        router.push(redirectPath)
        return true
      } catch (e) {
        console.error('自动登录失败:', e)
      }
    }
  } catch (error) {
    console.error('自动登录失败:', error)
  }
  return false
}

// 页面挂载时检查自动登录
onMounted(async () => {
  // 先检查自动登录
  const loggedIn = await autoLogin()
  
  // 如果没有自动登录成功，再初始化页面
  if (!loggedIn) {
    // 如果默认显示注册页，自动获取验证码
    if (activeTab.value === 1) {
      fetchCaptcha()
    }
    
    // 如果有保存的登录信息（但可能过期或登录失败），自动填充用户名
    try {
      const loginInfoStr = localStorage.getItem('loginInfo')
      if (loginInfoStr) {
        const loginInfo = JSON.parse(loginInfoStr)
        loginForm.username = loginInfo.username || ''
      }
    } catch (e) {
      console.error('读取登录信息失败:', e)
    }
  }
})

// 监听 Tab 切换，切换到注册页时自动获取验证码
watch(activeTab, (newVal) => {
  if (newVal === 1 && !captchaUrl.value) {
    fetchCaptcha()
  }
})

// 登录表单（支持用户名、手机号、邮箱登录）
const loginForm = reactive({
  username: '',  // 用户名/手机号/邮箱
  password: '',
  remember: false
})

// 注册表单
const registerForm = reactive({
  username: '',
  phone: '',
  email: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  captcha: '',
  agree: true
})

// 图形验证码
const captchaUrl = ref('')
const captchaKey = ref('')

// 验证码
const codeText = ref('获取验证码')
let countdown = 0
let timer: ReturnType<typeof setInterval> | null = null

// 填充默认账号
const fillDefaultAccount = () => {
  loginForm.username = DEFAULT_USERNAME
  loginForm.password = DEFAULT_PASSWORD
  showToast('已填充默认账号')
}

// 登录（支持用户名、手机号、邮箱登录）
const onLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    showToast('请填写账号和密码')
    return
  }
  
  try {
    // 尝试调用后端登录接口
    const response = await fetch('/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: loginForm.username,
        password: loginForm.password
      })
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      handleLoginSuccess(result)
    } else {
      showToast(result.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    showToast('登录失败，请稍后重试')
  }
}

// 处理登录成功
const handleLoginSuccess = (result: { data?: any; token?: string }) => {
  // 存储用户信息到Pinia store
  if (result.data) {
    userStore.setUserInfo(result.data)
  }
  
  // 存储token到Pinia store
  userStore.setToken(result.token || 'user-token')
  
  // 如果勾选了记住密码，保存到localStorage，有效期7天
  if (loginForm.remember) {
    const loginInfo = {
      username: loginForm.username,
      expireTime: Date.now() + 7 * 24 * 60 * 60 * 1000
    }
    localStorage.setItem('loginInfo', JSON.stringify(loginInfo))
  } else {
    localStorage.removeItem('loginInfo')
  }
  
  showToast('登录成功')
  
  // 登录回跳：获取跳转前的页面地址，没有则跳首页
  const redirectPath = route.query.redirect as string || '/home'
  router.push(redirectPath)
}

// 注册
const onRegister = async () => {
  if (!registerForm.agree) {
    showToast('请先同意用户协议')
    return
  }
  
  try {
    const response = await fetch('/user/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Captcha-Key': captchaKey.value || localStorage.getItem('captchaKey') || ''
      },
      body: JSON.stringify({
        username: registerForm.username,
        password: registerForm.password,
        confirmPassword: registerForm.confirmPassword,
        phone: registerForm.phone,
        email: registerForm.email,
        nickname: registerForm.nickname,
        captcha: registerForm.captcha
      })
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      showToast('注册成功')
      // 注册成功后切换到登录页
      activeTab.value = 0
      // 清空注册表单
      registerForm.username = ''
      registerForm.phone = ''
      registerForm.email = ''
      registerForm.nickname = ''
      registerForm.password = ''
      registerForm.confirmPassword = ''
      registerForm.captcha = ''
      // 刷新验证码
      fetchCaptcha()
    } else {
      showToast(result.message || '注册失败')
      // 只有验证码相关错误才刷新验证码
      const msg = result.message || ''
      if (msg.includes('验证码') || msg.includes('captcha')) {
        fetchCaptcha()
      }
    }
  } catch (error) {
    console.error('注册失败:', error)
    showToast('注册失败，请稍后重试')
  }
}

// 忘记密码
const onForgot = () => {
  showToast('忘记密码')
}

// 发送验证码
const sendCode = () => {
  if (countdown > 0) return
  if (!registerForm.phone) {
    showToast('请输入手机号')
    return
  }
  countdown = 60
  codeText.value = `${countdown}s`
  timer = setInterval(() => {
    countdown--
    if (countdown <= 0) {
      codeText.value = '获取验证码'
      if (timer) clearInterval(timer)
    } else {
      codeText.value = `${countdown}s`
    }
  }, 1000)
  showToast('验证码已发送')
}

// 获取图形验证码
const fetchCaptcha = async () => {
  try {
    const response = await fetch('/user/captcha')
    
    if (!response.ok) {
      throw new Error('获取验证码失败')
    }
    
    // 从响应头获取 Captcha-Key
    const key = response.headers.get('Captcha-Key')
    if (key) {
      captchaKey.value = key
      localStorage.setItem('captchaKey', key)
    }
    
    // 将响应转为 Blob 并创建图片 URL
    const blob = await response.blob()
    const imageUrl = URL.createObjectURL(blob)
    captchaUrl.value = imageUrl
    
    console.log('验证码获取成功，Captcha-Key:', captchaKey.value)
  } catch (error) {
    console.error('获取验证码失败:', error)
    showToast('获取验证码失败，请稍后重试')
  }
}

// 确认密码验证
const validateConfirmPassword = () => {
  return registerForm.password === registerForm.confirmPassword
}

// 显示协议
const showAgreement = () => {
  showDialog({ title: '用户协议', message: '这里是用户协议内容...' })
}

// 显示隐私政策
const showPrivacy = () => {
  showDialog({ title: '隐私政策', message: '这里是隐私政策内容...' })
}

// 第三方登录
const thirdLogin = (type: string) => {
  showToast(`${type} 登录`)
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #f7f8fa;
}

/* 顶部区域 */
.header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px 20px;
}

.logo {
  margin-bottom: 16px;
}

.title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin: 0 0 8px;
}

.subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* Tab 切换 */
.tabs {
  margin-top: 10px;
}

.tabs :deep(.van-tabs__nav) {
  background: transparent;
}

.tabs :deep(.van-tab) {
  font-size: 16px;
}

.tabs :deep(.van-tab--active) {
  font-weight: bold;
}

/* 表单 */
.form-container {
  padding: 20px 0;
}

.field-icon {
  font-size: 18px;
  color: #999;
  margin-right: 8px;
}

.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
}

.remember {
  font-size: 13px;
}

.forgot {
  font-size: 13px;
  color: #1989fa;
}

.default-account {
  padding: 0 24px;
  margin-bottom: 12px;
}

.default-btn {
  height: 36px;
  font-size: 13px;
  background: #f5f5f5;
  color: #666;
  border-color: #ddd;
}

.default-hint {
  text-align: center;
  font-size: 11px;
  color: #999;
  margin: 8px 0 0;
}

.agreement {
  padding: 12px 24px;
  font-size: 12px;
}

.agreement .link {
  color: #1989fa;
}

.btn-wrap {
  padding: 20px 24px;
}

.submit-btn {
  height: 44px;
  font-size: 16px;
}

.code-btn {
  height: 32px;
  font-size: 12px;
}

/* 图形验证码 */
.captcha-img {
  width: 100px;
  height: 36px;
  border-radius: 4px;
  cursor: pointer;
}

/* 第三方登录 */
.third-party {
  padding: 20px 40px;
}

.third-icons {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-top: 16px;
}

.third-icon {
  font-size: 36px;
  cursor: pointer;
}

.wechat {
  color: #07c160;
}

.alipay {
  color: #1677ff;
}

.qq {
  color: #12b7f5;
}
</style>
