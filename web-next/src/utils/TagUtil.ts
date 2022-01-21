interface TagMap {
    [key: string]: any
}

class Color {
    color: string
    textColor: string
    borderColor: string

    constructor(mainColor: string, darkTheme: boolean = false) {
        this.textColor = mainColor
        this.borderColor = mainColor + "B2"

        if (darkTheme) {
            this.color = "#00000000"
        } else {
            this.color = mainColor
            this.textColor = "#FFFFFF"
        }
    }
}

const mainColors = [
    "#01AAED",
    "#FFB300",
    "#F17C67",
    "#4EAA25",
    "#8C63A0",
    "#3AB2A6",
    "#67C23A"
]

/**
 * 题目分类标签工具，根据标签名称返回颜色配置
 */
class TagUtil {
    private index: number = 0
    private indexDark: number = 0

    private colorsLight = mainColors.map((value) => {
        return new Color(value)
    })

    private colorsDark = mainColors.map((value) => {
        return new Color(value, true)
    })

    tagsLight: TagMap = {}
    tagsDark: TagMap = {}

    getColor(tag: string, theme: any): Color {
        let tagColor

        if (theme == null) {
            if (typeof this.tagsLight[tag] === "undefined") {
                const i = this.index % 7
                this.tagsLight[tag] = this.colorsLight[i]
                this.index += 1
            }

            tagColor = this.tagsLight[tag]
        } else {
            if (typeof this.tagsDark[tag] === "undefined") {
                const i = this.indexDark % 7
                this.tagsDark[tag] = this.colorsDark[i]
                this.indexDark += 1
            }

            tagColor = this.tagsDark[tag]
        }

        return tagColor
    }
}

const tagUtil = new TagUtil()

export default tagUtil
