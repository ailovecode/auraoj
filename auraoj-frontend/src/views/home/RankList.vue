<script setup lang="ts">
import { ref } from 'vue'
import { Card, Table } from '@arco-design/web-vue'
import { IconTrophy } from '@arco-design/web-vue/es/icon'

interface RankItem {
  key: string
  rank: number
  nickname: string
  level: string
  coins: number
  avatar: string
}

const tableData = ref<RankItem[]>([
  { key: '1', rank: 1, nickname: 'man', level: '红名', coins: 1703, avatar: '' },
  { key: '2', rank: 2, nickname: 'Oniy404zr', level: '红名', coins: 1685, avatar: '' },
  { key: '3', rank: 3, nickname: 'Ericm', level: '红名', coins: 1511, avatar: '' },
  { key: '4', rank: 4, nickname: '关东煮破碗', level: '红名', coins: 2160, avatar: '' },
  { key: '5', rank: 5, nickname: '王衍', level: '红名', coins: 911, avatar: '' },
  { key: '6', rank: 6, nickname: '彭琪琪', level: '神牛', coins: 971, avatar: '' },
  { key: '7', rank: 7, nickname: '鼎城区上小范某', level: '神牛', coins: 911, avatar: '' },
  { key: '8', rank: 8, nickname: 'mito-江子涵', level: '神牛', coins: 1150, avatar: '' },
  { key: '9', rank: 9, nickname: '曹语辰', level: '神牛', coins: 843, avatar: '' },
  { key: '10', rank: 10, nickname: '心若浮尘', level: '神牛', coins: 843, avatar: '' }
])

const columns = [
  {
    title: '排名',
    dataIndex: 'rank',
    width: 80
  },
  {
    title: '昵称',
    dataIndex: 'nickname'
  },
  {
    title: '等级',
    dataIndex: 'level',
    width: 100
  },
  {
    title: '金币',
    dataIndex: 'coins',
    width: 120
  }
]
</script>

<template>
  <Card class="rank-card">
    <template #title>
      <div class="card-title">
        <IconTrophy class="title-icon" />
        <span>排名</span>
      </div>
    </template>
    <Table
      :columns="columns"
      :data="tableData"
      :pagination="false"
      :bordered="false"
      size="small"
    />
  </Card>
</template>

<style scoped>
.rank-card {
  margin-bottom: 24px;
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
</style>
