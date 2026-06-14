import { useTheme } from '../../composables/useTheme'

export function useAdminLayout() {
  const { theme } = useTheme()

  return { theme }
}
