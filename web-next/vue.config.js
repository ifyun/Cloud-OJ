const { defineConfig } = require("@vue/cli-service")

module.exports = defineConfig({
  devServer: {
    proxy: {
      "/api": {
        target: "http://127.0.0.1:8080",
        changeOrigin: true
      }
    }
  }
})
