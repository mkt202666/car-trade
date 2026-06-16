import { ref, computed, watch, type Ref, type WatchSource } from 'vue'

export interface PaginationOptions<T> {
  source: Ref<T[]>
  defaultPageSize?: number
  resetOn?: WatchSource[]
}

export interface PaginationReturn<T> {
  currentPage: Ref<number>
  pageSize: Ref<number>
  paginatedItems: Ref<T[]>
  pageRangeStart: Ref<number>
  pageRangeEnd: Ref<number>
}

export function usePagination<T>(options: PaginationOptions<T>): PaginationReturn<T> {
  const { source, defaultPageSize = 20, resetOn = [] } = options

  const currentPage = ref(1)
  const pageSize = ref(defaultPageSize)

  const paginatedItems = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    return source.value.slice(start, start + pageSize.value)
  })

  const pageRangeStart = computed(() =>
    source.value.length === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1,
  )

  const pageRangeEnd = computed(() =>
    Math.min(currentPage.value * pageSize.value, source.value.length),
  )

  if (resetOn.length > 0) {
    watch(resetOn, () => {
      currentPage.value = 1
    })
  }

  return { currentPage, pageSize, paginatedItems, pageRangeStart, pageRangeEnd }
}
