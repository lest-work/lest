/**
 * 主题状态管理
 */
import type { CSSProperties } from 'vue';
import { defineStore } from 'pinia';
import { cloneDeep } from 'lodash-es';
import type {
  TabItem,
  Layout,
  SidebarLayout,
  HeaderStyle,
  SidebarStyle,
  TabStyle,
  TabItemEventOption,
  MenuItemTrigger
} from 'ele-admin-plus/es/ele-pro-layout/types';
import { changeColor, changeSkin } from 'ele-admin-plus/es/utils/theme-util';
import type { SkinConfig } from 'ele-admin-plus/es/utils/theme-util';
import { THEME_CACHE_NAME } from '@/config/setting';
import wallpaper01 from '@/assets/wallpaper-01.jpg';
import wallpaper01Cover from '@/assets/wallpaper-01-cover.jpg';
import wallpaper01Dark from '@/assets/wallpaper-01-dark.jpg';
import wallpaper01DarkCover from '@/assets/wallpaper-01-dark-cover.jpg';
import wallpaper02 from '@/assets/wallpaper-02.jpg';
import wallpaper02Cover from '@/assets/wallpaper-02-cover.jpg';
import wallpaper02Dark from '@/assets/wallpaper-02-dark.jpg';
import wallpaper02DarkCover from '@/assets/wallpaper-02-dark-cover.jpg';
import wallpaper03 from '@/assets/wallpaper-03.jpg';
import wallpaper03Cover from '@/assets/wallpaper-03-cover.jpg';
import wallpaper04 from '@/assets/wallpaper-04.jpg';
import wallpaper04Cover from '@/assets/wallpaper-04-cover.jpg';

/**
 * 关闭页签返回结果
 */
export interface TabRemoveResult {
  /** 关闭后要跳转的地址 */
  path?: string;
  /** 关闭后是否跳转到首页 */
  home?: boolean;
}

export type TabRemoveReturn = Promise<TabRemoveResult>;

/**
 * 主题状态数据
 */
export interface ThemeState {
  tabs: TabItem[];
  collapse: boolean;
  compact: boolean;
  maximized: boolean;
  tabBar: boolean;
  layout: Layout;
  sidebarLayout: SidebarLayout;
  headerStyle: HeaderStyle;
  sidebarStyle: SidebarStyle;
  mixSidebarStyle: SidebarStyle;
  tabStyle: TabStyle;
  fixedHeader: boolean;
  fixedSidebar: boolean;
  fixedBody: boolean;
  fluid: boolean;
  logoInHeader: boolean;
  colorfulIcon: boolean;
  uniqueOpened: boolean;
  fixedHome: boolean;
  tabInHeader: boolean;
  sidebarCustomStyle: CSSProperties | null;
  sideboxCustomStyle: CSSProperties | null;
  sideCustomStyle: CSSProperties | null;
  headerCustomStyle: CSSProperties | null;
  tabsCustomStyle: CSSProperties | null;
  contentCustomStyle: CSSProperties | null;
  transitionName: string;
  weakMode: boolean;
  darkMode: boolean;
  color: string | null;
  contentWidth: number | null;
  roundedTheme: boolean;
  menuItemTrigger: MenuItemTrigger;
  footer: boolean;
  tabIcon: boolean;
  tabsCache: boolean;
  pageKeepAlive: boolean;
  skinConfig: SkinConfig | null;
  responsive: boolean;
}

export type ThemeStateProp = keyof ThemeState;

/**
 * 默认值
 */
