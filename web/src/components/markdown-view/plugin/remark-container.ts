import type { Root } from "hast"
import type { ContainerDirective } from "mdast-util-directive"
import { visit } from "unist-util-visit"

export default function remarkContainer() {
  return (tree: Root) => {
    visit(tree, "containerDirective", (node: ContainerDirective) => {
      if (node.type === "containerDirective") {
        const data = node.data || (node.data = {})
        data.hName = "div"
        data.hProperties = {
          class: `markdown-alert ${node.name}`
        }
      }
    })
  }
}
