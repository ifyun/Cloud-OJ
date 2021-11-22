<template>
  <router-view v-slot="{Component}" :key="$route.fullPath">
    <keep-alive>
      <component :is="Component"/>
    </keep-alive>
  </router-view>
</template>

<script lang="ts">
import {Vue} from "vue-class-component"
import {useStore} from "vuex"
import {AuthApi} from "@/api/request"
import {ErrorMsg} from "@/api/type"
import {useMessage} from "naive-ui"

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

<style scoped>

</style>