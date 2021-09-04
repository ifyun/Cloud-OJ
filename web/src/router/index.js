import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter)
const Page404 = () => import("@/page/404")
const Problems = () => import("@/components/Problems")
const Contest = () => import("@/components/Contests")
const Profile = () => import("@/page/Profile")
const Leaderboard = () => import("@/components/Leaderboard")
const ContestLeaderboard = () => import("@/components/ContestLeaderboard")
const Commit = () => import("@/components/Commit")
const Help = () => import("@/page/Help")
const Manage = () => import("@/page/Manage")
const ManageProblems = () => import("@/components/manage/problem/Index")
const ManageContest = () => import("@/components/manage/contest/Index")
const ManageUser = () => import("@/components/manage/user/Index")
const Settings = () => import("@/components/manage/Settings")

const routes = [
    {path: "/page_not_found", component: Page404},
    {path: "/", redirect: "problems"},
    {path: "/problems", component: Problems},
    {path: "/contest", component: Contest},
    {path: "/profile", component: Profile},
    {path: "/leaderboard", component: Leaderboard},
    {path: "/contest_leaderboard/:id", component: ContestLeaderboard},
    {path: "/commit", component: Commit},
    {path: "/help", component: Help},
    {
        path: "/manage",
        component: Manage,
        children: [
            {
                path: "problems",
                component: ManageProblems
            },
            {
                path: "contest",
                component: ManageContest
            },
            {
                path: "user",
                component: ManageUser
            },
            {
                path: "settings",
                component: Settings
            },
            {
                path: "",
                redirect: "problems"
            }
        ]
    },
    {path: "*", component: Page404},
]

const router = new VueRouter({
    routes,
    mode: "history"
})

export default router