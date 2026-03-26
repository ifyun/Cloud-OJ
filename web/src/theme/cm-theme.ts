import type { Extension } from "@codemirror/state"
import { EditorView } from "@codemirror/view"
import { githubDark } from "@fsegurai/codemirror-theme-github-dark"
import { githubLight } from "@fsegurai/codemirror-theme-github-light"

export const cmLight: Extension = [
  githubLight,
  EditorView.theme({
    ".cm-content": {
      borderColor: "#f6f8fa",
      borderStyle: "solid",
      borderWidth: "1px 1px 1px 0"
    }
  })
]

export const cmDark: Extension = [
  githubDark,
  EditorView.theme({
    ".cm-content": {
      borderColor: "#161b22",
      borderStyle: "solid",
      borderWidth: "1px 1px 1px 0"
    }
  })
]
