<!-- 全局页脚 -->
<template>
  <ele-text type="placeholder" class="ele-footer">
    <el-space :size="24">
      <el-link underline="never" href="http://doc.ruoyi.vip" target="_blank">
        RuoYi
      </el-link>
      <el-link
        underline="never"
        href="https://eleadmin.com/doc/eleadminplus/"
        target="_blank"
      >
        EleAdmin
      </el-link>
    </el-space>
    <div style="margin-top: 8px">
      Copyright © 2025 RuoYi & EleAdmin All Rights Reserved.
    </div>
  </ele-text>
</template>

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
