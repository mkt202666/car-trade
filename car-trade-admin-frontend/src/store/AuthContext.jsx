import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

const ADMIN_TOKEN_KEY = 'admin_token'
const ADMIN_USER_KEY = 'admin_user'

export { ADMIN_TOKEN_KEY }

export function AuthProvider({ children }) {
  const [adminToken, setAdminToken] = useState(() => {
    return localStorage.getItem(ADMIN_TOKEN_KEY) || ''
  })
  const [adminUser, setAdminUser] = useState(() => {
    try {
      const raw = localStorage.getItem(ADMIN_USER_KEY)
      return raw ? JSON.parse(raw) : null
    } catch {
      return null
    }
  })

  useEffect(() => {
    if (adminToken) {
      localStorage.setItem(ADMIN_TOKEN_KEY, adminToken)
    } else {
      localStorage.removeItem(ADMIN_TOKEN_KEY)
    }
  }, [adminToken])

  useEffect(() => {
    if (adminUser) {
      localStorage.setItem(ADMIN_USER_KEY, JSON.stringify(adminUser))
    } else {
      localStorage.removeItem(ADMIN_USER_KEY)
    }
  }, [adminUser])

  const login = (token, user) => {
    setAdminToken(token)
    setAdminUser(user)
  }

  const logout = () => {
    setAdminToken('')
    setAdminUser(null)
  }

  return (
    <AuthContext.Provider value={{ adminToken, adminUser, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

export { AuthContext }
