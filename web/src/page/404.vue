<template>
  <div>
    <error-info :error="error"/>
  </div>
</template>

<script>
import ErrorInfo from "@/components/ErrorInfo"

export default {
  name: "Page404",
  components: {
    ErrorInfo
  },
  beforeMount() {
    this.$siteSetting.setTitle("404")
  },
  data() {
    return {
      error: {
        code: 404,
        msg: "页面找不到了"
      }
    }
  }
}
</script>

<style scoped>
</style>