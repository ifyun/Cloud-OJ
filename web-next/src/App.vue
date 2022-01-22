<template>
  <n-config-provider :theme="theme" :theme-overrides="themeOverrides" :locale="locale" :date-locale="dateLocale"
                     abstract>
    <n-dialog-provider>
      <n-message-provider>
        <n-global-style/>
        <router-view v-if="!reload" v-slot="{Component}" :key="$route.fullPath">
          <keep-alive>
            <component :is="Component"/>
          </keep-alive>
        </router-view>
        <n-modal v-model:show="showAuthDialog" preset="dialog" :mask-closable="false" :show-icon="false"
                 transform-origin="center" style="margin-top: 220px">
          <auth/>
        </n-modal>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {dateZhCN, NConfigProvider, NDialogProvider, NGlobalStyle, NMessageProvider, NModal, zhCN} from "naive-ui"
import themeOverrides from "@/theme"
import Auth from "@/views/components/Auth/Index.vue"
import Mutations from "@/store/mutations"

@Options({
  components: {
    zhCN,
    dateZhCN,
    NConfigProvider,
    NDialogProvider,
    NMessageProvider,
    NGlobalStyle,
    NModal,
    Auth
  }
})
export default class App extends Vue {
  private store = useStore()

  private locale = zhCN
  private dateLocale = dateZhCN

  get theme() {
    return this.store.state.theme
  }

  get themeOverrides() {
    return themeOverrides
  }

  get showAuthDialog(): boolean {
    return this.store.state.showAuthDialog
  }

  set showAuthDialog(value: boolean) {
    this.store.commit(Mutations.SHOW_AUTH_DIALOG, value)
  }

  get reload() {
    return this.store.state.reload
  }
}
</script>

<style lang="scss">
:root {
  --layout-padding: 12px;
  --header-height: 60px;
  --primary-color: #18A058;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;

  .header {
    box-shadow: 0 1px 4px #00152914;
    z-index: 1;
  }

  .aside {
    box-shadow: 2px 0 8px #1D23290D;
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
