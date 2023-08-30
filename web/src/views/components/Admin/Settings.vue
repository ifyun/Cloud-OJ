<template>
  <div class="admin-wrap">
    <error-result v-if="error != null" :error="error" />
    <n-space v-else vertical size="large">
      <n-card>
        <n-thing>
          <template #header>显示所有竞赛</template>
          <template #description>
            <n-text depth="3">未开始的竞赛也会在竞赛页面中显示</n-text>
          </template>
          <template #action>
            <n-switch
              v-model:value="settings.showAllContest"
              :round="false"
              size="small" />
          </template>
        </n-thing>
      </n-card>
      <n-card>
        <n-thing>
          <template #header>总是显示竞赛排名</template>
          <template #description>
            <n-text depth="3">竞赛排名在结束前就可以查看</n-text>
          </template>
          <template #action>
            <n-switch
              v-model:value="settings.alwaysShowRanking"
              :round="false"
              size="small" />
          </template>
        </n-thing>
      </n-card>
      <n-card>
        <n-thing>
          <template #header>显示通过的测试点数量</template>
          <template #description>
            <n-text depth="3">判题结果会显示通过的测试点数量</n-text>
          </template>
          <template #action>
            <n-switch
              v-model:value="settings.showPassedPoints"
              :round="false"
              size="small" />
          </template>
        </n-thing>
      </n-card>
      <n-card>
        <n-thing>
          <template #header>自动删除判题产物</template>
          <template #description>
            <n-text depth="3">
              判题结束后删除写入硬盘的用户代码、编译的二进制文件
            </n-text>
          </template>
          <template #action>
            <n-switch
              v-model:value="settings.autoDelSolutions"
              :round="false"
              size="small" />
          </template>
        </n-thing>
      </n-card>
      <n-button type="primary" secondary @click="saveSettings">
        保存设置
        <template #icon>
          <n-icon>
            <save-round />
          </n-icon>
        </template>
      </n-button>
    </n-space>
  </div>
</template>

<script lang="ts">
export default {
  name: "SystemSettings"
}
</script>

<script setup lang="ts">
import { computed, onBeforeMount, ref } from "vue"
import { useRoute } from "vue-router"
import {
  NButton,
  NCard,
  NText,
  NIcon,
  NSpace,
  NSwitch,
  NThing,
  useMessage
} from "naive-ui"
import { SaveRound } from "@vicons/material"
import ErrorResult from "@/components/ErrorResult.vue"
import store from "@/store"
import { ErrorMessage, Settings, UserInfo } from "@/api/type"
import { SettingsApi } from "@/api/request"
import { setTitle } from "@/utils"

const route = useRoute()
const message = useMessage()

const error = ref<ErrorMessage | null>(null)
const settings = ref<Settings>({})
const userInfo = computed<UserInfo>(() => store.state.userInfo)

onBeforeMount(() => {
  setTitle(route.meta.title as string)
  getSettings()
})

function getSettings() {
  SettingsApi.get(userInfo.value)
    .then((data) => (settings.value = data))
    .catch((err: ErrorMessage) => (error.value = err))
}

function saveSettings() {
  SettingsApi.save(settings.value, userInfo.value)
    .then(() => message.success("设置已保存"))
    .catch((err: ErrorMessage) => message.error(err.toString()))
}
</script>
