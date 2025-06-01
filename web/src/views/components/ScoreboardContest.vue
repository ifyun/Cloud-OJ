<template>
  <div class="wrap" style="max-width: 100%">
    <empty-data v-if="noContent" />
    <div v-else>
      <n-flex vertical>
        <n-flex v-if="contestState != null" justify="space-between">
          <n-flex align="center">
            <n-tag round :bordered="false" :type="contestState.type">
              <template #icon>
                <n-icon :component="contestState.icon" />
              </template>
              {{ contestState.state }}
            </n-tag>
            <n-h4 style="margin: 0">
              {{ ranking!.contest!.contestName }}
            </n-h4>
          </n-flex>
        </n-flex>
        <empty-data v-if="noRanking" style="margin-top: 48px" />
        <n-table v-else>
          <thead>
            <tr>
              <th class="table-rank">排名</th>
              <th class="table-user">用户</th>
              <th class="table-badge"></th>
              <th
                v-for="(id, i) in ranking?.problemIds"
                :key="id"
                class="table-score">
                {{ String.fromCharCode(i + 65) }}
              </th>
              <th class="table-sum">提交 - 总分</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in ranking?.ranking" :key="user.uid">
              <td class="table-rank">
                <n-text>{{ user.rank }}</n-text>
              </td>
              <!-- User -->
              <td class="table-user">
                <n-flex size="small" align="center">
                  <user-avatar
                    size="large"
                    :uid="user.uid!"
                    :nickname="user.nickname"
                    :has-avatar="user.hasAvatar" />
                  <n-flex vertical :size="0">
                    <RouterLink
                      :to="{ name: 'account', params: { uid: user.uid } }">
                      <n-button text strong>
                        {{ `${user.nickname}${user.star ? "⭐" : ""}` }}
                      </n-button>
                    </RouterLink>
                    <n-text v-if="user.username" depth="3">
                      {{ user.realName }}@{{ user.username }}
                    </n-text>
                  </n-flex>
                </n-flex>
              </td>
              <td class="table-badge">
                <span class="badge">{{ user.badge }}</span>
              </td>
              <td
                v-for="item in user.details"
                :key="item.problemId"
                class="table-score">
                <n-text :style="{ color: resultColor(item.result) }">
                  {{ item.score ?? "" }}
                </n-text>
              </td>
              <td class="table-sum">
                <n-flex justify="center">
                  <n-text depth="2">
                    {{ user.committed }}
                  </n-text>
                  <n-text depth="3">-</n-text>
                  <n-text type="success" strong>
                    {{ user.score }}
                  </n-text>
                </n-flex>
              </td>
            </tr>
          </tbody>
        </n-table>
      </n-flex>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RankingApi } from "@/api/request"
import { ErrorMessage, RankingContest } from "@/api/type"
import { EmptyData, UserAvatar } from "@/components"
import { useStore } from "@/store"
import { setTitle, type StateTag, stateTag } from "@/utils"
import dayjs from "dayjs"
import {
  NButton,
  NFlex,
  NH4,
  NIcon,
  NTable,
  NTag,
  NText,
  useThemeVars
} from "naive-ui"
import {
  computed,
  nextTick,
  onDeactivated,
  onMounted,
  ref,
  shallowRef
} from "vue"

const props = defineProps<{
  cid: string
}>()

const store = useStore()
const themeVars = useThemeVars()

const loading = ref<boolean>(true)
const ranking = ref<RankingContest | null>(null)
const contestState = shallowRef<StateTag | null>(null)

const noContent = computed<boolean>(
  () => !loading.value && ranking.value == null
)

const noRanking = computed<boolean>(
  () => ranking.value != null && ranking.value.ranking?.length === 0
)

let contestId: number
let timeout: number | undefined

onMounted(() => {
  const reg = /^\d+$/
  if (reg.test(props.cid)) {
    contestId = Number(props.cid)
    queryRanking()
  } else {
    store.app.setError({
      status: 404,
      error: "Not Found",
      message: "找不到竞赛"
    })
  }
})

onDeactivated(() => {
  window.clearTimeout(timeout)
})

function queryRanking() {
  loading.value = true
  RankingApi.getContestRanking(contestId)
    .then((data) => {
      setTitle(`${data.contest!.contestName} | 排名`)
      contestState.value = stateTag(data.contest!)
      ranking.value = data

      nextTick(() => {
        const time = dayjs.unix(data.contest!.endAt! + 1800)
        if (dayjs().isBefore(time, "second")) {
          timeout = window.setTimeout(queryRanking, 30000)
        }
      })
    })
    .catch((err: ErrorMessage) => {
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}

function resultColor(r?: number) {
  if (typeof r === "undefined") {
    return themeVars.value.textColor3
  }

  if (r === 0) {
    return themeVars.value.successColor
  } else if (r === 3) {
    return themeVars.value.warningColor
  } else {
    return themeVars.value.errorColor
  }
}
</script>

<style lang="scss" scoped>
.table-rank {
  text-align: center;
  width: 30px;
}

.table-badge {
  text-align: center;
  width: 40px;

  .badge {
    line-height: 18px;
    font-size: 24px;
    vertical-align: middle;
  }
}

.table-user {
  text-align: start;
  width: 220px;
}

.table-score {
  text-align: center;
  width: 30px;
}

.table-sum {
  text-align: center;
  width: 100px;
}
</style>
