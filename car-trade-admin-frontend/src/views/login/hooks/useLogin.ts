import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useAuth } from '../../../composables/useAuth'
import { AI_TAGLINES, DEMO_ACCOUNT } from './constants'
import type { LoginForm } from './types'

export function useLogin() {
  const router = useRouter()
  const route = useRoute()
  const { login } = useAuth()

  const formRef = ref<FormInstance>()
  const loading = ref(false)
  const aiTagline = ref<string>(AI_TAGLINES[0])

  const form = reactive<LoginForm>({
    username: '',
    password: '',
    remember: true,
  })

  const rules: FormRules<LoginForm> = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 2, message: '用户名至少 2 个字符', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码至少 6 位', trigger: 'blur' },
    ],
  }

  let taglineTimer: ReturnType<typeof setInterval> | undefined
  let taglineIndex = 0

  onMounted(() => {
    taglineTimer = setInterval(() => {
      taglineIndex = (taglineIndex + 1) % AI_TAGLINES.length
      aiTagline.value = AI_TAGLINES[taglineIndex]
    }, 4200)
  })

  onUnmounted(() => {
    if (taglineTimer) clearInterval(taglineTimer)
  })

  async function handleSubmit() {
    const valid = await formRef.value?.validate().catch(() => false)
    if (!valid) return

    loading.value = true

    const success = await login(form.username, form.password)
    loading.value = false

    if (!success) {
      ElMessage.error('账号或密码错误，请检查后重试')
      return
    }

    ElMessage.success('身份验证通过，正在进入运维控制台')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/dashboard'
    router.replace(redirect)
  }

  function fillDemoAccount() {
    form.username = DEMO_ACCOUNT.username
    form.password = DEMO_ACCOUNT.password
  }

  return {
    formRef,
    form,
    rules,
    loading,
    aiTagline,
    handleSubmit,
    fillDemoAccount,
  }
}
