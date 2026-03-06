<script setup lang="ts">
import NoticeBoard from './NoticeBoard.vue'
import SubmissionChart from './SubmissionChart.vue'
import RankList from './RankList.vue'
import TodayACRank from './TodayACRank.vue'
import { Grid, Card } from '@arco-design/web-vue'
const { Row, Col } = Grid
</script>

<template>
  <div class="home-container">
    <div class="home-content">
      <Card class="main-card">
        <Row :gutter="24">
          <Col :span="17">
            <NoticeBoard />
            <SubmissionChart />
            <RankList />
          </Col>
          <Col :span="7">
            <TodayACRank />
          </Col>
        </Row>
      </Card>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 960px;
  margin: 0 auto;
}

.home-content {
  width: 100%;
}

.main-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
</style>
