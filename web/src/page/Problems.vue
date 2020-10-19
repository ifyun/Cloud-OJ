<template>
  <div>
    <TopNavigation :active="active"/>
    <ProblemList
        :contest-id="this.contestId"
        :contest-name="this.contestName"/>
    <BottomArea style="margin-top: 35px"/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation"
import BottomArea from "@/components/common/BottomArea"
import ProblemList from "@/components/ProblemList"
import {searchParams} from "@/js/util"

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
    BottomArea,
    ProblemList
  }
}
</script>

<style scoped>
</style>
