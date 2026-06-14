import { ElMessage, ElMessageBox } from 'element-plus'
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../../composables/useAuth'
import { useTheme } from '../../composables/useTheme'

const FALLBACK_USER = {
  name: '管理员 A',
  role: '超级合规风控',
  initials: 'AD',
} as const

export function useAppHeader() {
  const router = useRouter()
  const { theme, toggleTheme } = useTheme()
  const { user, logout } = useAuth()

  const userName = computed(() => user.value?.name ?? FALLBACK_USER.name)
  const userRole = computed(() => user.value?.role ?? FALLBACK_USER.role)
  const userInitials = computed(() => user.value?.initials ?? FALLBACK_USER.initials)
  const userEmail = computed(() => user.value?.email ?? 'yuan2026@5d.com')

  async function handleUserCommand(command: string) {
    if (command !== 'logout') return

    try {
      await ElMessageBox.confirm('确定要退出当前运维登录吗？', '退出登录', {
        confirmButtonText: '退出',
        cancelButtonText: '取消',
        type: 'warning',
      })
      logout()
      ElMessage.success('已退出登录')
      router.replace('/login')
    } catch {
      // 用户取消
    }
  }

  return {
    theme,
    toggleTheme,
    userName,
    userRole,
    userInitials,
    userEmail,
    handleUserCommand,
  }
}
