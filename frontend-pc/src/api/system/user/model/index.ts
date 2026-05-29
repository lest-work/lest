import type { PageParam } from '@/api';

/**
 * 部门
 */
export interface Dept {
  deptId?: number;
  deptName?: string;
  parentId?: number;
  orderNum?: number;
  leader?: string;
  status?: string;
}

/**
 * 角色
 */
export interface Role {
  roleId?: number;
  roleName?: string;
  roleKey?: string;
  roleSort?: number;
  dataScope?: string;
  status?: string;
  flag?: boolean;
}

/**
 * 岗位
 */
export interface Post {
  postId?: number;
  postCode?: string;
  postName?: string;
  postSort?: number;
  status?: string;
}

/**
 * 用户 (RuoYi 格式)
 */
export interface User {
  /** 用户id */
  userId?: number;
  /** 部门id */
  deptId?: number;
  /** 登录账号 */
  userName?: string;
  /** 用户昵称 */
  nickName?: string;
  /** 用户邮箱 */
  email?: string;
  /** 手机号码 */
  phonenumber?: string;
  /** 用户性别 */
  sex?: string;
  /** 头像 */
  avatar?: string;
  /** 帐号状态(0正常 1停用) */
  status?: string;
  /** 最后登录IP */
  loginIp?: string;
  /** 最后登录时间 */
  loginDate?: string;
  /** 备注 */
  remark?: string;
  /** 创建时间 */
  createTime?: string;
  /** 部门信息 */
  dept?: Dept;
  /** 角色列表 */
  roles?: Role[];
  /** 角色id列表 */
  roleIds?: number[];
  /** 岗位id列表 */
  postIds?: number[];
  /** 岗位列表 */
  posts?: Post[];
}

/**
 * 用户详情返回（GET /system/user/{userId}）
 * SysUserController.getInfo(userId) 使用 ajax.put() 将字段放到响应顶层
 */
export interface UserDetailResult {
  code: number;
  msg: string;
  data?: User;
  /** 角色id列表 */
  roleIds?: number[];
  /** 岗位id列表 */
  postIds?: number[];
  /** 全量角色列表 */
  roles?: Role[];
  /** 全量岗位列表 */
  posts?: Post[];
}

/**
 * 用户授权角色返回（GET /system/user/authRole/{userId}）
 * 返回 user 和 roles（带 flag 字段标记已分配）
 */
export interface UserRoleResult {
  code: number;
  msg: string;
  user?: User;
  roles?: Role[];
}

/**
 * 用户信息返回（GET /system/user/getInfo 接口返回顶层字段）
 * SysUserController.getInfo() 使用 ajax.put() 将字段放到响应顶层
 */
export interface UserInfoResult {
  code: number;
  msg: string;
  /** 用户信息 */
  user?: User;
  /** 角色 key 集合 */
  roles?: string[];
  /** 权限标识集合 */
  permissions?: string[];
  /** 密码字符类型配置 */
  pwdChrtype?: string;
  /** 初始密码是否提示修改 */
  isDefaultModifyPwd?: boolean;
  /** 密码是否过期 */
  isPasswordExpired?: boolean;
}

/**
 * 用户查询参数
 */
export interface UserParam extends PageParam {
  /** 账号 */
  userName?: string;
  /** 手机号 */
  phonenumber?: string;
  /** 状态 */
  status?: string;
  /** 部门id */
  deptId?: number;
  /** 开始时间 */
  beginTime?: string;
  /** 结束时间 */
  endTime?: string;
}
