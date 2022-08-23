module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  extends: [
    "eslint:recommended",
    "plugin:vue/vue3-recommended",
    "plugin:prettier/recommended",
    "@vue/typescript",
    "prettier"
  ],
  parser: "vue-eslint-parser",
  parserOptions: {
    ecmaVersion: 2022,
    ecmaFeatures: {
      jsx: true,
      modules: true
    },
    sourceType: "module"
  },
  plugins: ["vue", "prettier"],
  rules: {
    "vue/multi-word-component-names": "off",
    quotes: ["error", "double", { allowTemplateLiterals: true }],
    semi: ["error", "never"]
  }
}
