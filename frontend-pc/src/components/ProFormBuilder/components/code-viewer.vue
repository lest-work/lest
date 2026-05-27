<!-- eslint-disable vue/no-v-html -->
<template>
  <div class="ele-pro-form-builder-code-line-numbers">
    <div v-for="n in codeLines" :key="n">{{ n }}</div>
  </div>
  <pre class="ele-pro-form-builder-code-pre" v-html="codeHtml"></pre>
</template>

<script lang="ts" setup>
  import { ref, watch } from 'vue';
  import hljs from 'highlight.js';
  import 'highlight.js/styles/github-dark.css';
  import xml from 'highlight.js/lib/languages/xml';

  hljs.registerLanguage('vue', xml);

  const props = defineProps<{
    /** 代码 */
    code?: string;
  }>();

  /** 高亮后代码 */
  const codeHtml = ref('');

  /** 代码行数 */
  const codeLines = ref(1);

  watch(
    () => props.code,
    (code) => {
      const result = hljs.highlight(code ?? '', { language: 'vue' }).value;
      codeHtml.value = result;
      codeLines.value = result.split('\n').length;
    },
    { immediate: true }
  );
</script>