const DEFAULT_STATE: ThemeState = {
  /** 页签数据 */
  tabs: [],
  /** 是否折叠侧栏 */
  collapse: false,
  /** 侧栏一级是否紧凑风格 */
  compact: false,
  /** 内容区是否最大化 */
  maximized: false,
  /** 是否需要页签栏 */
  tabBar: true,
  /** 布局类型 */
  layout: 'default',
  /** 侧栏布局类型 */
  sidebarLayout: 'default',
  /** 顶栏风格 */
  headerStyle: 'light',
  /** 侧栏风格 */
  sidebarStyle: 'dark',
  /** 双侧栏二级风格 */
  mixSidebarStyle: 'light',
  /** 页签风格 */
  tabStyle: 'simple',
  /** 是否固定顶栏 */
  fixedHeader: true,
  /** 是否固定侧栏 */
  fixedSidebar: true,
  /** 是否固定内容区 */
  fixedBody: true,
  /** 内容区是否撑满 */
  fluid: true,
  /** 图标是否置于顶栏 */
  logoInHeader: false,
  /** 侧栏菜单是否彩色图标 */
  colorfulIcon: false,
  /** 侧栏排他展开 */
  uniqueOpened: true,
  /** 固定主页页签 */
  fixedHome: true,
  /** 页签是否置于顶栏 */
  tabInHeader: false,
  /** 自定义侧栏样式 */
  sidebarCustomStyle: null,
  /** 自定义双侧栏一级样式 */
  sideboxCustomStyle: null,
  /** 自定义侧栏容器样式 */
  sideCustomStyle: null,
  /** 自定义顶栏样式 */
  headerCustomStyle: null,
  /** 自定义页签栏样式 */
  tabsCustomStyle: null,
  /** 自定义内容区样式 */
  contentCustomStyle: null,
  /** 路由切换动画 */
  transitionName: 'slide-right',
  /** 是否色弱模式 */
  weakMode: false,
  /** 是否暗黑模式 */
  darkMode: false,
  /** 主题色 */
  color: null,
  /** 内容区宽度 */
  contentWidth: null,
  /** 是否开启圆角主题 */
  roundedTheme: true,
  /** 菜单触发模式 */
  menuItemTrigger: 'click',
  /** 是否开启全局页脚 */
  footer: true,
  /** 页签是否显示图标 */
  tabIcon: true,
  /** 刷新是否保留已打开页签 */
  tabsCache: false,
  /** 切换路由是否缓存 */
  pageKeepAlive: true,
  /** 皮肤背景配置 */
  skinConfig: null,
  /** 是否开启响应式 */
  responsive: true
};

/**
 * 需要排除缓存的属性
 */
const CACHE_EXCLUDES: ThemeStateProp[] = [
  'collapse',
  'compact',
  'maximized',
  'contentWidth'
];

/**
 * 预设皮肤背景
 */
