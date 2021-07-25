<template>
  <div :style="{ height: `${editorHeight()}px` }">
    <div class="editor-wrapper">
      <el-row>
        <el-col :span="12">
          <div class="toolbar">
            <el-tooltip placement="top" content="标题">
              <el-button size="mini" @click="heading">
                <Icon name="heading" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="加粗">
              <el-button size="mini" @click="bold">
                <Icon name="bold" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="斜体">
              <el-button size="mini" @click="italic">
                <Icon name="italic" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="引用">
              <el-button size="mini" @click="quote">
                <Icon name="quote-left" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="代码块">
              <el-button size="mini" @click="code">
                <Icon name="file-code" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="公式(KaTeX)">
              <el-button size="mini" @click="katex">
                <Icon name="square-root-alt" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="链接">
              <el-button size="mini" @click="link">
                <Icon name="link" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="插入图片">
              <el-button size="mini" @click="image">
                <Icon name="image" inverse/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="提示">
              <el-button size="mini" @click="info('info')">
                <Icon style="color: #3D90FE" name="info-circle"/>
              </el-button>
            </el-tooltip>
            <el-tooltip placement="top" content="警告">
              <el-button size="mini" @click="info('warning')">
                <Icon style="color: #F3AD25" name="info-circle"/>
              </el-button>
            </el-tooltip>
          </div>
          <div :style="{ height: `${editorHeight()}px` }">
            <textarea style="line-height: 1.5" ref="cm"/>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="preview" :style="{ height: `${editorHeight() + 10}px`}">
            <markdown-it :content="content"/>
          </div>
        </el-col>
      </el-row>
    </div>
    <el-dialog title="上传图片或插入图片链接" width="300"
               :visible.sync="imageDialog.visible"
               :append-to-body="true">
      <el-row>
        <el-col :span="6">
          <div class="upload-wrapper">
            <el-upload list-type="picture-card" :action="imageDialog.uploadUrl"
                       :headers="imageDialog.headers" :before-upload="checkImageType"
                       :on-success="imageUploadSuccess" :on-error="imageUploadError"
                       :on-preview="imagePreview" :on-remove="removeImage">
              <i class="el-icon-plus"></i>
            </el-upload>
            <el-dialog :visible.sync="imageDialog.previewDialogVisible" append-to-body>
              <img width="100%" :src="imageDialog.imageUrl" alt="">
            </el-dialog>
          </div>
          <div style="margin: 5px 0 0 5px">
            PNG / JPG / SVG
          </div>
        </el-col>
        <el-col :span="18">
          <el-form label-width="90px">
            <el-form-item label="图片名称">
              <el-input size="medium" prefix-icon="el-icon-collection-tag" placeholder="可选"
                        v-model="imageDialog.imageName"/>
            </el-form-item>
            <el-form-item label="图片链接">
              <el-input size="medium" placeholder="支持相对路径" prefix-icon="el-icon-link"
                        v-model="imageDialog.imageUrl">
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button size="medium" type="primary" icon="el-icon-bottom"
                         @click="insertImage">
                插入
              </el-button>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import MarkdownIt from "@/components/MarkdownIt"
import codemirror from "codemirror"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/markdown/markdown.js"
import "codemirror/theme/monokai.css"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/addon/edit/closebrackets.js"
import "codemirror/lib/codemirror.css"
import "codemirror/mode/markdown/markdown.js"
import "codemirror/theme/monokai.css"
import "codemirror/addon/edit/matchbrackets.js"
import "codemirror/addon/edit/closebrackets.js"
import Icon from "vue-awesome/components/Icon"
import "vue-awesome/icons/heading"
import "vue-awesome/icons/bold"
import "vue-awesome/icons/italic"
import "vue-awesome/icons/image"
import "vue-awesome/icons/quote-left"
import "vue-awesome/icons/link"
import "vue-awesome/icons/info-circle"
import "vue-awesome/icons/file-code"
import "vue-awesome/icons/square-root-alt"
import {Notice, userInfo} from "@/util"
import {ApiPath} from "@/service"

const CodeMirror = window.CodeMirror || codemirror

