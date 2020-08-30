import qs from 'qs'

let tagColor = 1,
    colorMap = new Map()

function getTagColor(tag) {
    if (colorMap[tag] === undefined) {
        colorMap[tag] = `tag-color-${(++tagColor % 7) + 1}`
    }
    return colorMap[tag]
}

function getUserInfo() {
    return JSON.parse(sessionStorage.getItem('cloud_oj_token'))
}

function searchParams() {
    return qs.parse(location.search.replace('?', ''))
}

let apiPath = {
    problemManage: 'api/manager/problem/pro',
    contestManage: 'api/manager/contest/pro'
}

export {
    getTagColor,
    getUserInfo,
    searchParams,
    apiPath
}