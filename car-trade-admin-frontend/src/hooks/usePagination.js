import { useState, useCallback } from 'react'

export function usePagination(defaultSize = 20) {
  const [page, setPage] = useState(1)
  const [size, setSize] = useState(defaultSize)
  const [total, setTotal] = useState(0)

  const setPagination = useCallback((totalItems) => {
    setTotal(totalItems)
  }, [])

  const reset = useCallback(() => {
    setPage(1)
  }, [])

  const changePage = useCallback((newPage) => {
    setPage(newPage)
  }, [])

  const changeSize = useCallback((newSize) => {
    setSize(newSize)
    setPage(1)
  }, [])

  return {
    page,
    size,
    total,
    totalPages: Math.ceil(total / size),
    setPage: changePage,
    setSize: changeSize,
    setTotal: setPagination,
    reset,
  }
}
