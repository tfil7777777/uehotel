<template>
  <div class="layout-shell">
    <div class="nav-wrap glass">
      <el-menu
        :default-active="activeIndex"
        router
        class="el-menu-demo"
        mode="horizontal"
        background-color="transparent"
        text-color="#1f3b4d"
        active-text-color="#d1a75c"
      >
        <el-menu-item class="brand">
          <div class="brand-mark">UE</div>
          <div class="brand-text">
            <span class="brand-cn">逸旅酒店</span>
            <small>Urban Escape Hotel</small>
          </div>
        </el-menu-item>
        <div class="nav-spacer"></div>
        <div class="nav-items">
          <el-menu-item index="/front">首页</el-menu-item>
          <el-menu-item v-if="!token" index="/login">登录</el-menu-item>
          <el-menu-item v-if="!token" index="/register">注册</el-menu-item>
          <el-menu-item index="/front/notice">系统公告</el-menu-item>
          <el-menu-item index="/front/message">用户留言</el-menu-item>
          <el-menu-item index="/front/room">预约客房</el-menu-item>
          <el-menu-item index="/front/chatBook">对话订房</el-menu-item>
          <el-menu-item v-if="token" index="/front/appointment">我的预约</el-menu-item>
          <el-menu-item v-if="token" index="/front/orders">我的订单</el-menu-item>
        </div>
        <el-submenu v-if="token" index="#">
          <template slot="title">
            <el-avatar v-if="head" :src="$baseFileUrl + head"></el-avatar>
            <img v-else="head" src="@/assets/head.jpg" class="avatar-fallback" />
            <span class="user-name">{{ user?.name }}</span>
          </template>
          <el-menu-item index="/front/userInfo">个人信息</el-menu-item>
          <el-menu-item index="#" @click.native="logout">退出登录</el-menu-item>
        </el-submenu>
      </el-menu>
    </div>

    <div class="content">
      <router-view />
    </div>

    <div class="footer">
      <div>Copyright © {{ year }} 逸旅酒店</div>
      <div class="footer-sub">Crafted for modern travelers · 24h concierge</div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import { removeToken } from '@/utils/auth'

export default {
  name: 'Index',

  data() {
    return {
      activeIndex: '1',
      head: '',
      year: ''
    }
  },

  async mounted() {
    this.year = new Date().getFullYear()
    this.head = this.user?.head
  },

  computed: {
    ...mapState(['user', 'role', 'token'])
  },

  methods: {
    ...mapMutations(['setUser', 'setRole', 'setToken']),
    logout() {
      this.$confirm('确认退出登录吗？', '温馨提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          removeToken()
          this.setUser({})
          this.setToken('')
          this.$router.replace('/front')
        })
        .catch(() => {})
    },
    handleAppointmentClick() {
      this.$router.push('/front/room')
    }
  }
}
</script>

<style lang="less" scoped>
.layout-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.nav-wrap {
  position: sticky;
  top: 0;
  z-index: 999;
  padding: 10px 28px 6px;
  background: linear-gradient(135deg, #0f1f2e 0%, #1c2f45 60%, #1f3b4d 100%);
  box-shadow: 0 10px 28px rgba(0, 0, 0, 0.24);
}

.el-menu-demo {
  border-bottom: none;
  display: flex;
  align-items: center;
  height: 68px;
  gap: 12px;
}

/deep/ .el-menu.el-menu--horizontal {
  border-bottom: none !important;
}

/deep/ .el-menu-item,
/deep/ .el-submenu__title {
  font-size: 18px;
  font-weight: 700;
  padding: 0 22px;
  color: #e9edf2 !important;
  letter-spacing: 0.2px;
  transition: color 0.2s ease, transform 0.2s ease;
}

/deep/ .el-menu-item:hover,
/deep/ .el-submenu__title:hover {
  color: #e4c07a !important;
  transform: translateY(-2px);
}

/deep/ .el-menu-item.is-active {
  border-bottom: 2px solid #e4c07a !important;
  font-weight: 700 !important;
  color: #e4c07a !important;
}

.brand {
  border: none !important;
  cursor: default;
  padding-right: 22px !important;
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-spacer {
  flex: 1;
}

.nav-items {
  display: flex;
  flex: 2;
  justify-content: space-evenly;
  align-items: center;
}

.brand-mark {
  width: 38px;
  height: 38px;
  border-radius: 10px;
  background: linear-gradient(135deg, #11263b, #0b1a2a);
  color: #f8f9fb;
  display: grid;
  place-items: center;
  font-weight: 800;
  letter-spacing: 0.4px;
  box-shadow: 0 8px 18px rgba(0, 0, 0, 0.35);
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
  color: #f2f4f7;
  font-family: 'Playfair Display','Noto Sans SC','PingFang SC','Microsoft YaHei','Source Sans Pro',sans-serif;
}

.brand-cn {
  font-weight: 800;
  font-size: 18px;
}

.brand-text small {
  font-size: 12px;
  opacity: 0.9;
  letter-spacing: 0.8px;
}

.avatar-fallback {
  border-radius: 50%;
  width: 30px;
  height: 30px;
}

.user-name {
  color: #f7f8fb;
  margin-left: 8px;
}

.content {
  flex: 1;
}

.footer {
  height: 100px;
  padding: 18px 24px;
  text-align: center;
  color: #f7f8fb;
  background: linear-gradient(180deg, #1f3b4d, #0f172a);
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
  letter-spacing: 0.2px;
}

.footer-sub {
  color: rgba(255, 255, 255, 0.75);
  font-size: 13px;
}
</style>
