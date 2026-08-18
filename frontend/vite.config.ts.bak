import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'


// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  const iotOnly = env.VITE_IOT_ONLY === 'true'

  return {
    plugins: [react()],
    server: {
      port: 3000,
      proxy: {
        '/api': {
          target: iotOnly ? 'http://localhost:8082' : 'http://localhost:8080',
          changeOrigin: true,
          rewrite: iotOnly ? (path) => path.replace(/^\/api/, '') : undefined,
        },
      },
    },
  }
})
