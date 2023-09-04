<template>
  <n-space size="large" align="center">
    <user-avatar
      :size="72"
      :uid="uid"
      :nickname="profile.nickname"
      :has-avatar="profile.hasAvatar" />
    <div>
      <n-space vertical size="small">
        <n-space size="large" align="center">
          <n-text strong style="font-size: 16px">{{ profile.nickname }}</n-text>
          <n-button size="small" @click="edit">
            修改个人信息
            <template #icon>
              <n-icon>
                <user-edit />
              </n-icon>
            </template>
          </n-button>
        </n-space>
        <n-text italic depth="3">{{ profile.username }}</n-text>
      </n-space>
    </div>
  </n-space>
</template>

<script setup lang="ts">
import { onBeforeMount, ref } from "vue"
import { useRouter } from "vue-router"
import { NButton, NIcon, NSpace, NText, useMessage } from "naive-ui"
import { UserEdit } from "@vicons/fa"
import { UserAvatar } from "@/components"
import { ErrorMessage, User } from "@/api/type"
import { UserApi } from "@/api/request"

const router = useRouter()
const message = useMessage()

const props = defineProps<{ uid: number }>()
const profile = ref<User>(new User())

onBeforeMount(() => {
  UserApi.getProfile(props.uid)
    .then((data) => {
      profile.value = data
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
})

function edit() {
  router.push({ name: "edit_account" })
}
</script>
