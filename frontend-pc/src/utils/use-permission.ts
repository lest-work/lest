import { storeToRefs } from 'pinia';
import { useUserStore } from '@/store/modules/user';

/**
 * 权限判断hook
 */
export function usePermission() {
  const userStore = useUserStore();
  const { authorities, roles } = storeToRefs(userStore);

  /**
   * 是否有某些权限
   * @param value 权限字符或字符数组
   */
  const hasPermission = (value: string | string[]): boolean => {
    return arrayHas(authorities.value, value);
  };

  /**
   * 是否有任意权限
   * @param value 权限字符或字符数组
   */
  const hasAnyPermission = (value: string | string[]): boolean => {
    return arrayHasAny(authorities.value, value);
  };

  /**
   * 是否有某些角色
   * @param value 角色字符或字符数组
   */
  const hasRole = (value: string | string[]): boolean => {
    return arrayHas(roles.value, value);
  };

  /**
   * 是否有任意角色
   * @param value 角色字符或字符数组
   */
  const hasAnyRole = (value: string | string[]): boolean => {
    return arrayHasAny(roles.value, value);
  };

  return { hasPermission, hasAnyPermission, hasRole, hasAnyRole };
}

/**
 * 判断数组是否有某些值（支持超管通配符 *:*:* / admin）
 */
function arrayHas(array: string[], value: string | string[]) {
  if (!value) {
    return true;
  }
  if (!array) {
    return false;
  }
  if (array.includes('*:*:*') || array.includes('admin')) {
    return true;
  }
  if (Array.isArray(value)) {
    return value.every((v) => array.includes(v));
  }
  return array.includes(value);
}

/**
 * 判断数组是否有任意值（支持超管通配符 *:*:* / admin）
 */
function arrayHasAny(array: string[], value: string | string[]) {
  if (!value) {
    return true;
  }
  if (!array) {
    return false;
  }
  if (array.includes('*:*:*') || array.includes('admin')) {
    return true;
  }
  if (Array.isArray(value)) {
    return array.some((d) => d && value.includes(d));
  }
  return array.includes(value);
}
