module.exports = {
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://172.20.231.81:8080",
                changeOrigin: true
            }
        }
    }
}
