/**
 * 登录用户状态管理
 */
import { defineStore } from 'pinia';
import { mapTree, isExternalLink } from 'ele-admin-plus';
import type { MenuItem } from 'ele-admin-plus/es/ele-pro-layout/types';
import { getUserInfo, getUserMenu } from '@/api/layout';
import { API_BASE_URL } from '@/config/setting';
import type { User } from '@/api/system/user/model';
import defaultAvatarUrl from '@/assets/avatar.jpg';

export interface UserState {
  info: User | null;
  menus: MenuItem[] | null;
  authorities: string[];
  roles: string[];
  dicts: Record<string, any[]>;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    /** 当前登录用户的信息 */
    info: null,
    /** 当前登录用户的菜单 */
    menus: null,
    /** 当前登录用户的权限 */
    authorities: [],
    /** 当前登录用户的角色 */
    roles: [],
    /** 字典数据缓存 */
    dicts: {}
  }),
  actions: {
    /**
     * 请求登录用户的个人信息/权限/角色/菜单
     */
    async fetchUserInfo() {
      const result = await getUserInfo().catch((e) => console.error(e));
      if (!result) {
        return {};
      }
      // 用户信息
      this.setInfo(result.user);
      // 用户权限
      this.authorities = result.permissions ?? [];
      // 用户角色
      this.roles = result.roles ?? [];
      // 用户菜单
      const userMenu = await getUserMenu().catch((e) => console.error(e));
      if (!userMenu) {
        return {};
      }
      const { menus, homePath } = formatMenus(userMenu);
      this.setMenus(menus);
      return { menus, homePath };
    },
    /**
     * 更新用户信息
     */
    setInfo(data?: User) {
      if (data) {
        if (!data.avatar) {
          (data as any).avatar = defaultAvatarUrl;
        } else if (!isExternalLink(data.avatar)) {
          data.avatar = API_BASE_URL + data.avatar;
        }
      }
      this.info = data ?? null;
    },
    /**
     * 更新菜单数据
     */
    setMenus(value: MenuItem[] | null) {
      this.menus = value;
    },
    /**
     * 更新字典数据
     */
    setDicts(value: any[] | Record<string, any[]>, code?: string | null) {
      if (code == null) {
        this.dicts = value as Record<string, any[]>;
        return;
      }
      this.dicts[code] = value as any[];
    }
  }
});

/**
 * 菜单数据处理为EleProLayout所需要的格式
 */
function formatMenus(data: any[], childField = 'children') {
  let homePath: string | undefined;
  let homeTitle: string | undefined;
  const menus = mapTree(
    data,
    (item: any, _index: number, parent: any) => {
      const meta = item.meta;
      const { path, rPath } = formatPath(item.path, parent?.path, item.query);
      const menu: any = {
        path: path,
        component: formatComponent(item.component),
        meta: {
          hide: !!item.hidden,
          keepAlive: !meta?.noCache,
          routePath: rPath,
          ...meta
        }
      };
      const children = item[childField]
        ? item[childField].filter((d: any) => !(d.meta?.hide ?? d.hide))
        : void 0;
      if (!children?.length) {
        if (!homePath && menu.path && !isExternalLink(menu.path)) {
          homePath = menu.path;
          homeTitle = menu.meta?.title;
        }
      } else {
        const childPath = children[0].path;
        if (childPath) {
          if (!menu.redirect) {
            menu.redirect = childPath;
          }
          if (!menu.path) {
            menu.path = childPath.substring(0, childPath.lastIndexOf('/'));
          }
        }
      }
      if (!menu.path) {
        console.error('菜单path不能为空且要唯一:', item);
        return;
      }
      return menu;
    },
    childField
  );
  return { menus, homePath, homeTitle };
}

/**
 * 组件路径处理以兼容若依默认数据
 */
function formatComponent(component?: string): string | undefined {
  if (!component || component === 'Layout' || component === '#') {
    return;
  }
  if (isExternalLink(component)) {
    return component;
  }
  return component.startsWith('/') ? component : `/${component}`;
}

/**
 * 菜单地址处理以兼容若依
 */
function formatPath(
  mPath?: string,
  pPath?: string,
  query?: string
): { path?: string; rPath?: string } {
  if (!mPath || isExternalLink(mPath)) {
    return { path: mPath };
  }
  const path = !pPath || mPath.startsWith('/') ? mPath : `${pPath}/${mPath}`;
  if (query) {
    try {
      const params = new URLSearchParams(JSON.parse(query)).toString();
      if (params) {
        return { path: `${path}?${params}`, rPath: path };
      }
    } catch (e) {
      console.error(e);
    }
  }
  return { path };
}
