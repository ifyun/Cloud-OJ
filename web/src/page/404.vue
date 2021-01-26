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
  beforeMount() {
    this.siteSetting.setTitle("404")
  },
  data() {
    return {
      error: {
        code: 404,
        msg: "Page Not Found"
      }
    }
  }
}
</script>

<style scoped>
</style>