export const PREDEFINE_SKINS: SkinConfig[] = [
  {
    name: 'gradient',
    cover: `url(${wallpaper01Cover}) center / 100% 100%`,
    wallpaper: `url(${wallpaper01}) center / 100% 100%`,
    maskColor: 'rgba(222, 242, 249, 0.4)',
    headerBg: 'rgba(255, 255, 255, 0.28)',
    sidebarBg: 'rgba(255, 255, 255, 0.28)',
    cardBg: 'rgba(255, 255, 255, 0.88)',
    overlayBg: `url(${wallpaper01}) center / 100% 100%`,
    overlayMaskColor:
      'linear-gradient(90deg, rgba(255, 255, 255, 0.8), rgba(0, 0, 0, 0), rgba(255, 255, 255, 0.8)) center / 102% 102%',
    darkConfig: {
      cover: `url(${wallpaper01DarkCover}) center / 100% 100%`,
      wallpaper: `url(${wallpaper01Dark}) center / 100% 100%`,
      maskColor: 'rgba(0, 0, 0, 0.8)',
      headerBg: 'rgba(0, 0, 0, 0.2)',
      sidebarBg: 'rgba(0, 0, 0, 0.2)',
      cardBg: 'rgba(0, 0, 0, 0.2)',
      overlayBg: `url(${wallpaper01Dark}) center / 100% 100%`,
      overlayMaskColor: 'rgba(0, 0, 0, 0.82)'
    }
  },
  {
    name: 'technology',
    color: '#1677ff',
    cover: `url(${wallpaper02Cover}) center / 100% 100%`,
    wallpaper: `url(${wallpaper02}) center / 100% 100%`,
    maskColor: 'rgba(214, 227, 250, 0.4)',
    headerBg: 'rgba(255, 255, 255, 0.28)',
    sidebarBg: 'rgba(255, 255, 255, 0.28)',
    cardBg: 'rgba(255, 255, 255, 0.6)',
    overlayBg: `url(${wallpaper02}) center / 100% 100%`,
    overlayMaskColor:
      'linear-gradient(0deg, rgba(255, 255, 255, 0.8), rgba(214, 227, 250, 0.4), rgba(255, 255, 255, 0.8)) center / 102% 102%',
    darkConfig: {
      color: '#13c2c2',
      cover: `url(${wallpaper02DarkCover}) center / 100% 100%`,
      wallpaper: `url(${wallpaper02Dark}) center / 100% 100%`,
      maskColor: 'rgba(0, 0, 0, 0)',
      headerBg: 'rgba(22, 44, 78, 0.4)',
      sidebarBg: 'rgba(22, 44, 78, 0.4)',
      cardBg: 'rgba(22, 44, 78, 0.4)',
      overlayBg: `url(${wallpaper02Dark}) center / 100% 100%`,
      overlayMaskColor:
        'linear-gradient(0deg, rgba(18, 44, 82, 0.8), rgba(22, 44, 78, 0.4), rgba(18, 44, 82, 0.8)) center / 102% 102%'
    }
  },
  {
    name: 'aesthetic',
    color: '#2f54eb',
    cover: `url(${wallpaper03Cover}) bottom center / cover`,
    wallpaper: `url(${wallpaper03}) bottom center / cover`,
    maskColor:
      'linear-gradient(22deg, rgba(240, 242, 245, 0.08) 0%, rgba(240, 242, 245, 0.88) 100%) center / 100% 100%',
    headerBg: 'rgba(255, 255, 255, 0.2)',
    sidebarBg: 'rgba(255, 255, 255, 0.2)',
    cardBg: 'rgba(255, 255, 255, 0.68)',
    overlayBg: `url(${wallpaper03}) bottom center / cover`,
    overlayMaskColor: 'rgba(255, 255, 255, 0.8)',
    darkConfig: {
      color: '#2f54eb',
      cover: `url(${wallpaper03Cover}) bottom center / cover`,
      wallpaper: `url(${wallpaper03}) bottom center / cover`,
      maskColor: 'rgba(0, 0, 0, 0.68)',
      headerBg: 'rgba(0, 0, 0, 0.08)',
      sidebarBg: 'rgba(0, 0, 0, 0.08)',
      cardBg: 'rgba(0, 0, 0, 0.28)',
      overlayBg: `url(${wallpaper03}) bottom center / cover`,
      overlayMaskColor: 'rgba(0, 0, 0, 0.8)'
    }
  },
  {
    name: 'cartoon',
    color: '#32a2d4',
    cover: `url(${wallpaper04Cover}) top center / cover`,
    wallpaper: `url(${wallpaper04}) top center / cover`,
    maskColor: 'rgba(255, 255, 255, 0.2)',
    headerBg: 'rgba(255, 255, 255, 0.2)',
    sidebarBg: 'rgba(255, 255, 255, 0.2)',
    cardBg: 'rgba(255, 255, 255, 0.8)',
    overlayBg: 'linear-gradient(238deg, #fff1eb 0%, #ace0f9 120%)',
    darkConfig: {
      color: '#32a2d4',
      cover: `url(${wallpaper04Cover}) top center / cover`,
      wallpaper: `url(${wallpaper04}) top center / cover`,
      maskColor: 'rgba(0, 0, 0, 0.68)',
      headerBg: 'rgba(0, 0, 0, 0.08)',
      sidebarBg: 'rgba(0, 0, 0, 0.08)',
      cardBg: 'rgba(0, 0, 0, 0.28)',
      overlayBg: 'linear-gradient(238deg, #372406 0%, #04293a 120%)'
    }
  }
];

/**
 * 读取缓存配置
 */
function getCacheSetting(): Record<string, any> {
  try {
    const value = localStorage.getItem(THEME_CACHE_NAME);
    if (value) {
      const cache = JSON.parse(value);
      if (typeof cache === 'object') {
        return cache;
      }
    }
  } catch (e) {
    console.error(e);
  }
  return {};
}

/**
 * 缓存配置
 * @param key 属性名
 * @param value 值
 */
