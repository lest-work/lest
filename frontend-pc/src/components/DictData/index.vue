<!-- 字典组件 -->
<template>
  <template v-if="type === 'text'">
    <span
      v-for="item in valueData"
      :key="item.key"
      v-bind="componentProps || {}"
    >
      {{ item.label }}
    </span>
  </template>
  <template v-else-if="type === 'tag'">
    <el-tag
      v-for="item in valueData"
      :key="item.key"
      size="small"
      :type="item.listClass === 'default' ? 'primary' : item.listClass"
      :disable-transitions="true"
      v-bind="componentProps || {}"
    >
      {{ item.label }}
    </el-tag>
  </template>
  <el-radio-group
    v-else-if="type === 'radio'"
    :disabled="disabled"
    v-bind="componentProps || {}"
    v-model="model"
  >
    <el-radio
      v-for="item in data"
      :key="item.key"
      :value="item.value"
      :label="item.label"
    />
  </el-radio-group>
  <el-checkbox-group
    v-else-if="type === 'checkbox'"
    :disabled="disabled"
    v-bind="componentProps || {}"
    v-model="model"
  >
    <el-checkbox
      v-for="item in data"
      :key="item.key"
      :value="item.value"
      :label="item.label"
    />
  </el-checkbox-group>
  <el-select
    v-else
    :disabled="disabled"
    :clearable="clearable"
    :placeholder="placeholder"
    :filterable="filterable"
    :teleported="teleported"
    class="ele-fluid"
    v-bind="componentProps || {}"
    :multiple="type === 'multipleSelect'"
    v-model="model"
  >
    <el-option
      v-for="item in data"
      :key="item.key"
      :value="item.value"
      :label="item.label"
    />
  </el-select>
</template>

<script setup>
  import { computed, watch } from 'vue';
  import { EleMessage } from 'ele-admin-plus';
  import { storeToRefs } from 'pinia';
  import { useUserStore } from '@/store/modules/user';
  import { listDictDatas } from '@/api/system/dict-data';

  defineOptions({ name: 'DictData' });

  const props = defineProps({
    /** 字典类型 */
    code: String,
    /** 组件类型 */
    type: String,
    /** 字典数据值类型 */
    valueType: String,
    /** 组件属性 */
    componentProps: Object,
    /** 是否禁用 */
    disabled: Boolean,
    /** 提示文本 */
    placeholder: String,
    /** select是否可清除 */
    clearable: {
      type: Boolean,
      default: true
    },
    /** select的下拉是否插入到body下 */
    teleported: {
      type: Boolean,
      default: true
    },
    /** select是否可搜索 */
    filterable: Boolean
  });

  /** 字典值 */
  const model = defineModel({
    type: [String, Number, Boolean, Array]
  });

  /** 已缓存的字典 */
  const userStore = useUserStore();
  const { dicts } = storeToRefs(userStore);

  /** 字典的数据 */
  const data = computed(() => {
    const code = props.code;
    const list = (code ? dicts.value[code] : void 0) || [];
    return list.map((item) => ({
      ...item,
      key: item.dictValue,
      value:
        item.dictValue == null
          ? null
          : props.valueType === 'number'
            ? Number(item.dictValue)
            : props.valueType === 'boolean'
              ? Boolean(item.dictValue)
              : item.dictValue,
      label: item.dictLabel
    }));
  });

  /** 绑定值对应的数据 */
  const valueData = computed(() => {
    const result = [];
    const val = model.value;
    if (val == null || val === '') {
      return result;
    }
    const values = Array.isArray(val) ? val : [val];
    values.forEach((v) => {
      const temp = data.value.find((d) => d.value == v);
      if (temp != null) {
        result.push(temp);
      } else {
        result.push({ key: v, value: v, label: v });
      }
    });
    return result;
  });

  /** 若还未缓存过则获取字典数据 */
  watch(
    () => props.code,
    (code) => {
      if (!code || dicts.value[code] != null) {
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
    },
    { immediate: true }
  );
</script>
