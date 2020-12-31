<template>
  <div>
    <TopNavigation active=""/>
    <el-container class="container">
      <HistoryList/>
      <BottomArea class="bottom"/>
    </el-container>
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
    document.title = "提交记录 - Cloud OJ"
  }
}
</script>

<style scoped>
.container {
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>