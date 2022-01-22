module.exports = {
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://172.31.212.152:8080",
                changeOrigin: true
            }
        }
    }
}
