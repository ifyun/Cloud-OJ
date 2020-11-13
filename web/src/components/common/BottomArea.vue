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
        <img height="15" align="center" src="@/assets/img/github.svg" alt="GitHub Link">
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
    <span class="flex" style="margin: 6px">Based on</span>
    <div class="flex">
      <img class="framework-logo" src="@/assets/img/vue.png" alt="Vue.js">
      <a class="framework-link" style="color: #38495e"
         href="https://cn.vuejs.org/" target="_blank"><b>Vue.js</b></a>
      <el-divider direction="vertical"></el-divider>
      <img class="framework-logo" src="@/assets/img/spring-cloud.png" alt="Spring Cloud">
      <a class="framework-link" style="color: #75b240"
         href="https://spring.io/projects/spring-cloud/" target="_blank"><b>Spring Cloud</b></a>
      <el-divider direction="vertical"></el-divider>
      <img class="framework-logo" src="@/assets/img/docker.png" alt="Docker">
      <a class="framework-link" style="color: #4196eb"
         href="https://www.docker.com/" target="_blank"><b>Docker</b></a>
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
  margin: 0 0 10px;
  color: black;
  font-size: 10pt;
}

.flex {
  display: flex;
  justify-content: center;
  align-items: center;
}

.framework-logo {
  height: 20px;
}

.framework-link {
  margin-left: 5px;
  text-decoration: none;
}

.framework-link:hover {
  text-decoration: underline;
}
</style>