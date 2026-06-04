<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { aiApi, type AiProvider } from '@/api/ai';

const loading = ref(false);
const providers = ref<AiProvider[]>([]);
const total = ref(0);
const activeTab = ref<'providers' | 'chat'>('providers');

// Provider form
const formRef = ref();
const dialogVisible = ref(false);
const form = ref<Partial<AiProvider>>({});
const saving = ref(false);

const providerTypes = [
  { value: 'openai', label: 'OpenAI' },
  { value: 'claude', label: 'Claude' },
  { value: 'gemini', label: 'Gemini' },
  { value: 'local', label: '本地模型' },
];

async function loadProviders() {
  loading.value = true;
  try {
    const res = await aiApi.provider.list();
    if (res.code === 200) {
      providers.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch {
    // ignore
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  form.value = { name: '', providerType: 'openai', model: 'gpt-4', apiBase: 'https://api.openai.com', enabled: true };
  dialogVisible.value = true;
}

async function saveProvider() {
  saving.value = true;
  try {
    if (form.value.providerId) {
      await aiApi.provider.update(form.value);
      ElMessage.success('更新成功');
    } else {
      await aiApi.provider.create(form.value);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    await loadProviders();
  } catch (e: any) {
    ElMessage.error(e.message || '保存失败');
  } finally {
    saving.value = false;
  }
}

async function deleteProvider(p: AiProvider) {
  try {
    await ElMessageBox.confirm('确定删除该 AI Provider？', '提示', { type: 'warning' });
    await aiApi.provider.delete(p.providerId!);
    ElMessage.success('删除成功');
    await loadProviders();
  } catch { /* cancel */ }
}

// Chat
const chatMessages = ref<{ role: 'user' | 'assistant'; content: string }[]>([]);
const chatInput = ref('');
const chatLoading = ref(false);
const selectedProviderId = ref<number | undefined>(undefined);

async function sendMessage() {
  if (!chatInput.value.trim()) return;
  const userMsg = chatInput.value;
  chatInput.value = '';
  chatMessages.value.push({ role: 'user', content: userMsg });
  chatLoading.value = true;
  try {
    const res = await aiApi.chat(userMsg, selectedProviderId.value);
    if (res.code === 200) {
      chatMessages.value.push({ role: 'assistant', content: res.data });
    }
  } catch (e: any) {
    ElMessage.error(e.message || 'AI 响应失败');
  } finally {
    chatLoading.value = false;
  }
}

onMounted(() => { loadProviders(); });
</script>

<template>
  <div class="ai-view">
    <div class="page-header">
      <h1 class="page-title">AI 服务</h1>
    </div>

    <!-- Tab -->
    <div class="tab-bar">
      <button class="tab-btn" :class="{ active: activeTab === 'providers' }" @click="activeTab = 'providers'">Provider 配置</button>
      <button class="tab-btn" :class="{ active: activeTab === 'chat' }" @click="activeTab = 'chat'">AI 对话</button>
    </div>

    <!-- Provider Config -->
    <div v-if="activeTab === 'providers'" class="tab-content">
      <div class="section-actions">
        <button class="action-btn primary" @click="openCreate">+ 新建 Provider</button>
      </div>

      <div v-if="loading" class="loading-placeholder">加载中...</div>
      <div v-else-if="providers.length === 0" class="empty-state">
        暂无 AI Provider，点击上方按钮添加
      </div>
      <div v-else class="provider-list">
        <div v-for="p in providers" :key="p.providerId" class="provider-card">
          <div class="provider-info">
            <div class="provider-name">{{ p.name }}</div>
            <div class="provider-meta">
              <span class="badge">{{ p.providerType }}</span>
              <span class="model">{{ p.model }}</span>
            </div>
            <div class="provider-url">{{ p.apiBase }}</div>
          </div>
          <div class="provider-actions">
            <span class="status-dot" :class="{ active: p.enabled }" />
            <button class="text-btn danger" @click="deleteProvider(p)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Chat -->
    <div v-if="activeTab === 'chat'" class="tab-content chat-view">
      <div class="chat-messages">
        <div v-for="(msg, i) in chatMessages" :key="i" class="chat-msg" :class="msg.role">
          <div class="msg-role">{{ msg.role === 'user' ? '我' : 'AI' }}</div>
          <div class="msg-content">{{ msg.content }}</div>
        </div>
        <div v-if="chatLoading" class="chat-msg assistant">
          <div class="msg-role">AI</div>
          <div class="msg-content typing">思考中...</div>
        </div>
        <div v-if="chatMessages.length === 0" class="chat-empty">
          发送消息开始 AI 对话
        </div>
      </div>
      <div class="chat-input-bar">
        <input
          v-model="chatInput"
          class="chat-input"
          placeholder="输入问题..."
          :disabled="chatLoading"
          @keyup.enter="sendMessage"
        />
        <button class="send-btn" :disabled="chatLoading || !chatInput.trim()" @click="sendMessage">发送</button>
      </div>
    </div>

    <!-- Dialog -->
    <Teleport to="body">
      <div v-if="dialogVisible" class="dialog-overlay" @click.self="dialogVisible = false">
        <div class="dialog-panel">
          <div class="dialog-header">
            <span>{{ form.providerId ? '编辑 Provider' : '新建 Provider' }}</span>
            <button class="close-btn" @click="dialogVisible = false">&times;</button>
          </div>
          <div class="dialog-body">
            <div class="form-row">
              <label>名称</label>
              <input v-model="form.name" placeholder="如：OpenAI GPT-4" />
            </div>
            <div class="form-row">
              <label>类型</label>
              <select v-model="form.providerType">
                <option v-for="t in providerTypes" :key="t.value" :value="t.value">{{ t.label }}</option>
              </select>
            </div>
            <div class="form-row">
              <label>API Base URL</label>
              <input v-model="form.apiBase" placeholder="https://api.openai.com" />
            </div>
            <div class="form-row">
              <label>模型</label>
              <input v-model="form.model" placeholder="gpt-4" />
            </div>
            <div class="form-row">
              <label>API Key</label>
              <input v-model="form.apiKey" type="password" placeholder="sk-..." />
            </div>
          </div>
          <div class="dialog-footer">
            <button class="btn secondary" @click="dialogVisible = false">取消</button>
            <button class="btn primary" :disabled="saving" @click="saveProvider">保存</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped lang="scss">
.ai-view { padding: var(--space-6) var(--space-5); }
.page-header { margin-bottom: var(--space-4); }
.page-title { font-size: var(--text-3xl); font-weight: var(--font-semibold); color: var(--text-primary); margin: 0; }
.tab-bar { display: flex; gap: 0; margin-bottom: var(--space-4); border-bottom: 1px solid var(--border-color); }
.tab-btn { padding: var(--space-2) var(--space-4); border: none; background: transparent; color: var(--text-secondary); cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); border-bottom: 2px solid transparent; margin-bottom: -1px; transition: color var(--transition-fast); }
.tab-btn:hover { color: var(--text-primary); }
.tab-btn.active { color: var(--color-primary); border-bottom-color: var(--color-primary); font-weight: var(--font-semibold); }
.section-actions { display: flex; justify-content: flex-end; margin-bottom: var(--space-4); }
.action-btn { padding: var(--space-2) var(--space-4); border-radius: var(--radius-md); border: none; cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); transition: opacity var(--transition-fast); &:hover { opacity: 0.8; } &.primary { background: var(--color-primary); color: #fff; } }
.provider-list { display: flex; flex-direction: column; gap: var(--space-3); }
.provider-card { background: var(--bg-primary); border: 1px solid var(--border-color); border-radius: var(--radius-lg); padding: var(--space-4); display: flex; align-items: center; justify-content: space-between; }
.provider-info { flex: 1; }
.provider-name { font-weight: var(--font-semibold); color: var(--text-primary); margin-bottom: var(--space-1); }
.provider-meta { display: flex; gap: var(--space-2); margin-bottom: var(--space-1); }
.badge { background: var(--color-primary); color: #fff; font-size: 11px; padding: 1px 6px; border-radius: var(--radius-sm); font-weight: var(--font-medium); }
.model { font-size: var(--text-xs); color: var(--text-muted); }
.provider-url { font-size: var(--text-xs); color: var(--text-muted); font-family: var(--font-mono); }
.provider-actions { display: flex; align-items: center; gap: var(--space-3); }
.status-dot { width: 8px; height: 8px; border-radius: 50%; background: var(--text-muted); &.active { background: var(--color-success); } }
.text-btn { border: none; background: transparent; cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); &.danger { color: var(--color-danger); } }
.loading-placeholder, .empty-state { text-align: center; color: var(--text-muted); padding: var(--space-8); }
.chat-view { display: flex; flex-direction: column; height: calc(100vh - 200px); }
.chat-messages { flex: 1; overflow-y: auto; display: flex; flex-direction: column; gap: var(--space-4); padding: var(--space-4) 0; }
.chat-msg { max-width: 70%; &.user { align-self: flex-end; .msg-content { background: var(--color-primary); color: #fff; border-radius: var(--radius-lg) var(--radius-lg) 4px var(--radius-lg); } } &.assistant { align-self: flex-start; .msg-content { background: var(--bg-hover); color: var(--text-primary); border-radius: var(--radius-lg) var(--radius-lg) var(--radius-lg) 4px; } } }
.msg-role { font-size: 11px; color: var(--text-muted); margin-bottom: var(--space-1); }
.msg-content { padding: var(--space-3) var(--space-4); font-size: var(--text-sm); line-height: var(--leading-relaxed); white-space: pre-wrap; }
.chat-empty { text-align: center; color: var(--text-muted); padding: var(--space-8); }
.chat-input-bar { display: flex; gap: var(--space-2); padding-top: var(--space-4); border-top: 1px solid var(--border-color); }
.chat-input { flex: 1; padding: var(--space-3) var(--space-4); border: 1px solid var(--border-color); border-radius: var(--radius-md); background: var(--bg-primary); color: var(--text-primary); font-size: var(--text-sm); font-family: var(--font-primary); outline: none; &:focus { border-color: var(--color-primary); } }
.send-btn { padding: var(--space-3) var(--space-5); background: var(--color-primary); color: #fff; border: none; border-radius: var(--radius-md); cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); &:disabled { opacity: 0.5; cursor: not-allowed; } }
.dialog-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 2000; }
.dialog-panel { background: var(--bg-primary); border-radius: var(--radius-xl); padding: var(--space-6); width: 480px; max-width: 90vw; box-shadow: 0 20px 40px rgba(0,0,0,0.3); }
.dialog-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: var(--space-5); font-weight: var(--font-semibold); color: var(--text-primary); }
.close-btn { border: none; background: transparent; font-size: var(--text-xl); cursor: pointer; color: var(--text-muted); }
.dialog-body { display: flex; flex-direction: column; gap: var(--space-4); margin-bottom: var(--space-5); }
.form-row { display: flex; flex-direction: column; gap: var(--space-2); label { font-size: var(--text-sm); color: var(--text-secondary); font-weight: var(--font-medium); } input, select { padding: var(--space-2) var(--space-3); border: 1px solid var(--border-color); border-radius: var(--radius-md); background: var(--bg-secondary); color: var(--text-primary); font-size: var(--text-sm); font-family: var(--font-primary); outline: none; &:focus { border-color: var(--color-primary); } } select { cursor: pointer; } }
.dialog-footer { display: flex; gap: var(--space-3); justify-content: flex-end; }
.btn { padding: var(--space-2) var(--space-5); border-radius: var(--radius-md); border: none; cursor: pointer; font-size: var(--text-sm); font-family: var(--font-primary); &.primary { background: var(--color-primary); color: #fff; } &.secondary { background: var(--bg-hover); color: var(--text-primary); } &:disabled { opacity: 0.5; cursor: not-allowed; } }
</style>
