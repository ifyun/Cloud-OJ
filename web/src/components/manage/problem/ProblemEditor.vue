<template>
  <div>
    <el-form label-width="80px" ref="problemForm"
             :model="problem" v-loading="loading"
             :rules="problemRules"
             :status-icon="true">
      <el-row>
        <el-col :span="8">
          <el-form-item label="题目名称" prop="title">
            <el-input size="medium" v-model="problem.title"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="分值" prop="score">
            <el-input size="medium" v-model.number="problem.score">
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="限时" prop="timeout">
            <el-input size="medium" v-model.number="problem.timeout">
              <template slot="append">ms</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="分类/标签">
        <el-tag :key="tag" effect="plain" type="primary"
                closable @close="tagClose(tag)"
                v-for="tag in tags">{{ tag }}
        </el-tag>
        <el-input class="input-new-tag" ref="tagInput" v-if="newTagVisible"
                  v-model="newTag" size="small"
                  @keyup.enter.native="newTagConfirm"
                  @blur="newTagConfirm">
        </el-input>
        <el-button class="button-new-tag"
                   v-else size="small"
                   @click="showTagInput">+ 新分类
        </el-button>
      </el-form-item>
      <el-divider>题目内容</el-divider>
      <MarkdownEditor :height="700" :data="problem.description"
                      @change="editorChange"/>
      <el-form-item label-width="0" prop="description">
        <el-input style="display: none" type="textarea"
                  v-model="problem.description">
        </el-input>
      </el-form-item>
      <el-form-item label-width="0">
        <el-button type="primary" :disabled="!dataChanged"
                   :icon="problemId === null ? 'el-icon-plus': 'el-icon-check'"
                   @click="save">
          {{ problemId === null ? '提交' : '保存修改' }}
        </el-button>
        <el-popconfirm style="margin-left: 10px" title="确定要重置吗，所有更改都会丢失？"
                       placement="right-end" @onConfirm="resetForm">
          <el-button slot="reference" type="danger" icon="el-icon-refresh-left"
                     :disabled="!dataChanged" :loading="loading">
            重置
          </el-button>
        </el-popconfirm>
      </el-form-item>
    </el-form>
  </div>
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
          let formLoaded = this.$refs["problemForm"] !== undefined
          this.firstChange = true
          if (this.problemId != null) {
            this.getProblem()
          } else {
            if (formLoaded) {
              this.$refs["problemForm"].resetFields()
            }
            this.tags = []
          }
          if (formLoaded) {
            this.$refs["problemForm"].clearValidate()
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
    return {
      // problem 对象是否首次改变
      firstChange: true,
      // problem 对象是否已发生改变
      dataChanged: false,
      loading: false,
      reset: false,
      problem: {
        title: "",
        description: "",
        timeout: "",
        score: ""
      },
      problemRules: {
        title: [
          {required: true, message: "请输入题目名称", trigger: "blur"}
        ],
        score: [
          {required: true, type: "number", message: "请填写分值", trigger: "blur"}
        ],
        timeout: [
          {required: true, type: "number", message: "请填写时限", trigger: "blur"}
        ],
        description: [
          {required: true, message: "请输入题目描述", trigger: "blur"}
        ]
      },
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
        this.$refs["problemForm"].resetFields()
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
            this.tags = data.category.split(",")
            if (this.reset)
              this.dataChanged = false
          })
          .catch((error) => {
            if (error.code === 401) {
              toLoginPage()
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
      this.$refs["problemForm"].validate((valid) => {
        if (!valid) {
          return false
        }
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
                  toLoginPage()
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

<style scoped>
.el-tag {
  vertical-align: middle;
}

.el-tag + .el-tag {
  margin-left: 10px;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
  vertical-align: middle;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: middle;
}
</style>