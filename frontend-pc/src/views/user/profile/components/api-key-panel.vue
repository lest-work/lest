<template>
  <ele-card :loading="loading">
    <template #header>
      <div class="header">
        <span class="title">WakaTime API Key</span>
        <el-button
          v-if="!apiKey"
          type="primary"
          size="small"
          @click="generateKey"
          :loading="generating"
        >
          生成 API Key
        </el-button>
      </div>
    </template>

    <div v-if="apiKey" class="key-content">
      <div class="key-value">
        <span class="key-text">{{ apiKey }}</span>
        <div class="key-actions">
          <el-button size="small" @click="copyKey">
            <el-icon><CopyDocument /></el-icon>
            复制
          </el-button>
          <el-button size="small" type="danger" plain @click="showRegenerate = true">
            <el-icon><RefreshRight /></el-icon>
            重新生成
          </el-button>
        </div>
      </div>
      <div class="key-tips">
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            <span>将此 API Key 配置到 WakaTime 客户端插件中：</span>
          </template>
          <div style="margin-top: 4px">
            在 WakaTime 插件设置中输入以上 Key，或在
            <code>~/.wakatime.cfg</code> (Windows: <code>%USERPROFILE%\.wakatime.cfg</code>) 中添加：
          </div>
          <div class="config-example">
            <pre>[settings]
api_key = {{ apiKey }}
api_url = {{ apiUrl }}</pre>
          </div>
        </el-alert>
      </div>
    </div>
    <el-empty v-else description="您还没有生成 API Key，点击上方按钮生成" />

    <!-- 重新生成确认 -->
    <el-dialog v-model="showRegenerate" title="重新生成 API Key" width="400px">
      <el-alert type="warning" :closable="false" show-icon>
        重新生成后，旧的 API Key 将立即失效，请及时更新客户端配置！
      </el-alert>
      <template #footer>
        <el-button @click="showRegenerate = false">取消</el-button>
        <el-button type="danger" :loading="generating" @click="confirmRegenerate">确认重新生成</el-button>
      </template>
    </el-dialog>
  </ele-card>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { EleMessage } from 'ele-admin-plus';
  import { CopyDocument, RefreshRight } from '@element-plus/icons-vue';
  import { getWakapiUserKey, regenerateWakapiKey } from '@/api/wakapi/admin';

  defineOptions({ name: 'WakapiApiKeyPanel' });

  const loading = ref(false);
  const generating = ref(false);
  const apiKey = ref<string>('');
  const apiUrl = ref(import.meta.env.VITE_API_URL ?? window.location.origin + '/api');
  const showRegenerate = ref(false);

  const fetchKey = async () => {
    loading.value = true;
    try {
      const res = await getWakapiUserKey();
      apiKey.value = (res as unknown as { apiKey?: string })?.apiKey ?? '';
    } catch {
      apiKey.value = '';
    } finally {
      loading.value = false;
    }
  };

  const generateKey = async () => {
    generating.value = true;
    try {
      await regenerateWakapiKey();
      EleMessage.success('API Key 生成成功');
      await fetchKey();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '生成失败');
    } finally {
      generating.value = false;
    }
  };

  const confirmRegenerate = async () => {
    showRegenerate.value = false;
    await generateKey();
  };

  const copyKey = async () => {
    try {
      await navigator.clipboard.writeText(apiKey.value);
      EleMessage.success('已复制到剪贴板');
    } catch {
      const el = document.createElement('input');
      el.value = apiKey.value;
      document.body.appendChild(el);
      el.select();
      document.execCommand('copy');
      document.body.removeChild(el);
      EleMessage.success('已复制到剪贴板');
    }
  };

  fetchKey();
</script>

<style lang="scss" scoped>
  .header {
    display: flex;
    align-items: center;
    gap: 12px;

    .title {
      font-size: 15px;
      font-weight: 500;
    }
  }

  .key-content {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .key-value {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: var(--el-fill-color-light);
    border-radius: 6px;
    border: 1px solid var(--el-border-color-lighter);

    .key-text {
      flex: 1;
      font-family: 'SF Mono', Consolas, monospace;
      font-size: 14px;
      color: var(--el-text-color-primary);
      word-break: break-all;
      user-select: all;
    }

    .key-actions {
      display: flex;
      gap: 8px;
      flex-shrink: 0;
    }
  }

  .config-example {
    margin-top: 8px;

    pre {
      margin: 0;
      padding: 8px 12px;
      background: var(--el-fill-color-dark);
      border-radius: 4px;
      font-size: 12px;
      color: var(--el-text-color-regular);
      overflow-x: auto;
    }
  }

  code {
    padding: 1px 4px;
    background: var(--el-fill-color-light);
    border-radius: 3px;
    font-family: 'SF Mono', Consolas, monospace;
    font-size: 12px;
  }
</style>
