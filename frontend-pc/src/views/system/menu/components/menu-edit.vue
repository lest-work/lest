<!-- 编辑弹窗 -->
<template>
  <ele-modal
    :form="true"
    :destroy-on-close="true"
    :width="740"
    v-model="visible"
    :title="isUpdate ? '修改菜单' : '新建菜单'"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      :validate-on-rule-change="false"
      @submit.prevent=""
    >
      <el-row :gutter="16">
        <el-col :sm="12" :xs="24">
          <el-form-item label="上级菜单" prop="parentId">
            <menu-select v-model="form.parentId" />
          </el-form-item>
          <el-form-item label="菜单名称" prop="menuName">
            <el-input
              clearable
              v-model="form.menuName"
              placeholder="请输入菜单名称"
            />
          </el-form-item>
        </el-col>
        <el-col :sm="12" :xs="24">
          <el-form-item label="菜单类型" prop="menuType">
            <el-radio-group
              v-model="form.menuType"
              @change="handleMenuTypeChange"
            >
              <el-radio value="M" label="目录" />
              <el-radio value="C" label="菜单" />
              <el-radio value="F" label="按钮" />
            </el-radio-group>
          </el-form-item>
          <el-form-item label="打开方式">
            <el-radio-group
              v-model="form.openType"
              :disabled="form.menuType == 'M' || form.menuType == 'F'"
              @change="handleOpenTypeChange"
            >
              <el-radio :value="0" label="组件" />
              <el-radio :value="1" label="内嵌" />
              <el-radio :value="2" label="外链" />
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-divider style="margin: 8px 0 22px 0; opacity: 0.6" />
      <el-row :gutter="16">
        <el-col :sm="12" :xs="24">
          <el-form-item label="菜单图标" prop="icon">
            <icon-select
              clearable
              filterable="popper"
              :popper-width="460"
              :popper-height="388"
              :popper-options="{ strategy: 'fixed' }"
              placeholder="请选择图标"
              v-model="form.icon"
              :disabled="form.menuType == 'F'"
            />
          </el-form-item>
          <el-form-item prop="path">
            <template #label>
              <ele-tooltip
                v-if="form.openType === 2"
                content="需要以`http://`、`https://`、`//`开头"
                placement="top-start"
                :popper-options="{
                  modifiers: [
                    { name: 'offset', options: { offset: [-12, 10] } }
                  ]
                }"
              >
                <el-icon
                  :size="15"
                  style="align-self: center; margin-right: 4px; cursor: help"
                >
                  <QuestionCircleOutlined style="opacity: 0.6" />
                </el-icon>
              </ele-tooltip>
              <span>{{ form.openType === 2 ? '外链地址' : '路由地址' }}</span>
            </template>
            <el-input
              clearable
              v-model="form.path"
              :disabled="form.menuType == 'F'"
              :placeholder="
                form.openType === 2 ? '请输入外链地址' : '请输入路由地址'
              "
            />
          </el-form-item>
          <el-form-item prop="component">
            <template #label>
              <ele-tooltip
                v-if="form.openType === 1"
                content="需要以`http://`、`https://`、`//`开头"
                placement="top-start"
                :popper-options="{
                  modifiers: [
                    { name: 'offset', options: { offset: [-12, 10] } }
                  ]
                }"
              >
                <el-icon
                  :size="15"
                  style="align-self: center; margin-right: 4px; cursor: help"
                >
                  <QuestionCircleOutlined style="opacity: 0.6" />
                </el-icon>
              </ele-tooltip>
              <span>{{ form.openType === 1 ? '内嵌地址' : '组件路径' }}</span>
            </template>
            <el-input
              clearable
              v-model="form.component"
              :disabled="
                form.menuType == 'M' ||
                form.menuType == 'F' ||
                form.openType === 2
              "
              :placeholder="
                form.openType === 1 ? '请输入内嵌地址' : '请输入组件路径'
              "
            />
          </el-form-item>
          <el-form-item label="路由参数" prop="query">
            <template #label>
              <ele-tooltip
                placement="top-start"
                :popper-options="{
                  modifiers: [
                    { name: 'offset', options: { offset: [-12, 10] } }
                  ]
                }"
              >
                <el-icon
                  :size="15"
                  style="align-self: center; margin-right: 4px; cursor: help"
                >
                  <QuestionCircleOutlined style="opacity: 0.6" />
                </el-icon>
                <template #content>
                  访问路由的默认传递参数, 如: {"id": 1, "name": "ry"}
                </template>
              </ele-tooltip>
              <span>路由参数</span>
            </template>
            <el-input
              clearable
              v-model="form.query"
              :maxlength="255"
              :disabled="
                form.menuType == 'M' ||
                form.menuType == 'F' ||
                form.openType === 2 ||
                form.openType === 1
              "
              placeholder="请输入路由参数"
            />
          </el-form-item>
          <el-form-item label="菜单状态" prop="status">
            <dict-data
              code="sys_normal_disable"
              type="radio"
              v-model="form.status"
            />
          </el-form-item>
        </el-col>
        <el-col :sm="12" :xs="24">
          <el-form-item label="权限字符" prop="perms">
            <el-input
              clearable
              v-model="form.perms"
              placeholder="请输入权限字符"
              :maxlength="100"
              :disabled="
                form.menuType == 'M' ||
                (form.menuType == 'C' && form.openType === 2)
              "
            />
          </el-form-item>
          <el-form-item label="显示排序" prop="orderNum">
            <el-input-number
              :min="0"
              :max="99999"
              v-model="form.orderNum"
              placeholder="请输入显示排序"
              controls-position="right"
              class="ele-fluid"
            />
          </el-form-item>
          <el-form-item label="显示状态">
            <dict-data
              code="sys_show_hide"
              type="radio"
              v-model="form.visible"
              :disabled="form.menuType == 'F'"
            />
            <ele-tooltip
              content="选择不展示只注册路由不展示在侧边栏, 比如添加页面应该选择不展示"
              :popper-style="{ maxWidth: '240px' }"
            >
              <el-icon :size="15" style="margin-left: 16px; cursor: help">
                <QuestionCircleOutlined style="opacity: 0.6" />
              </el-icon>
            </ele-tooltip>
          </el-form-item>
          <el-form-item label="是否缓存" prop="isCache">
            <el-radio-group
              v-model="form.isCache"
              :disabled="
                form.menuType == 'M' ||
                form.menuType == 'F' ||
                form.openType === 2
              "
            >
              <el-radio value="0" label="缓存" />
              <el-radio value="1" label="不缓存" />
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="loading" @click="save">
        保存
      </el-button>
    </template>
  </ele-modal>
