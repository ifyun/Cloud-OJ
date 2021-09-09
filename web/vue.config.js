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
                target: "http://172.17.101.46:8080",
                changeOrigin: true
            }
        }
    }
}