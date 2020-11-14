<template>
  <div>
    <TopNavigation/>
    <CommitCode/>
    <BottomArea class="bottom"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import CommitCode from "@/components/CodeCommit"

export default {
  name: "Commit",
  components: {
    TopNavigation,
    BottomArea,
    CommitCode
  }
}
</script>

<style scoped>

</style>