<template>
  <n-button-group size="small">
    <n-button quaternary @click="click('italic')">
      <n-icon :size="iconSize">
        <italic-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('bold')">
      <n-icon :size="iconSize">
        <bold-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('quote')">
      <n-icon :size="iconSize">
        <quote-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('info')">
      <n-icon :size="iconSize">
        <info-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('warning')">
      <n-icon :size="iconSize">
        <warning-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('code')">
      <n-icon :size="iconSize">
        <code-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('ul')">
      <n-icon :size="iconSize">
        <list-ul-icon/>
      </n-icon>
    </n-button>
    <n-button quaternary @click="click('ol')">
      <n-icon :size="iconSize">
        <list-ol-icon/>
      </n-icon>
    </n-button>
    <n-dropdown placement="bottom-start" :show-arrow="true" :options="tableOptions">
      <n-button quaternary>
        <n-icon :size="iconSize">
          <table-icon/>
        </n-icon>
      </n-button>
    </n-dropdown>
    <n-dropdown placement="bottom-start" size="small" :show-arrow="true"
                :options="imgOptions" @select="click">
      <n-button quaternary>
        <n-icon :size="iconSize">
          <image-icon/>
        </n-icon>
      </n-button>
    </n-dropdown>
  </n-button-group>
</template>

<script lang="tsx">
import {Options, Vue} from "vue-class-component"
import {Emit} from "vue-property-decorator"
import {
  NSpace,
  NButtonGroup,
  NButton,
  NInputNumber,
  NDropdown,
  NIcon
} from "naive-ui"
import {
  FormatItalicRound as ItalicIcon,
  FormatBoldRound as BoldIcon,
  FormatQuoteRound as QuoteIcon,
  InfoOutlined as InfoIcon,
  WarningAmberRound as WarningIcon,
  CodeRound as CodeIcon,
  FormatListBulletedRound as ListUlIcon,
  FormatListNumberedRound as ListOlIcon,
  TableChartOutlined as TableIcon,
  AddPhotoAlternateOutlined as ImageIcon,
  AddLinkRound as LinkIcon,
  FileUploadOutlined as UploadIcon
} from "@vicons/material"
import {renderIcon} from "@/utils"

let self: any

@Options({
  components: {
    NSpace,
    NButtonGroup,
    NButton,
    NInputNumber,
    NDropdown,
    NIcon,
    ItalicIcon,
    BoldIcon,
    QuoteIcon,
    InfoIcon,
    WarningIcon,
    CodeIcon,
    ListUlIcon,
    ListOlIcon,
    TableIcon,
    ImageIcon,
    LinkIcon,
    UploadIcon
  }
})
export default class Toolbar extends Vue {
  private iconSize: number = 20

  private cols: number = 3
  private rows: number = 3

  // region 表格选项
  private tableOptions = [
    {
      key: "header",
      type: "render",
      render: () => (
          <NSpace vertical={true} style="padding: 0 5px">
            <NInputNumber value={this.cols} onUpdateValue={value => this.cols = value as number} style="width: 120px">
              {{suffix: () => "列"}}
            </NInputNumber>
            <NInputNumber value={this.rows} onUpdateValue={value => this.rows = value as number} style="width: 120px">
              {{suffix: () => "行"}}
            </NInputNumber>
            <NButton type="primary" onClick={() => self.insertTable()} style="width: 100%">插入表格</NButton>
          </NSpace>
      )
    }
  ]
  // endregion

  // 图片选项
  private imgOptions = [
    {
      label: "插入图片链接",
      key: "img_link",
      icon: renderIcon(LinkIcon)
    },
    {
      label: "上传本地图片",
      key: "img_upload",
      icon: renderIcon(UploadIcon)
    }
  ]

  @Emit("insertTable")
  insertTable(): any {
    return {
      cols: this.cols,
      rows: this.rows
    }
  }

  @Emit("click")
  click(key: string): string {
    return key
  }

  beforeMount() {
    // 将代理对象赋值给 self 变量，以便在渲染函数中使用
    self = this
  }
}
</script>

<style scoped>

</style>
