<template>
  <n-layout
    position="absolute"
    content-style="display: flex; flex-direction: column">
    <n-layout-header bordered style="height: var(--header-height)">
      <n-flex align="center" justify="center" style="height: 100%">
        <logo />
      </n-flex>
    </n-layout-header>
    <n-layout>
      <n-scrollbar class="global">
        <div v-if="checked" style="width: 320px; margin: 24px auto 0 auto">
          <n-tabs type="line" :value="tab" animated @update:value="tabChange">
            <n-tab-pane name="login" tab="登录">
              <login />
            </n-tab-pane>
            <n-tab-pane name="signup" tab="注册">
              <signup />
            </n-tab-pane>
          </n-tabs>
        </div>
        <n-layout-footer style="margin-top: auto">
          <bottom-info />
        </n-layout-footer>
      </n-scrollbar>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import { Logo } from "@/components"
import { useStore } from "@/store"
import BottomInfo from "@/views/layout/BottomInfo.vue"
import {
  NFlex,
  NLayout,
  NLayoutFooter,
  NLayoutHeader,
  NScrollbar,
  NTabPane,
  NTabs
} from "naive-ui"
import { onBeforeMount, ref } from "vue"
import { useRouter } from "vue-router"
import Login from "./Login.vue"
import Signup from "./Signup.vue"

const store = useStore()
const router = useRouter()
const checked = ref<boolean>(false)

withDefaults(defineProps<{ tab: string }>(), { tab: "login" })

onBeforeMount(async () => {
  if (store.user.userInfo != null) {
    try {
      await AuthApi.verify()
      await router.push({ name: "index" })
    } catch (error) {
      checked.value = true
    }
  } else {
    checked.value = true
  }
})

function tabChange(tab: string) {
  router.replace({ params: { tab } })
}
</script>
