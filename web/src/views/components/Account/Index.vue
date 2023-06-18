<template>
  <div style="width: 1100px; padding: var(--layout-padding) 0; margin: 0 auto">
    <n-card>
      <template #cover>
        <user-profile :user-id="userId" style="padding: 12px 24px" />
      </template>
      <n-space vertical>
        <n-tabs type="line" :value="tab" @update:value="changeTab">
          <n-tab-pane name="profile" tab="概览">
            <overview :user-id="userId" />
          </n-tab-pane>
          <n-tab-pane name="solutions" tab="提交记录" display-directive="show">
            <solution-list :problem-id="null" />
          </n-tab-pane>
        </n-tabs>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { setTitle } from "@/utils"
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRoute, useRouter } from "vue-router"
import { NCard, NSpace, NTabPane, NTabs } from "naive-ui"
import UserProfile from "./UserProfile.vue"
import Overview from "./Overview.vue"
import SolutionList from "@/views/components/Solutions.vue"
import { UserInfo } from "@/api/type"

const route = useRoute()
const router = useRouter()
const store = useStore()

const userInfo = computed<UserInfo | null>(() => {
  return store.state.userInfo
})

const userId = computed<string>(() => {
  const userId = route.params.id
  if (userId) {
    return userId.toString()
  } else if (userInfo.value != null) {
    return userInfo.value.userId!
  } else {
    return ""
  }
})

const tab = ref("profile")

onBeforeMount(() => {
  setTitle("个人中心")
  if (route.query.tab && route.query.tab.toString() === "solutions") {
    tab.value = "solutions"
  }
})

function changeTab(value: string) {
  tab.value = value
  router.push({
    query: { tab: value }
  })
}
</script>
