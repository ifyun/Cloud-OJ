<template>
  <div>
    <TopNavigation active="1-1"/>
    <ProblemList
        :contest-id="this.contestId"
        :contest-name="this.contestName"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import ProblemList from "@/components/ProblemList"
import {searchParams} from "@/js/util";

export default {
  name: 'Index',
  data() {
    return {
      contestId: searchParams().contestId,
      contestName: searchParams().contestName
    }
  },
  components: {
    TopNavigation,
    ProblemList
  }
}
</script>

<style scoped>

</style>
