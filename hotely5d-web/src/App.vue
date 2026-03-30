<template>
  <div class="app-container">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'VueTemplateApp',
  mounted() {
    // 固定标题，避免乱码或被后续覆盖
    document.title = '逸旅酒店 | Urban Escape Hotel'
  }
}
</script>

<style lang="less" scoped>
.app-container {
}
</style>
