<template>
  <div class="container">
    <div class="login-box">
      <div class="logo-line" @click="$router.push('/front')">
        <div class="brand-mark">UE</div>
        <div class="title">
          <div class="cn">逸旅酒店</div>
          <small class="en">Urban Escape Hotel</small>
        </div>
      </div>
      <el-form :model="loginForm" label-position="left">
        <el-form-item>
          <el-input
            v-model="loginForm.username"
            placeholder="请输入账号"
            clearable
          >
            <template #prefix>
              <i class="el-icon-user"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <i class="el-icon-lock"></i>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="loginForm.role">
            <el-radio :label="1">管理员</el-radio>
            <el-radio :label="2">用户</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-btn"
            :loading="loading"
            @click="login"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>
          <div class="text-right">
            没有账号？ <router-link to="/register">去注册</router-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { loginAPI, getUserInfoByTokenAPI } from '@/api/system'
import { mapMutations } from 'vuex'
import { setToken } from '@/utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      errorMessage: '',
      loginForm: {
        role: 1,
        username: '',
        password: ""
      }
    }
  },
  methods: {
    ...mapMutations(['setUser','setRole','setToken']),
    async login() {
      try {
        this.loading = true
        this.errorMessage = ''
        const res = await loginAPI(this.loginForm)
        
        if (res.flag) {
          setToken(res.data)
          this.setToken(res.data)
          
          const result = await getUserInfoByTokenAPI()
          this.setUser(result.data)
          this.setRole(this.loginForm.role)
          
          this.$router.replace(this.loginForm.role == 1 ? "/admin" : "/front")
        } else {
          this.errorMessage = res.message
        }
      } catch (error) {
        this.errorMessage = '网络请求失败，请稍后重试'
        console.error('Login error:', error)
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="less" scoped>
.container {
  width: 100vw;
  height: 100vh;
  background: 
    linear-gradient(rgba(0, 0, 0, 0.02), 
    rgba(0, 0, 0, 0.02)),
    url(@/assets/houtai.jpg) center/cover no-repeat;
  background-attachment: fixed;
  display: flex;
  transition: background 0.5s ease;

  &:before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
    opacity: 0.1;
    z-index: -1;
  }

  .login-box {
    padding: 15px 30px;
    border-radius: 20px;
    min-width: 350px;
    max-width: 420px;
    width: 90%;
    background: rgba(255, 255, 255, 0.88);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(255, 255, 255, 0.1);
    margin: auto;
    transition: all 0.3s ease;
    animation: fadeIn 0.8s ease-out;

    &:hover {
      box-shadow: 
        0 12px 40px rgba(0, 0, 0, 0.15),
        0 0 0 2px rgba(255, 255, 255, 0.2);
    }

    .logo-line {
      display: flex;
      align-items: center;
      gap: 12px;
      cursor: pointer;
      margin-bottom: 26px;
    }

    .brand-mark {
      width: 52px;
      height: 52px;
      border-radius: 14px;
      background: linear-gradient(135deg, #1f3b4d, #0f172a);
      color: #f8f9fb;
      display: grid;
      place-items: center;
      font-weight: 800;
      letter-spacing: 0.5px;
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.18);
    }

    .title {
      display: flex;
      flex-direction: column;
      line-height: 1.1;
      color: #1f2937;
      font-family: 'Source Sans Pro','PingFang SC','Microsoft YaHei',sans-serif;

      .cn {
        font-size: 22px;
        font-weight: 800;
      }
      .en {
        font-size: 13px;
        color: #4b5563;
      }
    }

    .el-input {
      border-radius: 8px;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.5);
      }
      
      &:focus {
        box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.8);
      }
    }

    .login-btn {
      width: 100%;
      height: 48px;
      border-radius: 8px;
      font-size: 16px;
      background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
      border: none;
      transition: all 0.3s;
      box-shadow: 0 4px 12px rgba(37, 117, 252, 0.4);
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(37, 117, 252, 0.6);
      }
      
      &:active {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(37, 117, 252, 0.4);
      }
    }

    .error-message {
      color: #f56c6c;
      text-align: center;
      margin-top: 10px;
      font-size: 13px;
      min-height: 20px;
    }

    .text-right {
      text-align: center;
      margin-top: 15px;
      
      a {
        color: #409eff;
        text-decoration: none;
        transition: all 0.3s;
        
        &:hover {
          color: #66b1ff;
          text-decoration: underline;
        }
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .login-box {
    padding: 30px 20px !important;
  }
  
  .login-btn {
    height: 44px !important;
  }
}
</style>
