<template>
  <div>
    <TopNavigation active="3"/>
    <RankingList/>
    <BottomArea style="margin-top: 35px"/>
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

</style>