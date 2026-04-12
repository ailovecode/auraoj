<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Card } from '@arco-design/web-vue'
import { IconBarChart } from '@arco-design/web-vue/es/icon'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'

const chartRef = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)

  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['提交', '解答'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['2026/02/27', '2026/02/28', '2026/03/01', '2026/03/02', '2026/03/03', '2026/03/04', '2026/03/05']
    },
    yAxis: {
      type: 'value',
      max: 3500
    },
    series: [
      {
        name: '提交',
        type: 'line',
        smooth: true,
        stack: 'Total',
        data: [3200, 3100, 3400, 3000, 2800, 2600, 2500],
        itemStyle: {
          color: '#86e8ab'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(134, 232, 171, 0.3)' },
            { offset: 1, color: 'rgba(134, 232, 171, 0.1)' }
          ])
        }
      },
      {
        name: '解答',
        type: 'line',
        smooth: true,
        stack: 'Total',
        data: [1800, 1750, 1900, 1700, 1600, 1500, 1450],
        itemStyle: {
          color: '#73c0de'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(115, 192, 222, 0.3)' },
            { offset: 1, color: 'rgba(115, 192, 222, 0.1)' }
          ])
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<template>
  <Card class="chart-card">
    <template #title>
      <div class="card-title">
        <IconBarChart class="title-icon" />
        <span>OJ提交近况</span>
      </div>
    </template>
    <div ref="chartRef" style="width: 100%; height: 300px"></div>
  </Card>
</template>

<style scoped>
.chart-card {
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
