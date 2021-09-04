<template>
  <div class="markdown-body md-github__body" v-html="markdown"/>
</template>

<script>
import "katex/dist/katex.min.css"
import "github-markdown-css/github-markdown.css"
import "highlight.js/styles/a11y-light.css"

const md = require("markdown-it")({
  html: true
})

const markdownItContainer = require("markdown-it-container")

md.use(require("markdown-it-container"))
    .use(require("markdown-it-highlightjs"))
    .use(require("markdown-it-katex"), {throwOnError: false, errorColor: "#F56C6C"})
    .use(markdownItContainer, 'warning', {
      validate: function (params) {
        return params.trim() === 'warning'
      },
      render: (tokens, idx) => {
        if (tokens[idx].nesting === 1) {
          const icon = `<i class="markdown-it-alert-icon markdown-it-alert-icon-warning"><svg viewBox="64 64 896 896" data-icon="exclamation-circle" width="1em" height="1em" fill="currentColor" aria-hidden="true" class=""><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm-32 232c0-4.4 3.6-8 8-8h48c4.4 0 8 3.6 8 8v272c0 4.4-3.6 8-8 8h-48c-4.4 0-8-3.6-8-8V296zm32 440a48.01 48.01 0 0 1 0-96 48.01 48.01 0 0 1 0 96z"></path></svg></i>`
          return `<div class="markdown-it-alter markdown-it-alter-warning">${icon}`
        } else {
          return '</div>'
        }
      }
    })
    .use(markdownItContainer, 'info', {
      validate: function (params) {
        return params.trim() === 'info'
      },
      render: (tokens, idx) => {
        if (tokens[idx].nesting === 1) {
          const icon = `<i class="markdown-it-alert-icon markdown-it-alert-icon-info"><svg viewBox="64 64 896 896" data-icon="info-circle" width="1em" height="1em" fill="currentColor" aria-hidden="true" class=""><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm32 664c0 4.4-3.6 8-8 8h-48c-4.4 0-8-3.6-8-8V456c0-4.4 3.6-8 8-8h48c4.4 0 8 3.6 8 8v272zm-32-344a48.01 48.01 0 0 1 0-96 48.01 48.01 0 0 1 0 96z"></path></svg></i>`
          return `<div class="markdown-it-alter markdown-it-alter-info">${icon}`
        } else {
          return '</div>'
        }
      }
    })
    .use(markdownItContainer, 'success', {
      validate: function (params) {
        return params.trim() === 'success'
      },
      render: (tokens, idx) => {
        if (tokens[idx].nesting === 1) {
          const icon = `<i class="markdown-it-alert-icon markdown-it-alert-icon-success"><svg viewBox="64 64 896 896" data-icon="check-circle" width="1em" height="1em" fill="currentColor" aria-hidden="true" class=""><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm193.5 301.7l-210.6 292a31.8 31.8 0 0 1-51.7 0L318.5 484.9c-3.8-5.3 0-12.7 6.5-12.7h46.9c10.2 0 19.9 4.9 25.9 13.3l71.2 98.8 157.2-218c6-8.3 15.6-13.3 25.9-13.3H699c6.5 0 10.3 7.4 6.5 12.7z"></path></svg></i>`
          return `<div class="markdown-it-alter markdown-it-alter-success">${icon}`
        } else {
          return '</div>'
        }
      }
    })
    .use(markdownItContainer, 'error', {
      validate: function (params) {
        return params.trim() === 'error'
      },
      render: (tokens, idx) => {
        if (tokens[idx].nesting === 1) {
          const icon = `<i class="markdown-it-alert-icon markdown-it-alert-icon-error"><svg viewBox="64 64 896 896" data-icon="close-circle" width="1em" height="1em" fill="currentColor" aria-hidden="true" class=""><path d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm165.4 618.2l-66-.3L512 563.4l-99.3 118.4-66.1.3c-4.4 0-8-3.5-8-8 0-1.9.7-3.7 1.9-5.2l130.1-155L340.5 359a8.32 8.32 0 0 1-1.9-5.2c0-4.4 3.6-8 8-8l66.1.3L512 464.6l99.3-118.4 66-.3c4.4 0 8 3.5 8 8 0 1.9-.7 3.7-1.9 5.2L553.5 514l130 155c1.2 1.5 1.9 3.3 1.9 5.2 0 4.4-3.6 8-8 8z"></path></svg></i>`
          return `<div class="markdown-it-alter markdown-it-alter-error">${icon}`
        } else {
          return '</div>'
        }
      }
    })

export default {
  name: "MarkdownIt",
  props: {
    content: String
  },
  watch: {
    content: {
      handler(newVal) {
        this.markdown = md.render(newVal)
      },
      immediate: true
    }
  },
  data() {
    return {
      markdown: ""
    }
  }
}
</script>

<style lang="scss">
.markdown-it-alter-info {
  border: 1px solid #91d5ff;
  background-color: #e6f7ff;
}

.markdown-it-alert-icon-info {
  color: #1890ff;
}

.markdown-it-alter-success {
  border: 1px solid #b7eb8f;
  background-color: #f6ffed;
}

.markdown-it-alert-icon-success {
  color: #52c41a;
}

.markdown-it-alter-error {
  border: 1px solid #f5222d;
  background-color: #fff1f0;
}

.markdown-it-alert-icon-error {
  color: #f5222d;
}

.markdown-it-alter-warning {
  border: 1px solid #ffe58f;
  background-color: #fffbe6;
}

.markdown-it-alert-icon-warning {
  color: #faad14;
}

.markdown-it-alter {
  border: 0;
  display: inline-flex;
  font-family: 'Chinese Quote', -apple-system, BlinkMacSystemFont, 'Segoe UI',
  'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue',
  Helvetica, Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji',
  'Segoe UI Symbol';
  font-size: 14px;
  font-variant: tabular-nums;
  line-height: 1.5;
  color: rgba(0, 0, 0, 0.65);
  box-sizing: border-box;
  list-style: none;
  position: relative;
  padding: 8px 15px 8px 37px;
  border-radius: 4px;
  width: 100%;
  margin-bottom: 16px;

  p {
    margin-top: 1px;
    margin-bottom: 0
  }
}

.markdown-it-alert-icon {
  top: 11px;
  left: 12px;
  position: absolute;
}
</style>