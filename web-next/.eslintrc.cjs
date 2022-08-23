/* eslint-env node */
module.exports = {
  root: true,
  extends: [
    "eslint:recommended",
    "plugin:vue/vue3-recommended",
    "@vue/typescript",
    "plugin:prettier/recommended",
    "prettier"
  ],
  parserOptions: {
    ecmaVersion: "2022"
  },
  rules: {
    "vue/multi-word-component-names": "off",
    "vue/no-v-html": "off",
    quotes: ["error", "double", { allowTemplateLiterals: true }],
    semi: ["error", "never"]
  }
}
