import { createRouter, createWebHistory } from "vue-router"

const RouterLayout = () => import("@/views/layout/RouterLayout.vue")
const Auth = () => import("@/views/components/Auth/Index.vue")
const Front = () => import("@/views/FrontRoot.vue")
const ProblemList = () => import("@/views/components/Problems.vue")
const ContestList = () => import("@/views/components/Contests.vue")
const ContestProblemList = () =>
  import("@/views/components/ContestProblems.vue")
const Scoreboard = () => import("@/views/components/Scoreboard.vue")
const ScoreboardContest = () =>
  import("@/views/components/ScoreboardContest.vue")
const Submission = () => import("@/views/components/Submission/Index.vue")
const HelpDoc = () => import("@/views/components/Help.vue")
const Account = () => import("@/views/components/Account/Index.vue")
const AccountEditor = () => import("@/views/components/Account/Editor.vue")
const NotFound = () => import("@/views/components/NotFound.vue")
const ErrorPage = () => import("@/views/components/Error.vue")

const Admin = () => import("@/views/AdminRoot.vue")
const AdminOverview = () =>
  import("@/views/components/Admin/Overview/Index.vue")
const ProblemAdmin = () => import("@/views/components/Admin/Problem/Index.vue")
const ProblemEditor = () =>
  import("@/views/components/Admin/Problem/Editor.vue")
const TestData = () => import("@/views/components/Admin/Problem/DataManage.vue")
const ContestAdmin = () => import("@/views/components/Admin/Contest/Index.vue")
const ContestEditor = () =>
  import("@/views/components/Admin/Contest/Editor.vue")
const UserAdmin = () => import("@/views/components/Admin/User/Index.vue")
const SolutionAdmin = () =>
  import("@/views/components/Admin/Solution/Index.vue")
const SystemSettings = () => import("@/views/components/Admin/Settings.vue")

const router = createRouter({
  scrollBehavior() {
    return { top: 0 }
  },
  history: createWebHistory(),
  routes: [
    {
      path: "/auth/:tab",
      name: "auth",
      component: Auth,
      props: (route) => ({
        tab: route.params.tab
      })
    },
    {
      path: "/",
      name: "index",
      redirect: "/problems",
      component: Front,
      children: [
        {
          path: "/problems",
          name: "problems",
          meta: {
            title: "题目"
          },
          component: ProblemList
        },
        {
          path: "/contests",
          name: "contests",
          meta: {
            title: "竞赛 & 练习"
          },
          component: ContestList
        },
        {
          path: "/contests/:cid",
          name: "contest_problems",
          component: ContestProblemList,
          props: (route) => ({
            cid: route.params.cid
          })
        },
        {
          path: "/scoreboard",
          name: "scoreboard",
          meta: {
            title: "排名"
          },
          component: Scoreboard
        },
        {
          path: "/scoreboard/:cid",
          name: "scoreboard_contest",
          component: ScoreboardContest,
          props: (route) => ({
            cid: route.params.cid
          })
        },
        {
          path: "/submission/:pid",
          name: "submission",
          component: Submission,
          props: (route) => ({
            pid: route.params.pid,
            cid: route.query.cid
          })
        },
        {
          path: "/help",
          name: "help",
          meta: {
            title: "帮助文档"
          },
          component: HelpDoc
        },
        {
          path: "/account/:uid?",
          name: "account",
          component: Account
        },
        {
          path: "/account/edit",
          name: "edit_account",
          meta: {
            title: "编辑个人信息"
          },
          component: AccountEditor
        },
        {
          path: "/error",
          name: "error",
          meta: {
            title: "错误"
          },
          component: ErrorPage
        },
        {
          path: "/:catchAll(.*)",
          name: "404",
          meta: {
            title: "404"
          },
          component: NotFound
        }
      ]
    },
    {
      path: "/admin",
      name: "admin",
      redirect: "/admin/overview",
      component: Admin,
      children: [
        {
          path: "overview",
          name: "admin_overview",
          meta: {
            title: "概览",
            inBreadcrumb: true
          },
          component: AdminOverview
        },
        {
          path: "problem",
          redirect: "/admin/problem/index",
          meta: {
            title: "题目",
            inBreadcrumb: true
          },
          component: RouterLayout,
          children: [
            {
              path: "index",
              name: "problem_admin",
              component: ProblemAdmin
            },
            {
              path: "edit/:id",
              name: "edit_problem",
              meta: {
                title: "编辑题目",
                _title: "新建题目",
                inBreadcrumb: true
              },
              component: ProblemEditor
            },
            {
              path: "data/:id",
              name: "test_data",
              meta: {
                title: "测试数据管理",
                inBreadcrumb: true
              },
              component: TestData
            }
          ]
        },
        {
          path: "contest",
          redirect: "/admin/contest/index",
          meta: {
            title: "竞赛",
            inBreadcrumb: true
          },
          component: RouterLayout,
          children: [
            {
              path: "index",
              name: "contest_admin",
              component: ContestAdmin
            },
            {
              path: "edit/:id",
              name: "edit_contest",
              meta: {
                title: "编辑竞赛",
                _title: "新建竞赛",
                inBreadcrumb: true
              },
              component: ContestEditor
            }
          ]
        },
        {
          path: "user",
          redirect: "/admin/user/index",
          meta: {
            title: "用户",
            inBreadcrumb: true
          },
          component: RouterLayout,
          children: [
            {
              path: "index",
              name: "user_admin",
              component: UserAdmin
            }
          ]
        },
        {
          path: "solution",
          name: "solution_admin",
          meta: {
            title: "提交记录",
            inBreadcrumb: true
          },
          component: SolutionAdmin
        },
        {
          path: "settings",
          name: "settings",
          meta: {
            title: "设置",
            inBreadcrumb: true
          },
          component: SystemSettings
        }
      ]
    }
  ]
})

export default router
