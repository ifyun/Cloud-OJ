module.exports = {
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://172.25.61.239:8080",
                changeOrigin: true
            }
        }
    }
}
