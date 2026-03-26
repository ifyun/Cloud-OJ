<template>
  <n-flex vertical size="small" class="code-editor">
    <n-input-group>
      <n-input-group-label :style="{ width: '100px' }">
        选择语言
      </n-input-group-label>
      <n-select
        v-model:value="language"
        :options="languageOptions"
        :render-label="renderLabel" />
      <n-button type="primary" :loading="loading" @click="submit">
        <template #icon>
          <n-icon>
            <send-round />
          </n-icon>
        </template>
        提交运行
      </n-button>
    </n-input-group>
    <n-scrollbar>
      <div ref="editor" class="cm-parent"></div>
    </n-scrollbar>
  </n-flex>
</template>

<script setup lang="ts">
import { cmDark, cmLight } from "@/theme/cm-theme"
import type { LanguageOption, SourceCode } from "@/type"
import { LanguageOptions } from "@/type"
import { LanguageUtil } from "@/utils"
import { closeBrackets } from "@codemirror/autocomplete"
import { indentWithTab } from "@codemirror/commands"
import { cpp } from "@codemirror/lang-cpp"
import { go } from "@codemirror/lang-go"
import { java } from "@codemirror/lang-java"
import { javascript } from "@codemirror/lang-javascript"
import { python } from "@codemirror/lang-python"
import {
  bracketMatching,
  indentOnInput,
  StreamLanguage
} from "@codemirror/language"
import { csharp, kotlin } from "@codemirror/legacy-modes/mode/clike"
import { shell } from "@codemirror/legacy-modes/mode/shell"
import { Compartment, type Extension } from "@codemirror/state"
import {
  EditorView,
  highlightActiveLine,
  keymap,
  lineNumbers
} from "@codemirror/view"
import { SendRound } from "@vicons/material"
import {
  NButton,
  NFlex,
  NIcon,
  NInputGroup,
  NInputGroupLabel,
  NScrollbar,
  NSelect
} from "naive-ui"
import { nextTick, onMounted, ref, watch } from "vue"

let cmView: EditorView

const langModes: { [key: number]: Extension } = {
  0: cpp(),
  1: cpp(),
  2: java(),
  3: python(),
  4: StreamLanguage.define(shell),
  5: StreamLanguage.define(csharp),
  6: javascript(),
  7: StreamLanguage.define(kotlin),
  8: go()
}

const renderLabel = (option: LanguageOption) => {
  return [option.label]
}

const language = ref<number>(0) // 当前选中的语言ID
const languageOptions = ref<Array<LanguageOption>>(LanguageOptions)
const editor = ref<HTMLTextAreaElement | null>(null)

const themeCompartment = new Compartment()
const langCompartment = new Compartment()
const cmExtensions: Extension = [
  [
    bracketMatching(),
    closeBrackets(),
    highlightActiveLine(),
    indentOnInput(),
    lineNumbers()
  ],
  langCompartment.of(langModes[0]),
  themeCompartment.of(cmLight),
  keymap.of([indentWithTab])
]

const props = withDefaults(
  defineProps<{
    value: string
    loading: boolean
    availableLanguages: number | null // 可用语言，未指定时使用所有语言
    theme: "light" | "dark"
  }>(),
  {
    loading: false,
    value: "",
    availableLanguages: null
  }
)

const emit = defineEmits<{
  (e: "submit", value: SourceCode): void
}>()

watch(
  () => props.availableLanguages,
  (val) => {
    if (val == null) {
      return
    }

    const languageArray = LanguageUtil.toArray(val)
    languageOptions.value = []
    languageArray.forEach((v) => {
      languageOptions.value.push(LanguageOptions[v])
    })
  },
  { immediate: true }
)

watch(
  () => props.theme,
  (val) => {
    const t = val === "light" ? cmLight : cmDark
    nextTick(() => {
      cmView.dispatch({
        effects: themeCompartment.reconfigure(t)
      })
    })
  },
  { immediate: true }
)

watch(language, (val) => {
  cmView.dispatch({
    effects: langCompartment.reconfigure(langModes[val])
  })
})

onMounted(() => {
  cmView = new EditorView({
    doc: props.value,
    parent: editor.value!,
    extensions: cmExtensions
  })
})

function submit() {
  emit("submit", {
    language: language.value,
    code: cmView.state.doc.toString()
  })
}
</script>

<style scoped lang="scss">
:deep(.n-scrollbar) {
  > .n-scrollbar-container {
    display: flex;
    flex-direction: column;

    > .n-scrollbar-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
  }
}

.cm-parent {
  display: flex;
  flex-direction: column;
  flex: 1;

  :deep(.cm-editor) {
    flex: 1;
  }
}
</style>
