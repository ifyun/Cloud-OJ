const LANGUAGE_LENGTH = 8

const LanguageUtil = {
  /**
   * 列出语言，将二进制表示的语言列表转换为数组
   *
   * @param languages 语言列表
   */
  toArray(languages: number): Array<number> {
    const arr: Array<number> = []

    for (let i = 0; i <= LANGUAGE_LENGTH; i += 1) {
      const t = 1 << i
      if ((languages & t) === t) {
        arr.push(i)
      }
    }

    return arr
  },

  /**
   * 将数组表示的语言转化为数字
   *
   * @param arr 语言数组
   */
  toNumber(arr: Array<number>) {
    let languages = 0

    arr.forEach((v) => {
      languages = languages | (1 << v)
    })

    return languages
  }
}

export default LanguageUtil
