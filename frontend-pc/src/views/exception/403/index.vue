<script setup lang="ts">
import { useRouter } from 'vue-router';
const router = useRouter();
</script>

<template>
  <div class="forbidden-page">
    <el-result
      icon="error"
      title="403"
      sub-title="抱歉，您无权访问该页面"
    >
      <template #extra>
        <el-button type="primary" @click="router.push('/dashboard')">
          返回首页
        </el-button>
      </template>
    </el-result>
  </div>
</template>

<style scoped lang="scss">
.forbidden-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
