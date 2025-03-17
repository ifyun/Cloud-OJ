<template>
  <n-form
    ref="signupForm"
    label-placement="left"
    :disabled="loading"
    :model="user"
    :rules="signupRules">
    <input type="password" hidden autocomplete="new-password" />
    <n-form-item path="username">
      <n-input v-model:value="user.username" placeholder="用户名 (字母和数字)">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <username-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="nickname">
      <n-input
        v-model:value="user.nickname"
        placeholder="昵称"
        maxlength="16"
        :input-props="{ autocomplete: 'off' }">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <nick-name-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="realName">
      <n-input
        v-model:value="user.realName"
        placeholder="真实姓名 (可选/仅管理员可见)"
        maxlength="16"
        :input-props="{ autocomplete: 'off' }">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <real-name-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="password">
      <n-input
        v-model:value="user.password"
        placeholder="密码"
        type="password"
        maxlength="16"
        :input-props="{ autocomplete: 'new-password' }">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <password-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="confirmPassword">
      <n-input
        v-model:value="user.confirmPassword"
        placeholder="确认密码"
        type="password"
        maxlength="16"
        :input-props="{ autocomplete: 'new-password' }">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <password-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="email">
      <n-input v-model:value="user.email" placeholder="邮箱 (可选)">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <mail-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="section">
      <n-input v-model:value="user.section" placeholder="来自哪里 (可选)">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <building />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item>
      <n-button
        style="width: 100%"
        type="info"
        :loading="loading"
        :disabled="loading"
        @click="signup">
        注 册
      </n-button>
    </n-form-item>
  </n-form>
</template>

<script setup lang="ts">
import { UserApi } from "@/api/request"
import { ErrorMessage, User } from "@/api/type"
import { setTitle } from "@/utils"
import { Building } from "@vicons/fa"
import {
  AccountBoxRound as NickNameIcon,
  AccountCircleFilled as RealNameIcon,
  LockRound as PasswordIcon,
  MailRound as MailIcon,
  PersonPinCircleRound as UsernameIcon
} from "@vicons/material"
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

const router = useRouter()
const message = useMessage()

const loading = ref<boolean>(false)
const signupForm = ref<HTMLFormElement | null>(null)
const user = ref<User>(new User())

const signupRules: FormRules = {
  username: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      if (!value) {
        return new Error("请输入用户名")
      } else if (value.length < 5) {
        return new Error("至少 5 个字符!")
      }

      return true
    }
  },
  nickname: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      if (!value) {
        return new Error("请输入昵称")
      } else if (value.length < 2) {
        return new Error("至少 2 个字符!")
      }

      return true
    }
  },
  password: {
    required: true,
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      const regx = /^[a-zA-Z0-9_.-]*$/

      if (!value) {
        return new Error("请输入密码")
      } else if (value.length < 6) {
        return new Error("至少 6 位!")
      } else if (!regx.test(value)) {
        return new Error("包含非法字符")
      }

      return true
    }
  },
  confirmPassword: {
    required: true,
    trigger: ["blur", "input"],
    validator: (_, value: string): Error | boolean => {
      if (!value) {
        return new Error("请确认密码")
      } else if (value !== user.value.password) {
        return new Error("密码不一致!")
      }
      return true
    }
  },
  email: {
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      const regex =
        /^[a-zA-Z\d.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z\d-]+(?:\.[a-zA-Z\d-]+)*$/

      if (!value || value.length === 0) {
        return true
      }

      if (regex.test(value)) {
        return true
      } else {
        return Error("格式不正确!")
      }
    }
  }
}

onMounted(() => {
  setTitle("注册")
})

function signup() {
  signupForm.value?.validate((errors: any) => {
    if (!errors) {
      loading.value = true
      UserApi.save(user.value, true)
        .then(() => {
          message.success("注册成功")
          router.push({ params: { tab: "login" } })
        })
        .catch((err: ErrorMessage) => {
          user.value.password = ""
          user.value.confirmPassword = ""
          message.error(err.toString())
        })
        .finally(() => {
          loading.value = false
        })
    }
  })
}
</script>
