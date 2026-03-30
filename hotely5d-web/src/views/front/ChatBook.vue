<template>
  <div class="chat-book">
    <el-card class="chat-card" shadow="never">
      <div slot="header" class="card-header">
        <span>对话式订房</span>
        <el-button type="text" size="small" @click="restart">重新预订</el-button>
      </div>

      <div ref="scrollArea" class="messages">
        <div
          v-for="(m, idx) in messages"
          :key="idx"
          :class="['bubble-row', m.role === 'user' ? 'is-user' : 'is-bot']"
        >
          <div class="bubble">
            <div class="bubble-text" v-html="m.html || escapeHtml(m.text)"></div>
          </div>
        </div>
      </div>

      <div class="composer">
        <template v-if="step === 'date'">
          <el-date-picker
            v-model="draft.startDate"
            type="date"
            placeholder="选择入住日期"
            value-format="yyyy-MM-dd"
            format="yyyy-MM-dd"
            :picker-options="{ disabledDate: disabledPast }"
            size="small"
            class="composer-grow"
          />
          <el-button type="primary" size="small" :disabled="!draft.startDate" @click="confirmDate">
            确认
          </el-button>
        </template>

        <template v-else-if="step === 'days'">
          <el-input-number v-model="draft.days" :min="1" :max="30" size="small" />
          <el-button type="primary" size="small" :disabled="!draft.days" @click="confirmDays">
            确认
          </el-button>
        </template>

        <template v-else-if="step === 'category'">
          <el-select v-model="draft.categoryId" clearable placeholder="全部房型" size="small" class="composer-grow">
            <el-option
              v-for="c in categoryList"
              :key="c.id"
              :label="c.categoryName"
              :value="c.id"
            />
          </el-select>
          <el-button type="primary" size="small" @click="confirmCategory">下一步</el-button>
        </template>

        <template v-else-if="step === 'pickRoom'">
          <div v-if="roomList.length === 0" class="hint">暂无可选房间，请调整日期或房型。</div>
          <div v-else class="room-pick">
            <div
              v-for="r in roomList"
              :key="r.id"
              class="room-chip"
              :class="{ disabled: !r.canUse }"
              @click="selectRoom(r)"
            >
              <span class="chip-title">{{ r.category.categoryName }} · {{ r.roomNum }}</span>
              <span class="chip-meta">¥{{ r.category.price }}</span>
              <el-tag :type="r.canUse ? 'success' : 'danger'" size="mini">
                {{ r.canUse ? '可订' : '已满' }}
              </el-tag>
            </div>
          </div>
        </template>

        <template v-else-if="step === 'confirm'">
          <el-button
            type="primary"
            size="small"
            :loading="submitting"
            :disabled="submitting"
            @click="submitBooking"
          >确认预订</el-button>
          <el-button size="small" :disabled="submitting" @click="goStep('pickRoom')">上一步</el-button>
        </template>

        <template v-else-if="step === 'done'">
          <el-button type="primary" size="small" plain @click="restart">再订一间</el-button>
        </template>

        <template v-else>
          <span class="hint">按上方提示在下方操作即可。</span>
        </template>
      </div>
    </el-card>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import { mapState } from 'vuex'
import { findAllCategoryAPI } from '@/api/category'
import { findRoomListAPI, findRoomByIdAPI } from '@/api/room'
import { addAppointmentAPI } from '@/api/appointment'

