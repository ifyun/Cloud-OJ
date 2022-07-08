<!-- 竞赛题目组件 -->
<template>
  <div v-if="error == null" class="problem-table">
    <n-card :bordered="false" :segmented="{ content: true }">
      <template #cover>
        <div v-if="contest != null" style="padding: 24px 24px 12px 24px">
          <n-space vertical size="large">
            <n-h2 style="margin: 0">
              <n-text type="primary">{{ contest.contestName }}</n-text>
            </n-h2>
            <n-space size="small" align="center">
              <n-icon
                v-for="id in languages"
                :key="id"
                :color="LanguageIcons[id].color"
                size="20">
                <component :is="LanguageIcons[id].component" />
              </n-icon>
            </n-space>
          </n-space>
        </div>
      </template>
      <n-space vertical size="large">
        <n-data-table :columns="columns" :data="problems" :loading="loading" />
      </n-space>
    </n-card>
  </div>
  <div v-else>
    <error-result :error="error" />
  </div>
</template>

<script setup lang="tsx">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import { NButton, NCard, NDataTable, NH2, NIcon, NSpace, NText } from "naive-ui"
import { ErrorResult } from "@/components"
import { LanguageIcons } from "@/type"
import { ContestApi } from "@/api/request"
import { Contest, ErrorMsg, Problem } from "@/api/type"
import { LanguageUtil } from "@/utils"

const store = useStore()
const router = useRouter()

const props = defineProps<{
  cid: string
}>()

const error = ref<ErrorMsg | null>(null)
const userInfo = computed(() => store.state.userInfo)

const loading = ref<boolean>(false)
const contest = ref<Contest | null>(null)
const languages = ref<Array<number>>([])
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
    queryContest(Number(props.cid))
  }
})

function queryContest(cid: number) {
  loading.value = true
  ContestApi.getById(cid)
    .then((data) => {
      contest.value = data
      if (data.languages < 511) {
        languages.value = LanguageUtil.toArray(data.languages)
      }
      queryProblems(cid)
    })
    .catch((err) => {
      error.value = err
      loading.value = false
    })
}

function queryProblems(cid: number) {
  ContestApi.getProblemsFromStarted(cid, userInfo.value)
    .then((data) => {
      problems.value = data
    })
    .catch((err) => {
      err.value = err
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
