<template>
  <div class="bottom-area">
    <div class="flex">
      <p>©2020 Cloud Li</p>
      <p>MIT License</p>
      <p>
        <a class="github-link" href="https://github.com/imcloudfloating/Cloud-OJ">GitHub</a>
      </p>
    </div>
    <div class="flex" style="margin: 6px">Based on</div>
    <div class="flex">
      <img class="framework-logo" src="../../assets/img/vue.png" alt="Vue.js">
      <a class="framework-link" style="color: #38495e"
         href="https://cn.vuejs.org/" target="_blank"><b>Vue.js</b></a>
      <img class="framework-logo" src="../../assets/img/spring-cloud.png" alt="Spring Cloud">
      <a class="framework-link" style="color: #75b240"
         href="https://spring.io/projects/spring-cloud/" target="_blank"><b>Spring Cloud</b></a>
      <img class="framework-logo" src="../../assets/img/docker.png" alt="Docker">
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
  margin: 0 20px;
  color: black;
  font-size: 10pt;
  border-top: 1px solid #e6e6e6;
}

.flex {
  display: flex;
  justify-content: center;
  align-items: center;
}

p {
  margin: 0 5px;
}

.github-link {
  color: #333333;
  text-decoration: none;
}

.github-link:hover {
  text-decoration: underline;
}

.framework-logo {
  height: 20px;
}

.framework-link {
  margin-left: 5px;
  margin-right: 15px;
  text-decoration: none;
}

.framework-link:hover {
  text-decoration: underline;
}
</style>