function cacheSetting(key: ThemeStateProp, value: unknown) {
  if (CACHE_EXCLUDES.includes(key)) {
    return;
  }
  const cache = getCacheSetting();
  if (cache[key] !== value) {
    cache[key] = value;
    localStorage.setItem(THEME_CACHE_NAME, JSON.stringify(cache));
  }
}

/**
 * 皮肤背景文件访问链接缓存
 */
const SKIN_BG_CACHE = new Map<string, string>();

/**
 * 缓存皮肤背景文件访问链接
 * @param id 文件 id
 * @param file 文件
 */
function cacheSkinBg(id: string, file?: File | Blob) {
  const bg = SKIN_BG_CACHE.get(id);
  if (bg) {
    return bg;
  }
  const url = file ? URL.createObjectURL(file) : void 0;
  if (url) {
    const bg = `url(${url}) top / cover`;
    SKIN_BG_CACHE.set(id, bg);
    return bg;
  }
}

/**
 * 皮肤背景文件缓存表名
 */
const WALLPAPER_STORE_NAME = 'wallpaperFileCacheStore';

/**
 * 初始化皮肤背景文件缓存数据库
 */
function initWallpaperDB() {
  const WALLPAPER_DB_NAME = 'wallpaperFileCacheDB';
  return new Promise<IDBDatabase>((resolve, reject) => {
    const request = indexedDB.open(WALLPAPER_DB_NAME, 1);
    request.onupgradeneeded = (e: any) => {
      const db: IDBDatabase = e.target?.result;
      if (!db.objectStoreNames.contains(WALLPAPER_STORE_NAME)) {
        db.createObjectStore(WALLPAPER_STORE_NAME, { keyPath: 'id' });
      }
    };
    request.onsuccess = (e: any) => resolve(e.target?.result);
    request.onerror = (e: any) => reject(e.target?.error);
  });
}

/**
 * 缓存皮肤背景文件
 * @param file 文件
 */
async function storeWallpaperFile(file: File) {
  const id = JSON.stringify({
    name: file.name,
    type: file.type,
    lastModified: file.lastModified
  });
  const content = await new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = (e) => resolve(new Uint8Array(e.target?.result as any));
    reader.onerror = (e) => reject(e.target?.error);
    reader.readAsArrayBuffer(file);
  });
  const db = await initWallpaperDB();
  const transaction = db.transaction(WALLPAPER_STORE_NAME, 'readwrite');
  const store = transaction.objectStore(WALLPAPER_STORE_NAME);
  const request = store.put({ id, type: file.type, content });
  await new Promise<void>((resolve, reject) => {
    request.onsuccess = () => resolve();
    request.onerror = (e: any) => reject(e.target?.error);
  });
  return id;
}

/**
 * 获取缓存的皮肤背景文件
 * @param id 文件 id
 */
async function getWallpaperFile(id: string) {
  const db = await initWallpaperDB();
  const transaction = db.transaction(WALLPAPER_STORE_NAME, 'readonly');
  const store = transaction.objectStore(WALLPAPER_STORE_NAME);
  const request = store.get(id);
  const content = await new Promise<Blob | undefined>((resolve, reject) => {
    request.onsuccess = (e: any) => {
      const data = e.target?.result;
      if (!data) {
        resolve(void 0);
      } else {
        const blob = new Blob([data.content], { type: data.type });
        resolve(blob);
      }
    };
    request.onerror = (e: any) => reject(e.target?.error);
  });
  return content;
}

/**
 * 清空缓存的皮肤背景文件
 */
async function clearAllWallpaperFile() {
  const db = await initWallpaperDB();
  const transaction = db.transaction(WALLPAPER_STORE_NAME, 'readwrite');
  const store = transaction.objectStore(WALLPAPER_STORE_NAME);
  const request = store.clear();
  await new Promise<void>((resolve, reject) => {
    request.onsuccess = () => resolve();
    request.onerror = (e: any) => reject(e.target?.error);
  });
}

/**
 * 删除未使用的缓存的皮肤背景文件
 * @param ids 还在使用的文件 id
 */
