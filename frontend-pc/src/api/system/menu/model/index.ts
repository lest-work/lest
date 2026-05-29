export interface Menu {
  menuId?: number;
  menuName?: string;
  parentId?: number;
  orderNum?: number;
  path?: string;
  component?: string;
  query?: string;
  isFrame?: number;
  isCache?: number;
  menuType?: string;
  visible?: string;
  status?: string;
  perms?: string;
  icon?: string;
  remark?: string;
  children?: Menu[];
  createTime?: string;
}

export interface MenuParam {
  menuName?: string;
  status?: string;
}
