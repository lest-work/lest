<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">告警管理</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <!-- 告警规则 -->
        <el-tab-pane label="告警规则" name="rules">
          <div class="tab-toolbar">
            <el-select v-model="ruleStatus" placeholder="状态筛选" clearable size="small" style="width: 120px">
              <el-option :value="true" label="已启用" />
              <el-option :value="false" label="已禁用" />
            </el-select>
            <el-button type="primary" size="small" @click="openRuleEdit()">
              <el-icon><Plus /></el-icon>
              新建规则
            </el-button>
          </div>

          <ele-table
            :loading="ruleLoading"
            :data="ruleData"
            :columns="ruleColumns"
            :pagination="{ show: true, total: ruleTotal, modelValue: rulePager, onUpdate: fetchRules }"
            row-key="id"
            stripe
          >
            <template #enabled="{ row }">
              <el-switch
                :model-value="row.enabled"
                @change="handleToggleRule(row)"
              />
            </template>
            <template #type="{ row }">
              <el-tag :type="alertTypeTag(row.type)" size="small">
                {{ alertTypeLabel(row.type) }}
              </el-tag>
            </template>
            <template #condition="{ row }">
              {{ formatCondition(row) }}
            </template>
            <template #action="{ row }">
              <el-button type="primary" link @click="openRuleEdit(row)">编辑</el-button>
              <el-popconfirm title="确定要删除此规则吗?" @confirm="handleDeleteRule(row)">
                <template #reference>
                  <el-button type="danger" link>删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </ele-table>
        </el-tab-pane>

        <!-- 告警历史 -->
        <el-tab-pane label="告警历史" name="history">
          <div class="tab-toolbar">
            <el-select v-model="historyStatus" placeholder="状态筛选" clearable size="small" style="width: 130px">
              <el-option value="triggered" label="已触发" />
              <el-option value="acknowledged" label="已确认" />
              <el-option value="resolved" label="已解决" />
              <el-option value="ignored" label="已忽略" />
            </el-select>
            <el-button size="small" @click="fetchHistory">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <ele-table
            :loading="historyLoading"
            :data="historyData"
            :columns="historyColumns"
            :pagination="{ show: true, total: historyTotal, modelValue: historyPager, onUpdate: fetchHistory }"
            row-key="id"
            stripe
          >
            <template #status="{ row }">
              <el-tag :type="statusTag(row.status)" size="small">
                {{ statusLabel(row.status) }}
              </el-tag>
            </template>
            <template #action="{ row }">
              <el-button
                v-if="row.status === 'triggered'"
                type="primary"
                link
                size="small"
                @click="handleAcknowledge(row)"
              >
                确认
              </el-button>
              <el-button
                v-if="row.status === 'acknowledged'"
                type="success"
                link
                size="small"
                @click="handleResolve(row)"
              >
                解决
              </el-button>
              <el-button
                v-if="row.status !== 'resolved'"
                type="warning"
                link
                size="small"
                @click="handleIgnore(row)"
              >
                忽略
              </el-button>
            </template>
          </ele-table>
        </el-tab-pane>
      </el-tabs>
    </ele-card>

    <!-- 编辑规则弹窗 -->
    <el-dialog
      v-model="ruleEditVisible"
      :title="ruleEditRow?.id ? '编辑规则' : '新建规则'"
      width="520px"
      :close-on-click-modal="false"
    >
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="ruleFormRules" label-width="90px">
        <el-form-item label="规则名称" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入规则名称" clearable />
        </el-form-item>
        <el-form-item label="规则类型" prop="type">
          <el-select v-model="ruleForm.type" placeholder="请选择规则类型" style="width: 100%">
            <el-option value="silence" label="沉默提醒" />
            <el-option value="coding_time_low" label="编码时长过低" />
            <el-option value="coding_time_high" label="编码时长异常高" />
            <el-option value="inactive" label="不活跃提醒" />
            <el-option value="project_change" label="项目变更提醒" />
          </el-select>
        </el-form-item>
        <el-form-item label="条件">
          <div class="condition-row">
            <el-select v-model="ruleForm.condition.operator" style="width: 90px">
              <el-option value="lt" label="小于" />
              <el-option value="gt" label="大于" />
              <el-option value="eq" label="等于" />
              <el-option value="lte" label="小于等于" />
              <el-option value="gte" label="大于等于" />
            </el-select>
            <el-input-number v-model="ruleForm.condition.value" :min="0" style="width: 100px" />
            <el-select v-model="ruleForm.condition.unit" style="width: 90px">
              <el-option value="hours" label="小时" />
              <el-option value="days" label="天" />
              <el-option value="count" label="次数" />
            </el-select>
          </div>
        </el-form-item>
        <el-form-item label="通知渠道">
          <el-checkbox-group v-model="ruleForm.notifyChannels">
            <el-checkbox value="email">邮件</el-checkbox>
            <el-checkbox value="webhook">Webhook</el-checkbox>
            <el-checkbox value="in_app">站内通知</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="ruleForm.description" type="textarea" :rows="2" placeholder="请输入规则描述" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="ruleForm.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ruleEditVisible = false">取消</el-button>
        <el-button type="primary" :loading="ruleSaving" @click="saveRule">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted } from 'vue';
  import { Plus, Refresh } from '@element-plus/icons-vue';
  import { EleMessage } from 'ele-admin-plus';
  import type { FormInstance, FormRules } from 'element-plus';
  import {
    pageAlertRules,
    createAlertRule,
    updateAlertRule,
    deleteAlertRule,
    toggleAlertRule,
    pageAlertHistory,
    acknowledgeAlert,
    resolveAlert,
    ignoreAlert,
    type AlertRule,
    type AlertHistory,
    type AlertRuleType
  } from '@/api/wakapi';

  defineOptions({ name: 'WakapiAlert' });

  const loading = ref(false);
  const activeTab = ref('rules');

  /* ---- 规则管理 ---- */
  const ruleLoading = ref(false);
  const ruleData = ref<AlertRule[]>([]);
  const ruleTotal = ref(0);
  const ruleStatus = ref<boolean | ''>('');
  const rulePager = reactive({ page: 1, limit: 20 });

  const ruleEditVisible = ref(false);
  const ruleEditRow = ref<AlertRule | null>(null);
  const ruleSaving = ref(false);
  const ruleFormRef = ref<FormInstance>();

  const ruleForm = reactive({
    name: '',
    type: 'silence' as AlertRuleType,
    condition: { operator: 'lt' as const, unit: 'hours' as const, value: 2 },
    threshold: 0,
    notifyChannels: ['in_app'] as string[],
    description: '',
    enabled: true
  });

  const ruleFormRules: FormRules = {
    name: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
    type: [{ required: true, message: '请选择规则类型', trigger: 'change' }]
  };

  const ruleColumns = [
    { columnKey: 'name', label: '规则名称', minWidth: 160, prop: 'name' },
    { columnKey: 'type', label: '类型', width: 130, slot: true },
    { columnKey: 'condition', label: '条件', minWidth: 180, slot: true },
    { columnKey: 'enabled', label: '状态', width: 80, slot: true, align: 'center' },
    { columnKey: 'triggerCount', label: '触发次数', width: 100, prop: 'triggerCount', formatter: (r: AlertRule) => r.triggerCount ?? 0 },
    { columnKey: 'lastTriggered', label: '最近触发', width: 170, prop: 'lastTriggered' },
    { columnKey: 'action', label: '操作', width: 120, slot: true, align: 'center' }
  ];

  const alertTypeLabel = (type: string): string => {
    const map: Record<string, string> = {
      silence: '沉默提醒',
      coding_time_low: '时长过低',
      coding_time_high: '时长异常高',
      inactive: '不活跃提醒',
      project_change: '项目变更'
    };
    return map[type] ?? type;
  };

  const alertTypeTag = (type: string): string => {
    const map: Record<string, string> = {
      silence: 'info',
      coding_time_low: 'warning',
      coding_time_high: 'danger',
      inactive: '',
      project_change: 'success'
    };
    return map[type] ?? 'info';
  };

  const formatCondition = (row: AlertRule): string => {
    const op = { lt: '<', gt: '>', eq: '=', lte: '<=', gte: '>=' }[row.condition?.operator ?? 'lt'];
    const unit = { hours: '小时', days: '天', count: '次' }[row.condition?.unit ?? 'hours'];
    return `${op} ${row.condition?.value ?? 0} ${unit}`;
  };

  /* ---- 历史管理 ---- */
  const historyLoading = ref(false);
  const historyData = ref<AlertHistory[]>([]);
  const historyTotal = ref(0);
  const historyStatus = ref<string>('');
  const historyPager = reactive({ page: 1, limit: 20 });

  const historyColumns = [
    { columnKey: 'ruleName', label: '规则名称', minWidth: 160, prop: 'ruleName' },
    { columnKey: 'username', label: '触发用户', width: 120, prop: 'username' },
    { columnKey: 'status', label: '状态', width: 90, slot: true },
    { columnKey: 'triggeredAt', label: '触发时间', width: 170, prop: 'triggeredAt' },
    { columnKey: 'message', label: '消息', minWidth: 200, prop: 'message', showOverflowTooltip: true },
    { columnKey: 'action', label: '操作', width: 160, slot: true, align: 'center' }
  ];

  const statusLabel = (s: string): string => {
    const map: Record<string, string> = {
      triggered: '已触发',
      acknowledged: '已确认',
      resolved: '已解决',
      ignored: '已忽略'
    };
    return map[s] ?? s;
  };

  const statusTag = (s: string): string => {
    const map: Record<string, string> = {
      triggered: 'danger',
      acknowledged: 'warning',
      resolved: 'success',
      ignored: 'info'
    };
    return map[s] ?? 'info';
  };

  /* ---- 操作 ---- */
  const fetchRules = async () => {
    ruleLoading.value = true;
    try {
      const params: any = { page: rulePager.page, limit: rulePager.limit };
      if (ruleStatus.value !== '') params.enabled = ruleStatus.value;
      const res = await pageAlertRules(params);
      ruleData.value = res?.list ?? [];
      ruleTotal.value = res?.count ?? 0;
    } catch {
      ruleData.value = [];
      ruleTotal.value = 0;
    } finally {
      ruleLoading.value = false;
    }
  };

  const fetchHistory = async () => {
    historyLoading.value = true;
    try {
      const params: any = { page: historyPager.page, limit: historyPager.limit };
      if (historyStatus.value) params.status = historyStatus.value;
      const res = await pageAlertHistory(params);
      historyData.value = res?.list ?? [];
      historyTotal.value = res?.count ?? 0;
    } catch {
      historyData.value = [];
      historyTotal.value = 0;
    } finally {
      historyLoading.value = false;
    }
  };

  const openRuleEdit = (row?: AlertRule) => {
    ruleEditRow.value = row ?? null;
    if (row) {
      ruleForm.name = row.name;
      ruleForm.type = row.type;
      ruleForm.condition = row.condition ?? { operator: 'lt', unit: 'hours', value: 2 };
      ruleForm.notifyChannels = row.notifyChannels ?? ['in_app'];
      ruleForm.description = row.description ?? '';
      ruleForm.enabled = row.enabled;
    } else {
      ruleForm.name = '';
      ruleForm.type = 'silence';
      ruleForm.condition = { operator: 'lt', unit: 'hours', value: 2 };
      ruleForm.notifyChannels = ['in_app'];
      ruleForm.description = '';
      ruleForm.enabled = true;
    }
    ruleEditVisible.value = true;
    ruleFormRef.value?.clearValidate();
  };

  const saveRule = async () => {
    const valid = await ruleFormRef.value?.validate().catch(() => false);
    if (!valid) return;
    ruleSaving.value = true;
    try {
      const payload: Partial<AlertRule> = { ...ruleForm };
      if (ruleEditRow.value?.id) {
        await updateAlertRule({ ...payload, id: ruleEditRow.value.id });
        EleMessage.success('修改成功');
      } else {
        await createAlertRule(payload);
        EleMessage.success('创建成功');
      }
      ruleEditVisible.value = false;
      fetchRules();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '保存失败');
    } finally {
      ruleSaving.value = false;
    }
  };

  const handleToggleRule = async (row: AlertRule) => {
    try {
      await toggleAlertRule(row.id!, !row.enabled);
      EleMessage.success(row.enabled ? '已禁用' : '已启用');
      fetchRules();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '操作失败');
    }
  };

  const handleDeleteRule = async (row: AlertRule) => {
    try {
      await deleteAlertRule(row.id!);
      EleMessage.success('删除成功');
      fetchRules();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '删除失败');
    }
  };

  const handleAcknowledge = async (row: AlertHistory) => {
    try {
      await acknowledgeAlert(row.id!);
      EleMessage.success('已确认');
      fetchHistory();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '操作失败');
    }
  };

  const handleResolve = async (row: AlertHistory) => {
    try {
      await resolveAlert(row.id!);
      EleMessage.success('已解决');
      fetchHistory();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '操作失败');
    }
  };

  const handleIgnore = async (row: AlertHistory) => {
    try {
      await ignoreAlert(row.id!);
      EleMessage.success('已忽略');
      fetchHistory();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '操作失败');
    }
  };

  onMounted(() => {
    fetchRules();
    fetchHistory();
  });
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    .title { font-size: 15px; font-weight: 500; }
  }

  .tab-toolbar {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-bottom: 12px;
  }

  .condition-row {
    display: flex;
    gap: 8px;
    align-items: center;
  }
</style>
