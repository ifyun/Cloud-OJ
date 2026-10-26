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

<script setup lang="tsx">
import { renderIcon } from "@/utils"
import {
  FormatBoldRound as BoldIcon,
  CodeRound as CodeIcon,
  AddPhotoAlternateOutlined as ImageIcon,
  InfoOutlined as InfoIcon,
  FormatItalicRound as ItalicIcon,
  AddLinkRound as LinkIcon,
  FormatListNumberedRound as ListOlIcon,
  FormatListBulletedRound as ListUlIcon,
  FormatQuoteRound as QuoteIcon,
  FileUploadOutlined as UploadIcon,
  WarningAmberRound as WarningIcon
} from "@vicons/material"
import { NButton, NButtonGroup, NDropdown, NIcon } from "naive-ui"

const iconSize = 20

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
  (e: "click", value: string): void
}>()

function click(key: string) {
  emit("click", key)
}
</script>
