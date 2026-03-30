<template>
  <div class="page-container">
    <section class="hero surface-card">
      <div class="hero-copy">
        <span class="pill">Refined stay · Inspired by boutique hotels</span>
        <h1 class="heading-serif">让入住更有仪式感</h1>
        <p>
          借鉴柏悦、文华东方等高端酒店的视觉语言，选择雾霾蓝与暖金的配色，
          用大幅沉浸式图片营造度假氛围，搭配衬线标题与充足留白，突出空间感与高级感。
        </p>
        <div class="hero-actions">
          <el-button type="primary" round size="medium" @click="$router.push('/front/room')">立即预订</el-button>
          <el-button round size="medium" @click="$router.push('/front/chatBook')">在线咨询</el-button>
        </div>
        <div class="hero-meta">
          <span>24h 前台</span>
          <span>高品质床品</span>
          <span>灵活取消</span>
        </div>
      </div>
      <div class="hero-carousel">
        <div class="carousel-overlay"></div>
        <el-carousel height="420px" indicator-position="outside">
          <el-carousel-item v-for="(img,idx) in heroImages" :key="idx">
            <img :src="img" class="carousel-img" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <section class="section surface-card">
      <div class="section-head">
        <div>
          <div class="pill">Room Collection</div>
          <h2 class="heading-serif">甄选房型</h2>
          <p>按照不同旅居场景设计的房型，轻松找到你的理想入住。</p>
        </div>
        <el-button type="text" class="link-btn" @click="$router.push('/front/room')">查看全部房型 →</el-button>
      </div>
      <div class="room-grid">
        <div
          v-for="item in categoryList"
          :key="item.id"
          class="room-card surface-card"
          @click="$router.push(`/front/room?categoryId=${item.id}`)"
        >
          <div class="room-img-wrap">
            <img :src="$baseFileUrl + item.photo" class="room-img" />
            <div class="price-tag">¥{{ item.price }}/晚</div>
          </div>
          <div class="room-info">
            <div class="heading-serif room-title">{{ item.categoryName }}</div>
            <p class="room-desc">臻选柔软床品、香氛与灯光，带来更松弛的夜晚。</p>
            <div class="room-cta">查看详情</div>
          </div>
        </div>
        <div v-if="!categoryList.length" class="empty-tip">暂无房型数据，稍后再试</div>
      </div>
    </section>

    <section class="section intro surface-card">
      <div class="intro-text">
        <div class="pill">Hotel Story</div>
        <h2 class="heading-serif">臻享城市度假体验</h2>
        <p>
          坐落于城市核心区，步行即可抵达商圈与艺术街区。以“都市绿洲”为设计灵感，金属质感与暖米色织物
          交错，配合全景落地窗，将城市天际线与室内氛围融为一体。我们提供精品早餐、健身房及 24 小时礼宾，
          让每次出行都像度假。
        </p>
        <div class="intro-highlights">
          <span>• 米其林合作早餐</span>
          <span>• Aromatherapy 洗护</span>
          <span>• 智能入住与延迟退房</span>
        </div>
      </div>
      <div class="intro-media">
        <img src="@/assets/introduce.jpeg" alt="hotel intro">
        <div class="floating-card">
          <div class="heading-serif">城市夜景房</div>
          <p>270° 景观落地窗，私享天际线，最适合双人度假。</p>
        </div>
      </div>
    </section>

    <section class="section surface-card notice">
      <div class="section-head">
        <div>
          <div class="pill">Latest</div>
          <h2 class="heading-serif">最新公告</h2>
          <p>重要入住信息、限时优惠与活动更新。</p>
        </div>
      </div>
      <el-table
        v-if="noticeList.length"
        class="notice-table"
        :data="noticeList"
        border
        fit
        highlight-current-row
        size="small"
      >
        <el-table-column align="center" label="ID" prop="id"></el-table-column>
        <el-table-column align="center" label="标题" prop="title" show-overflow-tooltip></el-table-column>
        <el-table-column align="center" label="发布时间" prop="createTime"></el-table-column>
        <el-table-column fixed="right" label="操作" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="$router.push(`/front/noticeDetails/${scope.row.id}`)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-else class="empty-tip">暂无公告，稍后再试或联系管理员发布</div>
    </section>
  </div>
</template>

