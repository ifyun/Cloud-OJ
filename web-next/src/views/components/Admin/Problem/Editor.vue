<template>
  <div class="problem-editor">
    <n-card :bordered="false" style="height: 100%"
            content-style="height: 100%; display: flex; flex-direction: column">
      <n-page-header class="page-header" :subtitle="subtitle" @back="back">
        <template #title>{{ title }}</template>
        <template #extra>
          <n-space size="small">
            <n-button type="success" size="small" @click="handleSave">
              <template #icon>
                <save-icon/>
              </template>
              保存
            </n-button>
            <n-button type="info" size="small" ghost @click="toggleHelp">
              <template #icon>
                <help-icon/>
              </template>
              帮助
            </n-button>
          </n-space>
        </template>
      </n-page-header>
      <!-- 表单 -->
      <n-space class="editor-from" vertical size="small">
        <n-spin :show="loading">
          <n-form :model="problem" label-placement="left" :rules="rules" ref="problemForm">
            <n-grid :cols="2" :x-gap="12">
              <n-form-item-grid-item label="题目名称" path="title" :span="1">
                <n-input v-model:value="problem.title" maxlength="16" show-count clearable/>
              </n-form-item-grid-item>
              <n-form-item-grid-item label="题目类型" path="type" :span="1">
                <n-radio-group v-model:value="problem.type">
                  <n-radio-button :disabled="disableType" :value="0">程序设计</n-radio-button>
                  <n-radio-button :disabled="disableType" :value="1">SQL(SQLite)</n-radio-button>
                </n-radio-group>
              </n-form-item-grid-item>
            </n-grid>
            <n-grid :cols="4" :x-gap="12">
              <n-form-item-grid-item label="题目分数" path="score" :span="1">
                <n-input-number v-model:value="problem.score" :min="0" :max="100" :show-button="false"
                                placeholder="0 ~ 100">
                  <template #suffix>分</template>
                </n-input-number>
              </n-form-item-grid-item>
              <n-form-item-grid-item label="时间限制" path="timeout" :span="1">
                <n-input-number v-model:value="problem.timeout" :show-button="false" :min="100" :max="10000"
                                placeholder="100 ~ 10000">
                  <template #suffix>毫秒</template>
                </n-input-number>
              </n-form-item-grid-item>
              <n-form-item-grid-item label="内存限制" path="memoryLimit" :span="1">
                <n-input-number v-model:value="problem.memoryLimit" :show-button="false" :min="16" :max="256"
                                placeholder="16 ~ 256">
                  <template #suffix>MB</template>
                </n-input-number>
              </n-form-item-grid-item>
              <n-form-item-grid-item label="输出限制" path="outputLimit" :span="1">
                <n-input-number v-model:value="problem.outputLimit" :show-button="false" :min="1" :max="128"
                                placeholder="1 ~ 128">
                  <template #suffix>MB</template>
                </n-input-number>
              </n-form-item-grid-item>
            </n-grid>
            <n-form-item label="分类/标签" path="tags">
              <n-dynamic-tags type="success" v-model:value="problem.tags"/>
            </n-form-item>
          </n-form>
        </n-spin>
      </n-space>
      <!-- 题目内容编辑器 -->
      <div class="editor-markdown">
        <div>
          <markdown-editor v-model="problem.description" :read-only="loading"/>
        </div>
        <div>
          <n-scrollbar style="max-height: 100%">
            <markdown-view :content="problem.description" style="margin: 30px 0 12px 0"/>
          </n-scrollbar>
        </div>
      </div>
    </n-card>
  </div>
  <n-drawer v-model:show="showHelp" :width="750" placement="right">
    <n-drawer-content :native-scrollbar="false" body-content-style="padding: 0">
      <markdown-view :content="helpDoc" style="padding: 24px"/>
    </n-drawer-content>
  </n-drawer>
</template>

<script lang="tsx">
import {Options, Vue} from "vue-class-component"
import {Watch} from "vue-property-decorator"
import {useRoute, useRouter} from "vue-router"
import {useStore} from "vuex"
import {
  FormRules,
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NButtonGroup,
  NCard,
  NDrawer,
  NDrawerContent,
  NDynamicTags,
  NForm,
  NFormItem,
  NFormItemGridItem,
  NGrid,
  NIcon,
  NInput,
  NInputNumber,
  NPageHeader,
  NRadioButton,
  NRadioGroup,
  NScrollbar,
  NSpace,
  NSpin,
  useMessage
} from "naive-ui"
import {HelpOutlineRound as HelpIcon, SaveOutlined as SaveIcon} from "@vicons/material"
import markdownHelp from "@/components/MarkdownEditor/markdown-help"
import MarkdownEditor from "@/components/MarkdownEditor/Index.vue"
import MarkdownView from "@/components/MarkdownView/Index.vue"
import {ErrorMsg, Problem, UserInfo} from "@/api/type"
import {ProblemApi} from "@/api/request"
import {setTitle} from "@/utils"
import MutationType from "@/store/mutation-type"

