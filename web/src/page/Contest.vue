<template>
  <div>
    <el-container class="container">
      <contest-list/>
      <bottom-area class="bottom"/>
    </el-container>
  </div>
</template>

<script>
import ContestList from "@/components/ContestList"
import BottomArea from "@/components/common/BottomArea"

export default {
  name: "Contest",
  components: {
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