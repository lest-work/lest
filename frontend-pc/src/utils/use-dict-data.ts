import type { ComputedRef } from 'vue';
import { computed } from 'vue';
import { EleMessage } from 'ele-admin-plus';
import { storeToRefs } from 'pinia';
import { useUserStore } from '@/store/modules/user';
import { listDictDatas } from '@/api/system/dict-data';
import type { DictData } from '@/api/system/dict-data/model';

/**
 * 获取字典数据hook
 * @param codes 字典类型
 */
export function useDictData(codes: string[]): ComputedRef<DictData[]>[] {
  const result: ComputedRef<DictData[]>[] = [];

  // 已缓存的字典
  const userStore = useUserStore();
  const { dicts } = storeToRefs(userStore);

  codes.forEach((code) => {
    result.push(computed<DictData[]>(() => dicts.value[code] || []));
    // 若还未缓存过则获取字典数据
    if (dicts.value[code] != null) {
      return;
    }
    userStore.setDicts([], code);
    listDictDatas(code)
      .then((list) => {
        userStore.setDicts(list, code);
      })
      .catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      });
  });

  return result;
}
