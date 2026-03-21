<script setup lang="ts">
</script>

<template>
  <router-view />
</template>

<style>
html,
body,
#app {
  height: 100%;
  /* 确保页面整体占满高度 */
  margin: 0;
  padding: 0;
  overflow: hidden;
  /* 防止外部滚动条出现 */
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f2f3f5;
}

.placeholder-page {
  text-align: center;
  padding: 60px 0;
}

.placeholder-page h1 {
  font-size: 36px;
  color: #1d2129;
}
</style>
