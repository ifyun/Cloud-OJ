<template>
  <n-layout position="absolute" :native-scrollbar="false">
    <n-config-provider :theme="darkTheme" :theme-overrides="themeOverrides">
      <n-layout-header class="header">
        <top-navbar />
      </n-layout-header>
    </n-config-provider>
    <n-layout-content
      class="main"
      :class="classes"
      :position="position"
      :native-scrollbar="false"
      content-style="display: flex; flex-direction: column">
      <router-view v-slot="{ Component }" :key="$route.fullPath">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
      <n-layout-footer class="footer">
        <bottom-info />
      </n-layout-footer>
    </n-layout-content>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import type { GlobalThemeOverrides } from "naive-ui"
import {
  darkTheme,
  NConfigProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader
} from "naive-ui"
import { BottomInfo, TopNavbar } from "@/views/layout"

const router = useRouter()
const store = useStore()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return {}
  }

  return {
    Layout: {
      headerColor: "#161b22"
    }
  }
})

const position = ref<"static" | "absolute">("static")
const classes = ref<string>("")

// 监听路由，如果是提交页面，改为绝对定位
watch(
  () => router.currentRoute.value.name,
  (value) => {
    if (value === "submission") {
      position.value = "absolute"
      classes.value = value
    } else {
      position.value = "static"
      classes.value = ""
    }
  },
  { immediate: true }
)
</script>
