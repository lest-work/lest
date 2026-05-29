declare module '*.vue' {
  import { DefineComponent } from 'vue';
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

declare module '@/components/IconSelect/util' {
  import type { App, Plugin } from 'vue';
  export const iconsInstaller: Plugin;
  export function getIconData(): Record<string, any>;
  export function getIconSelectData(): any[];
  export const elementIconData: Array<{ title: string; icons: string[] }>;
}

declare module '@/components/icons' {
  const icons: Record<string, any>;
  export = icons;
}
