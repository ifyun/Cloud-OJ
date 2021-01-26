<template>
  <div class="error">
    <span class="error-code">{{ error.code }}</span>
    <span class="error-text">{{ error.msg }}</span>
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
  mounted() {
    this.siteSetting.setTitle(this.error.code)
  }
}
</script>

<style scoped>
.error {
  margin-top: 86px;
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