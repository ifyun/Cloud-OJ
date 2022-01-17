<template>
  <n-form ref="loginForm" size="large" label-placement="left" :model="user"
          :rules="loginRules" style="margin-top: 10px">
    <input type="password" hidden autocomplete="new-password">
    <n-form-item path="username">
      <n-input placeholder="输入你的ID" v-model:value="user.username">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <orcid/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="password">
      <n-input type="password" :maxlength="16" placeholder="输入密码"
               v-model:value="user.password">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <lock/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item>
      <n-button style="width: 100%" type="success" :loading="loading" :disabled="loading" @click="login">
        登 录
      </n-button>
    </n-form-item>
  </n-form>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {FormRules, NButton, NForm, NFormItem, NIcon, NInput, useMessage} from "naive-ui"
import {Lock, Orcid} from "@vicons/fa"
import {AuthApi} from "@/api/request"
import {UsernamePassword} from "@/api/type"
import MutationType from "@/store/mutation-type"

const md5 = require("crypto-js/md5")

@Options({
  name: "Login",
  components: {
    NForm,
    NIcon,
    NFormItem,
    NInput,
    NButton,
    Orcid,
    Lock
  }
})
export default class Login extends Vue {
  private loading: boolean = false
  private readonly store = useStore()
  private readonly message = useMessage()

  private user: UsernamePassword = {
    username: "",
    password: ""
  }

  private loginRules: FormRules = {
    username: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: string): Error | boolean {
        if (!value) {
          return new Error("请输入用户ID！")
        } else if (value.length < 4) {
          return new Error("这么短不可能是ID")
        }
        return true
      }
    },
    password: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: string): Error | boolean {
        if (!value) {
          return new Error("请输入密码")
        } else if (value.length < 4) {
          return new Error("这么短不可能是密码")
        }
        return true
      }
    }
  }

  declare $refs: {
    loginForm: HTMLFormElement
  }

  login() {
    this.$refs.loginForm.validate((errors: any) => {
      if (!errors) {
        this.loading = true
        AuthApi.login({
          username: this.user.username,
          password: md5(this.user.password).toString()
        }).then((data) => {
          this.store.commit(MutationType.SAVE_TOKEN, data)
          this.store.commit(MutationType.SHOW_AUTH_DIALOG, false)
        }).catch((error) => {
          this.message.error(`${error.code}: ${error.msg}`)
        }).finally(() => {
          this.loading = false
        })
      } else {
        this.message.error("请检查你的输入")
      }
    })
  }
}
</script>

<style scoped>
</style>