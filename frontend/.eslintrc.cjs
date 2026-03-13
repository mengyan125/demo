module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    ecmaVersion: 'latest',
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: ['vue', '@typescript-eslint'],
  rules: {
    // 禁止使用 any 类型
    '@typescript-eslint/no-explicit-any': 'error',
    // 禁止使用 var
    'no-var': 'error',
    // 优先使用 const
    'prefer-const': 'error',
    // 要求使用分号
    'semi': ['error', 'never'],
    // 要求使用单引号
    'quotes': ['error', 'single'],
    // 禁止未使用的变量
    '@typescript-eslint/no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
    // Vue 组件命名规则
    'vue/multi-word-component-names': 'off',
    // 要求组件名称为 PascalCase
    'vue/component-definition-name-casing': ['error', 'PascalCase']
  }
}
