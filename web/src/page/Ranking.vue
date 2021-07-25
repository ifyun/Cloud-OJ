<template>
  <div>
    <el-container class="container">
      <ranking-list/>
      <bottom-area class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import RankingList from "@/components/RankingList"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Ranking",
  components: {
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