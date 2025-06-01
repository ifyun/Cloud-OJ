<template>
  <div class="admin-wrap">
    <n-space v-if="!loading" vertical size="large">
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
import { SettingsApi } from "@/api/request"
import { ErrorMessage, Settings } from "@/api/type"
import { useStore } from "@/store"
import { SaveRound } from "@vicons/material"
import {
  NButton,
  NCard,
  NIcon,
  NSpace,
  NSwitch,
  NText,
  NThing,
  useMessage
} from "naive-ui"
import { onMounted, ref } from "vue"
import { useRoute } from "vue-router"

const store = useStore()
const route = useRoute()
const message = useMessage()

const loading = ref<boolean>(true)
const settings = ref<Settings>({})

onMounted(() => {
  getSettings()
})

function getSettings() {
  SettingsApi.get()
    .then((data) => {
      settings.value = data
    })
    .catch((err: ErrorMessage) => {
      store.app.setError(err)
    })
    .finally(() => {
      loading.value = false
    })
}

function saveSettings() {
  SettingsApi.save(settings.value)
    .then(() => {
      message.success("设置已保存")
    })
    .catch((err: ErrorMessage) => {
      message.error(err.toString())
    })
}
</script>
