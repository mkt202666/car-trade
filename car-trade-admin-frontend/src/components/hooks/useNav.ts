import { useRoute } from 'vue-router'
import { useTheme } from '../../composables/useTheme'

export function useNav() {
  const route = useRoute()
  const { theme } = useTheme()

  function isActive(path: string) {
    return route.path === path
  }

  return { theme, isActive }
}
