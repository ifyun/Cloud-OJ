import { ApiPath } from "@/api"

/**
 * markdown-it 图片插件
 */
export const ImgPlugin = (md: any) => {
  const image = md.renderer.rules.image.bind(md.renderer.rules)

  md.renderer.rules.image = (
    tokens: any,
    index: number,
    options: any,
    env: any,
    slf: any
  ) => {
    const attrs = tokens[index].attrs as Array<string>
    const src = attrs[0][1]
    const alt = attrs[1][1]

    if (src.startsWith("http:") || src.startsWith("https:")) {
      return image(tokens, index, options, env, slf)
    }

    // 对用户上传的图片加上路径
    return `<img alt="${alt}" src="${ApiPath.PROBLEM_IMAGE}/${src}">`
  }
}
