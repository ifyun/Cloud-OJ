import type { Root } from "hast"
import { visit } from "unist-util-visit"

export default function rehypeImage() {
  return function (tree: Root) {
    visit(tree, "element", (node) => {
      if (node.tagName !== "img") {
        return
      }

      const url = node.properties.src as string

      if (
        url.startsWith("http://") ||
        url.startsWith("https://") ||
        url.startsWith("/")
      ) {
        return
      }

      node.properties.src = `/api/core/file/img/problem/${url}`
    })
  }
}
