import {h} from "vue"
import {NIcon} from "naive-ui"

export function setTitle(title: string) {
    document.title = `${title} - Cloud OJ`
}

export const renderIcon = (icon: any, color: string | undefined = undefined) => {
    return () => h(NIcon, color == null ? null : {color},
        {
            default: () => h(icon)
        })
}

export {default as TagUtil} from "./TagUtil"
export {default as LanguageUtil} from "./LanguageUtil"
