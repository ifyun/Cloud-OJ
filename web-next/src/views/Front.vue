<template>
  <n-layout position="absolute">
    <n-layout-header bordered>
      <navbar/>
    </n-layout-header>
    <n-layout-content class="main" position="absolute" :native-scrollbar="false"
                      content-style="display: flex; flex-direction: column">
      <router-view v-slot="{Component}" :key="$route.fullPath">
        <keep-alive>
          <component :is="Component"/>
        </keep-alive>
      </router-view>
      <n-layout-footer class="footer">
        <footer/>
      </n-layout-footer>
    </n-layout-content>
  </n-layout>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {useStore} from "vuex"
import {AuthApi} from "@/api/request"
import {ErrorMsg} from "@/api/type"
import {NLayout, NLayoutContent, NLayoutFooter, NLayoutHeader, useMessage} from "naive-ui"
import Navbar from "@/views/layout/Navbar.vue"
import Footer from "@/views/layout/Footer.vue"

@Options({
  name: "FrontApp",
  components: {
    NLayout,
    NLayoutHeader,
    NLayoutContent,
    NLayoutFooter,
    Navbar,
    Footer
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
            console.debug(error)
            if (error.code === 401) {
              this.message.warning("登录已失效，请重新登录！")
            }
          })
    }
  }
}
</script>
