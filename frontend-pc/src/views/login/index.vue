<template>
  <div class="login-wrapper">
    <div class="login-main">
      <ele-card shadow="always" class="login-card">
        <div class="login-cover">
          <img src="@/assets/logo.svg" class="login-logo" alt="Lest" />
          <h1 class="login-title">Lest</h1>
          <h4 class="login-subtitle">工程团队项目管理平台</h4>
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
            <el-form-item prop="code">
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
          changeCaptcha();
        });
    });
  };

  /** 获取图形验证码 */
  const changeCaptcha = () => {
    getCaptcha()
      .then((data) => {
        captcha.value = `data:image/gif;base64,${data.img}`;
        captchaStyle.value = {
          transform: `scaleX(1.${Math.floor(Math.random() * (0 - 5) + 5)}) skewX(-${Math.floor(Math.random() * (0 - 30) + 30)}deg) scale(1.04)`,
          filter: `contrast(200%) hue-rotate(${Math.floor(Math.random() * (0 - 360) + 360)}deg)`
        };
        form.uuid = data.uuid;
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
        height: 462px;
      }
    }
  }

  .login-cover {
    flex: 1;
    padding: 32px 8px;
    box-sizing: border-box;
    background-color: #1681fd;
    background-image: url('@/assets/login-img.png');
    background-repeat: no-repeat;
    background-position: bottom;
    background-size: contain;
    text-align: center;
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
      width: 108px;
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
    width: 64px;
    height: 64px;
    margin-bottom: 16px;
    filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.2));
  }

  /* 标题 */
  .login-title {
    color: rgba(255, 255, 255, 0.98);
    font-size: 34px;
    margin: 0 0 8px 0;
    font-weight: 700;
    letter-spacing: 4px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
      'Helvetica Neue', Arial, 'Noto Sans', sans-serif;
  }

  .login-subtitle {
    color: rgba(255, 255, 255, 0.85);
    font-size: 14px;
    margin: 0;
    font-weight: normal;
    letter-spacing: 3px;
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
      padding: 20px 12px 100px 12px;
      background-size: auto 100px;
    }

    .login-body {
      width: 100%;
    }
  }
</style>

<style lang="scss">
  html.dark .login-wrapper {
    background: #000;
  }
</style>
