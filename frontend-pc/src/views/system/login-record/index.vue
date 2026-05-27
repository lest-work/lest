<template>
  <ele-page>
    <login-record-search @search="reload" />
    <ele-card :body-style="{ paddingTop: '8px' }">
      <ele-pro-table
        ref="tableRef"
        row-key="id"
        :columns="columns"
        :datasource="datasource"
        :show-overflow-tooltip="true"
        :highlight-current-row="true"
        cache-key="systemLoginRecordTable"
      >
        <template #toolbar>
          <el-button
            type="primary"
            class="ele-btn-icon"
            :icon="DownloadOutlined"
            @click="exportData"
          >
            导出
          </el-button>
        </template>
        <template #loginType="{ row }">
          <el-tag
            v-if="row.loginType === 0"
            size="small"
            type="success"
            :disable-transitions="true"
          >
            登录成功
          </el-tag>
          <el-tag
            v-else-if="row.loginType === 1"
            size="small"
            type="danger"
            :disable-transitions="true"
          >
            登录失败
          </el-tag>
          <el-tag
            v-else-if="row.loginType === 2"
            size="small"
            type="info"
            :disable-transitions="true"
          >
            退出登录
          </el-tag>
          <el-tag
            v-else-if="row.loginType === 3"
            size="small"
            type="warning"
            :disable-transitions="true"
          >
            刷新TOKEN
          </el-tag>
        </template>
      </ele-pro-table>
    </ele-card>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { EleMessage } from 'ele-admin-plus';
  import type { EleProTable } from 'ele-admin-plus';
  import type {
    DatasourceFunction,
    Columns
  } from 'ele-admin-plus/es/ele-pro-table/types';
  import ExcelJS from 'exceljs';
  import { download } from '@/utils/common';
  import { DownloadOutlined } from '@/components/icons';
  import LoginRecordSearch from './components/login-record-search.vue';
  import {
    pageLoginRecords,
    listLoginRecords
  } from '@/api/system/login-record';
  import type { LoginRecordParam } from '@/api/system/login-record/model';

  defineOptions({ name: 'SystemLoginRecord' });

  /** 表格实例 */
  const tableRef = ref<InstanceType<typeof EleProTable> | null>(null);

  /** 表格列配置 */
  const columns = ref<Columns>([
    {
      type: 'index',
      columnKey: 'index',
      width: 50,
      align: 'center'
    },
    {
      prop: 'username',
      label: '账号',
      sortable: 'custom',
      minWidth: 110
    },
    {
      prop: 'nickname',
      label: '用户昵称',
      sortable: 'custom',
      minWidth: 110
    },
    {
      prop: 'ip',
      label: 'IP地址',
      sortable: 'custom',
      minWidth: 130
    },
    {
      prop: 'os',
      label: '操作系统',
      sortable: 'custom',
      minWidth: 110
    },
    {
      prop: 'browser',
      label: '浏览器',
      sortable: 'custom',
      minWidth: 110
    },
    {
      prop: 'loginType',
      label: '登录类型',
      sortable: 'custom',
      width: 110,
      slot: 'loginType',
      filters: [
        { text: '登录成功', value: '0' },
        { text: '登录失败', value: '1' },
        { text: '退出登录', value: '2' },
        { text: '刷新Token', value: '3' }
      ],
      filterMultiple: false,
      align: 'center',
      formatter: (row: any) =>
        ['登录成功', '登录失败', '退出登录', '刷新Token'][row.loginType]
    },
    {
      prop: 'msg',
      label: '消息',
      sortable: 'custom',
      minWidth: 150,
      align: 'center'
    },
    {
      prop: 'createTime',
      label: '登录时间',
      sortable: 'custom',
      width: 180,
      align: 'center'
    }
  ]);

  /** 表格数据源 */
  const datasource: DatasourceFunction = ({
    pages,
    where,
    orders,
    filters
  }) => {
    return pageLoginRecords({ ...where, ...orders, ...filters, ...pages });
  };

  /** 刷新表格 */
  const reload = (where?: LoginRecordParam) => {
    tableRef.value?.reload?.({ page: 1, where });
  };

  /** 导出数据 */
  const exportData = () => {
    const loading = EleMessage.loading({
      message: '请求中..',
      plain: true
    });
    tableRef.value?.fetch?.(({ where, orders, filters }) => {
      listLoginRecords({ ...where, ...orders, ...filters })
        .then((data) => {
          const workbook = new ExcelJS.Workbook();
          const sheet = workbook.addWorksheet('Sheet1');
          sheet.addRow([
            '账号',
            '用户昵称',
            'IP地址',
            '操作系统',
            '浏览器',
            '登录类型',
            '消息',
            '登录时间'
          ]);
          data.forEach((d: any) => {
            sheet.addRow([
              d.username,
              d.nickname,
              d.ip,
              d.os,
              d.browser,
              ['登录成功', '登录失败', '退出登录', '刷新Token'][d.loginType],
              d.msg,
              d.createTime
            ]);
          });
          [16, 16, 22, 18, 20, 14, 16, 24].forEach((width, index) => {
            sheet.getColumn(index + 1).width = width;
          });
          sheet.eachRow({ includeEmpty: true }, (row, rowIndex) => {
            row.height = 20;
            row.eachCell({ includeEmpty: true }, (cell) => {
              cell.border = {
                top: { style: 'thin' },
                left: { style: 'thin' },
                bottom: { style: 'thin' },
                right: { style: 'thin' }
              };
              cell.alignment = {
                vertical: 'middle',
                horizontal: 'center'
              };
              cell.font = { size: 12, bold: rowIndex === 1 };
            });
          });
          workbook.xlsx.writeBuffer().then((data) => {
            download(data, '登录日志.xlsx');
            loading.close();
          });
        })
        .catch((e) => {
          loading.close();
          EleMessage.error({ message: e.message, plain: true });
        });
    });
  };
</script>
