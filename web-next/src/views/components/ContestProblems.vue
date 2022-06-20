<!-- 竞赛题目组件 -->
<template>
  <div class="problem-table">
    <n-card :bordered="false">
      <n-space vertical size="large">
        <n-data-table :columns="columns" :data="problems" />
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import { NCard, NDataTable, NSpace, NButton } from "naive-ui"
import { ContestApi } from "@/api/request"
import { Problem } from "@/api/type"

const store = useStore()
const router = useRouter()

const props = defineProps<{
  cid: string
}>()

const userInfo = computed(() => store.state.userInfo)

const loading = ref<boolean>(false)
const problems = ref<Array<Problem>>([])

const columns = [
  {
    title: "#",
    align: "right",
    width: 50,
    render: (row: Problem, rowIndex: number) => <span>{rowIndex + 1}</span>
  },
  {
    title: "ID",
    key: "problemId",
    align: "right",
    width: 70
  },
  {
    title: "题目名称",
    render: (row: Problem) => (
      <NButton
        text
        onClick={() =>
          router.push({
            name: "submission",
            params: { pid: row.problemId }
          })
        }>
        {row.title}
      </NButton>
    )
  },
  {
    title: "分数",
    key: "score",
    align: "right"
  }
]

onBeforeMount(() => {
  const reg = /^\d+$/
  if (reg.test(props.cid)) {
    queryProblems(Number(props.cid))
  }
})

function queryProblems(cid: number) {
  loading.value = true
  ContestApi.getProblemsFromStarted(cid, userInfo.value)
    .then((data) => {
      problems.value = data
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style scoped lang="scss">
.problem-table {
  width: 1100px;
  padding: var(--layout-padding) 0;
  margin: 0 auto;
}
</style>
