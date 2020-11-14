<template>
  <div class="error-404">
    <span class="error-code">{{ error.code }}</span>
    <span class="error-text">{{ error.text }}</span>
    <el-button-group style="margin-top: 25px">
      <el-button size="mini" @click="back">返回上页</el-button>
      <el-button size="mini" @click="gotoIndex">返回主页</el-button>
    </el-button-group>
  </div>
</template>

<script>
export default {
  name: "Error",
  props: {
    error: {
      code: Number,
      text: String
    }
  },
  methods: {
    gotoIndex() {
      window.location.href = './'
    },
    back() {
      window.history.back()
    }
  }
}
</script>

<style scoped>
.error-404 {
  margin-top: 110px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #F56C6C;
}

.error-code {
  font-size: 130pt;
  font-weight: bold;
  opacity: 0.8;
}

.error-text {
  font-size: 20pt;
  opacity: 0.8;
}
</style>