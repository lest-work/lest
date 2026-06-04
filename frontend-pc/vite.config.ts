import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

export default defineConfig(() => {
  return {
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
      },
    },
    plugins: [
      vue(),
      AutoImport({
        resolvers: [ElementPlusResolver()],
        imports: ['vue', 'vue-router', 'pinia'],
        dts: 'src/auto-imports.d.ts',
        eslintrc: {
          enabled: true,
        },
      }),
      Components({
        resolvers: [ElementPlusResolver()],
        dts: 'src/components.d.ts',
      }),
    ],
    server: {
      port: 5173,
      proxy: {
        '/auth': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/project': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/task': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/release': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/user': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/dashboard': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/label': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/upload': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/notification': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/plugin': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/ai': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/meeting': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/openapi': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
        '/ws': {
          target: 'ws://localhost:8080',
          ws: true,
        },
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@/styles/variables" as *;`,
          silenceDeprecations: ['legacy-js-api'],
        },
      },
    },
    build: {
      target: 'es2022',
      rollupOptions: {
        output: {
          manualChunks: {
            'element-plus': ['element-plus'],
            'echarts': ['echarts'],
            'vendor': ['vue', 'vue-router', 'pinia', 'axios'],
          },
        },
      },
    },
  };
});