<script>
import { getTop5NoticeAPI } from '@/api/notice'
import { findAllCategoryAPI } from '@/api/category'
import intro1 from '@/assets/introduction.jpg'
import intro2 from '@/assets/jiudian2.jpg'
import intro3 from '@/assets/houtai.jpg'

export default {
  name: 'Hotely5dWebHome',
  data() {
    return {
      noticeList: [],
      categoryList: [],
      heroImages: [intro1, intro2, intro3]
    }
  },

  async mounted() {
    try {
      const { data: noticeList = [] } = await getTop5NoticeAPI()
      this.noticeList = noticeList || []
    } catch (e) {
      this.noticeList = []
    }

    try {
      const { data: categoryList = [] } = await findAllCategoryAPI()
      this.categoryList = categoryList || []
    } catch (e) {
      this.categoryList = []
    }
  }
}
</script>

<style lang="less" scoped>
.page-container {
  max-width: 1200px;
  margin: 30px auto 60px;
  display: grid;
  gap: 26px;
}

.hero {
  display: grid;
  grid-template-columns: 1fr 1.1fr;
  padding: 32px;
  position: relative;
  overflow: hidden;
}

.hero-copy h1 {
  font-size: 42px;
  margin: 12px 0 14px;
  color: var(--brand-primary);
}

.hero-copy p {
  color: #4b5563;
  line-height: 1.7;
}

.hero-actions {
  margin: 18px 0 10px;
  display: flex;
  gap: 12px;
}

.hero-meta {
  display: flex;
  gap: 14px;
  color: #6b7280;
  font-size: 14px;
}

.hero-carousel {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.carousel-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(31, 59, 77, 0.25), transparent 45%);
  z-index: 1;
}

.carousel-img {
  width: 100%;
  height: 420px;
  object-fit: cover;
}

.section {
  padding: 24px 26px;
}

.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 12px;
  gap: 12px;
}

.section-head h2 {
  margin: 6px 0;
  font-size: 28px;
}

.link-btn {
  color: var(--brand-primary);
  font-weight: 600;
}

.room-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 18px;
  margin-top: 12px;
}

.room-card {
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.room-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.15);
}

.room-img-wrap {
  position: relative;
  height: 180px;
  overflow: hidden;
  border-radius: var(--radius-lg) var(--radius-lg) 12px 12px;
}

.room-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.room-card:hover .room-img {
  transform: scale(1.04);
}

.price-tag {
  position: absolute;
  right: 14px;
  bottom: 14px;
  background: linear-gradient(135deg, #d1a75c, #c58c3c);
  color: #fff;
  padding: 8px 12px;
  border-radius: 999px;
  font-weight: 700;
  box-shadow: var(--card-shadow);
}

.room-info {
  padding: 14px 16px 16px;
}

.room-title {
  font-size: 20px;
  color: var(--brand-primary);
}

.room-desc {
  margin: 8px 0 14px;
  color: #4b5563;
  line-height: 1.5;
}

.room-cta {
  color: var(--brand-primary);
  font-weight: 700;
  letter-spacing: 0.2px;
}

.intro {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: 26px;
  align-items: center;
}

.intro-text h2 {
  font-size: 28px;
  margin: 10px 0;
}

.intro-text p {
  line-height: 1.7;
  color: #4b5563;
}

.intro-highlights {
  margin-top: 12px;
  color: #1f2937;
  display: grid;
  gap: 6px;
}

.intro-media {
  position: relative;
}

.intro-media img {
  width: 100%;
  border-radius: var(--radius-lg);
  height: 360px;
  object-fit: cover;
  box-shadow: var(--card-shadow);
}

.floating-card {
  position: absolute;
  bottom: 16px;
  left: 16px;
  background: rgba(255, 255, 255, 0.92);
  padding: 14px 16px;
  border-radius: 14px;
  box-shadow: var(--card-shadow);
  max-width: 70%;
}

.notice-table {
  margin-top: 10px;
}

.empty-tip {
  padding: 18px;
  text-align: center;
  color: #6b7280;
  font-size: 14px;
}

/deep/ .el-table th {
  background: #f6f1ea;
  color: #1f3b4d;
  font-weight: 700;
}

/deep/ .el-table--border,
/deep/ .el-table--group {
  border-color: #e5e7eb;
}

/deep/ .el-button--text {
  color: var(--brand-primary);
  font-weight: 600;
}

@media (max-width: 992px) {
  .hero,
  .intro {
    grid-template-columns: 1fr;
  }
  .hero-carousel {
    order: -1;
  }
}
</style>
