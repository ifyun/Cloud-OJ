import type { GlobalThemeOverrides } from "naive-ui"

const themeOverrides: GlobalThemeOverrides = {
  common: {
    primaryColor: "#18A058FF",
    primaryColorHover: "#36AD6AFF",
    primaryColorPressed: "#0C7A43FF",
    primaryColorSuppl: "#36AD6AFF",
    infoColor: "#409EFFFF",
    infoColorHover: "#66B1FFFF",
    infoColorPressed: "#3A8EE6FF",
    infoColorSuppl: "rgba(64, 158, 255, 1)",
    successColor: "#67C23AFF",
    successColorHover: "#85CE61FF",
    successColorPressed: "#5DAF34FF",
    successColorSuppl: "rgba(103, 194, 58, 1)",
    warningColor: "#E6A23CFF",
    warningColorHover: "#EBB563FF",
    warningColorPressed: "#CF9236FF",
    warningColorSuppl: "rgba(230, 162, 60, 1)",
    errorColor: "#F56C6CFF",
    errorColorHover: "#F78989FF",
    errorColorPressed: "#DD6161FF",
    errorColorSuppl: "rgba(245, 108, 108, 1)"
  },
  Switch: {
    railColorActive: "#18A058FF",
    loadingColor: "#18A058FF"
  }
}

const themeOverridesDark: GlobalThemeOverrides = {
  common: {
    borderColor: "#21262DFF"
  },
  Layout: {
    headerColor: "#161B22FF",
    headerBorderColor: "#161B22FF",
    siderColor: "#161B22FF",
    siderBorderColor: "#161B22FF",
    color: "#0D1117FF"
  },
  Button: {
    colorTertiary: "#161B22FF"
  },
  Switch: {
    railColorActive: "#18A058E0",
    loadingColor: "#18A058E0"
  },
  Input: {
    color: "#161B22FF"
  },
  Card: {
    color: "#00000000"
  },
  Drawer: {
    color: "#0D1117FF"
  },
  Dropdown: {
    color: "#161B22FF"
  },
  Table: {
    thColor: "#161b22FF",
    tdColor: "#00000000"
  },
  DataTable: {
    borderColor: "#161b22FF",
    thColor: "#161b22FF",
    tdColor: "#00000000",
    tdColorHover: "#161b22FF"
  },
  Transfer: {
    listColor: "#161b22FF"
  }
}

export { themeOverrides, themeOverridesDark }
