import katex from "katex"
import "katex/dist/katex.css"

/**
 * markdown-it KaTex 插件
 */
export const KatexPlugin = (md: any) => {
  const inline_code = md.renderer.rules.code_inline.bind(md.renderer.rules)
  const block_code = md.renderer.rules.fence.bind(md.renderer.rules)
  const text = md.renderer.rules.text.bind(md.renderer.rules)

  /**
   * 渲染行内代码块中的公式, eg: `$x \leq y^2$`
   */
  md.renderer.rules.code_inline = (
    tokens: any,
    index: number,
    options: any,
    env: any,
    slf: any
  ) => {
    let code = tokens[index].content as string

    if (code.startsWith("$") && code.endsWith("$")) {
      code = code.substring(1, code.length - 1)
      try {
        return katex.renderToString(code)
      } catch {
        return `<code>语法错误</code>`
      }
    }

    return inline_code(tokens, index, options, env, slf)
  }

  /**
   * 渲染行内公式, eg: $x \leq y^2$
   */
  md.renderer.rules.text = (
    tokens: any,
    index: number,
    options: any,
    env: any,
    slf: any
  ) => {
    let content = tokens[index].content as string
    const match = content.match(/\$+([^$\n]+?)\$+/g)

    if (match) {
      try {
        match.forEach((value) => {
          const katexElement = katex.renderToString(
            value.substring(1, value.length - 1)
          )
          content = content.replace(value, katexElement)
        })

        return content
      } catch {
        return `<code>语法错误</code>`
      }
    }

    return text(tokens, index, options, env, slf)
  }

  /**
   * 渲染代码块中的公式
   */
  md.renderer.rules.fence = (
    tokens: any,
    index: number,
    options: any,
    env: any,
    slf: any
  ) => {
    const token = tokens[index]
    const code = (token.content as string).trim()

    if (
      token.info === "math" ||
      token.info === "katex" ||
      token.info === "latex"
    ) {
      try {
        return `<pre class="math-block">${katex.renderToString(code)}</pre>`
      } catch {
        return `<pre>语法错误</pre>`
      }
    }

    return block_code(tokens, index, options, env, slf)
  }
}
