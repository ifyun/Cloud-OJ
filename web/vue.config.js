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
                target: "http://172.17.108.209:8080",
                changeOrigin: true
            }
        }
    }
}