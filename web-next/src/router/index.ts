import {createRouter, createWebHistory} from "vue-router"

const ProblemTable = () => import("@/views/components/ProblemTable.vue")
const ContestTable = () => import("@/views/components/ContestTable.vue")
const Leaderboard = () => import("@/views/components/Leaderboard.vue")
const Submission = () => import("@/views/components/Submission/index.vue")
const Help = () => import("@/views/components/Help.vue")
const Account = () => import("@/views/components/Account/index.vue")
const NotFound = () => import("@/views/components/NotFound.vue")

const Admin = () => import("@/views/components/Admin/index.vue")
const ProblemAdmin = () => import("@/views/components/Admin/Problem/index.vue")
const ProblemEditor = () => import("@/views/components/Admin/Problem/Editor.vue")
const ContestAdmin = () => import("@/views/components/Admin/Contest/index.vue")
const ContestEditor = () => import("@/views/components/Admin/Contest/Editor.vue")

const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: "/problems"
        },
        {
            path: "/problems",
            name: "problems",
            component: ProblemTable
        },
        {
            path: "/contests",
            name: "contests",
            component: ContestTable
        },
        {
            path: "/leaderboard",
            name: "leaderboard",
            component: Leaderboard
        },
        {
            path: "/submission",
            name: "submission",
            component: Submission
        },
        {
            path: "/help",
            name: "help",
            component: Help
        },
        {
            path: "/account",
            name: "account",
            component: Account
        },
        {
            path: "/:catchAll(.*)",
            name: "404",
            component: NotFound
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
                    path: "/admin/contest",
                    name: "contest_admin",
                    component: ContestAdmin
                },
                {
                    path: "/admin/contest/:id",
                    name: "edit_contest",
                    component: ContestEditor
                }
            ]
        }
    ]
})

export default router