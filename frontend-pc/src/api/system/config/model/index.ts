import type { PageParam } from '@/api';

export interface Config {
  configId?: number;
  configName?: string;
  configKey?: string;
  configValue?: string;
  configType?: string;
  remark?: string;
  createTime?: string;
}

export interface ConfigParam extends PageParam {
  configName?: string;
  configKey?: string;
  configType?: string;
  beginTime?: string;
  endTime?: string;
}
