<template>
  <router-view v-slot="{ Component }">
    <keep-alive :key="$route.path">
      <component :is="Component" />
    </keep-alive>
  </router-view>
</template>