</template>

<script setup>
  import { ref, computed, watch } from 'vue';
  import { EleMessage, isExternalLink } from 'ele-admin-plus';
  import { QuestionCircleOutlined } from '@/components/icons';
  import { useFormData } from '@/utils/use-form-data';
  import IconSelect from '@/components/IconSelect/index.vue';
  import MenuSelect from './menu-select.vue';
  import { addMenu, updateMenu } from '@/api/system/menu';

  const props = defineProps({
    /** 修改回显的数据 */
    data: Object,
    /** 上级菜单id */
    parentId: Number
  });

  const emit = defineEmits(['done']);

  /** 弹窗是否打开 */
  const visible = defineModel({ type: Boolean });

  /** 是否是修改 */
  const isUpdate = ref(false);

  /** 提交状态 */
  const loading = ref(false);

  /** 表单实例 */
  const formRef = ref(null);

  /** 表单数据 */
  const [form, resetFields, assignFields] = useFormData({
    menuId: void 0,
    parentId: void 0,
    menuName: '',
    menuType: 'M',
    openType: 0,
    icon: '',
    path: '',
    component: '',
    query: '',
    status: '0',
    perms: '',
    orderNum: void 0,
    visible: '0',
    isCache: '0'
  });

  /** 表单验证规则 */
  const rules = computed(() => {
    const obj = {
      menuName: [
        {
          required: true,
          type: 'string',
          message: '请输入菜单名称',
          trigger: 'blur'
        }
      ],
      orderNum: [
        {
          required: true,
          type: 'number',
          message: '请输入排序号',
          trigger: 'blur'
        }
      ],
      query: [
        {
          type: 'string',
          validator: (_rule, value, callback) => {
            if (value) {
              const msg = '请输入正确的参数格式';
              try {
                const obj = JSON.parse(value);
                if (obj == null || typeof obj !== 'object') {
                  return callback(msg);
                }
              } catch (_e) {
                return callback(msg);
              }
            }
            callback();
          },
          trigger: 'blur'
        }
      ]
    };
    if (form.menuType !== 'F') {
      obj.path = [
        {
          required: true,
          type: 'string',
          message: form.openType === 2 ? '请输入外链地址' : '请输入路由地址',
          trigger: 'blur'
        },
        {
          type: 'string',
          validator: (_rule, value, callback) => {
            if (value) {
              if (form.openType === 2) {
                if (!isExternalLink(value)) {
                  callback('请输入正确的链接地址');
                  return;
                }
              } else {
                if (value === '/') {
                  callback('路由地址不能为 /');
                  return;
                }
                /* if (!value.startsWith('/')) {
                  callback('路由地址需要以 / 开头');
                  return;
                } */
              }
            }
            callback();
          },
          trigger: 'blur'
        }
      ];
      if (form.menuType === 'C' && form.openType !== 2) {
        obj.component = [
          {
            required: true,
            type: 'string',
            message: form.openType === 1 ? '请输入内嵌地址' : '请输入组件路径',
            trigger: 'blur'
          },
          {
            type: 'string',
            validator: (_rule, value, callback) => {
              if (value) {
                if (form.openType === 1) {
                  if (!isExternalLink(value)) {
                    callback('请输入正确的链接地址');
                    return;
                  }
                }
              }
              callback();
            },
            trigger: 'blur'
          }
        ];
      }
    }
    return obj;
  });

  /** 关闭弹窗 */
  const handleCancel = () => {
    visible.value = false;
  };

  /** 保存编辑 */
  const save = () => {
    formRef.value?.validate?.((valid) => {
      if (!valid) {
        return;
      }
      loading.value = true;
      const saveOrUpdate = isUpdate.value ? updateMenu : addMenu;
      saveOrUpdate({ ...form, isFrame: form.openType == 2 ? '0' : '1' })
        .then((msg) => {
          loading.value = false;
          EleMessage.success({ message: msg, plain: true });
          handleCancel();
          emit('done');
        })
        .catch((e) => {
          loading.value = false;
          EleMessage.error({ message: e.message, plain: true });
        });
    });
  };

  /** 菜单类型选择改变 */
  const handleMenuTypeChange = () => {
    if (form.menuType == 'M') {
      form.perms = '';
      form.openType = 0;
      form.component = '';
      form.isCache = '0';
      form.query = '';
    } else if (form.menuType == 'F') {
      form.openType = 0;
      form.icon = '';
      form.path = '';
      form.component = '';
      form.visible = '0';
      form.isCache = '0';
      form.query = '';
    }
    formRef.value?.clearValidate?.();
  };

  /** openType选择改变 */
  const handleOpenTypeChange = () => {
    if (form.openType === 2) {
      form.component = '';
      form.perms = '';
      form.isCache = '0';
      form.query = '';
    } else if (form.openType === 1) {
      form.query = '';
    }
    formRef.value?.clearValidate?.();
  };

  /** 监听弹窗打开 */
  watch(visible, () => {
    if (visible.value) {
      if (props.data) {
        const isExternal = props.data.isFrame == '0';
        const isInner = isExternalLink(props.data.component);
        assignFields({
          ...props.data,
          menuType: isExternal ? 'C' : props.data.menuType,
          openType: isExternal ? 2 : isInner ? 1 : 0
        });
        isUpdate.value = true;
      } else {
        resetFields();
        form.parentId = props.parentId;
        isUpdate.value = false;
      }
    }
  });
</script>
