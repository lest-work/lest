<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { authApi } from '@/api/auth';

const userStore = useUserStore();
const nickname = ref(userStore.userInfo?.nickname || '');
const email = ref(userStore.userInfo?.email || '');
const username = computed(() => userStore.userInfo?.username || '');
const saving = ref(false);

onMounted(async () => {
  if (!userStore.userInfo?.id) {
    await userStore.fetchUserInfo();
    nickname.value = userStore.userInfo?.nickname || '';
    email.value = userStore.userInfo?.email || '';
  }
});

async function handleSave() {
  if (!userStore.userInfo) return;
  saving.value = true;
  try {
    await authApi.updateProfile({ nickname: nickname.value, email: email.value });
    userStore.userInfo = {
      ...userStore.userInfo,
      nickname: nickname.value,
      email: email.value,
    };
    ElMessage.success('个人设置已保存');
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败，请重试');
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <div class="profile-view">
    <div class="profile-header">
      <h2 class="page-title">个人设置</h2>
    </div>

    <div class="profile-card">
      <div class="field-group">
        <label class="field-label">用户名</label>
        <input class="field-input" :value="username" disabled />
      </div>
      <div class="field-group">
        <label class="field-label">昵称</label>
        <input v-model="nickname" class="field-input" placeholder="请输入昵称" />
      </div>
      <div class="field-group">
        <label class="field-label">邮箱</label>
        <input v-model="email" type="email" class="field-input" placeholder="请输入邮箱" />
      </div>
      <div class="form-actions">
        <button class="save-btn" :disabled="saving" @click="handleSave">
          {{ saving ? '保存中...' : '保存更改' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.profile-view {
  max-width: 560px;
  margin: 0 auto;
  padding: var(--space-6) var(--space-5);
  font-family: var(--font-primary);
}

.profile-header { margin-bottom: var(--space-5); }

.page-title {
  font-size: var(--text-3xl);
  font-weight: var(--font-semibold);
  color: var(--text-primary);
  margin: 0;
}

.profile-card {
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  display: flex;
  flex-direction: column;
  gap: var(--space-5);
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

.field-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--text-secondary);
}

.field-input {
  height: 38px;
  background: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  padding: 0 var(--space-3);
  font-size: var(--text-sm);
  color: var(--text-primary);
  font-family: var(--font-primary);
  outline: none;
  transition: border-color var(--transition-fast);

  &::placeholder { color: var(--text-muted); }
  &:focus { border-color: var(--color-primary); }
  &:disabled {
    background: var(--bg-secondary);
    color: var(--text-muted);
    cursor: not-allowed;
  }
}

.form-actions { margin-top: var(--space-2); }

.save-btn {
  height: 38px;
  padding: 0 var(--space-5);
  border: none;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: #fff;
  font-size: var(--text-sm);
  font-family: var(--font-primary);
  font-weight: var(--font-medium);
  cursor: pointer;
  transition: background var(--transition-fast);

  &:hover:not(:disabled) { background: var(--color-primary-hover); }
  &:disabled { opacity: 0.6; cursor: not-allowed; }
}
</style>
