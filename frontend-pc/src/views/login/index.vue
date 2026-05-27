<template>
  <div class="login-wrapper">
    <div class="login-main">
      <ele-card shadow="always" class="login-card">
        <div class="login-cover">
          <div class="login-logo">
            <svg viewBox="0 0 64 64" class="logo-icon">
              <defs>
                <linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" style="stop-color:#409EFF;stop-opacity:1" />
                  <stop offset="100%" style="stop-color:#1681fd;stop-opacity:1" />
                </linearGradient>
              </defs>
              <path d="M32 4L8 16v16c0 15.5 10.4 30.2 24 34 13.6-3.8 24-18.5 24-34V16L32 4z" fill="url(#logoGrad)"/>
              <path d="M32 14l-8 4.6v9.2c0 6.2 4.2 12.1 8 13.6 3.8-1.5 8-7.4 8-13.6v-9.2L32 14z" fill="white" opacity="0.9"/>
              <path d="M28 26h8v12h-8z" fill="url(#logoGrad)"/>
              <path d="M26 30h12v4H26z" fill="white"/>
            </svg>
          </div>
          <h1 class="login-title">LEST Platform</h1>
          <h4 class="login-subtitle">AI-Native 敏捷管理平台</h4>
          <div class="login-features">
            <div class="feature-item">
              <el-icon><Monitor /></el-icon>
              <span>敏捷管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Cpu /></el-icon>
              <span>AI 智能</span>
            </div>
            <div class="feature-item">
              <el-icon><Connection /></el-icon>
              <span>团队协作</span>
            </div>
          </div>
        </div>
        <div class="login-body">
          <ele-text type="heading" style="font-size: 24px; margin-bottom: 18px">
            {{ t('login.title') }}
          </ele-text>
          <el-form
            ref="formRef"
            size="large"
            :model="form"
            :rules="rules"
            @keyup.enter="handleLogin"
            @submit.prevent=""
          >
            <el-form-item prop="username">
              <el-input
                clearable
                v-model="form.username"
                :placeholder="t('login.username')"
                :prefix-icon="UserOutlined"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                show-password
                v-model="form.password"
                :placeholder="t('login.password')"
                :prefix-icon="LockOutlined"
              />
            </el-form-item>
            <el-form-item prop="captcha">
              <div class="login-captcha-group">
                <el-input
                  clearable
                  v-model="form.captcha"
                  :placeholder="t('login.code')"
                  :prefix-icon="ProtectOutlined"
                  style="flex: 1"
                />
                <div class="login-captcha" @click="changeCaptcha">
                  <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                  <span v-else class="captcha-placeholder">点击刷新</span>
                </div>
              </div>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="form.remember">
                {{ t('login.remember') }}
              </el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button
                size="large"
                type="primary"
                :loading="loading"
                style="width: 100%; height: 44px"
                @click="handleLogin"
              >
                {{ t('login.login') }}
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-footer">
            <el-link type="info" underline="never" style="font-size: 12px">
              {{ t('login.copyright', { year: currentYear }) }}
            </el-link>
          </div>
        </div>
      </ele-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, reactive, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import { EleMessage } from 'ele-admin-plus';
  import type { FormInstance, FormRules } from 'element-plus';
  import {
    UserOutlined,
    LockOutlined,
    ProtectOutlined,
    Monitor,
    Cpu,
    Connection
  } from '@/components/icons';
  import { getToken } from '@/utils/token-util';
  import { usePageTab } from '@/utils/use-page-tab';
  import { login, getCaptcha } from '@/api/login';
  import PageFooter from '@/layout/components/page-footer.vue';
  import { useI18n } from 'vue-i18n';

  const { t } = useI18n();
  const { goHomeRoute, cleanPageTabs } = usePageTab();
  const router = useRouter();

  const formRef = ref<FormInstance | null>(null);
  const loading = ref(false);
  const captchaImage = ref('');
  const captchaUuid = ref('');

  const currentYear = computed(() => new Date().getFullYear());

  const form = reactive({
    username: 'admin',
    password: 'Lest@2026',
    captcha: '',
    remember: true
  });

  const rules = computed<FormRules>(() => {
    return {
      username: [
        { required: true, message: t('login.username'), trigger: 'blur' }
      ],
      password: [
        { required: true, message: t('login.password'), trigger: 'blur' }
      ],
      captcha: [
        { required: true, message: t('login.code'), trigger: 'blur' }
      ]
    };
  });

  const handleLogin = () => {
    formRef.value?.validate?.((valid) => {
      if (!valid) return;
      loading.value = true;
      login({
        ...form,
        uuid: captchaUuid.value
      })
        .then((msg) => {
          EleMessage.success({ message: msg, plain: true });
          cleanPageTabs();
          goHomeRoute();
        })
        .catch((e: Error) => {
          loading.value = false;
          EleMessage.error({ message: e.message, plain: true });
          changeCaptcha();
        })
        .finally(() => {
          loading.value = false;
        });
    });
  };

  const changeCaptcha = () => {
    getCaptcha()
      .then((data) => {
        captchaImage.value = data.captcha;
        captchaUuid.value = data.uuid;
        form.captcha = '';
      })
      .catch((e: Error) => {
        EleMessage.error({ message: e.message || '获取验证码失败', plain: true });
      });
  };

  if (getToken()) {
    goHomeRoute();
  } else {
    changeCaptcha();
  }
