<script setup lang="ts">
import { ref } from 'vue'
import { Card, Avatar, Badge } from '@arco-design/web-vue'
import { IconCalendar } from '@arco-design/web-vue/es/icon'

interface ACItem {
  key: string
  rank: number
  nickname: string
  acCount: number
  avatar: string
}

const listData = ref<ACItem[]>([
  { key: '1', rank: 1, nickname: '徐锦涵', acCount: 15, avatar: '' },
  { key: '2', rank: 2, nickname: '陶子睿', acCount: 14, avatar: '' },
  { key: '3', rank: 3, nickname: '汪子骞', acCount: 9, avatar: '' },
  { key: '4', rank: 4, nickname: '杨晓和', acCount: 8, avatar: '' },
  { key: '5', rank: 5, nickname: '吴博', acCount: 8, avatar: '' },
  { key: '6', rank: 6, nickname: '万培恩', acCount: 8, avatar: '' },
  { key: '7', rank: 7, nickname: '彭博川', acCount: 7, avatar: '' },
  { key: '8', rank: 8, nickname: '李云溪', acCount: 7, avatar: '' },
  { key: '9', rank: 9, nickname: '李成枫', acCount: 6, avatar: '' }
])

const getRankBadge = (rank: number) => {
  if (rank === 1) return { status: 'danger', text: '🥇' }
  if (rank === 2) return { status: 'warning', text: '🥈' }
  if (rank === 3) return { status: 'processing', text: '🥉' }
  return { status: 'default', text: `#${rank}` }
}
</script>

<template>
  <Card class="ac-rank-card">
    <template #title>
      <div class="card-title">
        <IconCalendar class="title-icon" />
        <span>今日AC榜单</span>
      </div>
    </template>
    <div class="list-container">
      <div v-for="item in listData" :key="item.key" class="list-item">
        <div class="item-left">
          <Badge :status="getRankBadge(item.rank).status" :text="getRankBadge(item.rank).text" />
          <span class="nickname">{{ item.nickname }}</span>
        </div>
        <span class="ac-count">{{ item.acCount }}</span>
      </div>
    </div>
  </Card>
</template>

<style scoped>
.ac-rank-card {
  height: 100%;
  border-radius: 8px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1d2129;
}

.title-icon {
  color: #165dff;
}

.list-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.list-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 0;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nickname {
  color: #1d2129;
  font-size: 14px;
}

.ac-count {
  color: #00b42a;
  font-weight: 600;
  font-size: 14px;
}
</style>
