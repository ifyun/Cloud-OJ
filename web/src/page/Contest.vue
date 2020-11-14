<template>
  <div>
    <TopNavigation active="2"/>
    <ContestList/>
    <BottomArea style="margin-top: 35px"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ContestList from "@/components/ContestList"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Contest",
  components: {
    TopNavigation,
    ContestList,
    BottomArea
  }
}
</script>

<style scoped>

</style>