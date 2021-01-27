<template>
  <div>
    <TopNavigation/>
    <CommitCode/>
    <BottomArea class="bottom"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import CommitCode from "@/components/Commit"

export default {
  name: "Commit",
  components: {
    TopNavigation,
    BottomArea,
    CommitCode
  },
  mounted() {
    /* eslint-disable no-console */
    console.log(
        "%c别学了，打游戏不香吗?",
        "color: white; background: orange; padding: 2px 8px;" +
        "font-family: sans-serif;"
    )
  }
}
</script>

<style scoped>

</style>