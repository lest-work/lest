/**
 * usePlugin — composable for loading and managing plugin extensions.
 * Call usePlugin() in components to access registered extensions.
 */
import { computed } from 'vue';
import { usePluginStore } from '@/stores/plugin';
import { pluginApi } from '@/api/plugin';
import type { ExtensionPointId, PluginDescriptor } from '@/plugins/types';

/** Extension point IDs used by the frontend UI */
export const FE_EXTENSION_POINTS = {
  TASK_DETAIL_TAB: 'task.detail.tab',
  PROJECT_SIDEBAR_MENU: 'project.sidebar.menu',
} as const;

export function usePlugin() {
  const store = usePluginStore();

  async function loadExtensions() {
    try {
      const res = await pluginApi.listEnabled();
      if (res.code === 200 && Array.isArray(res.data)) {
        store.setPlugins(res.data as unknown as PluginDescriptor[]);
        for (const plugin of res.data) {
          const pid = plugin.pluginId;
          if (!pid) continue;
          const extRes = await pluginApi.getById(pid);
          if (extRes.code === 200 && extRes.data) {
            const p = plugin as unknown as PluginDescriptor;
            const hooks = p.hooks as string[] | undefined;
            if (hooks) {
              for (const hook of hooks) {
                const parts = hook.split(':');
                const point = parts[0] as ExtensionPointId;
                const label = parts[1] || plugin.name;
                store.registerExtension({
                  id: `${pid}-${point}`,
                  pluginId: String(pid),
                  point,
                  label,
                  component: parts[2],
                  order: p.order,
                  config: p.config,
                });
              }
            }
          }
        }
      }
    } catch {
      // plugin system is optional, silently ignore
    }
  }

  function getExtensions(point: ExtensionPointId) {
    return store.getExtensions(point);
  }

  function hasExtensions(point: ExtensionPointId) {
    return store.hasExtension(point);
  }

  return {
    store,
    loaded: computed(() => store.loaded),
    loadExtensions,
    getExtensions,
    hasExtensions,
  };
}
