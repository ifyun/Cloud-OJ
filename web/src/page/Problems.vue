<template>
  <div>
    <TopNavigation :active="active"/>
    <el-container class="container">
      <ProblemList/>
      <BottomArea class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import ProblemList from "@/components/ProblemList"

export default {
  name: "Problems",
  mounted() {
    this.active = this.contestId == null ? "1" : "2"
  },
  data() {
    return {
      active: ""
    }
  },
  components: {
    TopNavigation,
    BottomArea,
    ProblemList
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
