<template>
  <n-element tag="div" :class="badgeClasses">
    <div class="badge-label">
      <div v-if="$slots.icon" class="badge-icon">
        <n-icon>
          <slot name="icon" />
        </n-icon>
      </div>
      <span>{{ label }}</span>
    </div>
    <div class="badge-value" :style="{ backgroundColor: valueBackground }">
      <slot />
    </div>
  </n-element>
</template>

<script setup lang="ts">
import { computed } from "vue"
import { NElement, NIcon, useThemeVars } from "naive-ui"

const props = defineProps<{
  label: string
  size: "small" | "medium" | "large"
  color: string
}>()

const themeVars = useThemeVars().value

const valueBackground = computed<string>(() =>
  props.color ? props.color : themeVars.primaryColor
)

const badgeClasses = computed(() => [[`badge`], [`badge__${props.size}`]])
</script>

<style lang="scss" scoped>
.badge {
  display: flex;
  flex-direction: row;
  color: white;
  font-size: 14px;
  border-radius: 6px;

  & > div {
    padding: 0 7px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }

  .badge-icon {
    display: flex;
    align-items: center;
    margin-right: 4px;
  }

  .badge-label {
    background-color: #505050;
    border-top-left-radius: 2px;
    border-bottom-left-radius: 2px;
  }

  .badge-value {
    border-top-right-radius: 2px;
    border-bottom-right-radius: 2px;
  }
}

.badge__small {
  font-size: 12px;
  height: 20px;
}

.badge__medium {
  height: 24px;
}

.badge__large {
  height: 28px;
}
</style>
