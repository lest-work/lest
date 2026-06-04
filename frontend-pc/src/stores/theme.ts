import { defineStore } from 'pinia';
import { ref } from 'vue';

const THEME_KEY = 'lest-theme-dark';

export const useThemeStore = defineStore('theme', () => {
  const isDark = ref<boolean>(false);

  function applyTheme(dark: boolean) {
    const html = document.documentElement;

    if (dark) {
      html.setAttribute('data-theme', 'dark');
      html.classList.add('dark');
      document.body.style.background = '#0f0f0f';
      document.body.style.color = '#e5e7eb';
    } else {
      html.setAttribute('data-theme', 'light');
      html.classList.remove('dark');
      document.body.style.background = '';
      document.body.style.color = '';
    }

    localStorage.setItem(THEME_KEY, dark ? '1' : '0');
  }

  function toggle() {
    isDark.value = !isDark.value;
    applyTheme(isDark.value);
  }

  function init() {
    const saved = localStorage.getItem(THEME_KEY);
    isDark.value = saved === '1';
    applyTheme(isDark.value);
  }

  return { isDark, toggle, applyTheme, init };
});
