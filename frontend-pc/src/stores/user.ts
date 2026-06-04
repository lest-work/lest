import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { authApi, type UserInfo } from '@/api/auth';

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('access_token') || '');
  const userInfo = ref<UserInfo | null>(null);
  const loading = ref(false);

  const isLoggedIn = computed(() => !!token.value);

  async function login(form: { username: string; password: string; captchaId?: string; captchaCode?: string }) {
    loading.value = true;
    try {
      const result = await authApi.login(form);
      token.value = result.access_token;
      userInfo.value = result.user;
      localStorage.setItem('access_token', result.access_token);
      localStorage.setItem('refresh_token', result.refresh_token);
      return result;
    } finally {
      loading.value = false;
    }
  }

  async function logout() {
    try {
      await authApi.logout();
    } finally {
      token.value = '';
      userInfo.value = null;
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
    }
  }

  async function fetchUserInfo() {
    if (!token.value) return;
    try {
      const data = await authApi.getUserInfo();
      userInfo.value = data;
    } catch {
      logout();
    }
  }

  return { token, userInfo, loading, isLoggedIn, login, logout, fetchUserInfo };
});
