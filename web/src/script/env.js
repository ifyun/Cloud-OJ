const apiPath = {
    login: "/api/auth/login",
    logoff: "/api/auth/logoff",
    testDataManage: "/api/file/test_data",
    avatar: "/api/file/image/avatar",
    problemManage: "/api/manager/problem/pro",
    contestManage: "/api/manager/contest/pro",
    userManage: "/api/manager/user/pro",
    settings: "/api/manager/settings",
    user: "/api/manager/user",
    profile: "/api/manager/user/profile",
    problem: "/api/manager/problem",
    contest: "/api/manager/contest",
    ranking: "/api/manager/ranking",
    contestRanking: "/api/manager/ranking/contest",
    adminContestRanking: "/api/manager/ranking/pro/contest",
    contestDetail: "/api/manager/ranking/pro/contest/detail",
    history: "/api/manager/history",
    commit: "/api/judgement/commit",
    queueInfo: "/api/judgement/pro/queue_info",
    overview: "/api/manager/user/overview",
    backup: "/api/manager/backup"
}

const languages = ["C", "C++", "Java", "Python", "Bash", "C#", "JavaScript", "Kotlin"]

const stateTags = [
    {text: "等待判题", type: "primary", icon: "el-icon-loading"},
    {text: "等待写入", type: "primary", icon: "el-icon-loading"}
]

const resultTags = [
    {text: "完全正确", type: "success", icon: "el-icon-success"},
    {text: "时间超限", type: "warning", icon: "el-icon-warning"},
    {text: "内存超限", type: "warning", icon: "el-icon-warning"},
    {text: "部分通过", type: "warning", icon: "el-icon-warning"},
    {text: "答案错误", type: "danger", icon: "el-icon-error"},
    {text: "编译错误", type: "info", icon: "el-icon-info"},
    {text: "运行错误", type: "info", icon: "el-icon-info"},
    {text: "判题异常", type: "danger", icon: "el-icon-error"}
]

export {
    apiPath,
    languages,
    stateTags,
    resultTags
}