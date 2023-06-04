import { createRouter, createWebHistory } from "vue-router"

const Auth = () => import("@/views/components/Auth/Index.vue")
const Front = () => import("@/views/FrontRoot.vue")
const ProblemList = () => import("@/views/components/ProblemList.vue")
const ContestList = () => import("@/views/components/ContestList.vue")
const ContestProblemList = () =>
  import("@/views/components/ContestProblems.vue")
const Leaderboard = () => import("@/views/components/Leaderboard.vue")
const Submission = () => import("@/views/components/Submission/Index.vue")
const HelpDoc = () => import("@/views/components/HelpDoc.vue")
const Account = () => import("@/views/components/Account/Index.vue")
const AccountEditor = () => import("@/views/components/Account/Editor.vue")
const NotFound = () => import("@/views/components/NotFound.vue")

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
      component: Front,
      children: [
        {
          path: "/",
          redirect: "/problems"
        },
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
          path: "/account",
          name: "account",
          component: Account
        },
        {
          path: "/account/edit",
          name: "edit_account",
          component: AccountEditor
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
      component: Admin,
      children: [
        {
          path: "/admin/problem",
          name: "problem_admin",
          component: ProblemAdmin
        },
        {
          path: "/admin/problem/:id",
          name: "edit_problem",
          component: ProblemEditor
        },
        {
          path: "/admin/data/:id",
          name: "test_data",
          component: TestData
        },
        {
          path: "/admin/contest",
          name: "contest_admin",
          component: ContestAdmin
        },
        {
          path: "/admin/contest/:id",
          name: "edit_contest",
          component: ContestEditor
        },
        {
          path: "/admin/user",
          name: "user_admin",
          component: UserAdmin
        },
        {
          path: "/admin/settings",
          name: "settings",
          component: SystemSettings
        }
      ]
    }
  ]
})

export default router
