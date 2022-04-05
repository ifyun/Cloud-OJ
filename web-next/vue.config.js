module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      "/api": {
        target: "http://192.168.1.100:8080",
        changeOrigin: true
      }
    }
  }
}
