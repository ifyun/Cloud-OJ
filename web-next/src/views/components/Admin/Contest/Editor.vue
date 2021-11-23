<template>
  <div class="contest-editor">
    <n-page-header class="page-header" :subtitle="subtitle" @back="router.back()">
      <template #header>
        <n-breadcrumb>
          <n-breadcrumb-item>竞赛管理</n-breadcrumb-item>
          <n-breadcrumb-item>{{ title }}</n-breadcrumb-item>
        </n-breadcrumb>
      </template>
      <template #title>{{ title }}</template>
      <template #extra>
        <n-button v-if="showSaveButton" type="success" size="small" round @click="save">
          <template #icon>
            <save-icon/>
          </template>
          保存
        </n-button>
      </template>
    </n-page-header>
    <n-tabs type="line" default-value="base-info" @update:value="tabChange">
      <n-tab-pane name="base-info" tab="竞赛设置">
        <n-spin :show="loading">
          <n-form label-placement="left" :model="contest" :rules="rules" ref="contestForm">
            <n-grid :cols="2" x-gap="12">
              <n-form-item-grid-item :span="1" label="竞赛名称" path="contestName">
                <n-input v-model:value="contest.contestName" maxlength="24" show-count clearable/>
              </n-form-item-grid-item>
              <n-form-item-grid-item :span="1" label="时间范围" path="timeRange">
                <n-date-picker type="datetimerange" v-model:value="contest.timeRange" clearable
                               format="yyyy/MM/dd - HH:mm:ss"/>
              </n-form-item-grid-item>
            </n-grid>
            <n-form-item label="语言限制" path="languages">
              <n-transfer source-title="不允许的语言" target-title="允许的语言"
                          :options="languageOptions" v-model:value="languages"/>
            </n-form-item>
          </n-form>
        </n-spin>
      </n-tab-pane>
      <n-tab-pane v-if="!create" name="problems" tab="题目管理">
        <problem-manage :contest-id="contest.contestId"/>
      </n-tab-pane>
    </n-tabs>
  </div>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {Watch} from "vue-property-decorator"
import {useRoute, useRouter} from "vue-router"
import {useStore} from "vuex"
import {
  FormRules,
  NBreadcrumb,
  NBreadcrumbItem,
  NButton,
  NCheckbox,
  NCheckboxGroup,
  NDatePicker,
  NForm,
  NFormItem,
  NFormItemGridItem,
  NGrid,
  NInput,
  NPageHeader,
  NSpace,
  NSpin,
  NTabPane,
  NTabs,
  NTransfer,
  useMessage
} from "naive-ui"
import {SaveOutlined as SaveIcon} from "@vicons/material"
import ProblemManage from "@/views/components/Admin/Contest/ProblemManage.vue"
import {ContestApi} from "@/api/request"
import {Contest, ErrorMsg, UserInfo} from "@/api/type"
import {LanguageOption, LanguageOptions} from "@/type"
import {LanguageUtil} from "@/utils"
import moment from "moment"

@Options({
  components: {
    ProblemManage,
    NSpace,
    NGrid,
    NSpin,
    NTabs,
    NTabPane,
    NForm,
    NFormItem,
    NFormItemGridItem,
    NInput,
    NCheckboxGroup,
    NCheckbox,
    NButton,
    NTransfer,
    NPageHeader,
    NBreadcrumb,
    NBreadcrumbItem,
    NDatePicker,
    SaveIcon
  }
})
export default class ContestEditor extends Vue {
  private route = useRoute()
  private router = useRouter()
  private store = useStore()
  private message = useMessage()

  private contest: Contest = new Contest()
  private languageOptions: Array<LanguageOption> = LanguageOptions
  private languages: Array<number> = []
  private create: boolean = false
  private loading: boolean = false
  private currentTab = "base-info"

  private rules: FormRules = {
    contestName: {
      required: true,
      trigger: ["input", "blur"],
      message: "请输入竞赛名称"
    },
    timeRange: {
      required: true,
      trigger: ["input", "blur"],
      validator(rule: any, value: Array<number>): Error | boolean {
        if (!value) {
          return new Error("请输入时间范围")
        }
        return true
      }
    },
    languages: {
      required: true,
      trigger: ["input"],
      validator: (): Error | boolean => {
        if (this.languages.length === 0) {
          return new Error("请选择语言")
        }
        return true
      }
    }
  }

  get title(): string {
    return this.create ? "创建新竞赛" : "编辑竞赛"
  }

  get subtitle(): string {
    if (typeof this.contest.contestId === "undefined") {
      return ""
    } else {
      return this.contest.contestName
    }
  }

  get userInfo(): UserInfo {
    return this.store.state.userInfo
  }

  get showSaveButton(): boolean {
    return this.currentTab === "base-info"
  }

  $refs!: {
    contestForm: HTMLFormElement
  }

  @Watch("contest.timeRange")
  timeRangeChange(value: Array<number>) {
    const fmt = "yyyy-MM-DD HH:mm:ss"
    const [start, end] = value
    this.contest.startAt = moment(start).format(fmt)
    this.contest.endAt = moment(end).format(fmt)
  }

  @Watch("languages")
  languagesChange(value: Array<number>) {
    this.contest.languages = LanguageUtil.toNumber(value)
  }

  beforeMount() {
    const reg = /^[\d]+$/
    const id = this.route.params.id.toString()

    if (id === "new") {
      this.create = true
    } else if (reg.test(id)) {
      this.loading = true
      this.queryContest(Number(id))
    }
  }

  tabChange(tab: string) {
    this.currentTab = tab
  }

  queryContest(contestId: number) {
    ContestApi.getById(
        contestId
    ).then((data) => {
      data.timeRange = []
      data.timeRange.push(Date.parse(data.startAt))
      data.timeRange.push(Date.parse(data.endAt))
      this.languages = LanguageUtil.toArray(data.languages)
      this.contest = data
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    }).finally(() => {
      this.loading = false
    })
  }

  handleSave() {
    this.$refs.contestForm.validate((errors: any) => {
      if (!errors) {
        this.save()
      }
    })
  }

  save() {
    ContestApi.save(
        this.contest,
        this.userInfo,
        this.create
    ).then(() => {
      this.message.success(`${this.contest.contestName} 保存成功`)
    }).catch((error: ErrorMsg) => {
      this.message.error(error.toString())
    })
  }
}
</script>

<style scoped lang="scss">
.contest-editor {
  width: calc(100% - var(--layout-padding) * 2);
  padding: 0 25px 25px 25px;
  display: flex;
  flex-direction: column;

  .page-header {
    margin: 20px 0 20px 0;
  }
}
</style>

<style lang="scss">
.contest-editor {
  .n-transfer .n-transfer-list .n-transfer-list-body {
    height: 320px;
  }
}
</style>