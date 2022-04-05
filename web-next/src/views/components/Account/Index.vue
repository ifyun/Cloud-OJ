<template>
  <div class="account">
    <n-card :bordered="false">
      <template #cover>
        <profile :user-id="userId" style="padding: 12px 24px" />
      </template>
      <n-space vertical>
        <n-tabs type="line">
          <n-tab-pane name="profile" tab="概览">
            <overview :user-id="userId" />
          </n-tab-pane>
          <n-tab-pane name="solutions" tab="提交记录"></n-tab-pane>
        </n-tabs>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { setTitle } from "@/utils"
import { computed, onBeforeMount } from "vue"
import { useStore } from "vuex"
import { useRoute } from "vue-router"
import { NCard, NSpace, NTabPane, NTabs } from "naive-ui"
import Profile from "./Profile.vue"
import Overview from "./Overview.vue"
import { UserInfo } from "@/api/type"

const route = useRoute()
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

onBeforeMount(() => {
  setTitle("个人中心")
})
</script>

<style scoped>
.account {
  width: 1100px;
  padding: var(--layout-padding) 0;
  margin: 0 auto;
}
</style>
