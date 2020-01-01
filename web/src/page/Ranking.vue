<template>
  <div>
    <TopNavigation active="3"/>
    <RankingList/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import RankingList from "@/components/RankingList";

export default {
  name: "Ranking",
  components: {
    TopNavigation,
    RankingList
  }
}
</script>

<style scoped>

</style>