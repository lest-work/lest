export interface Dept {
  deptId?: number;
  parentId?: number;
  ancestors?: string;
  deptName?: string;
  orderNum?: number;
  leader?: string;
  phone?: string;
  email?: string;
  status?: string;
  children?: Dept[];
  createTime?: string;
}

export interface DeptParam {
  deptName?: string;
  status?: string;
}
