import { computed, ref } from 'vue'

export const AUTH_TOKEN_KEY = 'token'
const AUTH_USER_KEY = 'goodcar-admin-user'

export interface AuthUser {
  name: string
  role: string
  initials: string
  email: string
}

function readStoredUser(): AuthUser | null {
  if (typeof localStorage === 'undefined') return null
  const raw = localStorage.getItem(AUTH_USER_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as AuthUser
  } catch {
    return null
  }
}

const token = ref(typeof localStorage !== 'undefined' ? localStorage.getItem(AUTH_TOKEN_KEY) || '' : '')
const user = ref<AuthUser | null>(readStoredUser())

export function useAuth() {
  const isAuthenticated = computed(() => !!token.value)

  function persistSession(nextToken: string, nextUser: AuthUser) {
    token.value = nextToken
    user.value = nextUser
    localStorage.setItem(AUTH_TOKEN_KEY, nextToken)
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(nextUser))
  }

  function clearSession() {
    token.value = ''
    user.value = null
    localStorage.removeItem(AUTH_TOKEN_KEY)
    localStorage.removeItem(AUTH_USER_KEY)
  }

  function login(email: string, password: string): boolean {
    const normalizedEmail = email.trim().toLowerCase()
    const isValid =
      normalizedEmail === 'yuan2026@5d.com' && password === '5d2026'

    if (!isValid) return false

    persistSession(`demo-token-${Date.now()}`, {
      name: '管理员 A',
      role: '超级合规风控',
      initials: 'AD',
      email: normalizedEmail,
    })
    return true
  }

  function logout() {
    clearSession()
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout,
  }
}

export function hasAuthToken(): boolean {
  if (typeof localStorage === 'undefined') return false
  return !!localStorage.getItem(AUTH_TOKEN_KEY)
}
