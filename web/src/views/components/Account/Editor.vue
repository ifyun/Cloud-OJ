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
                :uid="userInfo.uid"
                :nickname="userInfo.nickname"
                :has-avatar="userInfo.hasAvatar"
                :timestamp="t" />
              <n-upload
                action="/api/core/file/img/avatar"
                accept=".jpg,.jpeg,.png"
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
            <n-input disabled :value="user.username" />
          </n-form-item>
          <n-form-item path="nickname" label="昵称">
            <n-input v-model:value="user.nickname" />
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
              placeholder="学院 / 班级 / Lab" />
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
import { computed, onMounted, ref } from "vue"
import { useRouter } from "vue-router"
import { useStore } from "vuex"
import {
  FormRules,
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
import { FileUploadOutlined, SaveRound } from "@vicons/material"
import { hashSync } from "bcryptjs"
import UserAvatar from "@/components/UserAvatar.vue"
import { AuthApi, UserApi } from "@/api/request"
import { ErrorMessage, User } from "@/api/type"
import { setTitle } from "@/utils"
import { Mutations } from "@/store"

const router = useRouter()
const store = useStore()
const message = useMessage()

const loading = ref<boolean>(false)
const userForm = ref<HTMLFormElement | null>(null)

const userInfo = computed(() => {
  return store.state.userInfo
})

const headers = computed(() => {
  return {
    userId: userInfo.value.userId,
    Authorization: `Baerer ${userInfo.value.token}`
  }
})

const user = ref<User>(new User())
const t = ref<number>(Date.now())

const rules: FormRules = {
  nickname: {
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
  setTitle("编辑个人信息")
  user.value.uid = userInfo.value!.uid
  user.value.username = userInfo.value!.username
  user.value.nickname = userInfo.value!.nickname
  user.value.email = userInfo.value!.email
  user.value.section = userInfo.value!.section
})

function save() {
  userForm.value?.validate((errors: any) => {
    if (!errors) {
      loading.value = true
      user.value.password = hashSync(user.value.password!, 10)
      UserApi.save(user.value, userInfo.value)
        .then(() => {
          message.success("个人信息已更新")
          refresh()
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
  location.reload()
}

function refresh() {
  AuthApi.refresh_token(userInfo.value)
    .then((token) => {
      store.commit(Mutations.SAVE_TOKEN, token)
      location.reload()
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>
