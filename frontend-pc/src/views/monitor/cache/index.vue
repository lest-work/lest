<template>
  <ele-page>
    <ele-card>
      <template #header>
        <el-icon :size="16" style="vertical-align: -2px; margin-right: 8px">
          <DesktopOutlined />
        </el-icon>
        <span>基本信息</span>
      </template>
      <ele-loading :loading="loading">
        <el-descriptions
          :border="true"
          :column="mobile ? 1 : 3"
          class="detail-table"
        >
          <el-descriptions-item label="Redis版本">
            <div>{{ data.info?.redis_version }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="运行模式">
            <div>{{ data.info?.redis_mode }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="端口">
            <div>{{ data.info?.tcp_port }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="客户端数">
            <div>{{ data.info?.connected_clients }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="运行时间(天)">
            <div>{{ data.info?.uptime_in_days }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="使用内存">
            <div>{{ data.info?.used_memory_human }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="使用CPU">
            <div>{{ data.info?.used_cpu_user_children }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="内存配置">
            <div>{{ data.info?.maxmemory_human }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="AOF是否开启">
            <div>{{ data.info?.aof_enabled }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="RDB是否成功">
            <div>{{ data.info?.rdb_last_bgsave_status }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="Key数量">
            <div>{{ data.dbSize }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="网络入口/出口">
            <div>{{ data.info?.instantaneous_input_kbps }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </ele-loading>
    </ele-card>
    <el-row :gutter="16">
      <el-col :md="12" :sm="24" :xs="24">
        <ele-card>
          <template #header>
            <el-icon :size="16" style="vertical-align: -3px; margin-right: 8px">
              <PieChartOutlined />
            </el-icon>
            <span>命令统计</span>
          </template>
          <v-chart
            ref="commandChartRef"
            :option="commandChartOption"
            style="height: 420px"
          />
        </ele-card>
      </el-col>
      <el-col :md="12" :sm="24" :xs="24">
        <ele-card>
          <template #header>
            <el-icon :size="16" style="vertical-align: -2px; margin-right: 8px">
              <DashboardOutlined />
            </el-icon>
            <span>内存信息</span>
          </template>
          <v-chart
            ref="memoryChartRef"
            :option="memoryChartOption"
            style="height: 420px"
          />
        </ele-card>
      </el-col>
    </el-row>
  </ele-page>
</template>

<script setup>
  import { ref, reactive } from 'vue';
  import { EleMessage } from 'ele-admin-plus';
  import { use } from 'echarts/core';
  import { CanvasRenderer } from 'echarts/renderers';
  import { PieChart as PieCharts, GaugeChart } from 'echarts/charts';
  import { TooltipComponent } from 'echarts/components';
  import VChart from 'vue-echarts';
  import {
    DesktopOutlined,
    PieChartOutlined,
    DashboardOutlined
  } from '@/components/icons';
  import { useMobile } from '@/utils/use-mobile';
  import { useEcharts } from '@/utils/use-echarts';
  import { getCache } from '@/api/monitor/cache';

  defineOptions({ name: 'MonitorCache' });

  use([CanvasRenderer, PieCharts, GaugeChart, TooltipComponent]);

  const { mobile } = useMobile();

  const data = ref({});

  /** 请求状态 */
  const loading = ref(true);

  /** 命令统计图表 */
  const commandChartRef = ref(null);

  /** 内存信息图表 */
  const memoryChartRef = ref(null);

  useEcharts([commandChartRef, memoryChartRef]);

  /** 命令统计图表配置 */
  const commandChartOption = reactive({});

  /** 内存信息图表配置 */
  const memoryChartOption = reactive({});

  /** 请求数据 */
  getCache()
    .then((res) => {
      loading.value = false;
      if (res.info != null && res.commandStats != null) {
        data.value = res;
      } else {
        data.value = JSON.parse(
          '{"commandStats":[{"name":"exists","value":"1"},{"name":"set","value":"18"},{"name":"setex","value":"4"},{"name":"del","value":"3"},{"name":"info","value":"5"},{"name":"ping","value":"1"},{"name":"dbsize","value":"2"},{"name":"get","value":"25"}],"info":{"aof_enabled":"0","redis_mode":"standalone","rdb_last_bgsave_status":"ok","tcp_port":"6379","used_memory_human":"720.15K","used_cpu_user_children":"0.000000","connected_clients":"1","redis_version":"5.0.14.1","maxmemory_human":"0B","uptime_in_days":"0","instantaneous_input_kbps":"0.00"},"dbSize":17}'
        );
      }
      const commandStats = data.value.commandStats || [];
      //
      Object.assign(commandChartOption, {
        tooltip: {
          trigger: 'item',
          confine: true,
          borderWidth: 1
        },
        series: [
          {
            name: '命令',
            type: 'pie',
            radius: ['25%', '70%'],
            center: ['50%', '50%'],
            itemStyle: {
              borderRadius: 8
            },
            label: {
              formatter: '{b}({d}%)'
            },
            data: commandStats.sort((a, b) => a.name.localeCompare(b.name))
          }
        ]
      });
      //
      Object.assign(memoryChartOption, {
        tooltip: {
          trigger: 'item',
          confine: true,
          borderWidth: 1,
          formatter: '{b} <br/>{a} : {c}K'
        },
        series: [
          {
            name: '峰值',
            type: 'gauge',
            min: 0,
            max: 1000,
            progress: {
              show: true,
              roundCap: true
            },
            detail: {
              valueAnimation: true,
              formatter: '{value}K'
            },
            data: [
              {
                value: parseFloat(data.value.info?.used_memory_human || 0),
                name: '内存消耗'
              }
            ]
          }
        ]
      });
    })
    .catch((e) => {
      loading.value = false;
      EleMessage.error({ message: e.message, plain: true });
    });
</script>

<style lang="scss" scoped>
  .detail-table :deep(td.el-descriptions__label.el-descriptions__cell) {
    width: 124px;
    text-align: right;
    font-weight: normal;
  }
</style>
