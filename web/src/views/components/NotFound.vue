<template>
  <error-result :error="error" />
</template>

<script setup lang="ts">
import { ErrorMessage } from "@/api/type"
import { ErrorResult } from "@/components"

const error: ErrorMessage = {
  status: 404,
  error: "Not Found",
  message: "找不到页面"
}
</script>
