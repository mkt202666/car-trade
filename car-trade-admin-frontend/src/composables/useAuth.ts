import { computed, ref } from 'vue'
import { login as loginApi, getCurrentUser } from '../api/auth'
import type { LoginResult } from '../api/auth'

export const AUTH_TOKEN_KEY = 'token'
const AUTH_USER_KEY = 'goodcar-admin-user'
const REFRESH_TOKEN_KEY = 'refreshToken'

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

/** Map backend AdminUser to frontend AuthUser display shape */
function mapAdminToAuthUser(data: LoginResult['admin']): AuthUser {
  const displayName = data.nickname || data.username
  return {
    name: displayName,
    role: data.role || '运营管理员',
    initials: displayName.slice(0, 2).toUpperCase(),
    email: data.email || '',
  }
}

const token = ref(typeof localStorage !== 'undefined' ? localStorage.getItem(AUTH_TOKEN_KEY) || '' : '')
const user = ref<AuthUser | null>(readStoredUser())

export function useAuth() {
  const isAuthenticated = computed(() => !!token.value)

  function persistSession(nextToken: string, nextRefreshToken: string, nextUser: AuthUser) {
    token.value = nextToken
    user.value = nextUser
    localStorage.setItem(AUTH_TOKEN_KEY, nextToken)
    localStorage.setItem(AUTH_USER_KEY, JSON.stringify(nextUser))
    if (nextRefreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, nextRefreshToken)
    }
  }

  function clearSession() {
    token.value = ''
    user.value = null
    localStorage.removeItem(AUTH_TOKEN_KEY)
    localStorage.removeItem(AUTH_USER_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
  }

  async function login(username: string, password: string): Promise<boolean> {
    try {
      const result = await loginApi({ username, password }) as unknown as LoginResult
      persistSession(
        result.accessToken,
        result.refreshToken,
        mapAdminToAuthUser(result.admin),
      )
      return true
    } catch {
      return false
    }
  }

  async function logout() {
    clearSession()
  }

  async function refreshUser() {
    try {
      const result = await getCurrentUser() as unknown as LoginResult['admin']
      if (result) {
        user.value = mapAdminToAuthUser(result)
        localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user.value))
      }
    } catch {
      // ignore — keep existing cached user
    }
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout,
    refreshUser,
  }
}

export function hasAuthToken(): boolean {
  if (typeof localStorage === 'undefined') return false
  return !!localStorage.getItem(AUTH_TOKEN_KEY)
}
