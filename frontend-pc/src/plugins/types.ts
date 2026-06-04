/**
 * LEST Platform Plugin Types
 * Mirrors backend PluginDescriptor / ExtensionPoints for frontend use.
 */

export type ExtensionPointId =
  | 'task.detail.tab'
  | 'project.sidebar.menu'
  | 'task.before.create'
  | 'task.after.create'
  | 'task.before.status.change'
  | 'task.after.status.change'
  | 'task.before.delete'
  | 'task.after.delete'
  | 'task.event'
  | 'project.event'
  | 'iteration.event'
  | 'automation.rule'
  | 'webhook.trigger';

export interface PluginConfig {
  [key: string]: any;
}

export interface PluginDescriptor {
  pluginId: string;
  name: string;
  version: string;
  description?: string;
  author?: string;
  website?: string;
  type?: string;
  order?: number;
  config?: PluginConfig;
  dependencies?: string[];
  hooks?: string[];
}

export interface ExtensionDescriptor {
  id: string;
  pluginId: string;
  point: ExtensionPointId;
  label: string;
  component?: string;
  action?: string;
  order?: number;
  config?: PluginConfig;
}

export interface PluginExtension {
  descriptor: ExtensionDescriptor;
  component?: any;
  action?: (...args: any[]) => any;
}

export const EXTENSION_POINTS = {
  TASK_DETAIL_TAB: 'task.detail.tab',
  PROJECT_SIDEBAR_MENU: 'project.sidebar.menu',
} as const;
