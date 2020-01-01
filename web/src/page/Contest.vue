<template>
  <div>
    <TopNavigation active="2"/>
    <ContestList/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ContestList from "@/components/ContestList"

export default {
  name: "Contest",
  components: {
    TopNavigation,
    ContestList
  }
}
</script>

<style scoped>

</style>