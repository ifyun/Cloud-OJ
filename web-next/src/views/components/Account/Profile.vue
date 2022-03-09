<template>
  <n-space size="large" align="center">
    <user-avatar :size="72" :user-id="userId"/>
    <div>
      <n-space vertical size="small">
        <n-text strong style="font-size: 16px">{{ profile.name }}</n-text>
        <n-text italic depth="3">{{ profile.userId }}</n-text>
      </n-space>
    </div>
  </n-space>
</template>

<script setup lang="ts">
import {onBeforeMount, ref} from "vue"
import {NSpace, NText, useMessage} from "naive-ui"
import {UserAvatar} from "@/components"
import {ErrorMsg, User} from "@/api/type"
import {UserApi} from "@/api/request"

const message = useMessage()

const props = defineProps<{ userId: string }>()
const profile = ref<User>(new User())

onBeforeMount(() => {
  UserApi.getProfile(props.userId).then(data => {
    profile.value = data
  }).catch((error: ErrorMsg) => {
    message.error(error.toString())
  })
})
</script>
