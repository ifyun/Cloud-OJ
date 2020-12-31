<template>
  <div>
    <TopNavigation active="3"/>
    <el-container class="container">
      <RankingList/>
      <BottomArea class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import RankingList from "@/components/RankingList"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Ranking",
  components: {
    TopNavigation,
    RankingList,
    BottomArea
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