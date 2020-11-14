<template>
  <div>
    <TopNavigation active=""/>
    <Error :error="error"/>
    <BottomArea class="bottom"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import Error from "@/components/Error"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Page404",
  components: {
    TopNavigation,
    Error,
    BottomArea
  },
  mounted() {
    document.title = '404 · Cloud OJ'
  },
  data() {
    return {
      error: {
        code: 404,
        text: '页面不存在'
      }
    }
  }
}
</script>

<style scoped>
</style>