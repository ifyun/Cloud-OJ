<template>
  <div class="wrap">
    <n-card v-if="uid">
      <template #cover>
        <user-profile
          :user="user"
          :show-edit="isSelf"
          style="padding: 12px 24px" />
      </template>
      <n-space vertical>
        <n-tabs type="line" :value="tab" @update:value="changeTab">
          <n-tab-pane name="profile" tab="概览">
            <overview :uid="uid!" :show-timeline="isSelf" />
          </n-tab-pane>
          <n-tab-pane
            v-if="isSelf"
            name="solutions"
            tab="提交记录"
            display-directive="show">
            <solution-list :problem-id="null" />
          </n-tab-pane>
        </n-tabs>
      </n-space>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { useStore } from "@/store"
import { setTitle } from "@/utils"
import { NCard, NSpace, NTabPane, NTabs } from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import Overview from "./Overview.vue"
import SolutionList from "./Solutions.vue"
import UserProfile from "./UserProfile.vue"
import { UserApi } from "@/api/request"
import { User } from "@/api/type"

const store = useStore()
const route = useRoute()
const router = useRouter()

const uid = ref<number>()
const user = ref<User>(new User())
const tab = ref("profile")

const isSelf = computed<boolean>(() => {
  if (!store.user.isLoggedIn) {
    return false
  }

  return store.user.userInfo!.uid! == uid.value
})

onBeforeMount(() => {
  const reg = /^\d+$/
  if (route.params.uid && reg.test(route.params.uid.toString())) {
    uid.value = Number(route.params.uid)
  } else if (store.user.isLoggedIn) {
    uid.value = store.user.userInfo!.uid!
  } else {
    // 未登录，uid 不是数字
    store.app.setError({
      status: 404,
      error: "Not Found",
      message: "找不到用户"
    })
  }

  if (
    isSelf.value &&
    route.query.tab &&
    route.query.tab.toString() === "solutions"
  ) {
    tab.value = "solutions"
  }

  getUserProfile()
})

function changeTab(value: string) {
  tab.value = value
  router.push({
    query: { tab: value }
  })
}

function getUserProfile() {
  user.value.uid = uid.value
  UserApi.getProfile(uid.value!)
    .then((data) => {
      user.value = data
      if (isSelf.value) {
        setTitle("个人中心")
      } else {
        setTitle(data.nickname!)
      }
    })
    .catch((err) => {
      store.app.setError(err)
    })
}
</script>
