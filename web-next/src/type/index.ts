export type LanguageOption = {
    value: number,
    label: string,
    version: string
}

export type LayoutConfig = {
    navbar: boolean,
    sideNav: boolean
}

export const LanguageOptions: Array<LanguageOption> = [
    {value: 0, label: "C", version: ""},
    {value: 1, label: "C++", version: ""},
    {value: 2, label: "Java", version: "OpenJDK 1.8"},
    {value: 3, label: "Python", version: "3.5"},
    {value: 4, label: "Bash Shell", version: ""},
    {value: 5, label: "C#", version: "Mono"},
    {value: 6, label: "JavaScript", version: "Node.js"},
    {value: 7, label: "Kotlin", version: "1.4.10"},
    {value: 8, label: "Go", version: "1.15.7"}
]
