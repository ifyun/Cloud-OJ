<template>
  <div class="bottom-area">
    <div class="flex">
      <span>© Cloud Li</span>
      <el-divider direction="vertical"></el-divider>
      <el-link target="_blank"
               href="https://raw.githubusercontent.com/imcloudfloating/Cloud-OJ/master/LICENSE">
        MIT License
      </el-link>
      <el-divider direction="vertical"></el-divider>
      <span>
        <img height="15" align="center" src="@/assets/icons/github.svg" alt="GitHub Link">
        <el-link style="margin-left: 5px" target="_blank"
                 href="https://github.com/imcloudfloating/Cloud-OJ">
          GitHub
        </el-link>
      </span>
      <el-divider direction="vertical"></el-divider>
      <el-link target="_blank" href="./swagger-ui/index.html">
        API
      </el-link>
    </div>
  </div>
</template>

<script>
export default {
  name: "BottomArea"
}
</script>

<style scoped>
.bottom-area {
  padding: 15px;
  color: black;
  font-size: 10pt;
}

.flex {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>