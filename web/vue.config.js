module.exports = {
    productionSourceMap: false,
    transpileDependencies: [
        /\bvue-awesome\b/
    ],
    chainWebpack: (config) => {
      config.plugins.delete("prefetch")
    },
    devServer: {
        port: 8081,
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true
            },
            "/swagger-ui": {
                target: "http://localhost:8080",
                changeOrigin: true
            },
            "/v2": {
                target: "http://localhost:8080",
                changeOrigin: true
            },
            "/swagger-resources": {
                target: "http://localhost:8080",
                changeOrigin: true
            }
        }
    }
}