</script>

<style lang="scss" scoped>
  .login-wrapper {
    min-height: 100vh;
    min-height: 100dvh;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    background-image: url('@/assets/login-bg.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;

    .login-main {
      flex: auto;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      box-sizing: border-box;
      padding: 20px;
    }

    .login-card {
      width: 920px;
      max-width: 100%;
      overflow: hidden;
      border-radius: 16px;

      :deep(.ele-card-body) {
        display: flex;
        padding: 0;
        height: 500px;
      }
    }
  }

  .login-cover {
    flex: 1;
    padding: 48px 24px;
    box-sizing: border-box;
    background: linear-gradient(135deg, #1681fd 0%, #0d47a1 100%);
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
      animation: pulse 8s ease-in-out infinite;
    }

    @keyframes pulse {
      0%, 100% { transform: scale(1); opacity: 0.5; }
      50% { transform: scale(1.1); opacity: 0.8; }
    }

    .login-logo {
      margin-bottom: 16px;
      position: relative;
      z-index: 1;

      .logo-icon {
        width: 72px;
        height: 72px;
        filter: drop-shadow(0 4px 12px rgba(0,0,0,0.3));
      }
    }
  }

  .login-title {
    color: rgba(255, 255, 255, 0.98);
    font-size: 32px;
    margin: 0 0 8px 0;
    font-weight: 600;
    letter-spacing: 1px;
    position: relative;
    z-index: 1;
    font-family: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  }

  .login-subtitle {
    color: rgba(255, 255, 255, 0.8);
    font-size: 16px;
    margin: 0 0 32px 0;
    font-weight: normal;
    letter-spacing: 4px;
    position: relative;
    z-index: 1;
  }

  .login-features {
    display: flex;
    gap: 24px;
    position: relative;
    z-index: 1;

    .feature-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      color: rgba(255, 255, 255, 0.9);
      font-size: 13px;

      .el-icon {
        font-size: 28px;
        padding: 12px;
        background: rgba(255, 255, 255, 0.15);
        border-radius: 12px;
        backdrop-filter: blur(4px);
        transition: all 0.3s;

        &:hover {
          background: rgba(255, 255, 255, 0.25);
          transform: translateY(-2px);
        }
      }
    }
  }

  .login-body {
    width: 400px;
    flex-shrink: 0;
    padding: 48px 40px;
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    justify-content: center;
    background: #fff;

    :deep(.el-input__prefix-inner > .el-icon) {
      margin-right: 12px;
      transform: scale(1.16);
    }
  }

  .login-captcha-group {
    width: 100%;
    display: flex;
    align-items: center;
    gap: 8px;

    :deep(.el-input) {
      flex: 1;
    }

    .login-captcha {
      flex-shrink: 0;
      width: 108px;
      height: 40px;
      border-radius: 6px;
      border: 1px solid var(--el-border-color);
      transition: border 0.2s;
      box-sizing: border-box;
      background: #fff;
      overflow: hidden;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
      }

      .captcha-placeholder {
        font-size: 12px;
        color: var(--el-text-color-placeholder);
      }

      &:hover {
        border-color: var(--el-color-primary);
      }
    }
  }

  .login-footer {
    margin-top: 24px;
    text-align: center;
  }

  @media screen and (max-width: 680px) {
    .login-wrapper {
      background: #fff;

      .login-main {
        padding: 0;
        display: block;
      }

      .login-card {
        width: 100%;
        background: none;
        box-shadow: none;
        border-radius: 0;

        :deep(.ele-card-body) {
          display: block;
          height: auto;
        }
      }
    }

    .login-cover {
      padding: 32px 16px 100px 16px;
      background-size: auto 80px;
    }

    .login-body {
      width: 100%;
      padding: 24px 20px;
    }
  }
</style>

<style lang="scss">
  html.dark .login-wrapper {
    background: #000;
  }
</style>
