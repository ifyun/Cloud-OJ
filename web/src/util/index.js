import qs from "qs"

let colorIndex = 1,
    colorMap = new Map()
const TOKEN = "token"

function tagColor(tag) {
    if (colorMap[tag] === undefined) {
        let i = colorIndex++ % 8
        colorMap[tag] = `tag-color-${i === 0 ? 1 : i}`
    }
    return colorMap[tag]
}

function userInfo() {
    return JSON.parse(localStorage.getItem(TOKEN))
}

function clearToken() {
    localStorage.removeItem(TOKEN)
}

function searchParams() {
    return qs.parse(location.search.replace("?", ""))
}

function toLoginPage() {
    clearToken()
    window.location.href = "/login"
}

function saveToken(value) {
    localStorage.setItem(TOKEN, value)
}

function copyObject(src) {
    return JSON.parse(JSON.stringify(src))
}

function prettyMemory(mem) {
    if (mem <= 1024) {
        return `${mem} KB`
    } else {
        return `${(mem / 1024).toFixed(2)} MB`
    }
}

function callNotify(ctx, data, type) {
    ctx.$notify[type]({
        offset: data["offset"] === undefined ? 50 : data["offset"],
        duration: 1500,
        title: data["title"],
        message: data["message"]
    })
}

function callMessage(ctx, msg, type) {
    ctx.$message[type]({
        offset: 75,
        duration: 2000,
        message: msg,
    })
}

const Notice = {
    notify: {
        success(ctx, data) {
            callNotify(ctx, data, "success")
        },
        info(ctx, data) {
            callNotify(ctx, data, "info")
        },
        warning(ctx, data) {
            callNotify(ctx, data, "warning")
        },
        error(ctx, data) {
            callNotify(ctx, data, "error")
        }
    },
    message: {
        success(ctx, msg) {
            callMessage(ctx, msg, "success")
        },
        info(ctx, msg) {
            callMessage(ctx, msg, "info")
        },
        warning(ctx, msg) {
            callMessage(ctx, msg, "warning")
        },
        error(ctx, msg) {
            callMessage(ctx, msg, "error")
        }
    }
}

export {
    tagColor,
    userInfo,
    searchParams,
    toLoginPage,
    saveToken,
    clearToken,
    copyObject,
    prettyMemory,
    Notice
}