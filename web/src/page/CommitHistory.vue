<template>
  <div>
    <TopNavigation active=""/>
    <HistoryList/>
    <BottomArea class="bottom"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import HistoryList from "@/components/HistoryList"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "CommitHistory",
  components: {
    HistoryList,
    TopNavigation,
    BottomArea
  },
  mounted() {
    document.title = '提交记录 · Cloud OJ'
  }
}
</script>

<style scoped>

</style>