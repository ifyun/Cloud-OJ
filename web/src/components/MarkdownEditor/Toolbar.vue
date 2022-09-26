<template>
  <n-button-group size="small">
    <n-button tertiary @click="click('italic')">
      <n-icon :size="iconSize">
        <italic-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('bold')">
      <n-icon :size="iconSize">
        <bold-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('quote')">
      <n-icon :size="iconSize">
        <quote-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('info')">
      <n-icon :size="iconSize">
        <info-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('warning')">
      <n-icon :size="iconSize">
        <warning-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('code')">
      <n-icon :size="iconSize">
        <code-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('ul')">
      <n-icon :size="iconSize">
        <list-ul-icon />
      </n-icon>
    </n-button>
    <n-button tertiary @click="click('ol')">
      <n-icon :size="iconSize">
        <list-ol-icon />
      </n-icon>
    </n-button>
    <n-dropdown
      placement="bottom-start"
      :show-arrow="true"
      :options="tableOptions">
      <n-button tertiary>
        <n-icon :size="iconSize">
          <table-icon />
        </n-icon>
      </n-button>
    </n-dropdown>
    <n-dropdown
      placement="bottom-start"
      size="small"
      :show-arrow="true"
      :options="imgOptions"
      @select="click">
      <n-button tertiary>
        <n-icon :size="iconSize">
          <image-icon />
        </n-icon>
      </n-button>
    </n-dropdown>
  </n-button-group>
</template>

<script lang="tsx">
export default {
  name: "MarkdownToolbar"
}
</script>

<script setup lang="tsx">
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
import { renderIcon } from "@/utils"

const iconSize = 20

let cols = 3
let rows = 3

// region 表格选项
const tableOptions = [
  {
    key: "header",
    type: "render",
    render: () => (
      <NSpace vertical={true} style="padding: 0 6px">
        <NInputNumber
          value={cols}
          onUpdateValue={(value) => (cols = value as number)}
          style="width: 120px">
          {{ suffix: () => "列" }}
        </NInputNumber>
        <NInputNumber
          value={rows}
          onUpdateValue={(value) => (rows = value as number)}
          style="width: 120px">
          {{ suffix: () => "行" }}
        </NInputNumber>
        <NButton
          type="primary"
          onClick={() => insertTable()}
          style="width: 100%">
          插入表格
        </NButton>
      </NSpace>
    )
  }
]
// endregion

// 图片选项
const imgOptions = [
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

const emit = defineEmits<{
  // eslint-disable-next-line no-unused-vars
  (e: "insertTable", value: any): void
  // eslint-disable-next-line no-unused-vars
  (e: "click", value: string): void
}>()

function insertTable() {
  emit("insertTable", {
    cols: cols,
    rows: rows
  })
}

function click(key: string) {
  emit("click", key)
}
</script>
