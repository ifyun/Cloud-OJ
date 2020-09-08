<template>
  <div>
    <TopNavigation/>
    <CommitCode/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import CommitCode from "@/components/CodeCommit"

export default {
  name: "Commit",
  components: {
    TopNavigation,
    CommitCode
  }
}
</script>

<style scoped>

</style>