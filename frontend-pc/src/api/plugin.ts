import request from './axios';
import type { PluginDescriptor, ExtensionDescriptor } from '@/plugins/types';

export interface Plugin {
  pluginId?: number;
  pluginIdKey?: string;
  name: string;
  version?: string;
  description?: string;
  author?: string;
  type?: string;
  isEnabled?: boolean;
  isInstalled?: boolean;
  installedAt?: string;
  enabledAt?: string;
  createTime?: string;
  updateTime?: string;
}

export const pluginApi = {
  /** 获取所有插件列表（含未安装）*/
  list(params?: { type?: string; isEnabled?: boolean }) {
    return request.get<any, { code: number; data: { records: Plugin[]; total: number } }>('/plugin/list', { params });
  },

  /** 获取已启用的插件列表 */
  listEnabled() {
    return request.get<any, { code: number; data: Plugin[] }>('/plugin/enabled');
  },

  /** 获取插件详情 */
  getById(pluginId: number) {
    return request.get<any, { code: number; data: Plugin }>(`/plugin/${pluginId}`);
  },

  /** 安装插件 */
  install(data: { pluginIdKey: string; version?: string }) {
    return request.post<any, { code: number; data: Plugin }>('/plugin', data);
  },

  /** 卸载插件 */
  uninstall(pluginId: number) {
    return request.delete<any, { code: number }>(`/plugin/${pluginId}`);
  },

  /** 启用插件 */
  enable(pluginId: number) {
    return request.put<any, { code: number }>(`/plugin/${pluginId}/enable`);
  },

  /** 禁用插件 */
  disable(pluginId: number) {
    return request.put<any, { code: number }>(`/plugin/${pluginId}/disable`);
  },

  /** 获取插件配置 */
  getConfig(pluginId: number) {
    return request.get<any, { code: number; data: Record<string, any> }>(`/plugin/${pluginId}/config`);
  },

  /** 保存插件配置 */
  saveConfig(pluginId: number, config: Record<string, any>) {
    return request.post<any, { code: number }>('/plugin/config', { pluginId, ...config });
  },
};
