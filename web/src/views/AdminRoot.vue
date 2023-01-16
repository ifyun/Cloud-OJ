<template>
  <n-layout position="absolute" :has-sider="true">
    <n-config-provider
      abstract
      :theme="darkTheme"
      :theme-overrides="themeOverrides">
      <n-layout-sider
        class="aside"
        collapse-mode="width"
        width="160"
        :native-scrollbar="false"
        :collapsed="collapsed">
        <div>
          <logo :collapsed="collapsed" style="height: 50px" />
          <admin-navbar />
        </div>
      </n-layout-sider>
    </n-config-provider>
    <n-layout :native-scrollbar="false">
      <n-layout-header class="header" position="absolute">
        <div class="admin-nav">
          <n-space align="center" size="small">
            <n-button quaternary style="padding: 0 6px" @click="collapse">
              <template #icon>
                <n-icon>
                  <menu-open :class="{ rotate: collapsed }" />
                </n-icon>
              </template>
            </n-button>
            <n-button quaternary style="padding: 0 6px" @click="reload">
              <template #icon>
                <n-icon>
                  <refresh-icon />
                </n-icon>
              </template>
            </n-button>
            <n-breadcrumb v-if="breadcrumb != null">
              <n-breadcrumb-item v-for="item in breadcrumb" :key="item">
                {{ item }}
              </n-breadcrumb-item>
            </n-breadcrumb>
          </n-space>
          <div style="margin-left: auto">
            <n-space size="large" align="center">
              <theme-switch />
              <user-menu />
            </n-space>
          </div>
        </div>
      </n-layout-header>
      <n-layout-content
        class="main admin"
        position="absolute"
        :embedded="true"
        :native-scrollbar="false"
        content-style="display: flex; flex-direction: column">
        <router-view v-slot="{ Component }" :key="$route.fullPath">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, nextTick } from "vue"
import { useStore } from "vuex"
import {
  darkTheme,
  GlobalThemeOverrides,
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NConfigProvider,
  NIcon,
  NLayout,
  NLayoutContent,
  NLayoutHeader,
  NLayoutSider,
  NSpace
} from "naive-ui"
import {
  MenuOpenRound as MenuOpen,
  RefreshRound as RefreshIcon
} from "@vicons/material"
import { AdminNavbar, ThemeSwitch, UserMenu } from "@/views/layout"
import { Logo } from "@/components"
import Mutations from "@/store/mutations"

const store = useStore()

const themeOverrides = computed<GlobalThemeOverrides>(() => {
  if (store.state.theme != null) {
    return {}
  }

  return {
    Layout: {
      siderColor: "#161b22"
    }
  }
})

const breadcrumb = computed<Array<string>>(() => {
  return store.state.breadcrumb
})

const collapsed = computed<boolean>(() => {
  return store.state.menuCollapsed
})

function reload() {
  store.commit(Mutations.SET_RELOAD, true)
  nextTick(() => {
    store.commit(Mutations.SET_RELOAD, false)
  })
}

function collapse() {
  store.commit(Mutations.TOGGLE_MENU_COLLAPSED)
}
</script>

<style scoped lang="scss">
.admin-nav {
  height: var(--header-height);
  padding: 0 var(--layout-padding);
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.rotate {
  transform: perspective(1px) rotateY(180deg);
}
</style>
