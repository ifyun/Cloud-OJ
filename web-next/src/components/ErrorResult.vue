<template>
  <n-result class="error-result" size="large"
            :status="status" :title="title" :description="desc">
    <template #footer>
      <n-button>找点乐子吧</n-button>
    </template>
  </n-result>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {NButton, NResult} from "naive-ui"
import {Prop, Watch} from "vue-property-decorator"
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
      this.title = "400 坏请求"
      this.desc = "太坏了太坏了"
    } else if (value.code === 401) {
      this.title = "401 未授权"
      this.desc = "快去登录"
    } else if (value.code === 403) {
      this.status = value.code.toString()
      this.title = "403 禁止访问"
      this.desc = "权限不够啊"
    } else if (value.code === 404) {
      this.status = value.code.toString()
      this.title = "404 资源不存在"
      this.desc = "生活总归带点荒谬"
    } else if (value.code === 500) {
      this.status = value.code.toString()
      this.title = "500 服务器错误"
      this.desc = "要不要先开一把？"
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