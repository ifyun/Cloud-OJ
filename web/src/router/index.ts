import { createRouter, createWebHistory } from "vue-router"

const RouterWrapper = () => import("@/components/RouterWrapper.vue")
const Auth = () => import("@/views/components/Auth/Index.vue")
const IndexLayout = () => import("@/views/layout/IndexLayout.vue")
const Problems = () => import("@/views/components/Problems.vue")
const Contests = () => import("@/views/components/Contests.vue")
const ContestProblems = () => import("@/views/components/ContestProblems.vue")
const Scoreboard = () => import("@/views/components/Scoreboard.vue")
const ScoreboardContest = () =>
  import("@/views/components/ScoreboardContest.vue")
const Problem = () => import("@/views/components/Problem/Index.vue")
const HelpDoc = () => import("@/views/components/Help.vue")
const Account = () => import("@/views/components/Account/Index.vue")
const AccountEditor = () => import("@/views/components/Account/Editor.vue")
const NotFound = () => import("@/views/NotFound.vue")

const AdminLayout = () => import("@/views/layout/AdminLayout.vue")
const AdminIndex = () => import("@/views/components/Admin/Dashboard/Index.vue")
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
      component: IndexLayout,
      children: [
        {
          path: "/problems",
          name: "problems",
          meta: {
            title: "题目"
          },
          component: Problems
        },
        {
          path: "/contests",
          name: "contests",
          meta: {
            title: "竞赛 & 练习"
          },
          component: Contests
        },
        {
          path: "/contests/:cid(\\d+)",
          name: "contest-problems",
          meta: {
            requiresAuth: true
          },
          component: ContestProblems,
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
          name: "contest-scoreboard",
          component: ScoreboardContest,
          props: (route) => ({
            cid: Number(route.params.cid)
          })
        },
        {
          path: "/problem/:pid(\\d+)/:cid(\\d+)?",
          name: "problem",
          component: Problem,
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
          name: "edit-account",
          meta: {
            title: "编辑个人信息",
            requireAuth: true
          },
          component: AccountEditor
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
      redirect: { name: "dashboard" },
      component: AdminLayout,
      children: [
        {
          path: "dashboard",
          name: "dashboard",
          meta: {
            title: "仪表板",
            inBreadcrumb: true
          },
          component: AdminIndex
        },
        {
          path: "problem",
          redirect: "/admin/problem/index",
          meta: {
            title: "题目管理",
            inBreadcrumb: true
          },
          component: RouterWrapper,
          children: [
            {
              path: "index",
              name: "admin-problems",
              component: ProblemAdmin
            },
            {
              path: "edit/:id(\\d+)?",
              name: "edit-problem",
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
              name: "test-data",
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
            title: "竞赛管理",
            inBreadcrumb: true
          },
          component: RouterWrapper,
          children: [
            {
              path: "index",
              name: "admin-contests",
              component: ContestAdmin
            },
            {
              path: "edit/:id(\\d+)?",
              name: "edit-contest",
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
            title: "用户管理",
            inBreadcrumb: true
          },
          component: RouterWrapper,
          children: [
            {
              path: "index",
              name: "admin-users",
              component: UserAdmin
            }
          ]
        },
        {
          path: "solution",
          name: "admin-solutions",
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
