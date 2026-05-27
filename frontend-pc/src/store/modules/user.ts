/**
 * 登录用户状态管理
 * @description 管理当前登录用户的信息、菜单、权限、角色和字典数据
 */
import { defineStore } from 'pinia';
import type { BadgeProps } from 'element-plus';
import { toTree, mapTree, isExternalLink } from 'ele-admin-plus';
import type { MenuItem } from 'ele-admin-plus/es/ele-pro-layout/types';
import type { User, UserItem } from '@/api/system/model';
import type { MenuItem as Menu } from '@/api/system/model';
import type { DictionaryDataItem } from '@/api/system/model';
import { getUserInfo } from '@/api/layout';

/** 直接指定菜单数据（可选，用于调试或特殊场景） */
const USER_MENUS: Menu[] | null = null;

export interface UserState {
  /** 当前登录用户的信息 */
  info: UserItem | null;
  /** 当前登录用户的菜单 */
  menus: MenuItem[] | null;
  /** 当前登录用户的权限 */
  authorities: (string | undefined)[];
  /** 当前登录用户的角色 */
  roles: (string | undefined)[];
  /** 字典数据缓存 */
  dicts: Record<string, DictionaryDataItem[]>;
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
     * @description 调用此方法获取当前用户信息并初始化状态
     */
    async fetchUserInfo() {
      const result = await getUserInfo().catch((e) => console.error(e));
      if (!result) {
        return {};
      }
      // 用户信息
      this.setInfo(result);
      // 用户权限
      if (result.authorities) {
        this.authorities =
          result.authorities.map((d) => d.authority).filter((a) => !!a) ?? [];
      }
      // 用户角色
      this.roles = result.roles?.map?.((d) => d.roleCode) ?? [];
      // 用户菜单, 过滤掉按钮类型并转为children形式
      const { menus, homePath } = formatMenus(
        USER_MENUS ??
          toTree({
            data: result.authorities?.filter?.((d) => d.menuType !== 1),
            idField: 'menuId',
            parentIdField: 'parentId'
          })
      );
      this.setMenus(menus);
      return { menus, homePath };
    },
    /**
     * 更新用户信息
     * @param value 用户信息
     */
    setInfo(value: UserItem) {
      this.info = value;
    },
    /**
     * 更新菜单数据
     */
    setMenus(menus: MenuItem[] | null) {
      this.menus = menus;
    },
    /**
     * 更新菜单的徽章
     * @param path 菜单地址
     * @param value 徽章值
     * @param type 徽章类型
     */
    setMenuBadge(
      path: string,
      value?: number | string | null,
      type?: BadgeProps['type']
    ) {
      this.menus = mapTree(this.menus, (m) => {
        if (path === m.path) {
          const meta = m.meta || {};
          return {
            ...m,
            meta: {
              ...meta,
              props: {
                ...meta.props,
                badge: value == null ? void 0 : { value, type }
              }
            }
          };
        }
        return m;
      });
    },
    /**
     * 更新字典数据
     * @param value 字典数据或字典数据Map
     * @param code 字典编码（可选）
     */
    setDicts(
      value: DictionaryDataItem[] | Record<string, DictionaryDataItem[]>,
      code?: string | null
    ) {
      if (code == null) {
        this.dicts = value as Record<string, DictionaryDataItem[]>;
        return;
      }
      this.dicts[code] = value as DictionaryDataItem[];
    }
  }
});

/**
 * 菜单数据处理为EleProLayout所需要的格式
 * @param data 菜单数据
 * @param childField 子级的字段名称
 */
function formatMenus(data: Menu[], childField = 'children') {
  let homePath: string | undefined;
  let homeTitle: string | undefined;
  const menus = mapTree<Menu, MenuItem>(
    data,
    (item) => {
      const meta: MenuItem['meta'] =
        typeof item.meta === 'string'
          ? JSON.parse(item.meta || '{}')
          : item.meta;
      const menu: MenuItem = {
        path: item.path,
        component: item.component,
        meta: { title: item.title, icon: item.icon, hide: !!item.hide, ...meta }
      };
      const children = item[childField]
        ? item[childField].filter((d) => !(d.meta?.hide ?? d.hide))
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
