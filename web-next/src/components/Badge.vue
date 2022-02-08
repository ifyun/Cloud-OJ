<template>
  <n-element tag="div" :class="badgeClasses">
    <div class="badge-label">
      <div v-if="$slots.icon" class="badge-icon">
        <n-icon>
          <slot name="icon"/>
        </n-icon>
      </div>
      <span>{{ label }}</span>
    </div>
    <div class="badge-value" :style="{backgroundColor: valueBackground}">
      <slot/>
    </div>
  </n-element>
</template>

<script lang="ts">
import {VueComponent} from "@/vue-ts-component"
import {Options} from "vue-class-component"
import {Prop} from "vue-property-decorator"
import {useStore} from "vuex"
import {NElement, NIcon, useThemeVars} from "naive-ui"

interface Props {
  label: string
  size: "small" | "medium" | "large"
  color: string
}

@Options({
  name: "Badge",
  components: {
    NIcon,
    NElement
  }
})
export default class Badge extends VueComponent<Props> {
  @Prop(String)
  private label?: string

  @Prop(String)
  private color?: string

  @Prop({
    type: String as () => "small" | "medium" | "large",
    default: "medium"
  })
  private size?: string

  private store = useStore()

  private themeVars = useThemeVars().value

  private get theme() {
    return this.store.state.theme
  }

  private get valueBackground(): string {
    if (this.color) {
      return this.color
    }

    return this.themeVars.primaryColor
  }

  private get badgeClasses() {
    return [
      [`badge`],
      [`badge__${this.size}`]
    ]
  }
}
</script>

<style lang="scss" scoped>
.badge {
  display: flex;
  flex-direction: row;
  color: white;
  font-size: 14px;

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
    background-color: #666666;
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
