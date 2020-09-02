<template>
  <div>
    <span>竞赛/作业管理</span>
    <el-divider></el-divider>
    <el-form>
      <el-form-item>
        <el-button type="success" icon="el-icon-circle-plus"
                   @click="onAddContestClick">创建新竞赛/作业
        </el-button>
      </el-form-item>
    </el-form>
    <!-- Contest Table -->
    <el-table :data="contests.data" stripe border>
      <el-table-column label="ID" prop="contestId" width="100px" align="center">
      </el-table-column>
      <el-table-column label="名称" prop="contestName" width="300px">
      </el-table-column>
      <el-table-column label="开始时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.startAt }}</i>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center">
        <template slot-scope="scope">
          <i class="el-icon-time"> {{ scope.row.endAt }}</i>
        </template>
      </el-table-column>
      <!-- Operation -->
      <el-table-column label="操作" width="200px" align="center">
        <template slot-scope="scope">
          <el-dropdown style="margin-right: 10px" trigger="click"
                       @command="onEditClick">
            <el-button size="mini"
                       @click="selectedIndex=scope.$index"
                       icon="el-icon-edit-outline">编辑
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="edit">编辑</el-dropdown-item>
              <el-dropdown-item command="manage-problems">题目管理</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="mini" type="danger" icon="el-icon-delete"
                     @click="onDeleteClick(scope.$index)">
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination style="margin-top: 10px"
                   background
                   layout="total, sizes, prev, pager, next, jumper"
                   :page-sizes="[10, 25, 50]"
                   :page-size="pageSize"
                   :total="contests.count"
                   :current-page.sync="currentPage"
                   @size-change="onSizeChange"
                   @current-change="getContests">
    </el-pagination>
    <!-- Edit Contest Dialog -->
    <el-dialog title="编辑"
               :visible.sync="showEditDialog">
      <el-form label-width="100px" ref="edit-contest"
               :rules="contestRules"
               :model="selectedContest">
        <el-form-item label="名称" prop="contestName">
          <el-input v-model="selectedContest.contestName">
          </el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startAt">
          <el-date-picker type="datetime" v-model="selectedContest.startAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endAt">
          <el-date-picker type="datetime" v-model="selectedContest.endAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="语言限制" prop="languages">
          <el-checkbox-group v-model="enabledLanguages">
            <el-checkbox name="language"
                         v-for="lang in languageOptions"
                         :label="lang.id"
                         :key="lang.id">{{ lang.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary"
                     @click="onSave('edit-contest', 'put')">保 存
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- New Contest Dialog -->
    <el-dialog title="创建新竞赛/作业"
               :visible.sync="showAddDialog">
      <el-form label-width="100px" ref="new-contest"
               :model="newContest"
               :rules="contestRules">
        <el-form-item label="名称" prop="contestName">
          <el-input v-model="newContest.contestName"
                    placeholder="竞赛/作业名称">
          </el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startAt">
          <el-date-picker type="datetime" v-model="newContest.startAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" prop="endAt">
          <el-date-picker type="datetime" v-model="newContest.endAt">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="语言限制" prop="languages">
          <el-checkbox-group v-model="enabledLanguages">
            <el-checkbox name="language"
                         v-for="lang in languageOptions"
                         :label="lang.id"
                         :key="lang.id">{{ lang.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary"
                     @click="onSave('new-contest', 'post')">创 建
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- Delete Confirm Dialog -->
    <el-dialog title="删除提示" :visible.sync="showDeleteDialog">
      <el-alert type="warning" show-icon
                :title="`你正在删除：${this.selectedContest.contestName}`"
                description="相关提交记录会消失（该竞赛/作业包含的题目不会被删除）"
                :closable="false">
      </el-alert>
      <el-form :model="deleteForm" ref="deleteForm"
               :rules="deleteRules">
        <el-form-item label="输入名称确认删除" prop="checkName">
          <el-input v-model="deleteForm.checkName"
                    :placeholder="selectedContest.contestName">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="danger"
                     @click="onDelete('deleteForm')">删除
          </el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- Problems Manage Dialog -->
    <el-dialog :title="selectedContest.contestName" width="1000px"
               :visible.sync="showProblemsDialog">
      <CompetitionProblemsManage :contest-id="selectedContest.contestId"/>
    </el-dialog>
  </div>
</template>

<script>
import {apiPath, getUserInfo, handle401} from "@/js/util";
import moment from 'moment'
import CompetitionProblemsManage from "@/components/manage/contest/ContestProblems";

export default {
  name: "CompetitionManage",
  components: {
    CompetitionProblemsManage
  },
  beforeMount() {
    document.title = `竞赛/作业管理 · Cloud OJ`
    this.getContests()
  },
  data() {
    let validateEndTime = (rule, value, callback) => {
      if (new Date(value) <= new Date(this.newContest.startAt))
        return callback(new Error('结束时间必须大于开始时间'))
      callback()
    }
    let validateLanguages = (rule, value, callback) => {
      if (this.enabledLanguages.length === 0)
        return callback(new Error('请至少选择一种语言'))
      callback()
    }
    let validateDelete = (rule, value, callback) => {
      if (value !== this.selectedContest.contestName)
        return callback(new Error('请确认输入正确'))
      callback()
    }
    return {
      userInfo: getUserInfo(),
      contests: {
        data: [],
        count: 0
      },
      selectedIndex: null,
      selectedContest: {
        contestId: null,
        contestName: '',
        startAt: '',
        endAt: '',
        languages: ''
      },
      newContest: {},
      contestRules: {
        contestName: [
          {required: true, message: '请输入竞赛/作业名称', trigger: 'blur'},
          {min: 4, max: 20, message: '长度在 4 ~ 20 个字符', trigger: 'blur'}
        ],
        startAt: [
          {required: true, message: '请选择开始时间', trigger: 'blur'}
        ],
        endAt: [
          {required: true, message: '请选择结束时间', trigger: 'blur'},
          {validator: validateEndTime, trigger: 'blur'}
        ],
        languages: [
          {required: true, validator: validateLanguages, trigger: 'change'}
        ]
      },
      languageOptions: [
        {id: 0, name: 'C'},
        {id: 1, name: 'C++'},
        {id: 2, name: 'Java'},
        {id: 3, name: 'Python'},
        {id: 4, name: 'Bash Shell'},
        {id: 5, name: 'C#'}
      ],
      enabledLanguages: [],
      pageSize: 25,
      currentPage: 1,
      showAddDialog: false,
      showEditDialog: false,
      showDeleteDialog: false,
      showProblemsDialog: false,
      deleteForm: {
        checkName: ''
      },
      deleteRules: {
        checkName: [
          {required: true, message: '请输入名称', trigger: 'blur'},
          {validator: validateDelete, trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    getContests() {
      this.$axios({
        url: `${apiPath.contestManage}`
            + `?page=${this.currentPage}&limit=${this.pageSize}`
            + `&userId=${this.userInfo.userId}`,
        method: 'get',
        headers: {
          'token': this.userInfo.token
        }
      }).then((res) => {
        this.contests = res.data
      }).catch((error) => {
        if (error.response.status === 401) {
          handle401()
        } else {
          this.$notify.error({
            title: `获取数据失败`,
            message: `${error.response.status}`
          })
        }
      })
    },
    onSizeChange(size) {
      this.pageSize = size
      this.getContests()
    },
    onEditClick(command) {
      // 克隆对象，避免编辑时与表格同步
      let t = JSON.stringify(this.contests.data[this.selectedIndex])
      this.selectedContest = JSON.parse(t)

      if (command === 'edit') {
        let lang = this.selectedContest.languages
        this.enabledLanguages = []
        // 列出语言
        for (let i = 0; i <= 5; i++) {
          let t = 1 << i
          if ((lang & t) === t) {
            this.enabledLanguages.push(i)
          }
        }
        this.showEditDialog = true
      } else if (command === 'manage-problems') {
        this.showProblemsDialog = true
      }
    },
    onAddContestClick() {
      this.newContest = {}
      this.enabledLanguages = []
      this.showAddDialog = true
    },
    onSave(formName, type) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let languages = 0
          // 计算允许的语言
          this.enabledLanguages.forEach((v) => {
            languages = languages | 1 << v
          })

          let contest = type === 'post' ? this.newContest : this.selectedContest
          contest.languages = languages
          contest.startAt = moment(contest.startAt).format("YYYY-MM-DD HH:mm:ss")
          contest.endAt = moment(contest.endAt).format("YYYY-MM-DD HH:mm:ss")

          this.$axios({
            url: `${apiPath.contestManage}?userId=${this.userInfo.userId}`,
            method: type,
            headers: {
              "Content-Type": "application/json",
              "token": this.userInfo.token
            },
            data: JSON.stringify(contest)
          }).then((res) => {
            type === 'post' ? this.showAddDialog = false : this.showEditDialog = false
            this.getContests()
            this.$notify({
              title: `${contest.contestName}已保存`,
              type: 'success',
              message: `Status: ${res.status}`
            })
            contest = {}
          }).catch((error) => {
            if (error.response.status === 401) {
              handle401()
            } else {
              this.$notify.error({
                title: `${contest.contestName}保存失败`,
                message: `${error.response.status}`
              })
            }
          })
        } else {
          return false
        }
      })
    },
    onDeleteClick(index) {
      this.showDeleteDialog = true
      this.selectedIndex = index
      this.selectedContest = this.contests.data[index]
    },
    onDelete(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.$axios({
            url: `${apiPath.contestManage}/`
                + `${this.selectedContest.contestId}?userId=${this.userInfo.userId}`,
            method: 'delete',
            headers: {
              'token': this.userInfo.token
            }
          }).then((res) => {
            this.showDeleteDialog = false
            this.getContests()
            this.$notify.info({
              title: `${this.selectedContest.contestName}已删除`,
              message: `Status: ${res.status}`
            })
          }).catch((error) => {
            if (error.response.status === 401) {
              handle401()
            } else {
              this.$notify.error({
                title: `${this.selectedContest.contestName}删除失败`,
                message: `${error.response.status}`
              })
            }
          })
          this.checkName = ''
        }
      })
    }
  }
}
</script>

<style scoped>
.el-checkbox + .el-checkbox,
.el-checkbox.is-bordered + .el-checkbox.is-bordered {
  margin-left: 0;
}
</style>