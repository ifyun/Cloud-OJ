<template>
  <n-layout position="absolute" :has-sider="true">
    <n-layout-sider
      collapse-mode="width"
      width="160"
      bordered
      :native-scrollbar="false"
      :collapsed="collapsed"
      style="z-index: 2">
      <div>
        <logo :collapsed="collapsed" style="height: var(--header-height)" />
        <admin-nav />
      </div>
    </n-layout-sider>
    <n-layout>
      <n-layout-header
        position="absolute"
        bordered
        style="z-index: 1; height: var(--header-height)">
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
        position="absolute"
        :native-scrollbar="false"
        style="top: var(--header-height)"
        :content-style="{
          display: 'flex',
          flexDirection: 'column',
          minHeight: '100%'
        }">
        <router-layout />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { Logo } from "@/components"
import { useStore } from "@/store"
import { AdminNav, RouterLayout, ThemeSwitch, UserMenu } from "@/views/layout"
import { MenuOpenRound, RefreshRound } from "@vicons/material"
import {
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NFlex,
  NIcon,
  NLayout,
  NLayoutContent,
  NLayoutHeader,
  NLayoutSider
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
.rotate {
  transform: perspective(1px) rotateY(180deg);
}

.admin-nav {
  height: var(--header-height);
  padding: 0 var(--layout-padding);
  display: flex;
  align-items: center;
}

:deep(.admin-wrap) {
  padding: var(--layout-padding);
}
</style>
