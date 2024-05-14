<template>
  <n-form ref="loginForm" :model="user" :rules="loginRules">
    <input type="password" hidden autocomplete="new-password" />
    <n-form-item label="用户名" path="username">
      <n-input v-model:value="user.username">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <user-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item label="密码" path="password">
      <n-input v-model:value="user.password" type="password" maxlength="16">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <password-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item label-placement="left">
      <n-button
        style="margin-left: auto"
        type="primary"
        icon-placement="right"
        :loading="loading"
        :disabled="loading"
        @click="login">
        <template #icon>
          <n-icon :component="LoginIcon" />
        </template>
        登 录
      </n-button>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { AuthApi } from "@/api/request"
import { ErrorMessage, UsernamePassword } from "@/api/type"
import { useStore } from "@/store"
import { setTitle } from "@/utils"
import { Lock as PasswordIcon, User as UserIcon } from "@vicons/fa"
import { LogInRound as LoginIcon } from "@vicons/material"
import {
  type FormRules,
  NButton,
  NForm,
  NFormItem,
  NIcon,
  NInput,
  useMessage
} from "naive-ui"
import { onMounted, ref } from "vue"
import { useRouter } from "vue-router"

const store = useStore()
const router = useRouter()
const message = useMessage()

const loading = ref<boolean>(false)
const loginForm = ref<HTMLFormElement | null>(null)
const user = ref<UsernamePassword>({
  username: "",
  password: ""
})

const loginRules: FormRules = {
  username: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      if (!value) {
        return new Error("请输入用户名")
      } else if (value.length < 4) {
        return new Error("这么短不可能是用户名！")
      }
      return true
    }
  },
  password: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      if (!value) {
        return new Error("请输入密码")
      } else if (value.length < 4) {
        return new Error("这么短不可能是密码！")
      }
      return true
    }
  }
}

onMounted(() => {
  setTitle("登录")
})

function login() {
  loginForm.value?.validate((errors: any) => {
    if (!errors) {
      loading.value = true
      AuthApi.login(user.value)
        .then((token) => {
          store.user.saveToken(token)
          if (store.user.userInfo!.role == 1) {
            router.push({ name: "index" })
          } else {
            router.push({ name: "admin" })
          }
        })
        .catch((err: ErrorMessage) => {
          message.error(err.toString())
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
