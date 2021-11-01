module.exports = {
    productionSourceMap: false,
    transpileDependencies: [
        /\bvue-awesome\b/,
        "resize-detector"
    ],
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://172.27.19.68:8080",
                changeOrigin: true
            }
        }
    }
}