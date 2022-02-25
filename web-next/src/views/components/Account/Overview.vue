<template>
  <div>

  </div>
</template>

<script setup lang="ts">
import {onBeforeMount, defineProps, ref} from "vue"
import {useMessage} from "naive-ui"
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

<style scoped>

</style>