export default {
  name: "MarkdownEditor",
  components: {
    MarkdownIt,
    Icon
  },
  props: {
    str: String,
    height: Number
  },
  watch: {
    str(val) {
      this.content = val
    },
    content: {
      handler(val) {
        this.$emit("change", val)
        if (this.editor.getValue() !== val) {
          this.editor.setValue(val)
        }
      }
    },
  },
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.cm, this.cmOptions)
    this.editor.on("change", (cm) => {
      this.content = cm.getValue()
    })
    const ctx = this
    window.onresize = () => {
      ctx.windowHeight = document.body.clientHeight
    }
  },
  data() {
    return {
      windowHeight: document.body.clientHeight,
      editor: null,
      cmOptions: {
        mode: "text/x-markdown",
        theme: "monokai",
        tabSize: 4,
        smartIndent: true,
        indentUnit: 4,
        lineNumbers: true,
        lineWrapping: true,
        line: true,
        showHint: true,
        matchBrackets: true,
        autoCloseBrackets: true
      },
      content: "",
      imageDialog: {
        visible: false,
        uploadUrl: ApiPath.PROBLEM_IMAGE,
        headers: {
          userId: userInfo().userId,
          token: userInfo().token
        },
        previewDialogVisible: false,
        imageName: "",
        imageUrl: ""
      }
    }
  },
  methods: {
    editorHeight() {
      const offset = 320

      if (this.windowHeight <= 1000) {
        return 1000 - offset
      } else if (this.windowHeight >= 1400) {
        return 1400 - offset
      } else {
        return this.windowHeight - offset
      }
    },
    getCursor() {
      const cursor = this.editor.getCursor()
      return {
        ch: cursor.ch,
        line: cursor.line
      }
    },
    somethingSelected() {
      return this.editor.somethingSelected()
    },
    listSelections() {
      return this.editor.listSelections()
    },
    setSelection(start, end) {
      return this.editor.setSelection(start, end)
    },
    getRange(start, end) {
      return this.editor.getRange(start, end)
    },
    replaceRange(str, start, end) {
      return this.editor.replaceRange(str, start, end)
    },
    replaceSelection(str) {
      return this.editor.replaceSelection(str)
    },
    setCursor(cursor) {
      return this.editor.setCursor(cursor)
    },
    focus() {
      return this.editor.focus()
    },
    heading() {
      this.toggle("#### ", true)
    },
    bold() {
      this.toggle("**")
    },
    italic() {
      this.toggle("*")
    },
    quote() {
      this.toggle("> ", true)
    },
    /**
     * 插入图片
     */
    image() {
      this.imageDialog.visible = true
    },
    checkImageType(file) {
      const isTypeOk = ["image/jpeg", "image/png", "image/svg+xml"].indexOf(file.type) !== -1
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isTypeOk) {
        Notice.message.error(this, "图片只能是 JPG/PNG/SVG")
      }

      if (!isLt2M) {
        Notice.message.error(this, "图片大小不能超过 2MB")
      }

      return isTypeOk && isLt2M
    },
    imageUploadSuccess(res) {
      this.imageDialog.imageUrl = `${ApiPath.PROBLEM_IMAGE}/${res}`
    },
    imageUploadError(err) {
      Notice.notify.error(this, {
        offset: 0,
        title: "上传图片失败",
        message: `${err.status} ${err.error}`
      })
    },
    imagePreview() {
      this.imageDialog.previewDialogVisible = true
    },
    removeImage() {
      this.imageDialog.imageUrl = ""
    },
    insertImage() {
      let content = `![${this.imageDialog.imageName}](${this.imageDialog.imageUrl})\n`
      if (this.getCursor().ch !== 0) {
        content = "\n" + content
      }
      this.replaceSelection(content);
      this.imageDialog.visible = false
    },
    /**
     * 插入代码块或将选中的文本设置为代码块
     */
    code() {
      const symbol = "```"
      let {anchor, head} = this.listSelections()[0]
      head.line >= anchor.line && head.sticky === "before" && ([head, anchor] = [anchor, head])
      let content = this.getRange(head, anchor)
      let str = `${symbol}\n${content}\n${symbol}\n`
      this.insertBlock(str)
    },
    katex() {
      const symbol = "$$"
      let {anchor, head} = this.listSelections()[0]
      head.line >= anchor.line && head.sticky === "before" && ([head, anchor] = [anchor, head])
      let content = this.getRange(head, anchor)
      let str = `${symbol}\n${content}\n${symbol}\n`
      this.insertBlock(str)
    },
    /**
     * 插入提示或将选中的文本设置为提示
     * @param type "info" or "warning"
     */
    info(type) {
      let {anchor, head} = this.listSelections()[0]
      head.line >= anchor.line && head.sticky === "before" && ([head, anchor] = [anchor, head])
      let content = this.getRange(head, anchor)

      let str = `::: ${type}\n${content}\n:::\n`
      this.insertBlock(str)
    },
    insertBlock(content) {
      if (this.getCursor().ch !== 0) {
        content = "\n\n" + content
      }

      this.replaceSelection(content)
      let cursor = this.getCursor()
      cursor = {
        line: cursor.line - 2,
        ch: content.length
      }
      this.setCursor(cursor)
      this.focus()
    },
    /**
     * 插入链接或将选中的文本变为链接
     */
    link() {
      let {anchor, head} = this.listSelections()[0]
      head.line >= anchor.line && head.sticky === "before" && ([head, anchor] = [anchor, head])
      let content = this.getRange(head, anchor)
      if (content.length === 0) {
        content = "链接"
      }
      this.replaceSelection(`[${content}](URL)`)
      let cursor = this.getCursor()
      this.setSelection(
          {line: cursor.line, ch: cursor.ch - 4},
          {line: cursor.line, ch: cursor.ch - 1})
      this.focus()
    },
    /**
     * 切换样式
     * @param symbol 样式字符
     * @param leftOnly 是否只有左边
     */
    toggle(symbol, leftOnly = false) {
      let hasPrefix = false,
          hasSuffix = false

      if (this.somethingSelected()) {
        const selectedContext = this.listSelections()[0]
        let {anchor, head} = selectedContext

        head.line >= anchor.line && head.sticky === "before" && ([head, anchor] = [anchor, head])

        let {line: preLine, ch: prePos} = head,
            {line: aftLine, ch: aftPos} = anchor

        // 左右字符的 Cursor 对象
        const left = {line: preLine, ch: prePos - symbol.length},
            right = {line: aftLine, ch: aftPos + symbol.length}

        this.getRange(left, head) === symbol && (hasPrefix = true)
        this.getRange(anchor, right) === symbol && (hasSuffix = true)

        !leftOnly && hasSuffix && this.replaceRange("", anchor, right)
        hasPrefix && this.replaceRange("", left, head)

        if (!hasPrefix && !hasSuffix) {
          if (!leftOnly) {
            this.setCursor(anchor)
            this.replaceSelection(symbol)
          }
          this.setCursor(head)
          this.replaceSelection(symbol)
          prePos += symbol.length
          aftPos += aftLine === preLine ? symbol.length : 0
          this.setSelection({line: aftLine, ch: aftPos}, {line: preLine, ch: prePos})
        }
      } else {
        const cursor = this.getCursor()
        const left = {line: cursor.line, ch: cursor.ch - symbol.length},
            right = {line: cursor.line, ch: cursor.ch + symbol.length}

        this.getRange(left, cursor) === symbol && (hasPrefix = true)
        !leftOnly && this.getRange(cursor, right) === symbol && (hasSuffix = true)

        if (hasPrefix && hasSuffix) {
          this.replaceRange("", cursor, right)
          this.replaceRange("", left, cursor)
          this.setCursor(left)
        } else if (!hasPrefix && !hasSuffix) {
          const s = leftOnly ? symbol : symbol + symbol
          this.replaceSelection(s)
          this.setCursor(right)
        }
      }

      this.focus()
    }
  }
}
</script>

<style scoped>
.editor-wrapper {
  background-color: #272822;
  border: 1px solid #F0F0F0;
}

.toolbar {
  padding: 2px;
  border-bottom: 1px solid #3F3F3F;
}

.toolbar button {
  margin-left: 0;
  border-color: transparent;
  background-color: #272822;
  height: 33px;
}

.toolbar button:hover {
  background-color: #3A3A3A;
}

.preview {
  padding: 15px 20px;
  background-color: white;
  overflow-y: auto;
}

.upload-wrapper {
  overflow: hidden;
  width: 150px;
  height: 150px;
}
</style>