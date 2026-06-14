import { ref, watch } from 'vue'

export type ThemeId = 'light' | 'dark'

const STORAGE_KEY = 'goodcar-admin-theme'

const theme = ref<ThemeId>('light')

function getStoredTheme(): ThemeId {
  if (typeof localStorage === 'undefined') return 'light'
  const stored = localStorage.getItem(STORAGE_KEY)
  return stored === 'dark' ? 'dark' : 'light'
}

function applyTheme(value: ThemeId) {
  document.documentElement.setAttribute('data-theme', value)
}

if (typeof document !== 'undefined') {
  theme.value = getStoredTheme()
  applyTheme(theme.value)
}

export function useTheme() {
  watch(theme, (value) => {
    applyTheme(value)
    localStorage.setItem(STORAGE_KEY, value)
  })

  function setTheme(value: ThemeId) {
    theme.value = value
  }

  function toggleTheme() {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
  }

  return { theme, setTheme, toggleTheme }
}
