<template>
  <n-layout position="absolute" :has-sider="true">
    <n-config-provider abstract :theme="darkTheme" :theme-overrides="themeOverrides">
      <n-layout-sider class="aside" collapse-mode="width" :native-scrollbar="false" width="180">
        <div>
          <logo style="height: 60px"/>
          <admin-menu/>
        </div>
      </n-layout-sider>
    </n-config-provider>
    <n-layout :native-scrollbar="false">
      <n-layout-header class="header" position="absolute">
        <div class="admin-nav">
          <n-space align="center" size="small">
            <n-button quaternary @click="reload">
              <template #icon>
                <n-icon>
                  <refresh-icon/>
                </n-icon>
              </template>
            </n-button>
            <v-node-renderer :v-node="breadcrumb"/>
          </n-space>
          <div style="margin-left: auto">
            <n-space size="large" align="center">
              <theme-switch/>
              <user-menu/>
            </n-space>
          </div>
        </div>
      </n-layout-header>
      <n-layout-content class="main" position="absolute" :embedded="true" :native-scrollbar="false"
                        content-style="display: flex; flex-direction: column">
        <router-view v-slot="{Component}" :key="$route.fullPath">
          <keep-alive>
            <component :is="Component"/>
          </keep-alive>
        </router-view>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import {computed, nextTick, VNode} from "vue"
import {useStore} from "vuex"
import {
  darkTheme,
  GlobalThemeOverrides,
  NButton,
  NConfigProvider,
  NIcon,
  NLayout,
  NLayoutContent,
  NLayoutHeader,
  NLayoutSider,
  NSpace
} from "naive-ui"
import {RefreshRound as RefreshIcon} from "@vicons/material"
import {AdminMenu, ThemeSwitch, UserMenu} from "@/views/layout"
import {Logo, VNodeRenderer} from "@/components"
import Mutations from "@/store/mutations"

const store = useStore()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return {}
  }

  return {
    Layout: {
      siderColor: "#161B22FF",
      siderColorInverted: "#161B22FF",
    }
  }
})

const breadcrumb = computed<VNode>(() => {
  return store.state.breadcrumb
})

async function reload() {
  store.commit(Mutations.SET_RELOAD, true)
  await nextTick()
  store.commit(Mutations.SET_RELOAD, false)
}
</script>

<style scoped lang="scss">
.admin-nav {
  height: 60px;
  padding: 0 var(--layout-padding);
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
</style>
