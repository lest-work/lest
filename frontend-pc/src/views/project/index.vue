<template>
  <ele-page>
    <!-- 顶部搜索栏 / Top search bar -->
    <ele-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="项目名称">
          <el-input
            v-model="query.name"
            clearable
            placeholder="请输入项目名称"
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
            <el-option label="活跃" :value="1" />
            <el-option label="已归档" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="SearchOutlined" @click="handleSearch">查询</el-button>
          <el-button :icon="ReloadOutlined" @click="handleReset">重置</el-button>
          <el-button
            v-permission="['project:project:add']"
            type="primary"
            :icon="PlusOutlined"
            @click="openAddDialog"
          >
            新建项目
          </el-button>
        </el-form-item>
      </el-form>
    </ele-card>

    <!-- 项目卡片网格 / Project card grid -->
    <ele-loading :loading="loading" style="min-height: 200px">
      <div v-if="list.length === 0 && !loading" style="text-align: center; padding: 60px 0; color: #999">
        暂无项目，点击「新建项目」开始
      </div>
      <el-row :gutter="16">
        <el-col
          v-for="item in list"
          :key="item.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          style="margin-bottom: 16px"
        >
          <el-card
            shadow="hover"
            style="cursor: pointer; height: 180px; display: flex; flex-direction: column"
            @click="goDetail(item)"
          >
            <div style="display: flex; justify-content: space-between; align-items: flex-start">
              <div>
                <div style="font-size: 16px; font-weight: 600; margin-bottom: 6px">{{ item.name }}</div>
                <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
                  {{ item.status === 1 ? '活跃' : '已归档' }}
                </el-tag>
                <el-tag type="warning" size="small" style="margin-left: 6px">
                  {{ TEMPLATE_LABEL[item.template ?? 'agile'] }}
                </el-tag>
              </div>
              <el-dropdown @click.stop>
                <el-icon style="cursor: pointer; padding: 4px"><IconElMore /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click.stop="openEditDialog(item)">编辑</el-dropdown-item>
                    <el-dropdown-item
                      v-if="item.status === 1"
                      @click.stop="handleArchive(item)"
                    >
                      归档
                    </el-dropdown-item>
                    <el-dropdown-item
                      v-permission="['project:project:remove']"
                      style="color: #f56c6c"
                      @click.stop="handleDelete(item)"
                    >
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <div
              style="
                flex: 1;
                margin-top: 10px;
                font-size: 13px;
                color: #666;
                overflow: hidden;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
              "
            >
              {{ item.description || '暂无描述' }}
            </div>
            <div style="margin-top: 10px; font-size: 12px; color: #999; display: flex; gap: 12px">
              <span v-if="item.startDate">开始：{{ item.startDate }}</span>
              <span v-if="item.endDate">截止：{{ item.endDate }}</span>
              <span v-if="item.ownerName">负责人：{{ item.ownerName }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 / Pagination -->
      <div v-if="total > 0" style="display: flex; justify-content: flex-end; margin-top: 8px">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next"
          @change="fetchList"
        />
      </div>
    </ele-loading>

    <!-- 新增/编辑弹窗 / Add/Edit dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="formData.id ? '编辑项目' : '新建项目'"
      width="520px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入项目描述"
          />
        </el-form-item>
        <el-form-item label="模板" prop="template">
          <el-radio-group v-model="formData.template">
            <el-radio value="agile">敏捷开发</el-radio>
            <el-radio value="kanban">看板</el-radio>
            <el-radio value="waterfall">瀑布流</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="formData.startDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择开始日期"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="formData.endDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择结束日期"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script setup>
  import { ref, reactive, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessageBox } from 'element-plus';
  import { EleMessage } from 'ele-admin-plus';
  import { SearchOutlined, PlusOutlined, ReloadOutlined } from '@/components/icons';
  import {
    pageProjects,
    addProject,
    updateProject,
    removeProject,
    archiveProject
  } from '@/api/project';

  const router = useRouter();

  /** 模板标签映射 / Template label map */
  const TEMPLATE_LABEL = { agile: '敏捷', kanban: '看板', waterfall: '瀑布' };

  const loading = ref(false);
  const submitLoading = ref(false);
  const list = ref([]);
  const total = ref(0);
  const dialogVisible = ref(false);
  const formRef = ref(null);

  const query = reactive({
    pageNum: 1,
    pageSize: 12,
    name: undefined,
    status: undefined
  });

  const formData = reactive({
    id: undefined,
    name: '',
    description: '',
    template: 'agile',
    startDate: undefined,
    endDate: undefined
  });

  const rules = {
    name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
    template: [{ required: true, message: '请选择模板', trigger: 'change' }]
  };

  async function fetchList() {
    loading.value = true;
    try {
      const res = await pageProjects(query);
      list.value = res.rows ?? [];
      total.value = res.total ?? 0;
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      loading.value = false;
    }
  }

  function handleSearch() {
    query.pageNum = 1;
    fetchList();
  }

  function handleReset() {
    query.name = undefined;
    query.status = undefined;
    query.pageNum = 1;
    fetchList();
  }

  function openAddDialog() {
    Object.assign(formData, {
      id: undefined,
      name: '',
      description: '',
      template: 'agile',
      startDate: undefined,
      endDate: undefined
    });
    dialogVisible.value = true;
  }

  function openEditDialog(item) {
    Object.assign(formData, { ...item });
    dialogVisible.value = true;
  }

  function goDetail(item) {
    router.push(`/project/detail/${item.id}`);
  }

  async function handleSubmit() {
    await formRef.value?.validate();
    submitLoading.value = true;
    try {
      if (formData.id) {
        await updateProject(formData);
        EleMessage.success({ message: '修改成功', plain: true });
      } else {
        await addProject(formData);
        EleMessage.success({ message: '新建成功', plain: true });
      }
      dialogVisible.value = false;
      fetchList();
    } catch (e) {
      EleMessage.error({ message: e.message, plain: true });
    } finally {
      submitLoading.value = false;
    }
  }

  function handleArchive(item) {
    archiveProject(item.id)
      .then(() => {
        EleMessage.success({ message: '已归档', plain: true });
        fetchList();
      })
      .catch((e) => {
        EleMessage.error({ message: e.message, plain: true });
      });
  }

  function handleDelete(item) {
    ElMessageBox.confirm(`确认删除项目「${item.name}」？`, '提示', {
      type: 'warning',
      draggable: true
    })
      .then(() => {
        removeProject(item.id)
          .then(() => {
            EleMessage.success({ message: '删除成功', plain: true });
            fetchList();
          })
          .catch((e) => {
            EleMessage.error({ message: e.message, plain: true });
          });
      })
      .catch(() => {});
  }

  onMounted(fetchList);
</script>
