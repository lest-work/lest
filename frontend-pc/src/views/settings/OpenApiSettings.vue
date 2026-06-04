<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { openApiApi, type ApiKey, type WebhookEndpoint } from '@/api/openapi';

const activeTab = ref<'apikey' | 'webhook'>('apikey');
const loading = ref(false);

// API Key
const apiKeys = ref<ApiKey[]>([]);
const keyDialogVisible = ref(false);
const keyForm = ref({ keyName: '', permissions: '', expiresAt: '' });

async function loadKeys() {
  loading.value = true;
  try {
    const res = await openApiApi.key.list();
    if (res.code === 200) apiKeys.value = res.data.records || [];
  } catch {}
  finally { loading.value = false; }
}

async function createKey() {
  try {
    const res = await openApiApi.key.create({ keyName: keyForm.value.keyName, expiresAt: keyForm.value.expiresAt || undefined });
    if (res.code === 200) {
      ElMessageBox.alert('API Key 已创建，请妥善保管：\n' + (res.data.apiKey || ''), 'API Key 创建成功', { confirmButtonText: '复制' })
        .then(() => { navigator.clipboard?.writeText(res.data.apiKey || ''); }).catch(() => {});
      keyDialogVisible.value = false;
      await loadKeys();
    }
  } catch (e: any) { ElMessage.error(e.message || '创建失败'); }
}

async function revokeKey(k: ApiKey) {
  try {
    await ElMessageBox.confirm('确定撤销此 API Key？撤销后立即失效。', '撤销确认', { type: 'warning' });
    await openApiApi.key.revoke(k.keyId!);
    ElMessage.success('已撤销');
    await loadKeys();
  } catch {}
}

// Webhook
const webhooks = ref<WebhookEndpoint[]>([]);
const webhookDialogVisible = ref(false);
const webhookForm = ref({ name: '', targetUrl: '', events: '', secret: '' });
const webhookEvents = ['task.created', 'task.updated', 'task.status.changed', 'task.assigned', 'iteration.started', 'iteration.completed'];

async function loadWebhooks() {
  loading.value = true;
  try {
    const res = await openApiApi.webhook.list();
    if (res.code === 200) webhooks.value = res.data.records || [];
  } catch {}
  finally { loading.value = false; }
}

async function createWebhook() {
  try {
    const events = webhookForm.value.events.split(',').map(e => e.trim()).filter(Boolean);
    await openApiApi.webhook.create({ name: webhookForm.value.name, targetUrl: webhookForm.value.targetUrl, events, secret: webhookForm.value.secret });
    ElMessage.success('Webhook 创建成功');
    webhookDialogVisible.value = false;
    await loadWebhooks();
  } catch (e: any) { ElMessage.error(e.message || '创建失败'); }
}

async function deleteWebhook(w: WebhookEndpoint) {
  try {
    await ElMessageBox.confirm('确定删除此 Webhook？', '删除确认', { type: 'warning' });
    await openApiApi.webhook.delete(w.webhookId!);
    ElMessage.success('已删除');
    await loadWebhooks();
  } catch {}
}

function openCreateKey() { keyForm.value = { keyName: '', permissions: '', expiresAt: '' }; keyDialogVisible.value = true; }
function openCreateWebhook() { webhookForm.value = { name: '', targetUrl: '', events: '', secret: '' }; webhookDialogVisible.value = true; }

onMounted(() => { loadKeys(); loadWebhooks(); });
</script>

<template>
  <div class="openapi-view">
    <div class="page-header">
      <h1 class="page-title">开放平台</h1>
    </div>

    <div class="tab-bar">
      <button class="tab-btn" :class="{ active: activeTab === 'apikey' }" @click="activeTab = 'apikey'; loadKeys()">API Key</button>
      <button class="tab-btn" :class="{ active: activeTab === 'webhook' }" @click="activeTab = 'webhook'; loadWebhooks()">Webhook</button>
    </div>

    <!-- API Key -->
    <div v-if="activeTab === 'apikey'">
      <div class="section-actions">
        <button class="action-btn primary" @click="openCreateKey">+ 创建 Key</button>
      </div>
      <div v-if="loading" class="loading-placeholder">加载中...</div>
      <div v-else-if="apiKeys.length === 0" class="empty-state">暂无 API Key，点击上方按钮创建一个</div>
      <div v-else class="key-list">
        <div v-for="k in apiKeys" :key="k.keyId" class="key-card">
          <div class="key-info">
            <div class="key-name">{{ k.keyName }}</div>
            <div class="key-prefix">{{ k.keyPrefix ? k.keyPrefix + '***' : '***' }}</div>
            <div v-if="k.expiresAt" class="key-expires">过期：{{ k.expiresAt }}</div>
          </div>
          <div class="key-actions">
            <span class="key-status" :class="{ active: k.isEnabled }">{{ k.isEnabled ? '启用' : '禁用' }}</span>
            <button class="text-btn danger" @click="revokeKey(k)">撤销</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Webhook -->
    <div v-if="activeTab === 'webhook'">
      <div class="section-actions">
        <button class="action-btn primary" @click="openCreateWebhook">+ 创建 Webhook</button>
      </div>
      <div v-if="loading" class="loading-placeholder">加载中...</div>
      <div v-else-if="webhooks.length === 0" class="empty-state">暂无 Webhook，点击上方按钮创建一个</div>
      <div v-else class="key-list">
        <div v-for="w in webhooks" :key="w.webhookId" class="key-card">
          <div class="key-info">
            <div class="key-name">{{ w.name }}</div>
            <div class="key-url">{{ w.targetUrl }}</div>
            <div class="key-events">
              <span v-for="e in (w.events || [])" :key="e" class="event-tag">{{ e }}</span>
            </div>
          </div>
          <div class="key-actions">
            <span class="key-status" :class="{ active: w.isEnabled }">{{ w.isEnabled ? '启用' : '禁用' }}</span>
            <button class="text-btn danger" @click="deleteWebhook(w)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Key Dialog -->
    <Teleport to="body">
      <div v-if="keyDialogVisible" class="dialog-overlay" @click.self="keyDialogVisible = false">
        <div class="dialog-panel">
          <div class="dialog-header">创建 API Key <button class="close-btn" @click="keyDialogVisible = false">&times;</button></div>
          <div class="dialog-body">
            <div class="form-row"><label>Key 名称</label><input v-model="keyForm.keyName" placeholder="如：我的应用" /></div>
            <div class="form-row"><label>过期时间（可选）</label><input v-model="keyForm.expiresAt" type="date" /></div>
          </div>
          <div class="dialog-footer">
            <button class="btn secondary" @click="keyDialogVisible = false">取消</button>
            <button class="btn primary" @click="createKey">创建</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- Webhook Dialog -->
    <Teleport to="body">
      <div v-if="webhookDialogVisible" class="dialog-overlay" @click.self="webhookDialogVisible = false">
        <div class="dialog-panel">
          <div class="dialog-header">创建 Webhook <button class="close-btn" @click="webhookDialogVisible = false">&times;</button></div>
          <div class="dialog-body">
            <div class="form-row"><label>名称</label><input v-model="webhookForm.name" placeholder="如：CI 构建通知" /></div>
            <div class="form-row"><label>目标 URL</label><input v-model="webhookForm.targetUrl" placeholder="https://your-server.com/webhook" /></div>
            <div class="form-row"><label>触发事件（逗号分隔）</label><input v-model="webhookForm.events" placeholder="task.created, task.updated" /></div>
            <div class="form-row"><label>Secret（可选）</label><input v-model="webhookForm.secret" placeholder="签名密钥" /></div>
          </div>
          <div class="dialog-footer">
            <button class="btn secondary" @click="webhookDialogVisible = false">取消</button>
            <button class="btn primary" @click="createWebhook">创建</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped lang="scss">
