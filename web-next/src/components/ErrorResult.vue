<template>
  <n-result class="error-result" size="large" :status="status" :title="title" :description="desc">
    <template #footer>
      <n-button>找点乐子吧</n-button>
    </template>
  </n-result>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {Prop, Watch} from "vue-property-decorator"
import {NButton, NResult} from "naive-ui"
import {ErrorMsg} from "@/api/type"

@Options({
  name: "ErrorResult",
  components: {
    NResult,
    NButton
  }
})
export default class ErrorResult extends Vue {
  @Prop(Object)
  private error?: ErrorMsg

  private status: string = "warning"
  private title: string = ""
  private desc: string = ""

  @Watch("error", {immediate: true, deep: true})
  errorChange(value: ErrorMsg) {
    if (typeof value === "undefined") {
      return
    }

    if (value.code === 400) {
      this.title = "400"
      this.desc = "请求错误"
    } else if (value.code === 401) {
      this.title = "401"
      this.desc = "未授权"
    } else if (value.code === 403) {
      this.status = value.code.toString()
      this.title = "403"
      this.desc = "禁止访问"
    } else if (value.code === 404) {
      this.status = value.code.toString()
      this.title = "404"
      this.desc = "资源不存在"
    } else if (value.code === 500) {
      this.status = value.code.toString()
      this.title = "500"
      this.desc = "内部错误"
    } else {
      this.title = value.msg
      this.desc = "不知道发生了什么"
    }
  }
}
</script>

<style scoped>
.error-result {
  margin-top: 60px;
}
</style>