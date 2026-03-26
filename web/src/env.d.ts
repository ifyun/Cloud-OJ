/// <reference types="vite/client" />
import "pinia"
import { RouteLocationNormalizedGeneric, Router } from "vue-router"

declare module "pinia" {
  export interface PiniaCustomProperties {
    router: Router
  }
}

declare module "vue-router" {
  interface RouteMeta {
    inBreadcrumb?: boolean
    requiresAuth?: boolean
    requiresAdmin?: boolean
    title?: string | ((route: RouteLocationNormalizedGeneric) => string)
  }
}
