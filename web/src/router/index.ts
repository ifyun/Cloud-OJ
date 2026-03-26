import { createRouter, createWebHistory } from "vue-router"

const RouterLayout = () => import("@/views/layout/RouterLayout.vue")
const Auth = () => import("@/views/components/Auth/Index.vue")
const FrontRoot = () => import("@/views/FrontRoot.vue")
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
const NotFound = () => import("@/views/NotFound.vue")
const Error = () => import("@/views/components/Error.vue")

const AdminRoot = () => import("@/views/AdminRoot.vue")
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
      path: "/:catchAll(.*)",
      name: "404",
      meta: {
        title: "404"
      },
      component: NotFound
    },
    {
      path: "/auth/:tab(login|signup)",
      name: "auth",
      component: Auth,
      props: (route) => ({
        tab: route.params.tab
      })
    },
    {
      path: "/",
      name: "index",
      redirect: { name: "problems" },
      component: FrontRoot,
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
          path: "/contests/:cid(\\d+)",
          name: "contest_problems",
          meta: {
            requiresAuth: true
          },
          component: ContestProblemList,
          props: (route) => ({
            cid: Number(route.params.cid)
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
          path: "/scoreboard/:cid(\\d+)",
          name: "scoreboard_contest",
          component: ScoreboardContest,
          props: (route) => ({
            cid: Number(route.params.cid)
          })
        },
        {
          path: "/submission/:pid(\\d+)/:cid(\\d+)?",
          name: "submission",
          component: Submission,
          props: (route) => ({
            pid: Number(route.params.pid),
            cid: route.params.cid ? Number(route.params.cid) : undefined
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
          path: "/account/:uid(\\d+)?",
          name: "account",
          component: Account,
          props: (route) => ({
            uid: route.params.uid ? Number(route.params.uid) : undefined
          })
        },
        {
          path: "/account/edit",
          name: "edit_account",
          meta: {
            title: "编辑个人信息",
            requireAuth: true
          },
          component: AccountEditor
        },
        {
          path: "/error",
          name: "error",
          meta: {
            title: "错误"
          },
          component: Error
        }
      ]
    },
    {
      path: "/admin",
      name: "admin",
      meta: {
        requiresAuth: true,
        requiresAdmin: true
      },
      redirect: { name: "admin_overview" },
      component: AdminRoot,
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
              path: "edit/:id(\\d+)?",
              name: "edit_problem",
              meta: {
                title: (route) => (route.params.id ? "编辑题目" : "新建题目"),
                inBreadcrumb: true
              },
              props: (route) => ({
                id: route.params.id ? Number(route.params.id) : undefined
              }),
              component: ProblemEditor
            },
            {
              path: "data/:id(\\d+)",
              name: "test_data",
              meta: {
                title: "测试数据管理",
                inBreadcrumb: true
              },
              props: (route) => ({
                id: Number(route.params.id)
              }),
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
              path: "edit/:id(\\d+)?",
              name: "edit_contest",
              meta: {
                title: (route) => (route.params.id ? "编辑竞赛" : "新建竞赛"),
                inBreadcrumb: true
              },
              props: (route) => ({
                id: route.params.id ? Number(route.params.id) : undefined
              }),
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
