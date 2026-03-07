<script setup lang="ts">
import { ref } from 'vue'
import { Card, Table } from '@arco-design/web-vue'
import { IconSound } from '@arco-design/web-vue/es/icon'

interface Notice {
  key: string
  title: string
  time: string
  author: string
}

const tableData = ref<Notice[]>([
  {
    key: '1',
    title: '本站开放老师账号申请以及暑期私教OI',
    time: '2026-03-05 10:00',
    author: 'admin'
  },
  {
    key: '2',
    title: '系统升级通知',
    time: '2026-03-04 15:30',
    author: '技术部'
  },
  {
    key: '3',
    title: '新功能上线：代码分享',
    time: '2026-03-03 09:00',
    author: '产品部'
  },
  {
    key: '4',
    title: '3月周赛预告',
    time: '2026-03-02 14:00',
    author: '运营部'
  },
  {
    key: '5',
    title: '帮助文档更新',
    time: '2026-03-01 11:00',
    author: '管理员'
  }
])

const columns = [
  {
    title: '标题',
    dataIndex: 'title',
    ellipsis: true
  },
  {
    title: '时间',
    dataIndex: 'time',
    width: 180
  },
  {
    title: '发布者',
    dataIndex: 'author',
    width: 100
  }
]
</script>

<template>
  <Card class="notice-card">
    <template #title>
      <div class="card-title">
        <IconSound class="title-icon" />
        <span>小黑板</span>
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
.notice-card {
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
