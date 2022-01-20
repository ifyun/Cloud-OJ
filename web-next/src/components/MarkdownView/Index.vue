<template>
  <div :class="theme" class="markdown-body" v-html="markdown"/>
</template>

<script lang="ts">
import {useStore} from "vuex"
import {Options, Vue} from "vue-class-component"
import {Prop} from "vue-property-decorator"
import {KatexPlugin} from "@/components/MarkdownView/markdown-katex"
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

const MarkdownIt = require("markdown-it")
const md = new MarkdownIt({
  html: true,
  highlight: function (str: string, lang: string) {
    if (lang && highlightJs.getLanguage(lang)) {
      try {
        return highlightJs.highlight(str, {language: lang}).value
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
}).use(markdownItContainer, "warning", {
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
}).use(markdownItContainer, "error", {
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

@Options({
  name: "MarkdownView"
})
export default class MarkdownView extends Vue {
  private store = useStore()

  @Prop(String)
  private content?: string

  get markdown(): string {
    return md.render(this.content)
  }

  get theme(): string {
    return this.store.state.theme != null ? "dark" : "light"
  }
}
</script>

<style lang="scss">
.markdown-body {
  font-family: inherit;

  &.light {
    @import "~highlight.js/scss/github";
  }

  & > *:first-child {
    margin-top: 0;
  }

  h1, h2, h3 {
    border-bottom: 1px solid #EFEFF5;
    margin-top: 28px;
    margin-bottom: 18px;
  }

  h1, h2, h3, h4, h5, h6 {
    font-weight: 500;
    padding-bottom: 5px;
  }

  h4, h5, h6 {
    margin-top: 20px;
    margin-bottom: 10px;
  }

  h1 {
    font-size: 30px;
  }

  h2 {
    font-size: 22px;
  }

  h3 {
    font-size: 18px;
  }

  h4 {
    font-size: 16px;
  }

  ul, ol {
    padding-left: 25px;
  }

  table {
    border: 1px solid #EFEFEF;
    border-radius: 4px;
    border-spacing: 0;
    width: 100%;

    thead {
      background-color: #F8F8F8;
    }

    th, td {
      padding: 10px 15px;
    }

    td {
      border-top: 1px solid #EFEFEF;
    }
  }

  blockquote {
    margin: 10px 0 0 0;
    padding: 6px 15px;
    border-left-width: 5px;
    border-left-style: solid;
    border-left-color: #EFEFEF;
    color: #A5A5A5;

    p {
      margin: 0;
    }
  }

  p code {
    font-family: v-mono, SFMono-Regular, Menlo, Consolas, Courier, monospace;
    background-color: #F8F8F8;
    padding: 1px 4px;
  }

  pre {
    font-family: v-mono, SFMono-Regular, Menlo, Consolas, Courier, monospace;
    background-color: #F8F8F8;
    padding: 15px;
    border-radius: 3px;
    overflow: auto;

    code {
      font-family: inherit;
    }

    &.math-block {
      text-align: center;
      background-color: transparent;

      .katex {
        .katex-html > .newline {
          margin-top: 10px;
        }
      }
    }

    &::-webkit-scrollbar {
      height: 8px;
      background-color: transparent;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 4px;
      background-color: #E8E8E8;
    }
  }

  .markdown-alert {
    padding: 2px 15px;
    margin-top: 10px;
    background-color: #F8F8F8;
    border-left-width: 5px;
    border-left-style: solid;
    border-radius: 2px;

    &.info {
      border-left-color: #1890FF;
    }

    &.warning {
      border-left-color: #F5B047;
    }
  }

  img {
    max-width: 100%;
    margin-top: 12px;
  }

  &.dark {
    @import "~highlight.js/scss/github-dark";

    background-color: #18181C;

    h1, h2, h3 {
      border-bottom: 1px solid #303033;
    }

    blockquote {
      border-left-color: #31313C;
    }

    table {
      border: 1px solid #303033;

      thead {
        background-color: #303033;
      }

      td {
        border-top: 1px solid #303033;
      }
    }

    p code {
      background-color: #303033;
    }

    pre {
      background-color: #303033;

      &.math-block {
        background-color: transparent;
      }

      &::-webkit-scrollbar-thumb {
        background-color: #28282C;
      }
    }

    .markdown-alert {
      background-color: #303033;
    }
  }
}
</style>
