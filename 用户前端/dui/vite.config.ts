import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    host: '::',
    proxy: {
      '/api/category': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/category/, '/category')
      },
      '/api/search': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/search/, '/search')
      },
      '/api/cart': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/cart/, '/cart')
      },
      '/api/customer-service': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/customer-service/, '/customer-service')
      },
      '/brand': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/images': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/user': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/recommend': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/catalog': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/favorite': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/product/detail': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/product\/detail/, '/api/product/detail')
      },
      '/product/favorite': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/MerchantChat': {
        target: 'http://localhost:8000',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
