<template>
  <div class="error">
    <span class="error-code">{{ error.code }}</span>
    <span class="error-text">{{ error.msg }}</span>
    <el-button-group style="margin-top: 25px">
      <el-button icon="el-icon-back" size="small" @click="back">返回上页</el-button>
      <el-button icon="el-icon-s-home" size="small" @click="gotoIndex">返回主页</el-button>
    </el-button-group>
  </div>
</template>

<script>
export default {
  name: "Error",
  props: {
    error: {
      code: Number,
      msg: String
    }
  },
  methods: {
    gotoIndex() {
      window.location.href = "./"
    },
    back() {
      window.history.back()
    }
  }
}
</script>

<style scoped>
.error {
  margin-top: 50px;
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