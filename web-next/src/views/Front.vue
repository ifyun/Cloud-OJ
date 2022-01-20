<template>
  <n-layout position="absolute">
    <n-layout-header class="header" position="absolute">
      <navbar/>
    </n-layout-header>
    <n-layout-content class="main" position="absolute" :embedded="true" :native-scrollbar="false"
                      content-style="display: flex; flex-direction: column">
      <router-view v-slot="{Component}" :key="$route.fullPath">
        <keep-alive>
          <component :is="Component"/>
        </keep-alive>
      </router-view>
      <n-layout-footer class="footer">
        <bottom-info/>
      </n-layout-footer>
    </n-layout-content>
  </n-layout>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {NLayout, NLayoutContent, NLayoutFooter, NLayoutHeader, useMessage} from "naive-ui"
import {BottomInfo, Navbar} from "@/views/layout"
import {AuthApi} from "@/api/request"
import {ErrorMsg} from "@/api/type"

@Options({
  name: "FrontApp",
  components: {
    NLayout,
    NLayoutHeader,
    NLayoutContent,
    NLayoutFooter,
    Navbar,
    BottomInfo
  }
})
export default class Content extends Vue {
  private store = useStore()
  private message = useMessage()

  get isLoggedIn() {
    return this.store.getters.isLoggedIn
  }

  mounted() {
    if (this.isLoggedIn) {
      AuthApi.verify(this.store.state.userInfo)
          .catch((error: ErrorMsg) => {
            if (error.code === 401) {
              this.message.warning("登录已失效，请重新登录！")
            }
          })
    }
  }
}
</script>
