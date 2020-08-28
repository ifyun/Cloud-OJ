<template>
  <div>
    <TopNavigation active="1-2"/>
    <CompetitionList/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import CompetitionList from "@/components/CompetitionList"

export default {
  name: "Competition",
  components: {
    TopNavigation,
    CompetitionList
  }
}
</script>

<style scoped>

</style>