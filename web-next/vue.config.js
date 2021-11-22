module.exports = {
    devServer: {
        port: 80,
        proxy: {
            "/api": {
                target: "https://cloudoj.204.group",
                changeOrigin: true
            }
        }
    }
}