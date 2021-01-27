<template>
  <div class="bottom-area">
    <div class="flex">
      <span>© Cloud Li</span>
      <el-divider direction="vertical"/>
      <el-link target="_blank"
               href="https://raw.githubusercontent.com/imcloudfloating/Cloud-OJ/master/LICENSE">
        MIT License
      </el-link>
      <el-divider direction="vertical"/>
      <span>
        <img height="15" align="center" src="@/assets/icons/github.svg" alt="GitHub Link">
        <el-link style="margin-left: 5px" target="_blank"
                 href="https://github.com/imcloudfloating/Cloud-OJ">
          GitHub
        </el-link>
      </span>
      <span v-if="siteSetting.icp !== ''">
        <el-divider direction="vertical"/>
        <el-link :href="siteSetting.icpUrl" target="_blank">
          {{ siteSetting.icp }}
        </el-link>
      </span>
    </div>
  </div>
</template>

<script>
import {siteSetting} from "@/util"

export default {
  name: "BottomArea",
  data() {
    return {
      siteSetting
    }
  }
}
</script>

<style scoped>
.bottom-area {
  font-size: 10pt;
}

.flex {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.flex * {
  margin-top: 2px;
}
</style>