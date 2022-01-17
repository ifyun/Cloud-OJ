<template>
  <error-result :error="{code: 400}"/>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import ErrorResult from "@/components/ErrorResult.vue"

@Options({
  name: "NotFound",
  components: {
    ErrorResult
  }
})
export default class NotFound extends Vue {
}
</script>
