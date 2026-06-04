<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const loading = ref(false);
const errorMsg = ref('');

const form = reactive({
  username: '',
  password: '',
});

async function handleLogin() {
  if (!form.username || !form.password) return;
  loading.value = true;
  errorMsg.value = '';
  try {
    await userStore.login({ username: form.username, password: form.password });
    const redirect = (route.query.redirect as string) || '/dashboard';
    router.push(redirect);
  } catch (err: any) {
    errorMsg.value = err?.message || '登录失败，请重试';
  } finally {
    loading.value = false;
  }
}

function handleKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !loading.value) {
    handleLogin();
  }
}
</script>

<template>
  <div class="login-page">
    <!-- Background -->
    <div class="login-bg">
      <div class="bg-glow" />
      <div class="bg-grid" />
    </div>

    <div class="login-container">
      <!-- Brand -->
      <div class="login-brand">
        <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
          <rect width="40" height="40" rx="10" fill="#6366F1"/>
          <path d="M11 28V12l9 9 9-9v16" stroke="#fff" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <span class="brand-name">LEST</span>
        <span class="brand-tagline">敏捷团队工作台</span>
      </div>

      <!-- Login Card -->
      <div class="login-card">
        <h1 class="login-title">欢迎回来</h1>
        <p class="login-desc">登录到你的敏捷工作台</p>

        <!-- Error message -->
        <div v-if="errorMsg" class="login-error">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" y1="8" x2="12" y2="12"/>
            <line x1="12" y1="16" x2="12.01" y2="16"/>
          </svg>
          {{ errorMsg }}
        </div>

        <form class="login-form" @submit.prevent="handleLogin" @keydown="handleKeydown">
          <!-- Username -->
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="form-input"
              placeholder="请输入用户名"
              autocomplete="username"
              autofocus
            />
          </div>

          <!-- Password -->
          <div class="form-group">
            <label class="form-label">密码</label>
            <input
              v-model="form.password"
              type="password"
              class="form-input"
              placeholder="请输入密码"
              autocomplete="current-password"
            />
          </div>

          <!-- Submit -->
          <button type="submit" class="login-btn" :disabled="loading || !form.username || !form.password">
            <span v-if="!loading">登录</span>
            <span v-else class="loading-dots">
              <span/><span/><span/>
            </span>
          </button>
        </form>

        <div class="login-divider">
          <span>或</span>
        </div>

        <div class="login-sso">
          <button class="sso-btn" disabled title="即将支持">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9"/>
              <path d="M13.73 21a2 2 0 01-3.46 0"/>
            </svg>
            企业微信登录
          </button>
          <button class="sso-btn" disabled title="即将支持">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
              <polyline points="22,6 12,13 2,6"/>
            </svg>
            飞书登录
          </button>
        </div>

        <p class="login-help">没有账号？<a href="javascript:void(0)" class="login-link">联系管理员</a></p>
      </div>

      <p class="login-copyright">AI-Native 敏捷工作台 · Agile Workspace</p>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0D0E11;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;

  .bg-glow {
    position: absolute;
    width: 700px;
    height: 700px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: radial-gradient(circle, rgba(99, 102, 241, 0.12) 0%, transparent 70%);
  }

  .bg-grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(255, 255, 255, 0.015) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.015) 1px, transparent 1px);
    background-size: 60px 60px;
  }
}

.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 32px;
}

.login-brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  .brand-name {
    font-size: 20px;
    font-weight: 700;
    color: #fff;
    letter-spacing: 4px;
    margin-top: 8px;
  }

  .brand-tagline {
    font-size: 13px;
    color: #52525B;
    letter-spacing: 0.5px;
  }
}

.login-card {
  width: 360px;
  background: #18181B;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 14px;
  padding: 36px 32px;
  box-shadow: var(--shadow-modal);
}

.login-title {
  font-size: 22px;
  font-weight: 600;
  color: #FAFAFA;
  margin: 0 0 6px;
  line-height: 1.2;
}

.login-desc {
  font-size: 14px;
  color: #71717A;
  margin: 0 0 28px;
}

.login-error {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  border-radius: 8px;
  color: #FCA5A5;
  font-size: 13px;
  margin-bottom: 16px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-group { }

.form-label {
  display: block;
  font-size: 13px;
  color: #71717A;
  margin-bottom: 6px;
  font-family: var(--font-primary);
}

.form-input {
  width: 100%;
  height: 42px;
  background: #27272A;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 0 14px;
  font-size: 14px;
  color: #FAFAFA;
  font-family: var(--font-primary);
  outline: none;
  transition: border-color 0.15s, background 0.15s;

  &::placeholder { color: #52525B; }

  &:focus {
    border-color: #6366F1;
    background: #1F1F23;
  }
}

.login-btn {
  width: 100%;
  height: 42px;
  background: #6366F1;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
  cursor: pointer;
  font-family: var(--font-primary);
  transition: background 0.15s, opacity 0.15s;
  margin-top: 4px;

  &:hover:not(:disabled) { background: #4F46E5; }
  &:disabled { opacity: 0.5; cursor: not-allowed; }
}

.loading-dots {
  display: flex;
  gap: 5px;
  align-items: center;
  justify-content: center;

  span {
    width: 6px;
    height: 6px;
    background: #fff;
    border-radius: 50%;
    animation: dot-bounce 1.4s infinite ease-in-out both;
    &:nth-child(1) { animation-delay: -0.32s; }
    &:nth-child(2) { animation-delay: -0.16s; }
  }
}

@keyframes dot-bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.login-divider {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 20px 0;
  color: #3F3F46;
  font-size: 13px;

  &::before, &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: rgba(255, 255, 255, 0.06);
  }
}

.login-sso {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.sso-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  height: 40px;
  background: #27272A;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 8px;
  font-size: 14px;
  color: #52525B;
  cursor: not-allowed;
  font-family: var(--font-primary);
  opacity: 0.5;
  transition: opacity 0.15s;

  &:hover { opacity: 0.7; }
}

.login-help {
  text-align: center;
  font-size: 13px;
  color: #71717A;
  margin: 20px 0 0;

  .login-link {
    color: #A5B4FC;
    text-decoration: none;
    &:hover { text-decoration: underline; }
  }
}

.login-copyright {
  font-size: 12px;
  color: #3F3F46;
  margin: 0;
}
</style>
