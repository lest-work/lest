import type { PageParam } from '@/api';

export interface Role {
  roleId?: number;
  roleName?: string;
  roleKey?: string;
  roleSort?: number;
  dataScope?: string;
  menuCheckStrictly?: boolean;
  deptCheckStrictly?: boolean;
  status?: string;
  remark?: string;
  flag?: boolean;
  menuIds?: number[];
  deptIds?: number[];
  createTime?: string;
}

export interface RoleParam extends PageParam {
  roleName?: string;
  roleKey?: string;
  status?: string;
  beginTime?: string;
  endTime?: string;
}
