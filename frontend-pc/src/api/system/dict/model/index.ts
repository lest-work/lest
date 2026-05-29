import type { PageParam } from '@/api';

export interface Dict {
  dictId?: number;
  dictName?: string;
  dictType?: string;
  status?: string;
  remark?: string;
  createTime?: string;
}

export interface DictParam extends PageParam {
  dictName?: string;
  dictType?: string;
  status?: string;
  beginTime?: string;
  endTime?: string;
}
