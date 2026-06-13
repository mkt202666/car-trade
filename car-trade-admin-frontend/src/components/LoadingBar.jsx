import { useState, useEffect, useRef } from 'react'

let loadingCount = 0
const listeners = new Set()

export function startLoading() {
  loadingCount++
  listeners.forEach(fn => fn(true))
}

export function stopLoading() {
  if (loadingCount > 0) loadingCount--
  if (loadingCount === 0) listeners.forEach(fn => fn(false))
}

export default function LoadingBar() {
  const [active, setActive] = useState(false)
  const timerRef = useRef(null)

  useEffect(() => {
    const fn = (val) => {
      if (val) {
        setActive(true)
        if (timerRef.current) clearTimeout(timerRef.current)
      } else {
        timerRef.current = setTimeout(() => setActive(false), 200)
      }
    }
    listeners.add(fn)
    return () => {
      listeners.delete(fn)
      if (timerRef.current) clearTimeout(timerRef.current)
    }
  }, [])

  return (
    <div className="fixed top-0 left-0 right-0 z-[9999] pointer-events-none">
      <div
        className={`h-[2px] bg-blue-500 shadow-[0_0_10px_rgba(59,130,246,0.5)] transition-all duration-300 ease-out ${active ? 'w-full' : 'w-0'}`}
      />
    </div>
  )
}
