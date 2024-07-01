<template>
  <n-notification-provider>
    <n-message-provider>
      <n-dialog-provider>
        <n-layout position="absolute" :has-sider="true">
          <n-layout-sider
            class="aside"
            collapse-mode="width"
            width="160"
            bordered
            :native-scrollbar="false"
            :collapsed="collapsed">
            <div>
              <logo
                :collapsed="collapsed"
                style="height: var(--header-height)" />
              <admin-navbar />
            </div>
          </n-layout-sider>
          <n-layout style="min-width: 700px">
            <n-layout-header class="header" position="absolute" bordered>
              <div class="admin-nav">
                <n-flex align="center" size="small">
                  <n-button quaternary style="padding: 0 6px" @click="collapse">
                    <template #icon>
                      <n-icon>
                        <menu-open-round :class="{ rotate: collapsed }" />
                      </n-icon>
                    </template>
                  </n-button>
                  <n-button quaternary style="padding: 0 6px" @click="reload">
                    <template #icon>
                      <n-icon>
                        <refresh-round />
                      </n-icon>
                    </template>
                  </n-button>
                  <n-breadcrumb v-if="breadcrumb != null">
                    <n-breadcrumb-item v-for="item in breadcrumb" :key="item">
                      {{ item }}
                    </n-breadcrumb-item>
                  </n-breadcrumb>
                </n-flex>
                <div style="margin-left: auto">
                  <n-flex size="large" align="center">
                    <theme-switch />
                    <user-menu />
                  </n-flex>
                </div>
              </div>
            </n-layout-header>
            <n-layout-content
              class="main admin"
              position="absolute"
              :native-scrollbar="false"
              content-style="display: flex; flex-direction: column">
              <router-view v-slot="{ Component }">
                <keep-alive :key="$route.path">
                  <component :is="Component" />
                </keep-alive>
              </router-view>
            </n-layout-content>
          </n-layout>
        </n-layout>
      </n-dialog-provider>
    </n-message-provider>
  </n-notification-provider>
</template>

<script setup lang="ts">
import { Logo } from "@/components"
import { useStore } from "@/store"
import { AdminNavbar, ThemeSwitch, UserMenu } from "@/views/layout"
import { MenuOpenRound, RefreshRound } from "@vicons/material"
import {
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NDialogProvider,
  NFlex,
  NIcon,
  NLayout,
  NLayoutContent,
  NLayoutHeader,
  NLayoutSider,
  NMessageProvider,
  NNotificationProvider
} from "naive-ui"
import { computed, inject } from "vue"

const store = useStore()

const breadcrumb = computed(() => {
  return store.app.breadcrumb
})

const collapsed = computed(() => {
  return store.app.menuCollapsed
})

const reload = inject("reload", () => {})

function collapse() {
  store.app.menuCollapse()
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
