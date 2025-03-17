<template>
  <div id="md-view" :class="theme" class="markdown-body" v-html="html" />
</template>

<script setup lang="ts">
import highlightJs from "highlight.js/lib/core"
import c from "highlight.js/lib/languages/c"
import cpp from "highlight.js/lib/languages/cpp"
import java from "highlight.js/lib/languages/java"
import markdown from "highlight.js/lib/languages/markdown"
import python from "highlight.js/lib/languages/python"
import MarkdownIt from "markdown-it"
import markdownItContainer from "markdown-it-container"
import { computed, onMounted, watch } from "vue"
import { ImgPlugin } from "./markdown-img"
import { KatexPlugin } from "./markdown-katex"
import githubLight from "highlight.js/styles/github.min.css?raw"
import githubDark from "highlight.js/styles/github-dark.min.css?raw"
import "./theme.scss"

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
md.use(ImgPlugin)
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

const props = defineProps<{
  content: string
  theme: "light" | "dark"
}>()

const html = computed<string>(() => md.render(props.content))

watch(
  () => props.theme,
  (val) => {
    const style = document.getElementById("md-style")
    if (style) {
      style.innerHTML = val === "light" ? githubLight : githubDark
    }
  }
)

onMounted(() => {
  const style = document.createElement("style")
  style.id = "md-style"
  style.innerHTML = props.theme === "light" ? githubLight : githubDark
  document.getElementById("md-view")?.after(style)
})
</script>
