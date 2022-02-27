<template>
  <n-space vertical>
    <div style="display: flex">
      <languages :languages="overview.preference" style="flex: 0.5"/>
    </div>
    <n-divider/>
    <timeline/>
  </n-space>
</template>

<script setup lang="ts">
import {defineProps, onBeforeMount, ref} from "vue"
import {NSpace, NDivider, useMessage} from "naive-ui"
import Languages from "./Languages.vue"
import Timeline from "./Timeline.vue"
import {UserApi} from "@/api/request"
import {ErrorMsg, Overview} from "@/api/type"
import moment from "moment"

const props = defineProps<{
  userId: string
}>()

const message = useMessage()

const year = ref<number>(moment().year())
const overview = ref<Overview>(new Overview())

onBeforeMount(() => {
  UserApi.getOverview(props.userId, year.value).then(data => {
    overview.value = data
  }).catch((error: ErrorMsg) => {
    message.error(error.toString())
  })
})
</script>
