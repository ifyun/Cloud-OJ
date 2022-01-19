<template>
  <n-layout position="absolute" :has-sider="true">
    <n-layout-sider class="aside" collapse-mode="width" :native-scrollbar="false" width="200">
      <div>
        <logo style="height: 60px"/>
        <admin-menu/>
      </div>
    </n-layout-sider>
    <n-layout :native-scrollbar="false">
      <n-layout-header class="header">
        <div class="admin-nav">
          <v-node-renderer :v-node="breadcrumb"/>
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

<script lang="ts">
import {VNode} from "vue"
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {NLayout, NLayoutContent, NLayoutHeader, NLayoutSider, NSpace} from "naive-ui"
import {AdminMenu, ThemeSwitch, UserMenu} from "@/views/layout"
import Logo from "@/components/Logo.vue"
import VNodeRenderer from "@/components/VNodeRenderer.vue"

@Options({
  name: "Admin",
  components: {
    VNodeRenderer,
    NLayout,
    NLayoutHeader,
    NLayoutContent,
    NLayoutSider,
    NSpace,
    Logo,
    ThemeSwitch,
    AdminMenu,
    UserMenu
  }
})
export default class Admin extends Vue {
  private store = useStore()

  get breadcrumb(): VNode {
    return this.store.state.breadcrumb
  }
}
</script>

<style scoped lang="scss">
.admin-nav {
  height: 60px;
  padding: 0 25px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
</style>