.openapi-view { padding: var(--space-6) var(--space-5); }
.page-header { margin-bottom: var(--space-4); }
.page-title { font-size: var(--text-3xl); font-weight: var(--font-semibold); color: var(--text-primary); margin: 0; }
.tab-bar { display: flex; gap: 0; margin-bottom: var(--space-5); border-bottom: 1px solid var(--border-color); }
.tab-btn { padding: var(--space-2) var(--space-4); border: none; background: transparent; color: var(--text-secondary); cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); border-bottom: 2px solid transparent; margin-bottom: -1px; transition: color var(--transition-fast); &.active { color: var(--color-primary); border-bottom-color: var(--color-primary); font-weight: var(--font-semibold); } &:hover { color: var(--text-primary); } }
.section-actions { display: flex; justify-content: flex-end; margin-bottom: var(--space-4); }
.action-btn { padding: var(--space-2) var(--space-4); border-radius: var(--radius-md); border: none; cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); transition: opacity var(--transition-fast); &:hover { opacity: 0.8; } &.primary { background: var(--color-primary); color: #fff; } }
.key-list { display: flex; flex-direction: column; gap: var(--space-3); }
.key-card { background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: var(--space-4); display: flex; align-items: center; justify-content: space-between; }
.key-info { flex: 1; min-width: 0; }
.key-name { font-weight: var(--font-semibold); color: var(--text-primary); margin-bottom: var(--space-1); }
.key-prefix, .key-url { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.key-expires { font-size: var(--text-xs); color: var(--text-muted); margin-top: var(--space-1); }
.key-events { display: flex; flex-wrap: wrap; gap: var(--space-1); margin-top: var(--space-2); }
.event-tag { font-size: 10px; padding: 1px 5px; border-radius: var(--radius-sm); background: var(--bg-hover); color: var(--text-secondary); }
.key-actions { display: flex; align-items: center; gap: var(--space-3); }
.key-status { font-size: 11px; padding: 2px 8px; border-radius: var(--radius-full); background: var(--bg-hover); color: var(--text-muted); &.active { background: rgba(34,197,94,0.1); color: #22c55e; } }
.text-btn { border: none; background: transparent; cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); &.danger { color: var(--color-danger); } }
.loading-placeholder, .empty-state { text-align: center; color: var(--text-muted); padding: var(--space-8); }
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; }
.dialog-panel { background: var(--bg-primary); border-radius: var(--radius-xl); padding: var(--space-6); width: 480px; max-width: 90vw; box-shadow: 0 20px 40px rgba(0,0,0,0.3); }
.dialog-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5); font-weight: var(--font-semibold); color: var(--text-primary); }
.close-btn { border: none; background: transparent; font-size: var(--text-xl); cursor: pointer; color: var(--text-muted); }
.dialog-body { display: flex; flex-direction: column; gap: var(--space-4); margin-bottom: var(--space-5); }
.form-row { display: flex; flex-direction: column; gap: var(--space-2); label { font-size: var(--text-sm); color: var(--text-secondary); font-weight: var(--font-medium); } input, select { padding: var(--space-2) var(--space-3); border: 1px solid var(--border-color); border-radius: var(--radius-md); background: var(--bg-secondary); color: var(--text-primary); font-size: var(--text-sm); font-family: var(--font-primary); outline: none; &:focus { border-color: var(--color-primary); } } }
.dialog-footer { display: flex; gap: var(--space-3); justify-content: flex-end; }
.btn { padding: var(--space-2) var(--space-5); border-radius: var(--radius-md); border: none; cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); &.primary { background: var(--color-primary); color: #fff; } &.secondary { background: var(--bg-hover); color: var(--text-primary); } }
</style>
