<template>
  <el-card shadow="never" class="borderless">
    <el-form label-width="80px" ref="problemForm" :model="problem" v-loading="loading"
             :rules="problemRules" :status-icon="true">
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item size="small" label="题目名称" prop="title">
            <el-input v-model="problem.title"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item size="small" label="题目类型" prop="type">
            <el-radio-group size="small" v-model="problem.type" :disabled="!create">
              <el-radio-button :label="0">程序设计</el-radio-button>
              <el-radio-button :label="1">数据库 (SQLite)</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="15">
        <el-col :span="6">
          <el-form-item size="small" label="时间限制" prop="timeout">
            <el-input v-model.number="problem.timeout">
              <template slot="append">毫秒</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item size="small" label="内存限制" prop="memoryLimit">
            <el-input :disabled="problem.type === 1" v-model.number="problem.memoryLimit">
              <template slot="append">MB</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item size="small" label="输出限制" prop="outputLimit">
            <el-input v-model.number="problem.outputLimit">
              <template slot="append">MB</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="15">
        <el-col :span="6">
          <el-form-item size="small" label="题目分值" prop="score">
            <el-input v-model.number="problem.score">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item size="small" label="分类/标签" class="tags">
        <el-tag :key="tag" effect="dark" type="primary"
                closable @close="tagClose(tag)"
                v-for="tag in tags">{{ tag }}
        </el-tag>
        <el-input class="input-new-tag" v-if="newTagVisible" ref="tagInput"
                  v-model="newTag" @keyup.enter.native="newTagConfirm" @blur="newTagConfirm">
        </el-input>
        <el-button class="button-new-tag" v-else @click="showTagInput">+ 新分类</el-button>
      </el-form-item>
      <el-form-item v-if="problem.type === 1" label="查询语句 " prop="sql">
        <el-input type="textarea" placeholder="这是一道 SQL 题目，请输入正确的 SQL 语句" v-model="problem.sql"/>
      </el-form-item>
      <markdown-editor :str="problem.description" :height="850" @change="editorChange"/>
      <!-- 用于表单验证 -->
      <el-form-item label-width="0" prop="description">
        <el-input style="display: none" type="textarea"
                  v-model="problem.description">
        </el-input>
      </el-form-item>
      <el-form-item size="small" label-width="0" style="margin-bottom: 0">
        <el-button type="success" :disabled="!dataChanged"
                   :icon="problemId === null ? 'el-icon-plus': 'el-icon-check'"
                   @click="save">
          保存
        </el-button>
        <el-popconfirm style="margin-left: 10px" title="确定要重置吗，所有更改都会丢失？"
                       placement="right-end" @confirm="resetForm">
          <el-button slot="reference" type="info" icon="el-icon-refresh-left"
                     :disabled="!dataChanged" :loading="loading">
            重置
          </el-button>
        </el-popconfirm>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import {userInfo, toLoginPage, Notice} from "@/util"
import MarkdownEditor from "@/components/manage/problem/MarkdownEditor"
import {ProblemApi} from "@/service"

