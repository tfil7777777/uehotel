<template>
  <div :class="{ home: true, openMenus: !isCollapse, closeMenus: isCollapse }">
    <div class="menus">
      <div class="brand text-center font-bold text-white pt-20 pb-10 flex items-center justify-center">
        <img src="@/assets/logo.png" height="32px" />
        <span class="brand-text">逸旅酒店 · 后台</span>
      </div>
      <el-menu :collapse="isCollapse" default-active="home" background-color="#304156" text-color="#fff"
        active-text-color="#e4c07a" router>
        <el-menu-item index="/admin" style="font-size:18px;">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/admin" style="font-size:18px;">
          <i class="el-icon-user"></i>
          <span slot="title">管理员管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/member" style="font-size:18px;">
          <i class="el-icon-user-solid"></i>
          <span slot="title">用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category" style="font-size:18px;">
          <i class="el-icon-s-promotion"></i>
          <span slot="title">房型管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/room" style="font-size:18px;">
          <i class="el-icon-s-cooperation"></i>
          <span slot="title">客房管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/appointment" style="font-size:18px;">
          <i class="el-icon-s-flag"></i>
          <span slot="title">预约管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders" style="font-size:18px;">
          <i class="el-icon-s-order"></i>
          <span slot="title">订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/message" style="font-size:18px;">
          <i class="el-icon-warning"></i>
          <span slot="title">留言管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notice" style="font-size:18px;">
          <i class="el-icon-bell"></i>
          <span slot="title">公告管理</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="content">
      <div class="navbar">
        <div class="navbar-btn" @click="isCollapse = !isCollapse">
          <i class="el-icon-s-fold" v-if="!isCollapse"></i>
          <i class="el-icon-s-unfold" v-else></i>
        </div>
        <div class="navbar-right">
          <el-dropdown trigger="click">
            <span class="el-dropdown-link">
              {{ user.name }}<i class="el-icon-caret-bottom el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item icon="el-icon-s-custom" @click.native="$router.push('/admin/adminInfo')">个人信息</el-dropdown-item>
              <el-dropdown-item icon="el-icon-caret-left" @click.native="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div class="section">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState,mapMutations } from 'vuex'
import { removeToken } from '@/utils/auth'
export default {
  name: "Home",
  computed: {
    ...mapState(['user'])
  },
  data() {
    return {
      isCollapse: false,
    };
  },
  methods: {
    ...mapMutations(['setUser', 'setToken']),
    logout() {
      this.$confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        removeToken()
        this.setUser({})
        this.setToken('')
        this.$router.replace('/front')
      }).catch(() => { });
    }
  },
};
</script>

<style lang="less" scoped>
.brand {
  font-size: 20px;
  letter-spacing: 0.4px;
  gap: 10px;
}
.brand-text {
  font-weight: 800;
}

.openMenus {
  .menus {
    height: 100vh;
    width: 200px;
    overflow-y: auto;
    position: fixed;
    left: 0;
    top: 0;
    background-color: #304156;
    transition: all 0.5s;

    .el-menu {
      border-right: none;
    }
  }

  .content {
    margin-left: 200px;
    width: calc(100% - 200px);
    height: 100vh;
    transition: all 0.5s;
  }
}

.closeMenus {
  .menus {
    height: 100vh;
    width: 64px;
    overflow-y: auto;
    position: fixed;
    left: 0;
    top: 0;
    transition: all 0.5s;
    background-color: #304156;

    .el-menu {
      border-right: none;
    }
  }

  .content {
    margin-left: 64px;
    width: calc(100% - 64px);
    height: 100vh;
    transition: all 0.5s;
  }
}

.home {
  .navbar {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 10px;
    justify-content: space-between;
    border-bottom: 1px solid #eee;

    .navbar-btn {
      height: 40px;
      line-height: 40px;
      text-align: center;
      cursor: pointer;
    }

    .navbar-right {
      min-width: 60px;
      height: 40px;
      display: flex;
      align-items: center;
      cursor: pointer;
    }
  }

  .content {
    .section {
      padding: 16px;
    }
  }
}
</style>
