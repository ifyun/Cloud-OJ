<template>
  <n-form
    ref="signupForm"
    label-placement="left"
    :disabled="loading"
    :model="user"
    :rules="signupRules">
    <input type="password" hidden autocomplete="new-password" />
    <n-form-item path="userId">
      <n-input v-model:value="user.userId" placeholder="用户名（字母和数字）">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <user-id-icon />
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="name">
      <n-input
        v-model:value="user.name"
        placeholder="昵称"
        maxlength="16"
        :input-props="{ autocomplete: 'off' }">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <user-icon />
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
      <n-input v-model:value="user.section" placeholder="学院/班级/部门 (可选)">
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
import { onMounted, ref } from "vue"
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
import {
  Building,
  Envelope as MailIcon,
  Lock as PasswordIcon,
  Orcid as UserIdIcon,
  User as UserIcon
} from "@vicons/fa"
import { ErrorMessage, User } from "@/api/type"
import { setTitle } from "@/utils"
import { UserApi } from "@/api/request"

const router = useRouter()
const message = useMessage()

const loading = ref<boolean>(false)
const user = ref<User>(new User())
const signupForm = ref<HTMLFormElement | null>(null)

const signupRules: FormRules = {
  userId: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: string): Error | boolean {
      if (!value) {
        return new Error("请输入ID")
      } else if (value.length < 6) {
        return new Error("至少 6 个字符!")
      }

      return true
    }
  },
  name: {
    required: true,
    trigger: ["blur", "input"],
    validator(rule: any, value: string): Error | boolean {
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
    validator(rule: any, value: string): Error | boolean {
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
    validator: (rule: any, value: string): Error | boolean => {
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
    validator(rule: any, value: string): Error | boolean {
      const regex =
        /^[a-zA-Z\d.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z\d-]+(?:\.[a-zA-Z\d-]+)*$/
      if (value.length === 0) {
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
  loading.value = true
  signupForm.value?.validate((errors: any) => {
    if (!errors) {
      UserApi.save(user.value, null, true)
        .then(() => {
          message.success("注册成功")
          router.push({ params: { tab: "login" } })
        })
        .catch((err: ErrorMessage) => {
          message.error(err.toString())
        })
        .finally(() => {
          loading.value = false
        })
    }
  })
}
</script>
