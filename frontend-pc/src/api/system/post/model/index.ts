import type { PageParam } from '@/api';

export interface Post {
  postId?: number;
  postCode?: string;
  postName?: string;
  postSort?: number;
  status?: string;
  remark?: string;
  createTime?: string;
}

export interface PostParam extends PageParam {
  postCode?: string;
  postName?: string;
  status?: string;
}
