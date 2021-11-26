<template>
  <n-space vertical style="padding: 10px 20px">
    <n-tabs size="large" type="line" default-value="login">
      <n-tab-pane name="login" tab="登录">
        <login/>
      </n-tab-pane>
      <n-tab-pane name="signup" tab="注册">
        <signup/>
      </n-tab-pane>
    </n-tabs>
  </n-space>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {NH1, NSpace, NTabPane, NTabs} from "naive-ui"
import Login from "@/views/components/Auth/Login.vue"
import Signup from "@/views/components/Auth/Signup.vue"

@Options({
  name: "Auth",
  components: {
    NSpace,
    NH1,
    NTabs,
    NTabPane,
    Login,
    Signup
  }
})
export default class Auth extends Vue {
}
</script>
