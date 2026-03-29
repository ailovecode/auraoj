<script setup lang="ts">
import AppNavbar from '@/components/AppNavbar.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="main-layout">
    <AppNavbar />
    <main class="main-content" :class="{ 'no-padding': route.name === 'problem-detail' }">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.main-layout {
  height: 100%;
  /* 使用百分比让布局高度填满 */
  display: flex;
  flex-direction: column;
  overflow: hidden;
  /* 防止外部滚动 */
}

.main-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  /* 允许内容滚动 */
}

.no-padding {
  padding: 0 !important;
  overflow: hidden !important;
  display: flex !important;
  flex-direction: column !important;
}
</style>