export default {
  name: 'ChatBook',

  data() {
    return {
      step: 'welcome',
      messages: [],
      categoryList: [],
      roomList: [],
      selectedRoom: null,
      submitting: false,
      draft: {
        startDate: dayjs().format('YYYY-MM-DD'),
        days: 1,
        categoryId: null
      }
    }
  },

  computed: {
    ...mapState(['token', 'role'])
  },

  mounted() {
    this.bootstrap()
  },

  methods: {
    escapeHtml(s) {
      if (!s) return ''
      return String(s)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/\n/g, '<br>')
    },

    pushBot(text) {
      this.messages.push({ role: 'bot', text })
      this.$nextTick(this.scrollBottom)
    },
    pushUser(text) {
      this.messages.push({ role: 'user', text })
      this.$nextTick(this.scrollBottom)
    },

    scrollBottom() {
      const el = this.$refs.scrollArea
      if (el) el.scrollTop = el.scrollHeight
    },

    disabledPast(time) {
      return dayjs(time).isBefore(dayjs(), 'day')
    },

    async bootstrap() {
      this.pushBot('你好，我是订房小助手。接下来会问您入住日期、天数，再帮您筛选可订房间。')
      const { data } = await findAllCategoryAPI()
      this.categoryList = data || []
      this.step = 'date'
      this.pushBot('请先选择计划入住的日期。')
    },

    restart() {
      this.step = 'welcome'
      this.messages = []
      this.roomList = []
      this.selectedRoom = null
      this.draft = {
        startDate: dayjs().format('YYYY-MM-DD'),
        days: 1,
        categoryId: null
      }
      this.bootstrap()
    },

    confirmDate() {
      if (!this.draft.startDate) return
      this.pushUser(`入住日期：${this.draft.startDate}`)
      this.step = 'days'
      this.pushBot('请确认一共住几晚。')
    },

    confirmDays() {
      if (!this.draft.days) return
      this.pushUser(`住 ${this.draft.days} 晚`)
      this.step = 'category'
      this.pushBot('需要限定房型吗？可选「全部房型」或直接点下一步。')
    },

    async confirmCategory() {
      const name = this.draft.categoryId
        ? (this.categoryList.find(c => c.id === this.draft.categoryId) || {}).categoryName || '所选房型'
        : '不限房型'
      this.pushUser(`房型筛选：${name}`)
      this.step = 'pickRoom'
      await this.loadRooms()
      this.pushBot('请点击一间「可订」的房间。')
    },

    async loadRooms() {
      const { data } = await findRoomListAPI({
        startDate: this.draft.startDate,
        categoryId: this.draft.categoryId,
        canUse: true
      })
      this.roomList = data || []
    },

    async selectRoom(room) {
      if (!room.canUse) {
        this.$message.warning('该房间当前不可预订')
        return
      }
      this.selectedRoom = room
      const { data } = await findRoomByIdAPI(room.id)
      this.selectedRoom = data
      this.pushUser(
        `选择房间：${data.category.categoryName} ${data.roomNum}（¥${data.category.price}/晚）`
      )
      this.step = 'confirm'
      const dateLabel = this.draft.startDate
      this.pushBot(
        `请确认：${dateLabel} 起住 ${this.draft.days} 晚，房型 ${data.category.categoryName}，房号 ${data.roomNum}。点击下方「确认预订」提交。`
      )
    },

    goStep(s) {
      this.step = s
      if (s === 'pickRoom') {
        this.loadRooms()
      }
    },

    async submitBooking() {
      if (this.submitting) return
      if (!this.token) {
        this.$message.error('请先登录')
        this.$router.push('/login')
        return
      }
      if (this.role === 1) {
        this.$message.error('管理员请使用后台办理入住')
        return
      }
      if (!this.draft.startDate || !this.draft.days || !this.selectedRoom) {
        this.$message.error('信息不完整')
        return
      }
      const { data: room } = await findRoomByIdAPI(this.selectedRoom.id)
      const d = this.draft.startDate
      if (room.notUseDateList && room.notUseDateList.includes(d)) {
        this.$message.error('该日期此房不可订，请更换日期或房间')
        this.step = 'date'
        this.pushBot('该日期在此房不可用，请重新选择入住日期。')
        return
      }
      this.submitting = true
      try {
        const res = await addAppointmentAPI({
          roomId: room.id,
          startTime: `${this.draft.startDate} 00:00:00`,
          days: this.draft.days
        })
        this.$message({
          message: res.message,
          type: res.flag ? 'success' : 'error'
        })
        if (res.flag) {
          this.pushUser('已提交预订')
          this.pushBot(res.message || '预约已提交，可到「我的预约」查看进度。')
          this.step = 'done'
        }
      } catch (e) {
        /* request 已 toast */
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="less" scoped>
.chat-book {
  max-width: 720px;
  margin: 24px auto;
  padding: 0 12px;
}

.chat-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.messages {
  min-height: 280px;
  max-height: 420px;
  overflow-y: auto;
  padding: 8px 4px 16px;
  background: #f5f7fa;
  border-radius: 6px;
}

.bubble-row {
  display: flex;
  margin-bottom: 12px;
  &.is-user {
    justify-content: flex-end;
    .bubble {
      background: #409eff;
      color: #fff;
    }
  }
  &.is-bot {
    justify-content: flex-start;
    .bubble {
      background: #fff;
      color: #303133;
      border: 1px solid #ebeef5;
    }
  }
}

.bubble {
  max-width: 85%;
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.5;
  font-size: 14px;
}

.bubble-text {
  word-break: break-word;
}

.composer {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.composer-grow {
  flex: 1;
  min-width: 200px;
}

.hint {
  color: #909399;
  font-size: 13px;
}

.room-pick {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.room-chip {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  cursor: pointer;
  background: #fff;
  transition: border-color 0.2s;
  &:hover:not(.disabled) {
    border-color: #409eff;
  }
  &.disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }
}

.chip-title {
  flex: 1;
  font-weight: 500;
}

.chip-meta {
  color: #f56c6c;
  font-size: 13px;
}
</style>
