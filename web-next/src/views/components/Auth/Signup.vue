<template>
  <n-form ref="signupForm" size="large" label-placement="left" :model="user" :rules="signupRules">
    <input type="password" hidden autocomplete="new-password">
    <n-form-item path="userId">
      <n-input placeholder="ID，不能和别人重复" v-model:value="user.userId">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <user-id-icon/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="name">
      <n-input placeholder="昵称" :maxlength="16" v-model:value="user.name"
               :input-props="{autocomplete: 'off'}">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <user-icon/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="password">
      <n-input placeholder="密码" type="password" :maxlength="16"
               v-model:value="user.password" :input-props="{autocomplete: 'new-password'}">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <password-icon/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="confirmPassword">
      <n-input placeholder="确认密码" type="password" :maxlength="16"
               v-model:value="user.confirmPassword" :input-props="{autocomplete: 'new-password'}">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <password-icon/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="email">
      <n-input placeholder="邮箱 (可选)" v-model:value="user.email">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <mail-icon/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item path="section">
      <n-input placeholder="学院/班级/部门 (可选)" v-model:value="user.section">
        <template #prefix>
          <n-icon class="input-prefix-icon">
            <building/>
          </n-icon>
        </template>
      </n-input>
    </n-form-item>
    <n-form-item>
      <n-button style="width: 100%" type="info" :loading="loading" :disabled="loading" @click="signup">
        注 册
      </n-button>
    </n-form-item>
  </n-form>
</template>

<script lang="ts">
import {Options, Vue} from "vue-class-component"
import {FormRules, NButton, NForm, NFormItem, NIcon, NInput} from "naive-ui"
import {Building, Envelope as MailIcon, Lock as PasswordIcon, Orcid as UserIdIcon, User as UserIcon} from "@vicons/fa"
import {User} from "@/api/type"

@Options({
  name: "Signup",
  components: {
    NForm,
    NFormItem,
    NButton,
    NInput,
    NIcon,
    UserIdIcon,
    UserIcon,
    MailIcon,
    PasswordIcon,
    Building
  }
})
export default class Signup extends Vue {
  private loading: boolean = false
  private user: User = new User()

  private signupRules: FormRules = {
    userId: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: string): Error | boolean {
        if (!value) {
          return new Error("请输入ID")
        } else if (value.length < 6) {
          return new Error("至少 6 个字符!")
        }
        return true
      }
    },
    name: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: string): Error | boolean {
        if (!value) {
          return new Error("请输入昵称")
        } else if (value.length < 2) {
          return new Error("至少 2 个字符!")
        }
        return true
      }
    },
    password: {
      required: true,
      trigger: ["blur", "input"],
      validator(rule: any, value: string): Error | boolean {
        if (!value) {
          return new Error("请输入密码")
        } else if (value.length < 6) {
          return new Error("至少 6 位!")
        }
        return true
      }
    },
    confirmPassword: {
      required: true,
      trigger: ["blur", "input"],
      validator: (rule: any, value: string): Error | boolean => {
        if (!value) {
          return new Error("请确认密码")
        } else if (value !== this.user.password) {
          return new Error("密码不一致!")
        }
        return true
      }
    },
    email: {
      trigger: ["blur", "input"],
      validator(rule: any, value: string): Error | boolean {
        const regex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/
        if (value.length === 0) {
          return true
        }
        if (regex.test(value)) {
          return true
        } else {
          return Error("格式不正确!")
        }
      }
    }
  }

  declare $refs: {
    signupForm: HTMLFormElement
  }

  signup() {
    this.$refs.signupForm.validate((errors: any) => {
      if (!errors) {
        console.debug(this.user)
      }
    })
  }
}
</script>

<style scoped>

</style>
