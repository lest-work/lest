/**
 * Plugin Registry — singleton store for registered plugins and extensions.
 * Holds runtime plugin state (loaded from backend via API).
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { PluginDescriptor, ExtensionDescriptor, ExtensionPointId } from '@/plugins/types';

export const usePluginStore = defineStore('plugin', () => {
  /** Currently enabled plugins */
  const plugins = ref<PluginDescriptor[]>([]);

  /** All registered extensions, keyed by extension point */
  const extensions = ref<Map<ExtensionPointId, ExtensionDescriptor[]>>(
    new Map()
  );

  /** Whether plugins have been loaded */
  const loaded = ref(false);

  const enabledPlugins = computed(() =>
    plugins.value.filter((p) => p.hooks && p.hooks.length > 0)
  );

  function registerPlugin(descriptor: PluginDescriptor) {
    if (!plugins.value.find((p) => p.pluginId === descriptor.pluginId)) {
      plugins.value.push(descriptor);
    }
  }

  function unregisterPlugin(pluginId: string) {
    plugins.value = plugins.value.filter((p) => p.pluginId !== pluginId);
    for (const [point, exts] of extensions.value.entries()) {
      const filtered = exts.filter((e) => e.pluginId !== pluginId);
      extensions.value.set(point, filtered);
    }
  }

  function registerExtension(extension: ExtensionDescriptor) {
    const point = extension.point as ExtensionPointId;
    if (!extensions.value.has(point)) {
      extensions.value.set(point, []);
    }
    const existing = extensions.value.get(point)!;
    if (!existing.find((e) => e.id === extension.id)) {
      existing.push(extension);
      existing.sort((a, b) => (a.order ?? 99) - (b.order ?? 99));
    }
  }

  function unregisterExtension(extensionId: string) {
    for (const [point, exts] of extensions.value.entries()) {
      const filtered = exts.filter((e) => e.id !== extensionId);
      if (filtered.length !== exts.length) {
        extensions.value.set(point, filtered);
      }
    }
  }

  function getExtensions(point: ExtensionPointId): ExtensionDescriptor[] {
    return extensions.value.get(point) ?? [];
  }

  function hasExtension(point: ExtensionPointId): boolean {
    return (extensions.value.get(point)?.length ?? 0) > 0;
  }

  function setPlugins(list: PluginDescriptor[]) {
    plugins.value = list;
    loaded.value = true;
  }

  function setExtensions(list: ExtensionDescriptor[]) {
    extensions.value.clear();
    for (const ext of list) {
      registerExtension(ext);
    }
  }

  function reset() {
    plugins.value = [];
    extensions.value.clear();
    loaded.value = false;
  }

  return {
    plugins,
    extensions,
    loaded,
    enabledPlugins,
    registerPlugin,
    unregisterPlugin,
    registerExtension,
    unregisterExtension,
    getExtensions,
    hasExtension,
    setPlugins,
    setExtensions,
    reset,
  };
});