export default {
  name: "ProblemEditor",
  props: {
    problemId: Number,
    create: Boolean,
    dialogVisible: Boolean
  },
  components: {
    MarkdownEditor
  },
  watch: {
    problemId: {
      immediate: true,
      handler() {
        if (this.dialogVisible) {
          let formLoaded = typeof this.$refs.problemForm !== "undefined"
          this.firstChange = true
          if (this.problemId != null) {
            this.getProblem()
          } else {
            if (formLoaded) {
              this.$refs.problemForm.resetFields()
            }
            this.tags = []
          }
          if (formLoaded) {
            this.$refs.problemForm.clearValidate()
          }
        }
      }
    },
    problem: {
      handler() {
        if (this.firstChange) {
          this.firstChange = false
        } else {
          !this.reset && (this.dataChanged = true)
          this.reset = false
        }
      },
      deep: true
    }
  },
  data() {
    const checkMemoryLimit = (rule, value, callback) => {
      if (value < 24 || value > 256) {
        callback(new Error("请输入 24 ~ 256 之间的数值"))
      } else {
        callback()
      }
    }
    const checkTimeLimit = (rule, value, callback) => {
      if (value < 100 || value > 10000) {
        callback(new Error("请输入 100 ~ 10000 之间的数值"))
      } else {
        callback()
      }
    }
    const checkOutputLimit = (rule, value, callback) => {
      if (value < 1 || value > 128) {
        callback(new Error("请输入 1 ~ 128 之间的数值"))
      } else {
        callback()
      }
    }
    return {
      firstChange: true,
      dataChanged: false,
      loading: false,
      reset: false,
      problem: {
        title: "",
        description: "",
        timeout: "",
        memoryLimit: "",
        outputLimit: "",
        type: 0,
        sql: "",
        score: ""
      },
      // region 表单验证
      problemRules: {
        title: [
          {required: true, message: "请输入题目名称", trigger: "blur"}
        ],
        score: [
          {required: true, type: "number", message: "请填写分值", trigger: "blur"}
        ],
        type: [
          {required: true}
        ],
        timeout: [
          {required: true, type: "number", message: "请填写运行时间限制", trigger: "blur"},
          {validator: checkTimeLimit, trigger: "blur"}
        ],
        memoryLimit: [
          {required: true, type: "number", message: "请填写内存限制", trigger: "blur"},
          {validator: checkMemoryLimit, trigger: "blur"}
        ],
        outputLimit: [
          {required: true, type: "number", message: "请填写输出限制", trigger: "blur"},
          {validator: checkOutputLimit, trigger: "blur"}
        ],
        description: [
          {required: true, message: "请输入题目描述", trigger: "blur"}
        ]
      },
      // endregion
      tags: [],
      newTag: "",
      newTagVisible: false,
      textAreaSize: {
        minRows: 3,
        maxRows: 12
      }
    }
  },
  methods: {
    editorChange(val) {
      this.problem.description = val
    },
    resetForm() {
      this.reset = true
      if (this.problemId == null) {
        this.$refs.problemForm.resetFields()
        this.tags = []
      } else {
        this.getProblem()
      }
    },
    getProblem() {
      this.loading = true
      ProblemApi.get(this.problemId, userInfo())
          .then((data) => {
            this.problem = data
            if (typeof data.category !== "undefined") {
              this.tags = data.category.split(",")
            }
            if (this.reset) {
              this.dataChanged = false
            }
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage(this)
            } else {
              Notice.notify.error(this, {
                title: "获取题目内容失败",
                message: `${error.code} ${error.msg}`
              })
            }
          })
          .finally(() => {
            this.loading = false
          })
    },
    tagClose(tag) {
      this.tags.splice(this.tags.indexOf(tag), 1)
      this.dataChanged = true
    },
    showTagInput() {
      this.newTagVisible = true;
      this.$nextTick(() => {
        this.$refs.tagInput.$refs.input.focus();
      });
    },
    newTagConfirm() {
      let tag = this.newTag
      if (tag && this.tags.indexOf(tag) === -1) {
        this.tags.push(tag)
        this.dataChanged = true
      }
      this.newTagVisible = false
      this.newTag = ""
    },
    save() {
      this.$refs.problemForm.validate((valid) => {
        if (!valid) {
          return false
        }
        this.problem.category = this.tags.join(",")
        ProblemApi.save(this.problem, userInfo(), this.create)
            .then(() => {
              this.$emit("update:dialogVisible", false)
              this.$emit("refresh")
              this.create && this.resetForm()
              Notice.notify.success(this, {
                title: `${this.problem.title} 已保存`
              })
            })
            .catch((error) => {
              switch (error.code) {
                case 401:
                  toLoginPage(this)
                  break
                default:
                  Notice.notify.error(this, {
                    title: `${this.problem.title} 保存失败`,
                    message: `${error.code} ${error.msg}`
                  })
              }
            })
      })
    }
  }
}
</script>

<style scoped lang="scss">
.tags {
  * {
    margin-left: 10px;
    vertical-align: middle;

    &:first-child {
      margin-left: 0;
    }
  }

  .button-new-tag {
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
    vertical-align: middle;
  }

  .input-new-tag {
    width: 90px;
    vertical-align: middle;
  }
}
</style>