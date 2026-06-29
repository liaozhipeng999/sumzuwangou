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
      '/user': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/recommend': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/product': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/category': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/images': {
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
      '/cart': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/search': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/MerchantChat': {
        target: 'http://localhost:8000',
        changeOrigin: true
      },
      '/customer-service': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
      }
    }
  }
})
