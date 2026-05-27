<template>
  <ele-page>
    <ele-card :loading="loading">
      <template #header>
        <div class="header-toolbar">
          <span class="title">团队管理</span>
          <div class="toolbar-right">
            <el-button type="primary" size="small" @click="openCreate">
              <el-icon><Plus /></el-icon>
              新建团队
            </el-button>
            <el-button size="small" @click="openJoin">
              <el-icon><ArrowRight /></el-icon>
              加入团队
            </el-button>
          </div>
        </div>
      </template>

      <!-- 团队列表 -->
      <el-row :gutter="16" v-if="!detailVisible">
        <el-col
          v-for="team in teams"
          :key="team.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
        >
          <div class="team-card" @click="openDetail(team)">
            <div class="team-header">
              <div class="team-avatar">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="team-meta">
                <div class="team-name">{{ team.name }}</div>
                <div class="team-visibility">
                  <el-tag :type="team.visibility === 'public' ? 'success' : 'warning'" size="small">
                    {{ team.visibility === 'public' ? '公开' : '私有' }}
                  </el-tag>
                </div>
              </div>
            </div>
            <div class="team-desc" v-if="team.description">{{ team.description }}</div>
            <div class="team-stats">
              <div class="team-stat">
                <el-icon><User /></el-icon>
                <span>{{ team.memberCount ?? 0 }} 人</span>
              </div>
              <div class="team-stat">
                <el-icon><Clock /></el-icon>
                <span>{{ formatTotal(team.totalSeconds) }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && teams.length === 0 && !detailVisible" description="暂无团队" />

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="!detailVisible && total > 0">
        <el-pagination
          v-model:current-page="pager.page"
          v-model:page-size="pager.limit"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchTeams"
          @current-change="fetchTeams"
        />
      </div>

      <!-- 团队详情 -->
      <div v-if="detailVisible && selectedTeam" class="team-detail">
        <div class="detail-header">
          <el-button link @click="detailVisible = false">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <div class="detail-title">{{ selectedTeam.name }}</div>
          <div class="detail-actions">
            <el-button size="small" @click="openEditTeam(selectedTeam)" v-if="isOwnerOrAdmin(selectedTeam)">
              编辑
            </el-button>
            <el-popconfirm title="确定离开该团队吗?" @confirm="handleLeave(selectedTeam)">
              <template #reference>
                <el-button size="small" type="danger">离开团队</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>

        <el-row :gutter="16" style="margin-bottom: 16px">
          <el-col :xs="12" :sm="8">
            <div class="stat-card">
              <div class="stat-label">成员数</div>
              <div class="stat-value">{{ members.length }}</div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="8">
            <div class="stat-card">
              <div class="stat-label">邀请码</div>
              <div class="stat-value" style="font-size: 16px; word-break: break-all">
                {{ selectedTeam.inviteCode || '-' }}
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="8">
            <div class="stat-card">
              <div class="stat-label">创建时间</div>
              <div class="stat-value" style="font-size: 14px">{{ selectedTeam.createTime || '-' }}</div>
            </div>
          </el-col>
        </el-row>

        <el-divider content-position="left">
          <span class="divider-title">成员列表</span>
          <el-button type="primary" link size="small" @click="openAddMember" v-if="isOwnerOrAdmin(selectedTeam)">
            + 添加成员
          </el-button>
        </el-divider>

        <ele-table :loading="memberLoading" :data="members" :columns="memberColumns" stripe>
          <template #role="{ row }">
            <el-select
              v-if="isOwnerOrAdmin(selectedTeam) && row.role !== 'owner'"
              v-model="row.role"
              size="small"
              style="width: 100px"
              @change="handleRoleChange(row)"
            >
              <el-option value="admin" label="管理员" />
              <el-option value="member" label="成员" />
            </el-select>
            <el-tag v-else :type="row.role === 'owner' ? 'primary' : row.role === 'admin' ? 'warning' : ''" size="small">
              {{ row.role === 'owner' ? '所有者' : row.role === 'admin' ? '管理员' : '成员' }}
            </el-tag>
          </template>
          <template #action="{ row }">
            <el-button
              v-if="isOwnerOrAdmin(selectedTeam) && row.role !== 'owner'"
              type="danger"
              link
              size="small"
              @click="handleRemoveMember(row)"
            >
              移除
            </el-button>
          </template>
        </ele-table>

        <el-divider content-position="left">
          <span class="divider-title">团队排行榜</span>
        </el-divider>

        <ele-table :loading="leaderLoading" :data="leaderboard" :columns="leaderColumns" stripe>
          <template #rank="{ row, $index }">
            <span class="rank-badge" :class="rankClass($index)">{{ row.rank ?? $index + 1 }}</span>
          </template>
        </ele-table>
      </div>
    </ele-card>

    <!-- 新建/编辑团队弹窗 -->
    <el-dialog
      v-model="editVisible"
      :title="editRow?.id ? '编辑团队' : '新建团队'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form ref="teamFormRef" :model="teamForm" :rules="teamRules" label-width="80px">
        <el-form-item label="团队名称" prop="name">
          <el-input v-model="teamForm.name" placeholder="请输入团队名称" clearable />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="teamForm.description" type="textarea" :rows="3" placeholder="请输入团队描述" />
        </el-form-item>
        <el-form-item label="可见性" prop="visibility">
          <el-radio-group v-model="teamForm.visibility">
            <el-radio value="public">公开</el-radio>
            <el-radio value="private">私有</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveTeam">确定</el-button>
      </template>
    </el-dialog>

    <!-- 加入团队弹窗 -->
    <el-dialog v-model="joinVisible" title="加入团队" width="400px">
      <el-form-item label="邀请码">
        <el-input v-model="joinCode" placeholder="请输入团队邀请码" clearable />
      </el-form-item>
      <template #footer>
        <el-button @click="joinVisible = false">取消</el-button>
        <el-button type="primary" :loading="joining" @click="handleJoin">加入</el-button>
      </template>
    </el-dialog>

    <!-- 添加成员弹窗 -->
    <el-dialog v-model="addMemberVisible" title="添加成员" width="440px">
      <el-form-item label="用户">
        <el-select
          v-model="addMemberUserId"
          placeholder="选择用户"
          filterable
          style="width: 100%"
        >
          <el-option
            v-for="u in allUsers"
            :key="u.userId"
            :value="u.userId"
            :label="u.username || u.displayName"
          />
        </el-select>
      </el-form-item>
      <template #footer>
        <el-button @click="addMemberVisible = false">取消</el-button>
        <el-button type="primary" :loading="adding" @click="handleAddMember">添加</el-button>
      </template>
    </el-dialog>
  </ele-page>
</template>

<script lang="ts" setup>
  import { ref, reactive, onMounted } from 'vue';
  import { Plus, ArrowRight, ArrowLeft, User, Clock, UserFilled } from '@element-plus/icons-vue';
  import { EleMessage } from 'ele-admin-plus';
  import type { FormInstance, FormRules } from 'element-plus';
  import {
    getTeams,
    getTeam,
    createTeam,
    updateTeam,
    deleteTeam,
    getTeamMembers,
    addTeamMember,
    removeTeamMember,
    updateTeamMemberRole,
    joinTeam,
    leaveTeam,
    getTeamLeaderboard,
    listWakapiUsers,
    type Team,
    type TeamMember
  } from '@/api/wakapi';

  defineOptions({ name: 'WakapiTeam' });

  const loading = ref(false);
  const saving = ref(false);
  const joining = ref(false);
  const adding = ref(false);
  const memberLoading = ref(false);
  const leaderLoading = ref(false);

  const teams = ref<Team[]>([]);
  const total = ref(0);
  const pager = reactive({ page: 1, limit: 12 });

  const detailVisible = ref(false);
  const selectedTeam = ref<Team | null>(null);
  const members = ref<TeamMember[]>([]);
  const leaderboard = ref<any[]>([]);
  const allUsers = ref<any[]>([]);

  const editVisible = ref(false);
  const editRow = ref<Team | null>(null);
  const teamForm = reactive({ name: '', description: '', visibility: 'public' as const });
  const teamFormRef = ref<FormInstance>();
  const teamRules: FormRules = {
    name: [{ required: true, message: '请输入团队名称', trigger: 'blur' }],
    visibility: [{ required: true, message: '请选择可见性', trigger: 'change' }]
  };

  const joinVisible = ref(false);
  const joinCode = ref('');

  const addMemberVisible = ref(false);
  const addMemberUserId = ref('');

  const memberColumns = [
    { columnKey: 'user', label: '用户', minWidth: 140, slot: true },
    { columnKey: 'role', label: '角色', width: 130, slot: true },
    { columnKey: 'joinedAt', label: '加入时间', width: 170, prop: 'joinedAt' },
    { columnKey: 'action', label: '操作', width: 80, slot: true, align: 'center' }
  ];

  const leaderColumns = [
    { columnKey: 'rank', label: '排名', width: 80, slot: true, align: 'center' },
    { label: '用户', minWidth: 140, slot: true },
    { label: '编码时长', width: 140, prop: 'text' },
    { label: '占比', width: 120, formatter: (row: any) => row.percent?.toFixed(1) + '%' }
  ];

  const formatTotal = (seconds?: number): string => {
    if (!seconds) return '-';
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    if (h > 0) return `${h}h ${m}m`;
    return `${m}m`;
  };

  const rankClass = (idx: number): string => {
    if (idx === 0) return 'gold';
    if (idx === 1) return 'silver';
    if (idx === 2) return 'bronze';
    return '';
  };

  const isOwnerOrAdmin = (team: Team): boolean => {
    return true;
  };

  const fetchTeams = async () => {
    loading.value = true;
    try {
      const res = await getTeams({ page: pager.page, limit: pager.limit });
      teams.value = res?.list ?? [];
      total.value = res?.count ?? 0;
    } catch {
      teams.value = [];
      total.value = 0;
    } finally {
      loading.value = false;
    }
  };

  const openCreate = () => {
    editRow.value = null;
    teamForm.name = '';
    teamForm.description = '';
    teamForm.visibility = 'public';
    editVisible.value = true;
    teamFormRef.value?.clearValidate();
  };

  const openEditTeam = (team: Team) => {
    editRow.value = team;
    teamForm.name = team.name;
    teamForm.description = team.description ?? '';
    teamForm.visibility = team.visibility ?? 'public';
    editVisible.value = true;
    teamFormRef.value?.clearValidate();
  };

  const saveTeam = async () => {
    const valid = await teamFormRef.value?.validate().catch(() => false);
    if (!valid) return;
    saving.value = true;
    try {
      if (editRow.value?.id) {
        await updateTeam({ ...teamForm, id: editRow.value.id });
        EleMessage.success('修改成功');
      } else {
        await createTeam(teamForm);
        EleMessage.success('创建成功');
      }
      editVisible.value = false;
      fetchTeams();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '保存失败');
    } finally {
      saving.value = false;
    }
  };

  const openDetail = async (team: Team) => {
    selectedTeam.value = team;
    detailVisible.value = true;
    await Promise.all([fetchMembers(team.id!), fetchLeaderboard(team.id!)]);
  };

  const fetchMembers = async (teamId: number) => {
    memberLoading.value = true;
    try {
      members.value = await getTeamMembers(teamId);
    } catch {
      members.value = [];
    } finally {
      memberLoading.value = false;
    }
  };

  const fetchLeaderboard = async (teamId: number) => {
    leaderLoading.value = true;
    try {
      const items = await getTeamLeaderboard(teamId, { limit: 20 });
      leaderboard.value = (items ?? []).map((l, idx) => ({ ...l, rank: idx + 1 }));
    } catch {
      leaderboard.value = [];
    } finally {
      leaderLoading.value = false;
    }
  };

  const openJoin = () => {
    joinCode.value = '';
    joinVisible.value = true;
  };

  const handleJoin = async () => {
    if (!joinCode.value.trim()) {
      EleMessage.warning('请输入邀请码');
      return;
    }
    joining.value = true;
    try {
      await joinTeam(joinCode.value.trim());
      EleMessage.success('加入成功');
      joinVisible.value = false;
      fetchTeams();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '加入失败');
    } finally {
      joining.value = false;
    }
  };

  const handleLeave = async (team: Team) => {
    try {
      await leaveTeam(team.id!);
      EleMessage.success('已离开团队');
      detailVisible.value = false;
      fetchTeams();
    } catch (e) {
      EleMessage.error((e as Error).message ?? '离开失败');
    }
  };

  const openAddMember = async () => {
    if (allUsers.value.length === 0) {
      try {
        allUsers.value = await listWakapiUsers();
      } catch {
        allUsers.value = [];
      }
    }
    addMemberUserId.value = '';
    addMemberVisible.value = true;
  };

  const handleAddMember = async () => {
    if (!addMemberUserId.value || !selectedTeam.value?.id) return;
    adding.value = true;
    try {
      await addTeamMember(selectedTeam.value.id, addMemberUserId.value);
      EleMessage.success('添加成功');
      addMemberVisible.value = false;
      fetchMembers(selectedTeam.value.id);
    } catch (e) {
      EleMessage.error((e as Error).message ?? '添加失败');
    } finally {
      adding.value = false;
    }
  };

  const handleRemoveMember = async (member: TeamMember) => {
    if (!selectedTeam.value?.id) return;
    try {
      await removeTeamMember(selectedTeam.value.id, member.userId);
      EleMessage.success('已移除');
      fetchMembers(selectedTeam.value.id);
    } catch (e) {
      EleMessage.error((e as Error).message ?? '移除失败');
    }
  };

  const handleRoleChange = async (member: TeamMember) => {
    if (!selectedTeam.value?.id) return;
    try {
      await updateTeamMemberRole(selectedTeam.value.id, member.userId, member.role);
      EleMessage.success('更新成功');
    } catch (e) {
      EleMessage.error((e as Error).message ?? '更新失败');
      fetchMembers(selectedTeam.value.id);
    }
  };

  onMounted(() => {
    fetchTeams();
  });
