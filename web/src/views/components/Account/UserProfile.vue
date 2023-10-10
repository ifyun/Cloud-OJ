<template>
  <n-space size="large" align="center">
    <user-avatar
      :size="72"
      :uid="user.uid!"
      :nickname="user.nickname ?? ''"
      :has-avatar="user.hasAvatar" />
    <div>
      <n-space vertical size="small">
        <n-space size="large" align="center">
          <n-text strong style="font-size: 16px">{{ user.nickname }}</n-text>
          <n-button v-if="showEdit" size="small" @click="edit">
            修改个人信息
            <template #icon>
              <n-icon>
                <user-edit />
              </n-icon>
            </template>
          </n-button>
        </n-space>
        <n-text italic depth="3">{{ user.username }}</n-text>
      </n-space>
    </div>
  </n-space>
</template>

<script setup lang="ts">
import { User } from "@/api/type"
import { UserAvatar } from "@/components"
import { UserEdit } from "@vicons/fa"
import { NButton, NIcon, NSpace, NText } from "naive-ui"
import { useRouter } from "vue-router"

const router = useRouter()

defineProps<{ user: User; showEdit: boolean }>()

function edit() {
  router.push({ name: "edit_account" })
}
</script>
