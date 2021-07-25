<template>
  <div style="margin-top: 61px">
    <error :error="error"/>
    <bottom-area class="bottom"/>
  </div>
</template>

<script>
import Error from "@/components/Error"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Page404",
  components: {
    Error,
    BottomArea
  },
  beforeMount() {
    this.siteSetting.setTitle("404")
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