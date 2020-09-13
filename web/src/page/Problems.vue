<template>
  <div>
    <TopNavigation :active="active"/>
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
  name: 'Problems',
  mounted() {
    this.active = this.contestId == null ? '1' : '2'
  },
  data() {
    return {
      active: '',
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
