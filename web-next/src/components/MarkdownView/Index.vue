<template>
  <div :class="theme" class="markdown-body" v-html="html" />
</template>

<script setup lang="ts">
import { computed } from "vue"
import { useStore } from "vuex"
import MarkdownIt from "markdown-it"
import { KatexPlugin } from "./markdown-katex"
import markdownItContainer from "markdown-it-container"
import highlightJs from "highlight.js/lib/core"
import markdown from "highlight.js/lib/languages/markdown"
import c from "highlight.js/lib/languages/c"
import cpp from "highlight.js/lib/languages/cpp"
import python from "highlight.js/lib/languages/python"
import java from "highlight.js/lib/languages/java"

highlightJs.registerLanguage("markdown", markdown)
highlightJs.registerLanguage("c", c)
highlightJs.registerLanguage("cpp", cpp)
highlightJs.registerLanguage("python", python)
highlightJs.registerLanguage("java", java)

const md = new MarkdownIt({
  html: true,
  highlight: function (str: string, lang: string) {
    if (lang && highlightJs.getLanguage(lang)) {
      try {
        return highlightJs.highlight(str, { language: lang }).value
      } catch (_) {
        return ""
      }
    }

    return ""
  }
})

md.use(KatexPlugin)
md.use(markdownItContainer, "info", {
  validate: (params: string) => {
    return params.trim() === "info"
  },
  render: (tokens: any, index: number) => {
    if (tokens[index].nesting === 1) {
      return `<div class="markdown-alert info">`
    } else {
      return `</div>`
    }
  }
})
  .use(markdownItContainer, "warning", {
    validate: (params: string) => {
      return params.trim() === "warning"
    },
    render: (tokens: any, index: number) => {
      if (tokens[index].nesting === 1) {
        return `<div class="markdown-alert warning">`
      } else {
        return `</div>`
      }
    }
  })
  .use(markdownItContainer, "error", {
    validate: (params: string) => {
      return params.trim() === "error"
    },
    render: (tokens: any, index: number) => {
      if (tokens[index].nesting === 1) {
        return `<div class="markdown-alert error">`
      } else {
        return `</div>`
      }
    }
  })

const store = useStore()

const props = defineProps<{
  content: string
}>()

const html = computed<string>(() => md.render(props.content))
const theme = computed<string>(() =>
  store.state.theme != null ? "dark" : "light"
)
</script>

<style lang="scss">
@import "theme";
</style>
