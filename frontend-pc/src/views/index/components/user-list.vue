<!-- 小组成员 -->
<template>
  <ele-card
    :header="title"
    :body-style="{ padding: '8px 0px', height: '370px' }"
  >
    <template #extra>
      <more-icon @command="handleCommand" />
    </template>
    <div v-for="item in userList" :key="item.id" class="user-list-item">
      <el-avatar :size="46" :src="item.avatar" style="flex-shrink: 0" />
      <div class="user-list-item-body">
        <div>{{ item.name }}</div>
        <ele-ellipsis type="placeholder" size="sm">
          {{ item.introduction }}
        </ele-ellipsis>
      </div>
      <div style="flex-shrink: 0">
        <el-tag
          v-if="item.status === 0"
          size="small"
          type="success"
          :disable-transitions="true"
        >
          在线
        </el-tag>
        <el-tag v-else size="small" type="danger" :disable-transitions="true">
          离线
        </el-tag>
      </div>
    </div>
  </ele-card>
</template>

<script setup>
  import { ref, onMounted } from 'vue';
  import MoreIcon from './more-icon.vue';
  import { getDashboardMembers } from '@/api/dashboard';

  defineProps({
    title: String
  });

  const emit = defineEmits(['command']);

  /** 小组成员数据 */
  const userList = ref([]);

  /** 查询小组成员 */
  const queryUserList = async () => {
    try {
      const data = await getDashboardMembers();
      userList.value = data.map((u) => ({
        id: u.id,
        name: u.name || u.userName,
        introduction: u.email || '',
        status: u.status,
        avatar: u.avatar || null
      }));
    } catch {
      userList.value = [];
    }
  };

  const handleCommand = (command) => {
    if (command === 'refresh') queryUserList();
    emit('command', command);
  };

  onMounted(queryUserList);
</script>

<style lang="scss" scoped>
  .user-list-item {
    display: flex;
    align-items: center;
    padding: 12px 18px;
    transition: background-color 0.2s;
    cursor: pointer;

    .user-list-item-body {
      flex: 1;
      overflow: hidden;
      padding-left: 12px;
      box-sizing: border-box;
    }

    & + .user-list-item {
      border-top: 1px solid hsla(0, 0%, 60%, 0.2);
    }

    &:hover {
      background: hsla(0, 0%, 60%, 0.08);
    }
  }
</style>
