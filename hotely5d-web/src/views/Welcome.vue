<template>
    <div class="text-center">
    <!-- 页面居中显示 -->
        <h1>Hi~ 欢迎使用{{ $sysTitle }}后台</h1>
        <img src="@/assets/houtai.jpg" />
    </div>
</template>

<script>
export default {
    name: 'Welcome',
};
</script>

<style lang="less" scoped>
img{

    width: 80%;
}
</style>