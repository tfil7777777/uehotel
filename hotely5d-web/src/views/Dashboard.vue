<template>
  <div class="dashboard-container">
    <!-- 顶部核心指标卡片 -->
    <el-row :gutter="20" class="card-row">
      <el-col :span="12">
        <el-card shadow="always" class="data-card">
          <div class="card-content">
            <div class="card-title"><i class="el-icon-s-order"></i> 总订单数</div>
            <div class="card-value">{{ dashboardData.totalOrders || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="always" class="data-card">
          <div class="card-content">
            <div class="card-title"><i class="el-icon-money"></i> 总销售额 (元)</div>
            <div class="card-value">{{ dashboardData.totalPrice || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中间图表区域 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div id="categoryChart" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div id="dailyOrdersChart" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部趋势图 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <div id="dailyMembersChart" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import request from '@/utils/request';

export default {
  name: 'Dashboard',
  data() {
    return {
      dashboardData: {
        totalOrders: 0,
        totalPrice: 0,
        ordersByCategory: [],
        dailyOrders: [],
        dailyMembers: []
      },
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      // 调用后端接口
      request.get('/dashboard/data').then(res => {
        if (res.flag) {
          this.dashboardData = res.data;
          this.$nextTick(() => {
            this.initCharts();
          });
        }
      }).catch(err => {
        console.error("获取数据失败", err);
      });
    },
    initCharts() {
      this.initCategoryChart();
      this.initDailyOrdersChart();
      this.initDailyMembersChart();
    },
    initCategoryChart() {
      const chart = echarts.init(document.getElementById('categoryChart'));
      chart.setOption({
        title: { text: '各房型预订占比', left: 'center' },
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: '55%',
          data: this.dashboardData.ordersByCategory,
          emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
        }]
      });
    },
    initDailyOrdersChart() {
      const chart = echarts.init(document.getElementById('dailyOrdersChart'));
      chart.setOption({
        title: { text: '近7日订单量', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.dashboardData.dailyOrders.map(item => item.date) },
        yAxis: { type: 'value' },
        series: [{ name: '订单量', type: 'bar', data: this.dashboardData.dailyOrders.map(item => item.count), itemStyle: { color: '#409EFF' } }]
      });
    },
    initDailyMembersChart() {
      const chart = echarts.init(document.getElementById('dailyMembersChart'));
      chart.setOption({
        title: { text: '近7日用户增长趋势', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.dashboardData.dailyMembers.map(item => item.date) },
        yAxis: { type: 'value' },
        series: [{ name: '新增用户', type: 'line', data: this.dashboardData.dailyMembers.map(item => item.count), smooth: true, areaStyle: {} }]
      });
    }
  }
};
</script>

<style scoped>
.dashboard-container { padding: 20px; background-color: #f0f2f5; min-height: 100vh; }
.card-row { margin-bottom: 20px; }
.data-card { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.card-title { font-size: 16px; opacity: 0.8; }
.card-value { font-size: 32px; font-weight: bold; margin-top: 10px; }
</style>
