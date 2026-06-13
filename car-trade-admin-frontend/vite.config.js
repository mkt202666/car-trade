import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5174,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      },
    },
  },
  build: {
    // 自动分包（Vite 默认策略，无循环依赖问题）
    rollupOptions: {
      output: {
        // 仅将 node_modules 打包为 vendor，其余按路由自动拆分
        manualChunks: {
          vendor: ['react', 'react-dom', 'react-router-dom', 'axios', 'lucide-react', 'zustand', 'clsx', 'dayjs'],
        },
      },
    },
    chunkSizeWarningLimit: 600,
    target: 'es2020',
    minify: 'esbuild',
    sourcemap: false,
    cssCodeSplit: true,
  },
})
