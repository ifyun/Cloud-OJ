<template>
  <div>
    个人中心
    <!-- TODO 未完成 -->
  </div>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {setTitle} from "@/utils"

@Options({
  name: "Account"
})
export default class Account extends Vue {
  beforeMount() {
    setTitle("个人中心")
  }
}
</script>

<style scoped>

</style>