async function clearWallpaperFile(ids: string[]) {
  const db = await initWallpaperDB();
  const transaction = db.transaction(WALLPAPER_STORE_NAME, 'readwrite');
  const store = transaction.objectStore(WALLPAPER_STORE_NAME);
  const request = store.getAll();
  const data: any = await new Promise((resolve, reject) => {
    request.onsuccess = (e: any) => resolve(e.target?.result);
    request.onerror = (e: any) => reject(e.target?.error);
  });
  for (const item of data) {
    if (!ids.includes(item.id)) {
      const deleteRequest = store.delete(item.id);
      await new Promise<void>((resolve, reject) => {
        deleteRequest.onsuccess = () => resolve();
        deleteRequest.onerror = (e: any) => reject(e.target?.error);
      });
    }
  }
}

/**
 * 判断图片背景是否为文件 id 形式
 * @param wallpaper 图片背景
 */
function isWallpaperFileId(wallpaper?: string) {
  return wallpaper != null && wallpaper.startsWith('{');
}

/**
 * 获取皮肤背景配置
 * @param config 配置
 * @param isId 背景图片是否返回文件 id 的形式
 * @param isCache 是否是缓存的数据需要同步最新的配置
 */
async function getSkinConfig(
  config?: SkinConfig | null,
  isId?: boolean,
  isCache?: boolean
) {
  if (!config) {
    return config ?? null;
  }
  const skin: SkinConfig = { ...config };
  if (isCache) {
    // 缓存的旧图片同步最新的预设皮肤
    if (skin.name) {
      const ps = PREDEFINE_SKINS.find((s) => s.name === skin.name);
      if (ps) {
        Object.assign(skin, ps);
      }
    } else if (skin.skinName) {
      const ps = PREDEFINE_SKINS.find((s) => s.name === skin.skinName);
      if (ps) {
        if (skin.wallpaper && !isWallpaperFileId(skin.wallpaper)) {
          skin.wallpaper = ps.wallpaper;
        }
        if (skin.overlayBg && !isWallpaperFileId(skin.overlayBg)) {
          skin.overlayBg = ps.overlayBg;
        }
        if (
          skin.darkConfig?.wallpaper &&
          !isWallpaperFileId(skin.darkConfig.wallpaper)
        ) {
          skin.darkConfig.wallpaper = ps.darkConfig?.wallpaper;
        }
        if (
          skin.darkConfig?.overlayBg &&
          !isWallpaperFileId(skin.darkConfig.overlayBg)
        ) {
          skin.darkConfig.overlayBg = ps.darkConfig?.overlayBg;
        }
      }
    }
  }
  const keys = Object.keys(skin);
  for (const key of keys) {
    const value = skin[key];
    if (key === 'darkConfig') {
      skin[key] = (await getSkinConfig(value, isId)) as any;
    } else if (value != null && typeof value === 'string') {
      if (isId) {
        for (const [id, url] of SKIN_BG_CACHE.entries()) {
          if (url === value) {
            skin[key] = id;
            break;
          }
        }
      } else if (isWallpaperFileId(value)) {
        const url = SKIN_BG_CACHE.get(value);
        if (url) {
          skin[key] = url;
        } else {
          const blob = await getWallpaperFile(value);
          skin[key] = cacheSkinBg(value, blob);
        }
      }
    }
  }
  return skin;
}

/**
 * 获取缓存的皮肤背景
 * @param cfg 默认的皮肤背景
 */
async function getCacheSkinConfig(cfg?: SkinConfig | null) {
  const cache = getCacheSetting();
  const skin = typeof cache.skinConfig !== 'undefined' ? cache.skinConfig : cfg;
  const skinConfig = await getSkinConfig(skin, false, true);
  return skinConfig;
}

/**
 * 切换主题
 * @param color 主题色
 * @param dark 是否是暗黑模式
 * @param skinConfig 皮肤背景配置
 */
function changeTheme(
  color?: string | null,
  dark?: boolean,
  skinConfig?: SkinConfig | null
) {
  const isTransparent = !!skinConfig;
  changeColor(
    isTransparent ? (color ?? '#2f54eb') : color,
    dark,
    isTransparent
  );
  changeSkin(skinConfig, dark);
}

