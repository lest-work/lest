<template>
  <ele-page>
    <!-- 搜索表单 -->
    <menu-search @search="reload" />
    <ele-card :body-style="{ paddingTop: '8px' }">
      <!-- 表格 -->
      <ele-pro-table
        sticky
        ref="tableRef"
        row-key="menuId"
        :columns="columns"
        :datasource="datasource"
        :show-overflow-tooltip="true"
        highlight-current-row
        :export-config="{ fileName: '菜单数据' }"
        :default-expand-all="false"
        :pagination="false"
        cache-key="systemMenuTable"
      >
        <template #toolbar>
          <el-button
            type="primary"
            class="ele-btn-icon"
            :icon="PlusOutlined"
            v-permission="'system:menu:add'"
            @click="openEdit()"
          >
            新建
          </el-button>
          <el-button
            class="ele-btn-icon"
            :icon="ColumnHeightOutlined"
            @click="expandAll"
          >
            展开全部
          </el-button>
          <el-button
            class="ele-btn-icon"
            :icon="VerticalAlignMiddleOutlined"
            @click="foldAll"
          >
            折叠全部
          </el-button>
        </template>
        <template #menuName="{ row }">
          <el-icon
            v-if="row.icon && row.icon != '#'"
            :size="16"
            style="margin-right: 8px; vertical-align: -2px"
          >
            <component :is="row.icon" />
          </el-icon>
          <span>{{ row.menuName }}</span>
        </template>
        <template #menuType="{ row }">
          <el-tag
            v-if="row.isFrame == '0'"
            size="small"
            type="danger"
            :disable-transitions="true"
          >
            外链
          </el-tag>
          <el-tag
            v-else-if="row.menuType === 'M'"
            size="small"
            :disable-transitions="true"
          >
            目录
          </el-tag>
          <el-tag
            v-else-if="row.menuType === 'C'"
            size="small"
            type="success"
            :disable-transitions="true"
          >
            菜单
          </el-tag>
          <el-tag
            v-else-if="row.menuType === 'F'"
            size="small"
            type="info"
            :disable-transitions="true"
          >
            按钮
          </el-tag>
        </template>
        <template #visible="{ row }">
          <dict-data
            code="sys_show_hide"
            type="tag"
            :model-value="row.visible"
          />
        </template>
        <template #action="{ row }">
          <div style="display: inline-flex; align-items: center">
            <el-link
              v-permission="'system:menu:add'"
              type="primary"
              underline="never"
              @click="openEdit(null, row.menuId)"
            >
              添加
            </el-link>
            <el-divider
              v-permission="'system:menu:edit'"
              direction="vertical"
              style="margin: 0 8px"
            />
            <el-link
              v-permission="'system:menu:edit'"
              type="primary"
              underline="never"
              @click="openEdit(row)"
            >
              修改
            </el-link>
            <el-divider
              v-permission="'system:menu:remove'"
              direction="vertical"
              style="margin: 0 8px"
            />
            <el-link
              v-permission="'system:menu:remove'"
              type="danger"
              underline="never"
              @click="remove(row)"
            >
              删除
            </el-link>
          </div>
        </template>
      </ele-pro-table>
    </ele-card>
    <!-- 编辑弹窗 -->
    <menu-edit
      ref="menuEditRef"
      v-model="showEdit"
      :data="current"
      :parent-id="parentId"
      @done="handleMenuEditDone"
    />
  </ele-page>
</template>

<script setup>
  import { ref } from 'vue';
  import { ElMessageBox } from 'element-plus';
  import { EleMessage, toTree } from 'ele-admin-plus';
  import {
    PlusOutlined,
    ColumnHeightOutlined,
    VerticalAlignMiddleOutlined
  } from '@/components/icons';
  import { useDictData } from '@/utils/use-dict-data';
  import MenuSearch from './components/menu-search.vue';
  import MenuEdit from './components/menu-edit.vue';
  import { listMenus, removeMenu } from '@/api/system/menu';

  defineOptions({ name: 'SystemMenu' });

  /** 字典数据 */
  const [visibleDicts] = useDictData(['sys_show_hide']);

  /** 表格实例 */
  const tableRef = ref(null);

  /** 表格列配置 */
  const columns = ref([
    {
      type: 'index',
      columnKey: 'index',
      width: 50,
      align: 'center' /* ,
      fixed: 'left' */
    },
    {
      prop: 'menuName',
      label: '菜单名称',
      slot: 'menuName',
      minWidth: 160
    },
    {
      prop: 'path',
      label: '路由地址',
      minWidth: 110
    },
    {
      prop: 'orderNum',
      label: '排序',
      align: 'center',
      width: 100
    },
    {
      prop: 'visible',
      label: '状态',
      align: 'center',
      slot: 'visible',
      width: 100,
      formatter: (row) =>
        visibleDicts.value.find((d) => d.dictValue == row.visible)?.dictLabel
    },
    {
      prop: 'menuType',
      label: '类型',
      align: 'center',
      slot: 'menuType',
      width: 100,
      formatter: (row) => ({ M: '目录', C: '菜单', F: '按钮' })[row.menuType]
    },
    {
      columnKey: 'action',
      label: '操作',
      width: 180,
      align: 'center',
      slot: 'action',
      hideInPrint: true,
      hideInExport: true
    }
  ]);

  /** 当前编辑数据 */
  const current = ref(null);

  /** 是否显示编辑弹窗 */
  const showEdit = ref(false);

  /** 上级菜单id */
  const parentId = ref();

  /** 菜单编辑弹窗组件 */
  const menuEditRef = ref(null);

  /** 表格数据源 */
  const datasource = async ({ where }) => {
    const data = await listMenus({ ...where });
    return toTree({
      data,
      idField: 'menuId',
      parentIdField: 'parentId'
    });
  };

  /** 刷新表格 */
  const reload = (where) => {
    tableRef.value?.reload?.({ where });
  };

  /** 打开编辑弹窗 */
  const openEdit = (row, id) => {
    current.value = row ?? null;
    parentId.value = id;
    showEdit.value = true;
  };

  /** 刷新菜单下拉选择数据 */
  const reloadMenuSelectData = () => {
    menuEditRef.value?.reloadMenuSelectData?.();
  };

  /** 删除单个 */
  const remove = (row) => {
    ElMessageBox.confirm(
      `是否确认删除名称为“${row.menuName}”的数据项？`,
      '系统提示',
      { type: 'warning', draggable: true }
    )
      .then(() => {
        const loading = EleMessage.loading({
          message: '请求中..',
          plain: true
        });
        removeMenu(row.menuId)
          .then(() => {
            loading.close();
            EleMessage.success({ message: '删除成功', plain: true });
            reload();
            reloadMenuSelectData();
          })
          .catch((e) => {
            loading.close();
            EleMessage.error({ message: e.message, plain: true });
          });
      })
      .catch(() => {});
  };

  /** 展开全部 */
  const expandAll = () => {
    tableRef.value?.toggleRowExpansionAll?.(true);
  };

  /** 折叠全部 */
  const foldAll = () => {
    tableRef.value?.toggleRowExpansionAll?.(false);
  };

  /** 菜单编辑完成事件 */
  const handleMenuEditDone = () => {
    reload();
    reloadMenuSelectData();
  };
</script>
