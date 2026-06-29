import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import Vant from 'vant'
import 'vant/lib/index.css'
import './style.css'
import App from './App.vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Vant)

// 初始化用户登录状态（从 localStorage/sessionStorage 恢复）
const userStore = useUserStore()
userStore.init()

// 初始化购物车数据（从 localStorage 恢复缓存）
const cartStore = useCartStore()
cartStore.init()

app.mount('#app')
