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
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 24px;
}

/* 沉浸式页面专用，去除所有外层边距 */
.no-padding {
  padding: 0 !important;
}
</style>
