<template>
  <n-config-provider abstract :theme-overrides="themeOverrides">
    <n-message-provider>
      <n-layout
        position="absolute"
        content-style="display: flex; flex-direction: column">
        <n-layout-header bordered>
          <app-logo style="height: 50px" />
        </n-layout-header>
        <n-layout-content>
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
        </n-layout-content>
        <n-layout-footer style="margin-top: auto">
          <bottom-info />
        </n-layout-footer>
      </n-layout>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import AppLogo from "@/components/Logo.vue"
import { useStore } from "@/store"
import { themeDark } from "@/theme"
import BottomInfo from "@/views/layout/BottomInfo.vue"
import {
  GlobalThemeOverrides,
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader,
  NMessageProvider,
  NTabPane,
  NTabs
} from "naive-ui"
import { computed, onBeforeMount, ref } from "vue"
import { useRouter } from "vue-router"
import Login from "./Login.vue"
import Signup from "./Signup.vue"

const store = useStore()
const router = useRouter()
const checked = ref<boolean>(false)

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.app.theme != null) {
    return themeDark
  }

  return {}
})

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
