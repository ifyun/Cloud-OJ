module.exports = {
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://172.29.133.238:8080",
                //target: "http://localhost:8080",
                changeOrigin: true
            }
        }
    }
}
