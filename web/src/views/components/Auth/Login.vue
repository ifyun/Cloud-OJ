<template>
  <n-form ref="loginForm" :model="user" :rules="loginRules">
    <input type="password" hidden autocomplete="new-password" />
    <n-form-item label="用户名" path="username">
      <n-input v-model:value="user.username">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <orcid />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item label="密码" path="password">
      <n-input v-model:value="user.password" type="password" maxlength="16">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <lock />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item label-placement="left">
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
import { onMounted, ref } from "vue"
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
import { ErrorMessage, UsernamePassword } from "@/api/type"
import { Mutations } from "@/store"
import { setTitle } from "@/utils"

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
    validator(rule: any, value: string): Error | boolean {
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
        .then((data) => {
          store.commit(Mutations.SAVE_TOKEN, data)
          if (store.state.userInfo.roleId == 1) {
            router.push({ path: "/" })
          } else {
            router.push({ path: "/admin/problem" })
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
