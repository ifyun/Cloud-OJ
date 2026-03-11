<script setup lang="ts">
import rehypeKatex from "rehype-katex"
import rehypeRaw from "rehype-raw"
import rehypeStringify from "rehype-stringify"
import remarkDirective from "remark-directive"
import remarkGfm from "remark-gfm"
import remarkMath from "remark-math"
import remarkParse from "remark-parse"
import remarkRehype from "remark-rehype"
import { unified } from "unified"
import { computed } from "vue"
import "./markdown.scss"
import rehypeImage from "./plugin/rehype-image"
import remarkContainer from "./plugin/remark-container"

const props = defineProps<{
  content: string
  theme: "light" | "dark"
}>()

const html = computed<string>(() =>
  String(markdownCompiler.processSync(props.content))
)

const markdownCompiler = unified()
  .use(remarkParse)
  .use(remarkGfm)
  .use(remarkMath)
  .use(remarkDirective)
  .use(remarkContainer)
  .use(remarkRehype, { allowDangerousHtml: true })
  .use(rehypeRaw)
  .use(rehypeImage)
  .use(rehypeKatex)
  .use(rehypeStringify)
</script>

<template>
  <div :class="theme" class="markdown-body" v-html="html" />
</template>