</script>

<style lang="scss" scoped>
  .header-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    .title { font-size: 15px; font-weight: 500; }
    .toolbar-right { display: flex; gap: 8px; align-items: center; }
  }

  .team-card {
    background: var(--el-fill-color-blank);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: border-color 0.2s, box-shadow 0.2s;
    &:hover {
      border-color: var(--el-color-primary);
      box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
    }
    .team-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 10px;
      .team-avatar {
        width: 40px;
        height: 40px;
        border-radius: 8px;
        background: var(--el-color-primary-light-8);
        color: var(--el-color-primary);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        flex-shrink: 0;
      }
      .team-meta {
        flex: 1;
        min-width: 0;
        .team-name {
          font-size: 14px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        .team-visibility { margin-top: 4px; }
      }
    }
    .team-desc {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    .team-stats {
      display: flex;
      gap: 16px;
      .team-stat {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: var(--el-text-color-secondary);
        .el-icon { font-size: 14px; }
      }
    }
  }

  .team-detail {
    .detail-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;
      .detail-title {
        flex: 1;
        font-size: 16px;
        font-weight: 600;
      }
      .detail-actions { display: flex; gap: 8px; }
    }
  }

  .stat-card {
    padding: 16px;
    background: var(--el-fill-color-light);
    border-radius: 8px;
    text-align: center;
    .stat-label { font-size: 12px; color: var(--el-text-color-secondary); margin-bottom: 6px; }
    .stat-value { font-size: 22px; font-weight: 600; color: var(--el-text-color-primary); }
  }

  .divider-title {
    font-size: 13px;
    font-weight: 500;
    color: var(--el-text-color-secondary);
  }

  .pagination-wrap {
    display: flex;
    justify-content: center;
    margin-top: 16px;
  }

  .rank-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    font-size: 12px;
    font-weight: 600;
    background: var(--el-fill-color-light);
    color: var(--el-text-color-regular);
    &.gold { background: #fdf6ec; color: #b8860b; }
    &.silver { background: #f5f5f5; color: #696969; }
    &.bronze { background: #fdf6ec; color: #a0522d; }
  }
</style>
