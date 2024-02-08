import type { GlobalThemeOverrides } from "naive-ui"

const themeBase: GlobalThemeOverrides = {
  common: {
    primaryColor: "#18A058",
    primaryColorHover: "#36AD6A",
    primaryColorPressed: "#0C7A43",
    primaryColorSuppl: "#36AD6A",
    infoColor: "#409EFF",
    infoColorHover: "#66B1FF",
    infoColorPressed: "#3A8EE6",
    infoColorSuppl: "rgba(64, 158, 255, 1)",
    successColor: "#18A058",
    successColorHover: "#36AD6A",
    successColorPressed: "#0C7A43",
    successColorSuppl: "#36AD6A",
    warningColor: "#E6A23C",
    warningColorHover: "#EBB563",
    warningColorPressed: "#CF9236",
    warningColorSuppl: "rgba(230, 162, 60, 1)",
    errorColor: "#F56C6C",
    errorColorHover: "#F78989",
    errorColorPressed: "#DD6161",
    errorColorSuppl: "rgba(245, 108, 108, 1)"
  },
  Layout: {
    footerColor: "#00000000"
  },
  Switch: {
    railColorActive: "#18A058",
    loadingColor: "#18A058"
  }
}

const themeDark: GlobalThemeOverrides = {
  common: {
    bodyColor: "#0D1117",
    borderColor: "#21262D"
  },
  Layout: {
    headerColor: "#161B22",
    headerBorderColor: "#161B22",
    siderColor: "#161B22",
    siderBorderColor: "#0D1117",
    footerColor: "#00000000",
    color: "#0D1117"
  },
  Divider: {
    color: "#161B22"
  },
  Button: {
    colorTertiary: "#161B22"
  },
  Switch: {
    railColorActive: "#18A058E0",
    loadingColor: "#18A058E0"
  },
  Select: {
    peers: {
      InternalSelection: {
        color: "#161B22",
        border: "1px solid #0D1117"
      },
      InternalSelectMenu: {
        color: "#161B22"
      }
    }
  },
  Input: {
    color: "#161B22",
    groupLabelColor: "#161B22",
    groupLabelBorder: "1px solid #0D1117"
  },
  Card: {
    color: "#00000000",
    colorModal: "#161B22",
    borderColor: "#161B22"
  },
  Tabs: {
    tabBorderColor: "#161B22"
  },
  Drawer: {
    color: "#0D1117"
  },
  Dropdown: {
    color: "#222D3D"
  },
  Table: {
    borderColor: "#161B22",
    borderColorModal: "#26292F",
    thColor: "#161B22",
    tdColor: "#00000000",
    tdColorHover: "#161B22",
    tdColorStriped: "#161B22",
    thColorModal: "#26292F",
    tdColorModal: "#00000000"
  },
  DataTable: {
    borderColor: "#161B22FF",
    thColor: "#161B22",
    tdColor: "#00000000",
    tdColorHover: "#161B22",
    tdColorStriped: "#161B22"
  },
  DatePicker: {
    panelColor: "#222D3D"
  },
  Popover: {
    color: "#222D3D"
  },
  Transfer: {
    listColor: "#161B22"
  },
  Message: {
    color: "#222D3D",
    colorInfo: "#222D3D",
    colorSuccess: "#222D3D",
    colorWarning: "#222D3D",
    colorError: "#222D3D",
    colorLoading: "#222D3D"
  },
  Notification: {
    color: "#222D3D"
  }
}

export { themeBase, themeDark }
