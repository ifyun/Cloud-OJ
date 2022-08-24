/* eslint-env node */
module.exports = {
  root: true,
  extends: [
    "plugin:vue/vue3-recommended",
    "eslint:recommended",
    "@vue/typescript",
    "@vue/prettier"
  ],
  parserOptions: {
    ecmaVersion: "latest"
  },
  rules: {
    "vue/multi-word-component-names": "off",
    "vue/no-v-html": "off",
    quotes: ["error", "double", { allowTemplateLiterals: true }],
    semi: ["error", "never"]
  }
}
