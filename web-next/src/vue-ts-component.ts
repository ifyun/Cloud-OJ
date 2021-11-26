import {Vue} from "vue-class-component"

export class VueComponent<P> extends Vue {
    declare $props: P
}