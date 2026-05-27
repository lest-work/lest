/**
 * 系统管理模块 - 统一类型定义
 * @description 包含用户、角色、菜单、机构、字典等核心类型定义
 * @author Lest
 * @since 2026-05-26
 */
import type { PageParam } from '@/api';

/* ==================== 用户相关类型 ==================== */

/**
 * 用户信息
 */
export interface UserItem {
  /** 用户id */
  userId?: number;
  /** 账号 */
  username?: string;
  /** 密码 */
  password?: string;
  /** 昵称 */
  nickname?: string;
  /** 头像 */
  avatar?: string;
  /** 性别(字典) */
  sex?: string;
  /** 性别名称 */
  sexName?: string;
  /** 手机号 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 出生日期 */
  birthday?: string;
  /** 个人简介 */
  introduction?: string;
  /** 机构id */
  organizationId?: number;
  /** 机构名称 */
  organizationName?: string;
  /** 状态, 0正常, 1冻结 */
  status?: number;
  /** 角色列表 */
  roles?: RoleItem[];
  /** 权限列表 */
  authorities?: MenuItem[];
  /** 创建时间 */
  createTime?: string;
  /** 街道地址 */
  address?: string;
  /** 联系电话前缀 */
  tellPre?: string;
  /** 联系电话 */
  tell?: string;
}

/**
 * 用户搜索参数
 */
export interface UserParam extends PageParam {
  /** 账号 */
  username?: string;
  /** 昵称 */
  nickname?: string;
  /** 性别(字典) */
  sex?: string;
  /** 手机号 */
  phone?: string;
  /** 状态 */
  status?: number;
  /** 机构id */
  organizationId?: number;
  /** 邮箱 */
  email?: string;
  /** 创建时间开始时间 */
  createTimeStart?: string;
  /** 创建时间截止时间 */
  createTimeEnd?: string;
}

/* ==================== 角色相关类型 ==================== */

/**
 * 角色信息
 */
export interface RoleItem {
  /** 角色id */
  roleId?: number;
  /** 角色标识 */
  roleCode?: string;
  /** 角色名称 */
  roleName?: string;
  /** 备注 */
  comments?: string;
  /** 创建时间 */
  createTime?: string;
}

/**
 * 角色搜索参数
 */
export interface RoleParam extends PageParam {
  /** 角色名称 */
  roleName?: string;
  /** 角色标识 */
  roleCode?: string;
  /** 备注 */
  comments?: string;
}

/* ==================== 菜单相关类型 ==================== */

/**
 * 菜单信息
 */
export interface MenuItem {
  /** 菜单id */
  menuId?: number;
  /** 上级id, 0是顶级 */
  parentId?: number;
  /** 菜单名称 */
  title?: string;
  /** 菜单路由地址 */
  path?: string;
  /** 菜单组件地址 */
  component?: string;
  /** 菜单类型, 0菜单, 1按钮 */
  menuType?: number;
  /** 排序号 */
  sortNumber?: number;
  /** 权限标识 */
  authority?: string;
  /** 菜单图标 */
  icon?: string;
  /** 是否隐藏, 0否,1是 */
  hide?: number;
  /** 路由元信息 */
  meta?: any;
  /** 创建时间 */
  createTime?: string;
  /** 子菜单 */
  children?: MenuItem[];
  /** 打开方式 */
  openType?: number;
  /** 权限树回显选中状态, 0未选中, 1选中 */
  checked?: boolean;
  /** 父级重定向 */
  redirect?: string;
}

/**
 * 菜单搜索参数
 */
export interface MenuParam {
  /** 菜单名称 */
  title?: string;
  /** 菜单路由地址 */
  path?: string;
  /** 权限标识 */
  authority?: string;
  /** 上级id */
  parentId?: number;
}

/* ==================== 机构相关类型 ==================== */

/**
 * 机构信息
 */
export interface OrganizationItem {
  /** 机构id */
  organizationId?: number;
  /** 上级id, 0是顶级 */
  parentId?: number;
  /** 机构名称 */
  organizationName?: string;
  /** 机构全称 */
  organizationFullName?: string;
  /** 机构代码 */
  organizationCode?: string;
  /** 机构类型(字典) */
  organizationType?: string;
  /** 机构类型名称 */
  organizationTypeName?: string;
  /** 排序号 */
  sortNumber?: number;
  /** 备注 */
  comments?: string;
  /** 创建时间 */
  createTime?: string;
  /** 子级 */
  children?: OrganizationItem[];
}

/**
 * 机构搜索参数
 */
export interface OrganizationParam extends PageParam {
  /** 机构名称 */
  organizationName?: string;
  /** 机构全称 */
  organizationFullName?: string;
  /** 机构类型(字典) */
  organizationType?: string;
}

/* ==================== 字典相关类型 ==================== */

/**
 * 字典类型信息
 */
export interface DictionaryItem {
  /** 字典id */
  dictId?: number;
  /** 字典标识 */
  dictCode?: string;
  /** 字典名称 */
  dictName?: string;
  /** 排序号 */
  sortNumber?: number;
  /** 备注 */
  comments?: string;
  /** 创建时间 */
  createTime?: string;
}

/**
 * 字典搜索参数
 */
export interface DictionaryParam extends PageParam {
  /** 字典标识 */
  dictCode?: string;
  /** 字典名称 */
  dictName?: string;
}

/**
 * 字典数据信息
 */
export interface DictionaryDataItem {
  /** 字典数据id */
  dictDataId?: number;
  /** 字典id */
  dictId?: number;
  /** 字典数据标识 */
  dictDataCode: string;
  /** 字典数据名称 */
  dictDataName: string;
  /** 排序号 */
  sortNumber?: number;
  /** 备注 */
  comments?: string;
  /** 创建时间 */
  createTime?: string;
}

/**
 * 字典数据搜索参数
 */
export interface DictionaryDataParam extends PageParam {
  /** 关键字 */
  keywords?: string;
  /** 字典数据名称 */
  dictDataName?: string;
  /** 字典数据标识 */
  dictDataCode?: string;
  /** 字典标识 */
  dictCode?: string;
  /** 字典id */
  dictId?: number;
}

/* ==================== 当前用户信息 ==================== */

/**
 * 当前登录用户信息
 */
export interface CurrentUserVO {
  /** 用户id */
  userId: number;
  /** 用户名 */
  username: string;
  /** 昵称 */
  nickname: string;
  /** 邮箱 */
  email?: string;
  /** 手机号 */
  phone?: string;
  /** 头像 */
  avatar?: string;
  /** 角色列表 */
  roles: string[];
  /** 权限列表 */
  permissions: string[];
  /** 菜单列表 */
  menus: MenuItem[];
}
