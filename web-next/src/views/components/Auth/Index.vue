<template>
  <n-space vertical style="padding: 12px 24px">
    <n-tabs size="large" type="line" default-value="login">
      <n-tab-pane name="login" tab="登录">
        <login/>
      </n-tab-pane>
      <n-tab-pane name="signup" tab="注册">
        <signup/>
      </n-tab-pane>
    </n-tabs>
  </n-space>
</template>

<script setup lang="ts">
import {NSpace, NTabPane, NTabs} from "naive-ui"
import Login from "./Login.vue"
import Signup from "./Signup.vue"
</script>
