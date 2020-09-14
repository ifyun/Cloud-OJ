<template>
  <div>
    <TopNavigation active=""/>
    <HistoryList/>
  </div>
</template>

<script>
import TopNavigation from "@/components/common/TopNavigation";
import HistoryList from "@/components/HistoryList";

export default {
  name: "CommitHistory",
  components: {HistoryList, TopNavigation},
  mounted() {
    document.title = '提交记录 · Cloud OJ'
  }
}
</script>

<style scoped>

</style>