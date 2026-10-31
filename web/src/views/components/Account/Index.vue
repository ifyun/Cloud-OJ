<template>
  <div class="wrap">
    <n-card v-if="user.uid">
      <template #cover>
        <user-profile
          :user="user"
          :show-edit="isSelf"
          style="padding: 12px 24px" />
      </template>
      <n-space vertical>
        <n-tabs type="line" :value="tab" @update:value="changeTab">
          <n-tab-pane name="profile" tab="概览" display-directive="show">
            <user-summary :uid="user.uid!" :show-timeline="isSelf" />
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
import { UserApi } from "@/api/request"
import { User } from "@/api/type"
import { useAppStore } from "@/store/app.ts"
import { useUserStore } from "@/store/user.ts"
import { setTitle } from "@/utils"
import { NCard, NSpace, NTabPane, NTabs } from "naive-ui"
import { computed, onMounted, ref, watchEffect } from "vue"
import { useRoute, useRouter } from "vue-router"
import SolutionList from "./Solutions.vue"
import UserSummary from "./Summary.vue"
import UserProfile from "./UserProfile.vue"

defineOptions({
  name: "AccountIndex"
})

const app = useAppStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const props = defineProps<{ uid?: number }>()
const user = ref<User>(new User())
const tab = ref("profile")

const isSelf = computed<boolean>(() => {
  if (userStore.isLoggedIn) {
    return props.uid === undefined || userStore.info!.uid! === props.uid
  } else {
    return false
  }
})

watchEffect(() => {
  if (isSelf.value) {
    user.value.uid = userStore.info!.uid!
  } else {
    user.value.uid = props.uid
  }

  if (!userStore.isLoggedIn && props.uid === undefined) {
    router.replace({ name: "auth" })
  }
})

onMounted(() => {
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
  router.replace({
    query: { tab: value }
  })
}

function getUserProfile() {
  UserApi.getProfile(user.value.uid!)
    .then((data) => {
      user.value = data
      if (isSelf.value) {
        setTitle("个人中心")
      } else {
        setTitle(data.nickname!)
      }
    })
    .catch((err) => {
      app.setError(err)
    })
}
</script>
