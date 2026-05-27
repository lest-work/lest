<!-- 搜索表单 -->
<template>
  <ele-card :body-style="{ paddingBottom: '2px' }">
    <el-form label-width="72px" @keyup.enter="search" @submit.prevent="">
      <el-row :gutter="8">
        <el-col :lg="6" :md="12" :sm="12" :xs="24">
          <el-form-item label="用户账号">
            <el-input
              clearable
              v-model.trim="form.username"
              placeholder="请输入"
            />
          </el-form-item>
        </el-col>
        <el-col :lg="6" :md="12" :sm="12" :xs="24">
          <el-form-item label="用户昵称">
            <el-input
              clearable
              v-model.trim="form.nickname"
              placeholder="请输入"
            />
          </el-form-item>
        </el-col>
        <el-col :lg="6" :md="12" :sm="12" :xs="24">
          <el-form-item label="登录类型">
            <el-select clearable v-model="form.loginType" placeholder="请选择" class="ele-fluid">
              <el-option :value="0" label="登录成功" />
              <el-option :value="1" label="登录失败" />
              <el-option :value="2" label="退出登录" />
              <el-option :value="3" label="刷新Token" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :lg="6" :md="12" :sm="12" :xs="24">
          <el-form-item label="登录时间">
            <el-date-picker
              unlink-panels
              type="daterange"
              v-model="dateRange"
              range-separator="-"
              value-format="YYYY-MM-DD"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              class="ele-fluid"
            />
          </el-form-item>
        </el-col>
        <el-col :lg="12" :sm="12" :xs="24">
          <el-form-item label-width="6px">
            <div style="white-space: nowrap">
              <el-button type="primary" @click="search">查询</el-button>
              <el-button @click="reset">重置</el-button>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ele-card>
</template>

<script lang="ts" setup>
  import { ref } from 'vue';
  import { useFormData } from '@/utils/use-form-data';
  import type { LoginRecordParam } from '@/api/system/login-record/model';

  const props = defineProps<{
    /** 默认搜索条件 */
    where?: LoginRecordParam;
  }>();

  const emit = defineEmits<{
    (e: 'search', where?: LoginRecordParam): void;
  }>();

  /** 表单数据 */
  const [form, resetFields] = useFormData<LoginRecordParam>({
    username: '',
    nickname: '',
    loginType: undefined,
    ...(props.where || {})
  });

  /** 日期范围 */
  const dateRange = ref<[string, string] | null>(null);

  /** 搜索 */
  const search = () => {
    const [d1, d2] = dateRange.value || [];
    emit('search', {
      ...form,
      createTimeStart: d1 || '',
      createTimeEnd: d2 || ''
    });
  };

  /**  重置 */
  const reset = () => {
    resetFields();
    dateRange.value = null;
    search();
  };
</script>
