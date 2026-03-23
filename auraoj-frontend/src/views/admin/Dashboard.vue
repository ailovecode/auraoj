<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { Card, Statistic, Row, Col } from '@arco-design/web-vue'
import {
  IconUser,
  IconFile,
  IconCheck,
  IconTrophy
} from '@arco-design/web-vue/es/icon'

const userStore = useUserStore()

const stats = ref([
  {
    title: '用户总数',
    value: 1234,
    icon: IconUser,
    color: '#165dff',
    trend: '+12%'
  },
  {
    title: '题目总数',
    value: 568,
    icon: IconFile,
    color: '#00b42a',
    trend: '+5%'
  },
  {
    title: '今日提交',
    value: 892,
    icon: IconCheck,
    color: '#ff7d00',
    trend: '+8%'
  },
  {
    title: '比赛数量',
    value: 45,
    icon: IconTrophy,
    color: '#f53f3f',
    trend: '+2%'
  }
])
</script>

<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>欢迎回来，{{ userStore.userInfo?.username }}！</h1>
      <p>这是 AuraOJ 管理后台仪表盘</p>
    </div>

    <Row :gutter="20" class="stats-row">
      <Col :span="6" v-for="stat in stats" :key="stat.title">
        <Card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color + '10' }">
              <component :is="stat.icon" :style="{ color: stat.color, fontSize: '28px' }" />
            </div>
            <div class="stat-info">
              <Statistic :value="stat.value" :group-separator="','" class="stat-value" />
              <div class="stat-title">{{ stat.title }}</div>
              <div class="stat-trend" :style="{ color: stat.trend.startsWith('+') ? '#00b42a' : '#f53f3f' }">
                {{ stat.trend }} 较上周
              </div>
            </div>
          </div>
        </Card>
      </Col>
    </Row>

    <Row :gutter="20" class="charts-row">
      <Col :span="16">
        <Card title="最近提交趋势" class="chart-card">
          <div class="chart-placeholder">
            <div class="placeholder-text">图表区域</div>
          </div>
        </Card>
      </Col>
      <Col :span="8">
        <Card title="最新动态" class="chart-card">
          <div class="activity-list">
            <div class="activity-item">用户 xiaoyu 提交了题目 #123</div>
            <div class="activity-item">新题目 "两数之和" 已上架</div>
            <div class="activity-item">新比赛 "新手练习赛" 已创建</div>
            <div class="activity-item">用户 admin 修改了系统设置</div>
          </div>
        </Card>
      </Col>
    </Row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
}

.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-header h1 {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
}

.dashboard-header p {
  margin: 0;
  font-size: 14px;
  color: #86909c;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value :deep(.arco-statistic-value) {
  font-size: 32px;
  font-weight: 700;
  color: #1d2129;
}

.stat-title {
  font-size: 14px;
  color: #86909c;
  margin-top: 4px;
}

.stat-trend {
  font-size: 12px;
  margin-top: 8px;
}

.charts-row {
  margin-bottom: 24px;
}

.chart-card {
  border-radius: 8px;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f7f8fa;
  border-radius: 4px;
}

.placeholder-text {
  color: #c9cdd4;
  font-size: 14px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  padding: 12px;
  background: #f7f8fa;
  border-radius: 4px;
  font-size: 14px;
  color: #4e5969;
}
</style>
