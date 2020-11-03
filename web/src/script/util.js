import qs from 'qs'

let tagColor = 1,
    colorMap = new Map()
const tokenKey = 'cloud_oj_token'

function getTagColor(tag) {
    if (colorMap[tag] === undefined) {
        colorMap[tag] = `tag-color-${(++tagColor % 7) + 1}`
    }
    return colorMap[tag]
}

function userInfo() {
    return JSON.parse(sessionStorage.getItem(tokenKey))
}

function searchParams() {
    return qs.parse(location.search.replace('?', ''))
}

function handle401() {
    clearToken()
    window.location.href = '/login'
}

function clearToken() {
    sessionStorage.removeItem(tokenKey)
}

function copyObject(src) {
    return JSON.parse(JSON.stringify(src))
}

const apiPath = {
    login: '/api/login',
    logoff: '/api/logoff',
    testDataManage: '/api/file/test_data',
    avatar: '/api/file/image/avatar',
    problemManage: '/api/manager/problem/pro',
    contestManage: '/api/manager/contest/pro',
    userManage: '/api/manager/user/pro',
    user: '/api/manager/user',
    profile: '/api/manager/user/profile',
    problem: `/api/manager/problem`,
    contest: '/api/manager/contest',
    ranking: '/api/manager/ranking',
    contestRanking: '/api/manager/ranking/contest',
    history: '/api/manager/history',
    commit: '/api/judge/commit',
    overview: '/api/manager/user/overview',
    backup: '/api/manager/backup'
}

export {
    getTagColor,
    userInfo,
    searchParams,
    handle401,
    clearToken,
    copyObject,
    apiPath
}