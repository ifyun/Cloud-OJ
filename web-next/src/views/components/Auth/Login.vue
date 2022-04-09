<template>
  <n-form
    ref="loginForm"
    size="large"
    label-placement="left"
    :model="user"
    :rules="loginRules">
    <input type="password" hidden autocomplete="new-password" />
    <n-form-item path="username">
      <n-input placeholder="输入你的ID" v-model:value="user.username">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <orcid />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="password">
      <n-input
        type="password"
        :maxlength="16"
        placeholder="输入密码"
        v-model:value="user.password">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <lock />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item>
      <n-button
        style="width: 100%"
        type="primary"
        :loading="loading"
        :disabled="loading"
        @click="login">
        登 录
      </n-button>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { useStore } from "vuex"
import { useRouter } from "vue-router"
import {
  FormRules,
  NButton,
  NForm,
  NFormItem,
  NIcon,
  NInput,
  useMessage
} from "naive-ui"
import { Lock, Orcid } from "@vicons/fa"
import { AuthApi } from "@/api/request"
import { UsernamePassword } from "@/api/type"
import { Mutations } from "@/store"

const md5 = require("crypto-js/md5")

const loading = ref<boolean>(false)
const store = useStore()
const router = useRouter()
const message = useMessage()

const user = ref<UsernamePassword>({
  username: "",
  password: ""
})

const loginForm = ref<HTMLFormElement | null>(null)

const loginRules: FormRules = {
  username: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: string): Error | boolean {
      if (!value) {
        return new Error("请输入用户ID")
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

function login() {
  loginForm.value?.validate((errors: any) => {
    if (!errors) {
      loading.value = true
      AuthApi.login({
        username: user.value.username,
        password: md5(user.value.password).toString()
      })
        .then((data) => {
          store.commit(Mutations.SAVE_TOKEN, data)
          router.push({ path: "/" })
        })
        .catch((error) => {
          message.error(`${error.code}: ${error.msg}`)
        })
        .finally(() => {
          loading.value = false
        })
    } else {
      message.error("请检查你的输入")
    }
  })
}
</script>

<style scoped></style>
