<template>
  <div>
    <el-form label-width="80px" ref="problemForm"
             :model="problem"
             :rules="problemRules"
             :status-icon="true">
      <el-row>
        <el-col :span="12">
          <el-form-item label="题目名称" prop="title">
            <el-input v-model="problem.title"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="分值" prop="score">
            <el-input v-model.number="problem.score">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="时间限制" prop="timeout">
            <el-input v-model.number="problem.timeout">
              <template slot="append">ms</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="分类">
        <el-tag :key="tag" effect="dark" type="success" closable
                @close="tagClose(tag)"
                v-for="tag in tags">{{ tag }}
        </el-tag>
        <el-input class="input-new-tag" ref="tagInput" v-if="newTagVisible"
                  v-model="newTag" size="small"
                  @keyup.enter.native="tagInputConfirm"
                  @blur="tagInputConfirm">
        </el-input>
        <el-button class="button-new-tag"
                   v-else size="small"
                   @click="showTagInput">+ 新分类
        </el-button>
      </el-form-item>
      <el-form-item label="题目描述" prop="description">
        <el-input type="textarea"
                  :autosize="textAreaSize"
                  v-model="problem.description">
        </el-input>
      </el-form-item>
      <el-divider content-position="center">格式说明</el-divider>
      <el-form-item label="输入说明" prop="input">
        <el-input type="textarea" placeholder="输入包含哪些内容"
                  :autosize="textAreaSize"
                  v-model="problem.input">
        </el-input>
      </el-form-item>
      <el-form-item label="输出说明" prop="output">
        <el-input type="textarea" placeholder="输出包含哪些内容"
                  :autosize="textAreaSize"
                  v-model="problem.output">
        </el-input>
      </el-form-item>
      <el-divider content-position="center">样例</el-divider>
      <el-alert type="info" style="margin-bottom: 15px"
                :closable="false" :show-icon="true"
                title="样例不参与判题">
      </el-alert>
      <el-form-item label="样例输入" prop="sampleInput">
        <el-input type="textarea" placeholder="若没有输入样例，可给出说明"
                  :autosize="textAreaSize"
                  v-model="problem.sampleInput">
        </el-input>
      </el-form-item>
      <el-form-item label="样例输出" prop="sampleOutput">
        <el-input type="textarea"
                  :autosize="textAreaSize"
                  v-model="problem.sampleOutput">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="success" :disabled="!dataChanged"
                   :icon="problemId === null ? 'el-icon-plus': 'el-icon-check'"
                   @click="onSave(saveType)">
          {{ problemId === null ? '提交' : '保存修改' }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {userInfo, toLoginPage} from "@/script/util"
import {apiPath} from "@/script/env"

export default {
  name: "ProblemEditor",
  props: {
    problemId: Number,
    saveType: String,
    dialogVisible: Boolean
  },
  watch: {
    problemId: {
      immediate: true,
      handler() {
        if (this.dialogVisible) {
          let formLoaded = this.$refs['problemForm'] !== undefined
          this.firstChange = true
          if (this.problemId != null)
            this.getProblem()
          else {
            if (formLoaded)
              this.$refs['problemForm'].resetFields()
            this.tags = []
          }
          if (formLoaded)
            this.$refs['problemForm'].clearValidate()
        }
      }
    },
    problem: {
      handler() {
        if (this.firstChange) {
          this.firstChange = false
        } else {
          this.dataChanged = true
        }
      },
      deep: true
    }
  },
  data() {
    return {
      firstChange: true,  // problem 对象是否首次改变
      dataChanged: false, // problem 对象是否已发生改变，无变化则禁用保存按钮
      problem: {
        title: '',
        description: '',
        timeout: '',
        score: '',
        input: '',
        output: '',
        sampleInput: '',
        sampleOutput: ''
      },
      problemRules: {
        title: [
          {required: true, message: '请输入题目名称', trigger: 'blur'}
        ],
        score: [
          {required: true, type: 'number', message: '请填写分值', trigger: 'blur'}
        ],
        timeout: [
          {required: true, type: 'number', message: '请填写时限', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '请输入题目描述', trigger: 'blur'}
        ],
        input: [
          {required: true, message: '请填写输入说明', trigger: 'blur'}
        ],
        output: [
          {required: true, message: '请填写输出说明', trigger: 'blur'}
        ],
        sampleInput: [
          {required: true, message: '请填写输入样例', trigger: 'blur'}
        ],
        sampleOutput: [
          {required: true, message: '请填写输出样例', trigger: 'blur'}
        ]
      },
      tags: [],
      newTag: '',
      newTagVisible: false,
      textAreaSize: {
        minRows: 3,
        maxRows: 12
      }
    }
  },
  methods: {
    getProblem() {
      this.$axios({
        url: `${apiPath.problemManage}/${this.problemId}`,
        method: 'get',
        headers: {
          'token': userInfo().token,
          'userId': userInfo().userId
        }
      }).then((res) => {
        this.problem = res.data
        this.tags = this.problem.category.split(',')
      }).catch((error) => {
        if (error.response.status === 401) {
          toLoginPage()
        }
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
    tagInputConfirm() {
      let tag = this.newTag
      if (tag && this.tags.indexOf(tag) === -1) {
        this.tags.push(tag)
        this.dataChanged = true
      }
      this.newTagVisible = false
      this.newTag = ''
    },
    onSave(type) {
      this.problem.createAt = null
      this.problem.category = this.tags.join(',')
      this.$axios({
        url: apiPath.problemManage,
        method: type,
        headers: {
          'token': userInfo().token,
          'userId': userInfo().userId,
          'Content-Type': 'application/json'
        },
        data: JSON.stringify(this.problem)
      }).then((res) => {
        this.$emit('update:dialogVisible', false)
        this.$emit('refresh')
        this.$notify({
          offset: 50,
          title: `【${this.problem.title}】已${type === 'post' ? '创建' : '保存'}`,
          type: 'success',
          message: `${res.status} ${res.statusText}`
        })
      }).catch((error) => {
        let res = error.response
        switch (res.status) {
          case 401:
            toLoginPage()
            break
          case 400:
            this.$notify.error({
              offset: 50,
              title: `【${this.problem.title}】保存失败`,
              message: `${res.data.msg}`
            })
            break
          default:
            this.$notify.error({
              offset: 50,
              title: `【${this.problem.title}】${type === 'post' ? '创建' : '保存'}失败`,
              message: `${res.status} ${res.statusText}`
            })
        }
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