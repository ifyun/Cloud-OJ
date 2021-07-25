import {SettingsApi} from "@/service"

let colorIndex = 1,
    colorMap = new Map()

const TOKEN = "token"

function tagColor(tag) {
    if (typeof colorMap.get(tag) === "undefined") {
        let i = colorIndex % 8
        colorMap.set(tag, `tag-color-${i === 0 ? 1 : i}`)
        colorIndex++
    }

    return colorMap.get(tag)
}

function userInfo() {
    return JSON.parse(localStorage.getItem(TOKEN))
}

function clearToken() {
    localStorage.removeItem(TOKEN)
}

/**
 * 跳转到登录页面
 * @param ctx Vue 实例
 * @param logoff 是否为退出操作
 */
function toLoginPage(ctx, logoff = false) {
    if (logoff === true) {
        clearToken()
        ctx.$router.push("/login")
    } else {
        ctx.$confirm("登录已失效，请重新登录", "提示", {
            confirmButtonText: "去登录",
            type: "warning"
        }).then(() => {
            clearToken()
            ctx.$router.push("/login").catch(() => {
            })
        })
    }
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
        offset: typeof data["offset"] === "undefined" ? 50 : data["offset"],
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

function SiteSetting() {
    const DEFAULT_NAME = "Cloud OJ"
    this.name = ""
    this.icp = ""
    this.icpUrl = ""
    this.hideLogo = true
    this.initialized = false
    this.preference = JSON.parse(localStorage.getItem("preference"))

    let setTitle = (title) => {
        document.title = `${title} - ${this.name}`
    }

    /**
     * 设置当前页面标题
     */
    this.setTitle = (title = null) => {
        if (this.initialized === false) {
            // 首次调用初始化
            SettingsApi.get().then((data) => {
                if (data.siteName !== "") {
                    this.name = data.siteName
                } else {
                    this.name = DEFAULT_NAME
                }
                this.icp = data.icp
                this.icpUrl = data.icpUrl
                this.initialized = true
                this.hideLogo = data.hideLogo
            }).catch(() => {
                this.name = DEFAULT_NAME
            }).finally(() => {
                title != null && setTitle(title)
            })
        } else {
            title != null && setTitle(title)
        }
    }

    this.reload = (title) => {
        this.initialized = false
        this.setTitle(title)
    }

    this.setFavicon = (url) => {
        let link = document.querySelector("link[rel*=icon]")
        link.href = url
    }

    this.savePreference = () => {
        localStorage.setItem("preference", JSON.stringify(this.preference))
    }
}

const siteSetting = new SiteSetting()

export {
    tagColor,
    userInfo,
    toLoginPage,
    saveToken,
    clearToken,
    copyObject,
    prettyMemory,
    siteSetting,
    Notice,
}