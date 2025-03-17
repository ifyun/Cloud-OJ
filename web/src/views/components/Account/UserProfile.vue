<template>
  <n-flex size="large" align="center">
    <user-avatar
      :size="72"
      :uid="user.uid!"
      :nickname="user.nickname ?? ''"
      :has-avatar="user.hasAvatar" />
    <n-flex vertical size="small">
      <n-flex size="large" align="center">
        <n-text strong style="font-size: 20px">{{ user.nickname }}</n-text>
        <n-button v-if="showEdit" size="small" @click="edit">
          修改个人信息
          <template #icon>
            <n-icon>
              <user-edit />
            </n-icon>
          </template>
        </n-button>
      </n-flex>
      <n-text v-if="user.username" italic depth="3">
        {{ user.realName }}@{{ user.username }}
      </n-text>
    </n-flex>
  </n-flex>
</template>

<script setup lang="ts">
import { User } from "@/api/type"
import { UserAvatar } from "@/components"
import { UserEdit } from "@vicons/fa"
import { NButton, NFlex, NIcon, NText } from "naive-ui"
import { useRouter } from "vue-router"

const router = useRouter()

defineProps<{ user: User; showEdit: boolean }>()

function edit() {
  router.push({ name: "edit_account" })
}
</script>
