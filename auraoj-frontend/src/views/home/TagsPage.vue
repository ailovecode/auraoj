<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Card, Tag, TypographyTitle, Message, Skeleton } from '@arco-design/web-vue'
import { IconTag } from '@arco-design/web-vue/es/icon'
import { listAllTags, deleteTag } from '@/api/tag'
import type { TagInfo } from '@/types/tagInfo'
import { getProblemsByTagId } from '@/api/problem'

const router = useRouter()

const loading = ref(false)
const tagList = ref<TagInfo[]>([])

// Arco Design 预定义颜色
const arcoColors = ['red', 'orange', 'gold', 'lime', 'green', 'cyan', 'blue', 'arcoblue', 'purple', 'magenta', 'gray']

// 丰富标签数据，添加随机颜色和随机大小以实现"星云"感
const enrichedTags = computed(() => {
  return tagList.value.map(tag => {
    // 随机选择一个 Arco 颜色
    const randomColor = arcoColors[Math.floor(Math.random() * arcoColors.length)]
    // 随机选择标签大小
    const sizeFactor = Math.random()
    let size: 'small' | 'medium' | 'large' = 'small'
    if (sizeFactor > 0.7) size = 'large'
    else if (sizeFactor > 0.3) size = 'medium'

    return {
      ...tag,
      displayColor: randomColor,
      displaySize: size
    }
  })
})

const totalTagsCount = computed(() => tagList.value.length)

const fetchTags = async () => {
  loading.value = true
  try {
    const res = await listAllTags()
    if (res.code === 200) {
      tagList.value = res.data || []
    } else {
      Message.error(res.message || '获取标签列表失败')
    }
  } catch (error) {
    console.error('获取标签列表失败:', error)
    Message.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

const handleTagClick = async (tag: TagInfo) => {
  try {
    Message.loading({ id: 'tag-click', content: `正在加载「${tag.name}」相关题目...` })

    const res = await getProblemsByTagId(tag.id)

    if (res.code === 200) {
      Message.success({ id: 'tag-click', content: `「${tag.name}」相关题目加载成功` })
      router.push(`/problem?tagId=${tag.id}`)
    } else {
      Message.error({ id: 'tag-click', content: res.message || '获取题目失败' })
    }
  } catch (error) {
    console.error('获取题目失败:', error)
    Message.error({ id: 'tag-click', content: '获取题目失败' })
  }
}

onMounted(() => {
  fetchTags()
})
</script>

<template>
  <div class="tags-page">
    <div class="page-header">
      <div class="header-content">
        <IconTag :size="28" class="title-icon" />
        <TypographyTitle :heading="3" class="page-title">标签星云</TypographyTitle>
      </div>
      <p class="page-subtitle">探索 {{ totalTagsCount }} 个知识维度</p>
    </div>

    <div class="content-area">
      <Card class="spherical-tag-card" :bordered="false">
        <div v-if="loading" class="loading-container">
          <Skeleton v-for="i in 15" :key="i" animation="wave">
            <SkeletonShape shape="tag" />
          </Skeleton>
        </div>

        <template v-else>
          <div v-if="totalTagsCount === 0" class="empty-state">
            <IconTag :size="48" class="empty-icon" />
            <TypographyTitle :heading="5">暂无标签</TypographyTitle>
            <p class="empty-text">当前没有任何标签</p>
          </div>

          <div v-else class="tag-cloud-sphere">
            <Tag v-for="tag in enrichedTags" :key="tag.id" class="tag-item" :color="tag.displayColor"
              :size="tag.displaySize" checkable @click="handleTagClick(tag)">
              {{ tag.name }}
            </Tag>
          </div>
        </template>
      </Card>
    </div>

    <div class="page-footer">
      <p class="footer-tip">点击标签，开启知识之旅</p>
    </div>
  </div>
</template>

<style scoped>
.tags-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #ebedf1 100%);
  display: flex;
  flex-direction: column;
}

.page-header {
  padding: 40px 24px 20px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 8px;
}

.title-icon {
  color: #4e5969;
}

.page-title {
  margin: 0 !important;
  color: #1d2129 !important;
  font-weight: 600;
}

.page-subtitle {
  margin: 0;
  color: #86909c;
  font-size: 14px;
}

.content-area {
  flex: 1;
  width: 100%;
  padding: 20px 24px 40px;
  display: flex;
  justify-content: center;
  /* 水平居中卡片 */
  align-items: flex-start;
  /* 让卡片靠上对齐，高度自由延展，不被垂直拉伸 */
  perspective: 1000px;
}

/* 动态卡片核心 CSS：完美还原截图比例 */
.spherical-tag-card {
  width: fit-content;
  /* 宽度包裹内容 */
  min-width: 50%;
  /* 给予一个较宽的基础底宽，看起来不大气 */
  max-width: 66%;
  /* 最宽不超过页面的 66% */
  min-height: 280px;
  /* 给一个基础高度，保证标签少的时候也好看 */
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.05);
  /* 增加四周的超大留白，营造出星云在宇宙中悬浮的空间感 */
  padding: 60px 80px;
  transition: all 0.3s;
}

/* 覆盖 Arco Card 默认的样式，让内容区域充满卡片，并完全居中 */
:deep(.arco-card-body) {
  padding: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  justify-content: center;
}

.empty-state {
  text-align: center;
  color: #86909c;
}

.empty-icon {
  opacity: 0.4;
  margin-bottom: 12px;
}

.empty-text {
  margin-top: 4px;
  font-size: 14px;
}

/* 核心布局：利用 Flex 居中换行，形成球形/云团效果 */
.tag-cloud-sphere {
  display: flex;
  flex-wrap: wrap;
  gap: 18px 24px;
  /* 加大一点标签之间的呼吸感 */
  justify-content: center;
  align-items: center;
}

/* 标签动画与悬停效果 */
.tag-item {
  cursor: pointer;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: transform 0.3s, box-shadow 0.3s, filter 0.3s;
  transform-origin: center center;
  backface-visibility: hidden;
  animation: float 6s infinite ease-in-out;
}

/* 随机抖动时间，让它们看起来像真的星云 */
.tag-item:nth-child(even) {
  animation-delay: -1s;
}

.tag-item:nth-child(3n) {
  animation-delay: -2s;
}

.tag-item:nth-child(5n) {
  animation-delay: -3s;
}

.tag-item:nth-child(7n) {
  animation-delay: -4s;
}

.tag-item:nth-child(11n) {
  animation-delay: -5s;
}

.tag-item:hover {
  transform: scale(1.15) translateY(-8px) rotateY(10deg);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15), 0 0 15px currentColor;
  filter: brightness(1.1);
  z-index: 10;
}

.tag-item .arco-tag-checkable {
  transition: all 0.3s;
}

@keyframes float {
  0% {
    transform: translate3d(0, 0, 0);
  }

  50% {
    transform: translate3d(0, -6px, 0) rotateY(4deg);
  }

  100% {
    transform: translate3d(0, 0, 0);
  }
}

.page-footer {
  text-align: center;
  padding: 24px;
  color: #86909c;
  font-size: 13px;
  margin-top: auto;
}

.footer-tip {
  margin: 0;
}

/* 移动端响应式调整 */
@media (max-width: 768px) {
  .spherical-tag-card {
    width: 90% !important;
    max-width: 90% !important;
    min-width: unset !important;
    padding: 30px 20px;
  }
}
</style>