@Options({
  name: "ProblemEditor",
  components: {
    NDrawer,
    NDrawerContent,
    NPageHeader,
    NScrollbar,
    NCard,
    NSpace,
    NSpin,
    NGrid,
    NForm,
    NFormItem,
    NFormItemGridItem,
    NInput,
    NInputNumber,
    NDynamicTags,
    NButtonGroup,
    NButton,
    NRadioGroup,
    NRadioButton,
    NIcon,
    SaveIcon,
    HelpIcon,
    MarkdownEditor,
    MarkdownView
  }
})
export default class ProblemEditor extends Vue {
  private route = useRoute()
  private router = useRouter()
  private store = useStore()
  private message = useMessage()

  private problem: Problem = new Problem()
  private showHelp: boolean = false
  private create: boolean = false
  private loading: boolean = false

  /* 表单验证规则 */
  private rules: FormRules = {
    title: {
      required: true,
      trigger: ["blur", "input"],
      message: "请输入题目名称"
    },
    type: {
      required: true
    },
    score: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: number): Error | boolean {
        if (value < 0 || value > 100) {
          return new Error("请输入分数")
        }
        return true
      }
    },
    timeout: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: number): Error | boolean {
        if (value < 100 || value > 10000) {
          return new Error("请输入时间限制")
        }
        return true
      }
    },
    memoryLimit: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: number): Error | boolean {
        if (value < 16 || value > 256) {
          return new Error("请输入内存限制")
        }
        return true
      }
    },
    outputLimit: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: number): Error | boolean {
        if (value < 1 || value > 128) {
          return new Error("请输入输出限制")
        }
        return true
      }
    },

  }

  get helpDoc() {
    return `\`\`\`\`markdown\n${markdownHelp}\n\`\`\`\`\n${markdownHelp}`
  }

  get isDarkTheme(): boolean {
    return this.store.state.theme != null
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  get subtitle(): string {
    if (typeof this.problem.problemId === "undefined") {
      return ""
    } else {
      return `${this.problem.problemId} - ${this.problem.title}`
    }
  }

  get title(): string {
    return this.create ? "创建题目" : "编辑题目"
  }

  get disableType(): boolean {
    return !this.create
  }

  @Watch("title")
  titleChange(value: string) {
    setTitle(value)
  }

  @Watch("problem.tags")
  tagsChange(value: Array<string>) {
    this.problem.category = value.join(",")
  }

  declare $refs: {
    problemForm: HTMLFormElement
  }

  beforeMount() {
    const reg = /^[\d]+$/
    const id = this.route.params.id.toString()

    if (id === "new") {
      this.create = true
    } else if (reg.test(id)) {
      this.loading = true
      this.queryProblem(Number(id))
    }

    this.setBreadcrumb()
  }

  setBreadcrumb() {
    const vNode =
        (<NBreadcrumb>
          <NBreadcrumbItem>题目管理</NBreadcrumbItem>
          <NBreadcrumbItem>{this.title}</NBreadcrumbItem>
        </NBreadcrumb>)
    this.store.commit(MutationType.SET_BREADCRUMB, vNode)
  }

  back() {
    this.router.back()
  }

  /**
   * 显示/隐藏帮助
   */
  toggleHelp() {
    this.showHelp = !this.showHelp
  }

  queryProblem(problemId: number) {
    ProblemApi.getSingle(
        problemId,
        this.userInfo
    ).then((data) => {
      this.problem = data
      if (this.problem.category.length > 0) {
        this.problem.tags = this.problem.category.split(",")
      } else {
        this.problem.tags = []
      }
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error.code}: ${error.msg}`)
    }).finally(() => {
      this.loading = false
    })
  }

  handleSave() {
    if (this.problem.description.trim().length === 0) {
      this.message.warning("请输入题目内容！")
      return
    }
    this.$refs.problemForm.validate((errors: any) => {
      if (!errors) {
        this.save()
      }
    })
  }

  /**
   * 保存
   */
  save() {
    this.loading = true
    ProblemApi.save(
        this.problem,
        this.userInfo,
        this.create
    ).then(() => {
      this.message.success(`${this.problem.title} 保存成功`)
      this.create && this.back()
    }).catch((error: ErrorMsg) => {
      this.message.error(`${error.code}: ${error.msg}`)
    }).finally(() => {
      this.loading = false
    })
  }
}
</script>

<style scoped lang="scss">
.problem-editor {
  min-height: 850px;
  height: calc(100% - var(--layout-padding));
  width: calc(100% - var(--layout-padding) * 2);
  padding: var(--layout-padding);
  display: flex;
  flex-direction: column;

  .page-header {
    margin-bottom: 12px;
  }

  .editor-markdown {
    margin-top: -12px;
    display: flex;
    flex-direction: row;
    flex: 1;
    overflow: hidden;

    & > * {
      flex: 1;
      margin-left: 12px;
      height: 100%;

      &:first-child {
        margin-left: 0;
      }
    }
  }
}
</style>
