<template>
  <n-result
    class="error-result"
    size="large"
    :status="status"
    :title="title"
    :description="desc">
    <template #footer>要不刷新一下试试？</template>
  </n-result>
</template>

<script setup lang="ts">
import { ref, watch } from "vue"
import { NResult } from "naive-ui"
import { ErrorMsg } from "@/api/type"

const props = defineProps<{
  error: ErrorMsg
}>()

const status = ref<string>("warning")
const title = ref<string>("")
const desc = ref<string>("")

watch(
  props.error,
  (value) => {
    if (typeof value === "undefined") {
      return
    }

    if (value.code === 400) {
      title.value = "400"
      desc.value = "错误的请求"
    } else if (value.code === 401) {
      title.value = "401"
      desc.value = "未授权"
    } else if (value.code === 403) {
      status.value = value.code.toString()
      title.value = "403"
      desc.value = "禁止访问"
    } else if (value.code === 404) {
      status.value = value.code.toString()
      title.value = "404"
      desc.value = "找不到了"
    } else if (value.code === 500) {
      status.value = value.code.toString()
      title.value = "500"
      desc.value = "内部错误"
    } else {
      title.value = value.msg
      desc.value = "不知道发生了什么"
    }
  },
  { immediate: true, deep: true }
)
</script>

<style scoped>
.error-result {
  margin-top: 24px;
}
</style>
