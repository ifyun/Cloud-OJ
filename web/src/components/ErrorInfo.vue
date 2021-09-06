<template>
  <div class="error">
    <el-result icon="error" :title="error.code.toString()" :sub-title="error.msg">
      <template slot="extra">
        <el-button type="primary" size="small" @click="back">返回上一页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script>
export default {
  name: "ErrorInfo",
  props: {
    error: {
      code: Number,
      msg: String
    }
  },
  mounted() {
    this.$siteSetting.setTitle(this.error.code)
  },
  methods: {
    back() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
</style>