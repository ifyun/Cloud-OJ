<template>
  <div style="width: 1100px; padding: var(--layout-padding) 0; margin: 0 auto">
    <n-card>
      <n-page-header @back="back">
        <template #title>编辑个人信息</template>
      </n-page-header>
      <div style="max-width: 520px; margin-top: 24px; margin-left: 4px">
        <n-form
          ref="userForm"
          label-placement="left"
          label-width="auto"
          label-align="left"
          :disabled="loading"
          :model="user"
          :rules="rules">
          <input type="password" hidden autocomplete="new-password" />
          <n-form-item label="头像">
            <n-space vertical align="center" size="large">
              <user-avatar
                :size="128"
                :uid="userInfo!.uid!"
                :nickname="userInfo!.nickname!"
                :has-avatar="userInfo!.hasAvatar"
                :timestamp="t" />
              <n-upload
                accept=".jpg,.jpeg,.png"
                :action="uploadUrl"
                :show-file-list="false"
                :headers="headers"
                :on-finish="uploadFinish">
                <n-button type="info" size="small" tertiary round>
                  上传新头像
                  <template #icon>
                    <n-icon>
                      <file-upload-outlined />
                    </n-icon>
                  </template>
                </n-button>
              </n-upload>
            </n-space>
          </n-form-item>
          <n-form-item label="用户名">
            <n-input
              :disabled="user.role === 1"
              v-model:value="user.username"
              placeholder="字母和数字(16 个字符以内)" />
          </n-form-item>
          <n-form-item path="nickname" label="昵称">
            <n-input
              v-model:value="user.nickname"
              placeholder="16 个字符以内" />
          </n-form-item>
          <n-form-item path="realName" label="真实姓名">
            <n-input v-model:value="user.realName" placeholder="仅管理员可见" />
          </n-form-item>
          <n-form-item path="password" label="新密码">
            <n-input
              v-model:value="user.password"
              type="password"
              maxlength="16"
              placeholder="6 ~ 16 位" />
          </n-form-item>
          <n-form-item path="confirmPassword" label="确认新密码">
            <n-input
              v-model:value="user.confirmPassword"
              type="password"
              maxlength="16"
              placeholder="再次输入新密码" />
          </n-form-item>
          <n-form-item path="email" label="邮箱(可选)">
            <n-input v-model:value="user.email" />
          </n-form-item>
          <n-form-item path="section" label="来自哪里(可选)">
            <n-input
              v-model:value="user.section"
              placeholder="地区 / 学院 / 班级 / Lab" />
          </n-form-item>
          <n-form-item label="&nbsp;">
            <n-button type="primary" secondary :loading="loading" @click="save">
              保存修改
              <template #icon>
                <n-icon>
                  <save-round />
                </n-icon>
              </template>
            </n-button>
          </n-form-item>
        </n-form>
      </div>
    </n-card>
  </div>
</template>

<script setup lang="ts">
import { ApiPath } from "@/api"
import { AuthApi, UserApi } from "@/api/request"
import { ErrorMessage, User } from "@/api/type"
import { UserAvatar } from "@/components"
import { useStore } from "@/store"
import { FileUploadOutlined, SaveRound } from "@vicons/material"
import {
  type FormRules,
  NButton,
  NCard,
  NForm,
  NFormItem,
  NIcon,
  NInput,
  NPageHeader,
  NSpace,
  NUpload,
  useMessage
} from "naive-ui"
import { computed, onMounted, ref } from "vue"
import { useRouter } from "vue-router"

const uploadUrl = ApiPath.AVATAR

const store = useStore()
const router = useRouter()
const message = useMessage()

const loading = ref<boolean>(false)
const userForm = ref<HTMLFormElement | null>(null)
const user = ref<User>(new User())
const t = ref<number>(Date.now())

const userInfo = computed(() => {
  return store.user.userInfo
})

const headers = computed<Record<string, string>>(() => {
  return {
    Authorization: `Baerer ${userInfo.value!.token}`
  }
})

const rules: FormRules = {
  nickname: {
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
    trigger: ["blur", "input"],
    validator(_, value: string): Error | boolean {
      const regx = /^[a-zA-Z0-9_.-]*$/

      if (!value) {
        return true
      }

      if (value.length < 6) {
        return new Error("至少 6 位!")
      } else if (!regx.test(value)) {
        return new Error("包含非法字符")
      }

      return true
    }
  },
  confirmPassword: {
    trigger: ["blur", "input"],
    validator: (_, value: string): Error | boolean => {
      if (!user.value.password) {
        return true
      }

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
  UserApi.getProfile(userInfo.value!.uid!)
    .then((data) => {
      user.value = data
    })
    .catch((err) => {
      store.app.setError(err)
    })
})

function save() {
  userForm.value?.validate((errors: any) => {
    if (!errors) {
      loading.value = true
      UserApi.update(user.value)
        .then(() => {
          refresh()
          message.success("个人信息已更新")
        })
        .catch((err: ErrorMessage) => {
          message.error(err.toString())
        })
        .finally(() => {
          user.value.password = ""
          user.value.confirmPassword = ""
          loading.value = false
        })
    }
  })
}

function back() {
  router.back()
}

function uploadFinish() {
  t.value = Date.now()
  refresh()
}

function refresh() {
  AuthApi.refresh_token()
    .then((token) => {
      store.user.saveToken(token)
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>
