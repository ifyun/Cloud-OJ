<template>
  <error-result v-if="error != null" :error="error" style="margin-top: 48px" />
</template>

<script lang="ts">
export default {
  name: "ErrorPage"
}
</script>

<script setup lang="ts">
import { ErrorResult } from "@/components"
import { useStore } from "@/store"
import { computed } from "vue"

const store = useStore()

const error = computed(() => store.app.error)
</script>
