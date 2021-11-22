<template>
  <n-config-provider :theme="theme" :locale="locale" :date-locale="dateLocale" abstract>
    <n-dialog-provider>
      <n-message-provider>
        <n-layout position="absolute">
          <!-- 顶部导航栏 -->
          <n-layout-header bordered>
            <top-nav/>
          </n-layout-header>
          <n-layout class="main" position="absolute" :has-sider="true">
            <!-- 侧边菜单（Admin） -->
            <n-layout-side v-if="layout.sideNav" bordered collapse-mode="width" :native-scrollbar="false"
                           :collapsed="true" :width="180" :collapsed-width="64">
              <side-navbar/>
            </n-layout-side>
            <!-- 内容区域 -->
            <n-layout :native-scrollbar="false" content-style="display: flex; flex-direction: column">
              <content/>
              <n-layout-footer class="footer">
                <Footer/>
              </n-layout-footer>
            </n-layout>
          </n-layout>
        </n-layout>
        <!-- 登录/注册对话框 -->
        <n-modal v-model:show="showAuthDialog" preset="dialog" :mask-closable="false"
                 :show-icon="false" style="margin-top: 220px">
          <Auth/>
        </n-modal>
      </n-message-provider>
    </n-dialog-provider>
  </n-config-provider>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {Watch} from "vue-property-decorator"
import {RouteLocationNormalizedLoaded} from "vue-router"
import {
  dateZhCN,
  NButton,
  NCard,
  NConfigProvider,
  NDialogProvider,
  NLayout,
  NLayoutContent,
  NLayoutFooter,
  NLayoutHeader,
  NLayoutSider as NLayoutSide,
  NMessageProvider,
  NModal,
  NSpace,
  zhCN
} from "naive-ui"
import TopNav from "@/views/layout/Navbar.vue"
import Footer from "@/views/layout/Footer.vue"
import SideNavbar from "@/views/layout/SideNavbar.vue"
import Content from "@/views/layout/Content.vue"
import Auth from "@/views/components/Auth/index.vue"
import MutationType from "@/store/mutation-type"
import {LayoutConfig} from "@/type"
import "./app.css"

@Options({
  components: {
    zhCN,
    dateZhCN,
    NConfigProvider,
    NDialogProvider,
    NMessageProvider,
    NSpace,
    NLayout,
    NLayoutHeader,
    NLayoutSide,
    NLayoutContent,
    NLayoutFooter,
    NCard,
    NButton,
    NModal,
    TopNav,
    SideNavbar,
    Content,
    Footer,
    Auth
  }
})
export default class App extends Vue {
  private store = useStore()

  private locale = zhCN
  private dateLocale = dateZhCN

  get layout(): LayoutConfig {
    return this.store.state.layout
  }

  get theme() {
    return this.store.state.theme
  }

  get showAuthDialog(): boolean {
    return this.store.state.showAuthDialog
  }

  set showAuthDialog(value: boolean) {
    this.store.commit(MutationType.SHOW_AUTH_DIALOG, value)
  }

  @Watch("$route")
  routeChanged(route: RouteLocationNormalizedLoaded) {
    if (route.fullPath.startsWith("/admin")) {
      this.store.commit(MutationType.CHANGE_LAYOUT, {navbar: true, sideNav: true})
    } else {
      this.store.commit(MutationType.CHANGE_LAYOUT, {navbar: true, sideNav: false})
    }
  }
}
</script>

<style lang="scss">
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100%;

  .main {
    top: var(--header-height);

    .n-layout > .n-scrollbar > .n-scrollbar-container {
      display: flex;
    }

    .footer {
      margin-top: auto;
      width: 100%
    }
  }

  .tag {
    // 题目标签样式
    margin: 1px 3px;
    cursor: pointer;
  }

  a {
    color: inherit;
    text-decoration: none;
  }
}
</style>
