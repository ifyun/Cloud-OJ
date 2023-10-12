import { createRouter, createWebHistory } from "vue-router"

const RouterLayout = () => import("@/views/layout/RouterLayout.vue")
const Auth = () => import("@/views/components/Auth/Index.vue")
const Front = () => import("@/views/FrontRoot.vue")
const ProblemList = () => import("@/views/components/Problems.vue")
const ContestList = () => import("@/views/components/Contests.vue")
const ContestProblemList = () =>
  import("@/views/components/ContestProblems.vue")
const Leaderboard = () => import("@/views/components/Leaderboard.vue")
const Submission = () => import("@/views/components/Submission/Index.vue")
const HelpDoc = () => import("@/views/components/Help.vue")
const Account = () => import("@/views/components/Account/Index.vue")
const AccountEditor = () => import("@/views/components/Account/Editor.vue")
const NotFound = () => import("@/views/components/NotFound.vue")
const ErrorPage = () => import("@/views/components/Error.vue")

const Admin = () => import("@/views/AdminRoot.vue")
const ProblemAdmin = () => import("@/views/components/Admin/Problem/Index.vue")
const ProblemEditor = () =>
  import("@/views/components/Admin/Problem/Editor.vue")
const TestData = () => import("@/views/components/Admin/Problem/DataManage.vue")
const ContestAdmin = () => import("@/views/components/Admin/Contest/Index.vue")
const ContestEditor = () =>
  import("@/views/components/Admin/Contest/Editor.vue")
const UserAdmin = () => import("@/views/components/Admin/User/Index.vue")
const SystemSettings = () => import("@/views/components/Admin/Settings.vue")

const router = createRouter({
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
          component: ProblemList
        },
        {
          path: "/contests",
          name: "contests",
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
          path: "/leaderboard",
          name: "leaderboard",
          component: Leaderboard,
          props: (route) => ({
            cid: route.query.cid
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
          component: NotFound
        }
      ]
    },
    {
      path: "/admin",
      name: "admin",
      redirect: "/admin/problem/index",
      component: Admin,
      children: [
        {
          path: "problem",
          component: RouterLayout,
          meta: {
            title: "题目管理"
          },
          children: [
            {
              path: "index",
              name: "problem_admin",
              component: ProblemAdmin
            },
            {
              path: "edit/:id",
              name: "edit_problem",
              component: ProblemEditor,
              meta: {
                title: "编辑题目"
              }
            },
            {
              path: "data/:id",
              name: "test_data",
              component: TestData,
              meta: {
                title: "测试数据管理"
              }
            }
          ]
        },
        {
          path: "contest",
          component: RouterLayout,
          meta: {
            title: "竞赛管理"
          },
          children: [
            {
              path: "index",
              name: "contest_admin",
              component: ContestAdmin
            },
            {
              path: "edit/:id",
              name: "edit_contest",
              component: ContestEditor,
              meta: {
                title: "编辑竞赛"
              }
            }
          ]
        },
        {
          path: "user",
          component: RouterLayout,
          meta: {
            title: "用户管理"
          },
          children: [
            {
              path: "index",
              name: "user_admin",
              component: UserAdmin
            }
          ]
        },
        {
          path: "settings",
          name: "settings",
          component: SystemSettings,
          meta: {
            title: "系统设置"
          }
        }
      ]
    }
  ]
})

export default router
