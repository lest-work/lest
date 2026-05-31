<template>
  <div class="login-wrapper">
    <div class="login-main">
      <ele-card shadow="always" class="login-card">
        <div class="login-cover">
          <div class="login-cover-brand">
            <img src="@/assets/logo.svg" class="login-logo" alt="Lest" />
            <div>
              <h1 class="login-title">Lest</h1>
              <p class="login-tagline">企业级项目协作平台</p>
            </div>
          </div>
          <div class="login-features">
            <div class="login-feature-item">
              <div class="login-feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 7C3 5.9 3.9 5 5 5H10L12 7H19C20.1 7 21 7.9 21 9V17C21 18.1 20.1 19 19 19H5C3.9 19 3 18.1 3 17V7Z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
                  <path d="M9 13L11 15L15 11" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="login-feature-text">
                <div class="login-feature-title">项目全生命周期管理</div>
                <div class="login-feature-desc">从立项到交付，迭代与里程碑全程可追溯</div>
              </div>
            </div>
            <div class="login-feature-item">
              <div class="login-feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="5" width="4" height="14" rx="1" stroke="currentColor" stroke-width="1.8"/>
                  <rect x="10" y="5" width="4" height="9" rx="1" stroke="currentColor" stroke-width="1.8"/>
                  <rect x="17" y="5" width="4" height="11" rx="1" stroke="currentColor" stroke-width="1.8"/>
                </svg>
              </div>
              <div class="login-feature-text">
                <div class="login-feature-title">敏捷看板与任务追踪</div>
                <div class="login-feature-desc">可视化工作流，实时掌握团队每日进度</div>
              </div>
            </div>
            <div class="login-feature-item">
              <div class="login-feature-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="1.8"/>
                  <path d="M12 7V12L15 15" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M8 3.5C9.2 3 10.6 2.8 12 3" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
                </svg>
              </div>
              <div class="login-feature-text">
                <div class="login-feature-title">工时统计与数据洞察</div>
                <div class="login-feature-desc">燃尽图、工时报告，多角色权限精细管控</div>
              </div>
            </div>
          </div>
          <div class="login-stats">
            <div class="login-stat-item">
              <div class="login-stat-num">多项目</div>
              <div class="login-stat-label">并行管理</div>
            </div>
            <div class="login-stat-divider"></div>
            <div class="login-stat-item">
              <div class="login-stat-num">实时</div>
              <div class="login-stat-label">协作同步</div>
            </div>
            <div class="login-stat-divider"></div>
            <div class="login-stat-item">
              <div class="login-stat-num">看板</div>
              <div class="login-stat-label">敏捷开发</div>
            </div>
          </div>
        </div>
        <div class="login-body">
          <ele-text type="heading" style="font-size: 24px; margin-bottom: 18px">
            用户登录
          </ele-text>
          <ele-segmented
            v-model="tabActive"
            :items="[
              { label: '密码登录', value: 1 },
              { label: '扫码登录', value: 2 }
            ]"
            style="margin-bottom: 18px"
            @change="handleTabChange"
          />
          <el-form
            v-if="tabActive == 1"
            ref="formRef"
            size="large"
            :model="form"
            :rules="rules"
            @keyup.enter="submit"
            @submit.prevent=""
          >
            <el-form-item prop="username">
              <el-input
                clearable
                v-model="form.username"
                placeholder="请输入登录账号"
                :prefix-icon="UserOutlined"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                show-password
                v-model="form.password"
                placeholder="请输入登录密码"
                :prefix-icon="LockOutlined"
              />
            </el-form-item>
            <el-form-item v-if="captchaEnabled" prop="code">
              <div class="login-captcha-group">
                <el-input
                  clearable
                  v-model="form.code"
                  placeholder="请输入验证码"
                  :prefix-icon="ProtectOutlined"
                />
                <div class="login-captcha" @click="changeCaptcha">
                  <img v-if="captcha" :src="captcha" :style="captchaStyle" />
                </div>
              </div>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="form.remember">记住密码</el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button
                size="large"
                type="primary"
                :loading="loading"
                style="width: 100%"
                @click="submit"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
          <div v-else class="login-qrcode-group">
            <el-empty description="扫码登录功能开发中" :image-size="80" style="padding: 32px 0" />
          </div>
        </div>
      </ele-card>
    </div>
    <PageFooter style="padding-top: 0" />
  </div>
</template>

