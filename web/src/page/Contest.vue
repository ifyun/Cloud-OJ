<template>
  <div>
    <TopNavigation active="2"/>
    <el-container class="container">
      <ContestList/>
      <BottomArea class="bottom"/>
    </el-container>
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
.container {
  padding: 0 20px;
  flex-direction: column;
  align-items: center;
}
</style>