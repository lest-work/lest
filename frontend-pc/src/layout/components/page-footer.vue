<!-- 全局页脚 -->
<template>
  <ele-text type="placeholder" class="ele-footer">
    <el-space :size="24">
      <el-link underline="never" href="https://github.com/lest-work/lest" target="_blank">
        GitHub
      </el-link>
      <el-link underline="never" href="https://github.com/lest-work/lest/issues" target="_blank">
        反馈
      </el-link>
      <el-link underline="never" href="https://github.com/lest-work/lest/blob/main/README.md" target="_blank">
        文档
      </el-link>
    </el-space>
    <div style="margin-top: 8px">
      Copyright © {{ currentYear }} Lest Platform. MIT License.
    </div>
  </ele-text>
</template>

<script setup>
  const currentYear = new Date().getFullYear();
</script>

<style lang="scss" scoped>
  @use 'element-plus/theme-chalk/src/mixins/function.scss' as *;

  .ele-footer {
    padding: 16px 0;
    text-align: center;
    #{getCssVarName('text-color', 'regular')}: getCssVar(
      'text-color',
      'placeholder'
    );
  }
</style>