<script setup>
  import { ref, reactive, unref } from 'vue';
  import { useRouter } from 'vue-router';
  import { EleMessage } from 'ele-admin-plus';
  import { UserOutlined, LockOutlined, ProtectOutlined } from '@/components/icons';
  import { getToken } from '@/utils/token-util';
  import { usePageTab } from '@/utils/use-page-tab';
  import { login, getCaptcha } from '@/api/login';
  import PageFooter from '@/layout/components/page-footer.vue';

  const { currentRoute } = useRouter();
  const { goHomeRoute, cleanPageTabs } = usePageTab();

  /** 页签选中 */
  const tabActive = ref(1);

  /** 表单 */
  const formRef = ref(null);

  /** 加载状态 */
  const loading = ref(false);

  /** 表单数据 */
  const form = reactive({
    username: 'admin',
    password: 'admin123',
    code: '',
    uuid: '',
    remember: true
  });

  /** 表单验证规则 */
  const rules = reactive({
    username: [
      {
        required: true,
        message: '请输入登录账号',
        type: 'string',
        trigger: 'blur'
      }
    ],
    password: [
      {
        required: true,
        message: '请输入登录密码',
        type: 'string',
        trigger: 'blur'
      }
    ],
    code: [
      {
        required: true,
        message: '请输入验证码',
        type: 'string',
        trigger: 'blur'
      }
    ]
  });

  /** 图形验证码 */
  const captcha = ref('');

  /** 图形验证码样式 */
  const captchaStyle = ref({});

  /** 验证码是否启用 */
  const captchaEnabled = ref(false);


  /** 提交 */
  const submit = () => {
    formRef.value?.validate?.((valid) => {
      if (!valid) {
        return;
      }
      loading.value = true;
      login(form)
        .then((msg) => {
          EleMessage.success({ message: msg, plain: true });
          cleanPageTabs();
          goHome();
        })
        .catch((e) => {
          loading.value = false;
          EleMessage.error({ message: e.message, plain: true });
          if (captchaEnabled.value) {
            changeCaptcha();
          }
        });
    });
  };

  /** 获取图形验证码 */
  const changeCaptcha = () => {
    getCaptcha()
      .then((data) => {
        captchaEnabled.value = data.captchaEnabled ?? false;
        if (captchaEnabled.value) {
          captcha.value = `data:image/png;base64,${data.img}`;
          captchaStyle.value = {};
          form.uuid = data.uuid;
        } else {
          captcha.value = '';
          form.uuid = '';
          form.code = '';
        }
        formRef.value?.clearValidate?.();
      })
      .catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      });
  };

  /** 选项卡切换事件 */
  const handleTabChange = (_active) => {};

  /** 跳转到首页 */
  const goHome = () => {
    const { query } = unref(currentRoute);
    goHomeRoute(query.from);
  };

  // 如果已登录直接进入首页
  if (getToken()) {
    goHome();
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

      :deep(.ele-card-body) {
        display: flex;
        padding: 0;
        height: 520px;
      }
    }
  }

  .login-cover {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 36px 40px 28px 40px;
    box-sizing: border-box;
    background: linear-gradient(150deg, #0f1e45 0%, #1a3a7a 50%, #1d5fc8 100%);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      width: 280px;
      height: 280px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.04);
      top: -80px;
      right: -80px;
      pointer-events: none;
    }

    &::after {
      content: '';
      position: absolute;
      width: 200px;
      height: 200px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.04);
      bottom: 60px;
      left: -60px;
      pointer-events: none;
    }
  }

  .login-cover-brand {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 36px;
  }

  .login-features {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .login-feature-item {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    padding: 16px 18px;
    background: rgba(255, 255, 255, 0.07);
    border-radius: 10px;
    border: 1px solid rgba(255, 255, 255, 0.10);
    transition: background 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.11);
    }
  }

  .login-feature-icon {
    width: 40px;
    height: 40px;
    flex-shrink: 0;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.12);
    display: flex;
    align-items: center;
    justify-content: center;
    color: rgba(255, 255, 255, 0.90);

    svg {
      width: 22px;
      height: 22px;
    }
  }

  .login-feature-text {
    padding-top: 2px;
  }

  .login-feature-title {
    font-size: 14px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.95);
    margin-bottom: 4px;
    letter-spacing: 0.3px;
  }

  .login-feature-desc {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.60);
    line-height: 1.6;
  }

  .login-stats {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0;
    margin-top: 24px;
    padding: 16px 0 0 0;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
  }

  .login-stat-item {
    flex: 1;
    text-align: center;
  }

  .login-stat-num {
    font-size: 15px;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.95);
    letter-spacing: 0.5px;
  }

  .login-stat-label {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.50);
    margin-top: 2px;
  }

  .login-stat-divider {
    width: 1px;
    height: 32px;
    background: rgba(255, 255, 255, 0.15);
  }

  .login-body {
    width: 400px;
    flex-shrink: 0;
    padding: 32px 48px 0 48px;
    box-sizing: border-box;

    :deep(.el-checkbox) {
      height: auto;

      .el-checkbox__label {
        color: inherit;
      }
    }

    :deep(.el-input__prefix-inner > .el-icon) {
      margin-right: 12px;
      transform: scale(1.16);
    }
  }

  /* 验证码 */
  .login-captcha-group {
    width: 100%;
    display: flex;
    align-items: center;

    :deep(.el-input) {
      flex: 1;
    }

    .login-captcha {
      flex-shrink: 0;
      width: 120px;
      height: 40px;
      margin-left: 8px;
      border-radius: var(--el-border-radius-base);
      border: 1px solid var(--el-border-color);
      transition: border 0.2s;
      box-sizing: border-box;
      background: #fff;
      overflow: hidden;
      cursor: pointer;

      img {
        width: 100%;
        height: 100%;
        object-fit: contain;
        display: block;
      }

      &:hover {
        border-color: var(--el-color-primary);
      }
    }
  }

  /* Logo */
  .login-logo {
    width: 48px;
    height: 48px;
    flex-shrink: 0;
    filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.3));
  }

  /* 标题 */
  .login-title {
    color: rgba(255, 255, 255, 0.98);
    font-size: 26px;
    margin: 0 0 4px 0;
    font-weight: 700;
    letter-spacing: 3px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
      'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
  }

  .login-tagline {
    color: rgba(255, 255, 255, 0.60);
    font-size: 12px;
    margin: 0;
    font-weight: normal;
    letter-spacing: 1px;
  }

  /* 二维码 */
  .login-qrcode-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px 0;
  }

  .login-qrcode {
    font-size: 0;
    display: inline-block;
    border: 1px solid #ddd;
    border-radius: var(--el-border-radius-base);
    overflow: hidden;
  }

  /* 小屏幕适应 */
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
      display: none;
    }

    .login-body {
      width: 100%;
      padding: 32px 24px;
    }
  }
</style>

<style lang="scss">
  html.dark .login-wrapper {
    background: #000;
  }
</style>
