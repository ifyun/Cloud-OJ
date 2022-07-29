<template>
  <n-layout
    position="absolute"
    content-style="display: flex; flex-direction: column">
    <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
      <n-layout-header>
        <app-logo style="height: 60px" />
      </n-layout-header>
    </n-config-provider>
    <n-layout-content embedded>
      <n-card
        v-if="checked"
        style="width: 400px; margin: 24px auto 0 auto"
        :bordered="false">
        <n-tabs size="large" type="line" default-value="login">
          <n-tab-pane name="login" tab="登录">
            <login />
          </n-tab-pane>
          <n-tab-pane name="signup" tab="注册">
            <signup />
          </n-tab-pane>
        </n-tabs>
      </n-card>
    </n-layout-content>
    <n-layout-footer style="margin-top: auto">
      <bottom-info />
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, onBeforeMount, ref } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import {
  darkTheme,
  GlobalThemeOverrides,
  NCard,
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader,
  NTabPane,
  NTabs
} from "naive-ui"
import Login from "./Login.vue"
import Signup from "./Signup.vue"
import AppLogo from "@/components/Logo.vue"
import BottomInfo from "@/views/layout/BottomInfo.vue"
import { AuthApi } from "@/api/request"

const store = useStore()
const router = useRouter()

const checked = ref<boolean>(false)

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return {}
  }

  return {
    Layout: {
      headerColor: "#161B22FF",
      headerColorInverted: "#161B22FF"
    }
  }
})

onBeforeMount(async () => {
  if (store.state.userInfo != null) {
    try {
      await AuthApi.verify(store.state.userInfo)
      await router.push({ path: "/" })
    } catch (error) {
      checked.value = true
    }
  } else {
    checked.value = true
  }
})
</script>
