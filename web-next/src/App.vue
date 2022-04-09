<template>
  <n-config-provider
    abstract
    :theme="theme"
    :theme-overrides="themeOverrides"
    :locale="zhCN"
    :date-locale="dateZhCN">
    <n-dialog-provider>
      <n-message-provider>
        <n-global-style />
        <router-view
          v-if="!reload"
          v-slot="{ Component }"
          :key="$route.fullPath">
          <component :is="Component" />
        </router-view>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import { computed } from "vue"
import { useStore } from "vuex"
import {
  dateZhCN,
  NConfigProvider,
  NDialogProvider,
  NGlobalStyle,
  NMessageProvider,
  zhCN
} from "naive-ui"
import themeOverrides from "@/theme"

const store = useStore()

const theme = computed(() => {
  return store.state.theme
})

const reload = computed(() => {
  return store.state.reload
})
</script>

<style lang="scss">
:root {
  --layout-padding: 12px;
  --header-height: 60px;
  --primary-color: #18a058;
}

#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;

  .header {
    box-shadow: 0 1px 4px #00152914;
    z-index: 1;
  }

  .aside {
    box-shadow: 2px 0 8px #1d23290d;
    z-index: 2;
  }

  .main {
    top: var(--header-height);

    .n-scrollbar > .n-scrollbar-container {
      display: flex;
    }

    .footer {
      margin-top: auto;
      width: 100%;
    }
  }

  .input-prefix-icon {
    margin-right: 4px;
  }

  .tag {
    margin: 1px 3px;
    cursor: pointer;
  }

  a {
    color: inherit;
    text-decoration: none;
  }
}
</style>
