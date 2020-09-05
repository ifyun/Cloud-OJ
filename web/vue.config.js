module.exports = {
    devServer:{
      proxy: 'http://cloudoj.204.group'
    },
    pages: {
        login: {
            entry: "./src/page/login/login.js",
            template: "./src/page/login/login.html",
            filename: "login.html",
            title: "登录"
        },
        index: {
            entry: "./src/page/index/index.js",
            template: "./src/page/index/index.html",
            filename: "index.html",
            title: "Cloud OJ"
        },
        competition: {
            entry: "./src/page/competition/competition.js",
            template: "./src/page/competition/competition.html",
            filename: "competition.html",
            title: "Cloud OJ"
        },
        commit: {
            entry: "./src/page/commit/commit.js",
            template: "./src/page/commit/commit.html",
            filename: "commit.html",
            title: "Cloud OJ"
        },
        manager: {
            entry: "./src/page/manage/manage.js",
            template: "./src/page/manage/manage.html",
            filename: "manage.html",
            title: "Cloud OJ"
        }
    }
}