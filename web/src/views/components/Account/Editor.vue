<template>
  <div style="width: 1100px; padding: var(--layout-padding) 0; margin: 0 auto">
    <n-card>
      <div style="max-width: 520px">
        <n-form
          label-placement="left"
          label-width="auto"
          label-align="left"
          :model="user">
          <input type="password" hidden autocomplete="new-password" />
          <n-form-item label="头像">
            <n-space vertical align="center" size="large">
              <user-avatar
                :size="128"
                :user-id="userInfo?.userId!"
                :timestamp="t" />
              <n-upload
                action="/api/core/file/img/avatar"
                accept=".jpg,.jpeg,.png"
                :show-file-list="false"
                :headers="headers"
                :on-finish="uploadFinish">
                <n-button type="info" secondary>
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
          <n-form-item label="UID">
            <n-input disabled :value="user.userId" />
          </n-form-item>
          <n-form-item path="name" label="用户名">
            <n-input
              v-model:value="user.name"
              :disabled="user.userId === 'admin'" />
          </n-form-item>
          <n-form-item path="password" label="新密码">
            <n-input v-model:value="user.password" type="password" />
          </n-form-item>
          <n-form-item label="邮箱(可选)">
            <n-input v-model:value="user.email" />
          </n-form-item>
          <n-form-item label="来自哪里(可选)">
            <n-input v-model:value="user.section" />
          </n-form-item>
          <n-form-item label="&nbsp;">
            <n-button type="primary" secondary>
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
import { useStore } from "vuex"
import {
  NButton,
  NCard,
  NForm,
  NFormItem,
  NIcon,
  NInput,
  NSpace,
  NUpload,
  useMessage
} from "naive-ui"
import { FileUploadOutlined, SaveRound } from "@vicons/material"
import UserAvatar from "@/components/UserAvatar.vue"
import { UserApi } from "@/api/request"
import { User, UserInfo } from "@/api/type"
import { setTitle } from "@/utils"

const store = useStore()
const message = useMessage()

const userInfo = computed<UserInfo | null>(() => {
  return store.state.userInfo
})

const headers = computed(() => {
  return {
    userId: userInfo.value!.userId,
    token: userInfo.value!.token
  }
})

const user = ref<User>(new User())
const t = ref<number>(Date.now())

onMounted(() => {
  setTitle("编辑个人信息")
  UserApi.getProfile(userInfo.value!.userId!)
    .then((data) => (user.value = data))
    .catch((err) => message.error(err.toString()))
})

function uploadFinish() {
  t.value = Date.now()
}
</script>
