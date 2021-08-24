module.exports = {
    productionSourceMap: false,
    transpileDependencies: [
        /\bvue-awesome\b/,
        "resize-detector"
    ],
    chainWebpack: (config) => {
        config.plugins.delete("prefetch")
    },
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                // target: "http://localhost:8080",
                target: "http://172.25.222.244:8080",
                changeOrigin: true
            }
        }
    }
}