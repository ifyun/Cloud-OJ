<template>
  <div>
    <el-container class="container">
      <problem-list/>
      <bottom-area class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import BottomArea from "@/components/common/BottomArea"
import ProblemList from "@/components/ProblemList"

export default {
  name: "Problems",
  components: {
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