/**
 * 切换圆角主题
 * @param roundedTheme 是否开启圆角主题
 */
function changeRoundedTheme(roundedTheme: boolean) {
  const classes = 'rounded';
  const $html = document.documentElement;
  if ($html && roundedTheme) {
    $html.classList.add(classes);
  } else if ($html) {
    $html.classList.remove(classes);
  }
}

/**
 * 切换色弱模式
 * @param weakMode 是否开启色弱模式
 */
function changeWeakMode(weakMode: boolean) {
  const classes = 'ele-admin-weak';
  if (weakMode) {
    document.body.classList.add(classes);
  } else {
    document.body.classList.remove(classes);
  }
}

/**
 * 开关响应式布局
 * @param responsive 是否开启移动端响应式
 */
function changeResponsive(responsive: boolean) {
  const classes = 'ele-body-limited';
  if (responsive) {
    document.body.classList.remove(classes);
  } else {
    document.body.classList.add(classes);
  }
}

export const useThemeStore = defineStore('theme', {
  state: (): ThemeState => {
    const state: ThemeState = cloneDeep(DEFAULT_STATE);
    // 读取本地缓存
    const cache = getCacheSetting();
    Object.keys(state).forEach((key) => {
      if (key !== 'skinConfig') {
        const value = cache[key];
        if (typeof value !== 'undefined') {
          state[key] = value;
        }
      }
    });
    return state;
  },
  getters: {
    /** 需要缓存的路由组件 */
    keepAliveInclude(): string[] {
      if (!this.pageKeepAlive || !this.tabs) {
        return [];
      }
      const components = new Set<string>();
      this.tabs.forEach((t) => {
        if (t.meta?.keepAlive !== false && !t.refresh && t.components) {
          t.components.forEach((c) => {
            if (typeof c === 'string' && c) {
              components.add(c);
            }
          });
        }
      });
      return Array.from(components);
    }
  },
  actions: {
    /**
     * 释放无用的皮肤背景文件缓存
     * @param isAll 是否全部释放
     */
    async releaseSkinBgCache(isAll?: boolean) {
      // 释放全部
      if (isAll || !this.skinConfig) {
        SKIN_BG_CACHE.forEach((url) => {
          URL.revokeObjectURL(url);
        });
        SKIN_BG_CACHE.clear();
        await clearAllWallpaperFile();
        return;
      }
      // 释放未使用的文件
      const urls = [
        this.skinConfig.wallpaper,
        this.skinConfig.overlayBg,
        this.skinConfig.darkConfig?.wallpaper,
        this.skinConfig.darkConfig?.overlayBg
      ];
      const delIds: string[] = [];
      SKIN_BG_CACHE.forEach((url, id) => {
        if (!urls.some((u) => u && u.includes(url))) {
          URL.revokeObjectURL(url);
          delIds.push(id);
        }
      });
      delIds.forEach((id) => {
        SKIN_BG_CACHE.delete(id);
      });
      await clearWallpaperFile(SKIN_BG_CACHE ? [...SKIN_BG_CACHE.keys()] : []);
    },
    /**
     * 修改配置
     * @param prop 属性名
     * @param value 值
     */
    async setValue(prop: ThemeStateProp, value?: any) {
      if (prop === 'skinConfig') {
        // 皮肤背景文件特殊处理
        if (typeof value === 'function') {
          const skin = await value(async (file: File) => {
            const id = await storeWallpaperFile(file);
            cacheSkinBg(id, file);
            return id;
          });
          this.skinConfig = await getSkinConfig(skin, false, true);
        } else {
          this.skinConfig = value;
        }
        cacheSetting(prop, await getSkinConfig(this.skinConfig, true, false));
      } else {
        this[prop as any] = value;
        if (prop !== 'tabs' || this.tabsCache) {
          cacheSetting(prop, value);
        }
      }
      if (prop === 'tabsCache') {
        // 页签缓存开关切换后同时更新缓存的页签数据
        cacheSetting('tabs', value ? this.tabs : void 0);
      } else if (
        prop === 'color' ||
        prop === 'darkMode' ||
        prop === 'skinConfig'
      ) {
        // 切换主题色、暗黑模式、皮肤背景
        if (prop === 'skinConfig') {
          await this.releaseSkinBgCache();
        }
        if (prop === 'darkMode' || prop === 'skinConfig') {
          const skinColor = this.darkMode
            ? this.skinConfig?.darkConfig?.color
            : this.skinConfig?.color;
          if (skinColor && skinColor !== this.color) {
            this.color = skinColor;
            cacheSetting('color', this.color);
          }
        }
        changeTheme(this.color, this.darkMode, this.skinConfig);
      } else if (prop === 'roundedTheme') {
        changeRoundedTheme(value); // 切换圆角主题
      } else if (prop === 'weakMode') {
        changeWeakMode(value); // 切换色弱模式
      } else if (prop === 'responsive') {
        changeResponsive(value); // 切换移动端响应式
      }
    },
    /**
     * 重置
     */
    async resetSetting() {
      const excludes = ['tabs', 'collapse', 'contentWidth'];
      Object.keys(DEFAULT_STATE).forEach((key) => {
        if (!excludes.includes(key)) {
          this[key] = cloneDeep(DEFAULT_STATE[key]);
        }
      });
      localStorage.removeItem(THEME_CACHE_NAME);
      await this.releaseSkinBgCache(true);
      changeResponsive(this.responsive);
      changeRoundedTheme(this.roundedTheme);
      changeWeakMode(this.weakMode);
      changeTheme(this.color, this.darkMode, this.skinConfig);
    },
    /**
     * 恢复主题
     */
    recoverTheme() {
      // 关闭响应式布局
      if (!this.responsive) {
        changeResponsive(false);
      }
      // 开启圆角主题
      if (this.roundedTheme) {
        changeRoundedTheme(true);
      }
      // 开启色弱模式
      if (this.weakMode) {
        changeWeakMode(true);
      }
      // 读取缓存的皮肤背景
      getCacheSkinConfig(this.skinConfig)
        .then((skin) => {
          this.skinConfig = skin;
          // 恢复主题色、暗黑模式、皮肤背景
          if (this.color || this.darkMode || this.skinConfig) {
            changeTheme(this.color, this.darkMode, this.skinConfig);
          }
        })
        .catch((e) => {
          console.error(e);
        });
    },
    /**
     * 添加页签或更新页签数据
     * @param data 页签数据
     */
    tabAdd(data: TabItem) {
      const i = this.tabs.findIndex((d) => d.key === data.key);
      if (i === -1) {
        const temps = [...this.tabs, data];
        this.setValue('tabs', temps).catch((e) => console.error(e));
      } else if (data.fullPath !== this.tabs[i].fullPath) {
        const temps = [...this.tabs];
        temps[i] = data;
        this.setValue('tabs', temps).catch((e) => console.error(e));
      }
    },
    /**
     * 关闭页签
     */
    async tabRemove({ key, active }: TabItemEventOption): TabRemoveReturn {
      const i = this.tabs.findIndex((t) => t.key === key || t.fullPath === key);
      if (i === -1) {
        return {};
      }
      const t = this.tabs[i];
      if (
        !t.closable ||
        (t.home && (this.tabs.length === 1 || this.fixedHome))
      ) {
        return Promise.reject();
      }
      const path = this.tabs[i + (i === 0 ? 1 : -1)]?.fullPath;
      this.setValue(
        'tabs',
        this.tabs.filter((_d, j) => j !== i)
      );
      return t.key === active ? { path, home: !path } : {};
    },
    /**
     * 关闭左侧页签
     */
    async tabRemoveLeft({ key, active }: TabItemEventOption): TabRemoveReturn {
      let index = -1; // 选中页签的索引
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === active) {
          index = i;
        }
        if (this.tabs[i].key === key) {
          if (i === 0) {
            break;
          }
          const temp = this.tabs.filter((d, j) => !d.closable && j < i);
          if (temp.length === i) {
            break;
          }
          const path = index === -1 ? void 0 : this.tabs[i].fullPath;
          await this.setValue('tabs', temp.concat(this.tabs.slice(i)));
          return { path };
        }
      }
      return Promise.reject();
    },
    /**
     * 关闭右侧页签
     */
    async tabRemoveRight({ key, active }: TabItemEventOption): TabRemoveReturn {
      let index = -1; // 选中页签的索引
      for (let i = 0; i < this.tabs.length; i++) {
        if (this.tabs[i].key === active) {
          index = i;
        }
        if (this.tabs[i].key === key) {
          if (i === this.tabs.length - 1) {
            break;
          }
          const temp = this.tabs.filter((d, j) => !d.closable && j > i);
          if (temp.length === this.tabs.length - i - 1) {
            break;
          }
          const path = index === -1 ? this.tabs[i].fullPath : void 0;
          await this.setValue('tabs', this.tabs.slice(0, i + 1).concat(temp));
          return { path };
        }
      }
      return Promise.reject();
    },
    /**
     * 关闭其它页签
     */
    async tabRemoveOther({ key, active }: TabItemEventOption): TabRemoveReturn {
      let path: string | undefined; // 关闭后跳转的地址
      const temps = this.tabs.filter((d) => {
        if (d.key === key) {
          path = d.fullPath;
        }
        return !d.closable || d.key === key;
      });
      if (temps.length === this.tabs.length) {
        return Promise.reject();
      }
      await this.setValue('tabs', temps);
      return key === active ? {} : { path };
    },
    /**
     * 关闭全部页签
     */
    async tabRemoveAll({ active }: TabItemEventOption): TabRemoveReturn {
      if (this.tabs.length === 1 && this.tabs[0].home) {
        return Promise.reject();
      }
      const temps = this.tabs.filter(
        (t) => !t.closable || (t.home && this.fixedHome)
      );
      if (temps.length === this.tabs.length) {
        return Promise.reject();
      }
      const t = active ? this.tabs.find((d) => d.key === active) : void 0;
      const jump = t != null && t.closable === true; // 关闭后是否跳转
      if (!temps.length) {
        const h = this.tabs.find((d) => d.home);
        if (!h) {
          await this.setValue('tabs', []);
          return { home: true };
        }
        await this.setValue('tabs', [h]);
        return { home: t?.home ? void 0 : true };
      }
      await this.setValue('tabs', temps);
      return { path: jump ? temps[0].fullPath : void 0 };
    },
    /**
     * 修改页签
     * @param data 页签数据
     */
    tabSetItem(data: TabItem) {
      if (!data.key && !data.fullPath) {
        if (!data.path) {
          return;
        }
        this.tabs.forEach((d) => {
          if (data.path === d.path) {
            this.tabSetItem({ ...data, key: d.key });
          }
        });
        return;
      }
      const k = data.key ? 'key' : 'fullPath';
      const i = this.tabs.findIndex((d) => data[k] === d[k]);
      if (i === -1) {
        return;
      }
      const item = { ...this.tabs[i] };
      if (data.title) {
        const title = data.title;
        item.title = title;
        if (item.meta) {
          item.meta.lang = { zh_CN: title, zh_TW: title, en: title };
        }
      }
      if (typeof data.closable === 'boolean') {
        item.closable = data.closable;
      }
      if (typeof data.refresh === 'boolean') {
        item.refresh = data.refresh;
      }
      if (data.components) {
        item.components = data.components;
      }
      const temps = [...this.tabs];
      temps[i] = item;
      this.setValue('tabs', temps).catch((e) => console.error(e));
    },
    /** 为兼容旧版保留方法 */
    setTabs(value: TabItem[]) {
      this.setValue('tabs', value).catch((e) => console.error(e));
    },
    setCollapse(value: boolean) {
      this.setValue('collapse', value).catch((e) => console.error(e));
    },
    setCompact(value: boolean) {
      this.setValue('compact', value).catch((e) => console.error(e));
    },
    setMaximized(value: boolean) {
      this.setValue('maximized', value).catch((e) => console.error(e));
    },
    setContentWidth(value: number | null) {
      this.setValue('contentWidth', value).catch((e) => console.error(e));
    },
    setDarkMode(value: boolean) {
      return this.setValue('darkMode', value);
    }
  }
});
