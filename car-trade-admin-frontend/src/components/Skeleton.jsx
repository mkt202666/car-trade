export function SkeletonCard() {
  return (
    <div className="bg-white p-5 rounded-2xl border border-gray-100 animate-pulse">
      <div className="flex justify-between items-start">
        <div className="space-y-2">
          <div className="h-3 w-24 bg-gray-200 rounded" />
          <div className="h-6 w-32 bg-gray-200 rounded" />
          <div className="h-3 w-20 bg-gray-200 rounded" />
        </div>
        <div className="w-9 h-9 bg-gray-200 rounded-xl" />
      </div>
    </div>
  )
}

export function SkeletonChart() {
  return (
    <div className="bg-white p-5 rounded-2xl border border-gray-100 shadow-sm animate-pulse">
      <div className="flex items-center justify-between mb-6">
        <div className="space-y-1">
          <div className="h-4 w-48 bg-gray-200 rounded" />
          <div className="h-3 w-64 bg-gray-200 rounded" />
        </div>
      </div>
      <div className="h-64 w-full bg-gray-100 rounded-lg" />
    </div>
  )
}

export function SkeletonTable({ rows = 5, cols = 9 }) {
  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden animate-pulse">
      <div className="overflow-x-auto">
        <table className="w-full text-xs">
          <thead>
            <tr className="bg-gray-50 border-b border-gray-100">
              {Array.from({ length: cols }).map((_, i) => (
                <th key={i} className="text-left px-4 py-3">
                  <div className="h-3 w-16 bg-gray-200 rounded" />
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {Array.from({ length: rows }).map((_, rowIdx) => (
              <tr key={rowIdx} className="border-b border-gray-50">
                {Array.from({ length: cols }).map((_, colIdx) => (
                  <td key={colIdx} className="px-4 py-3">
                    <div className="h-4 w-20 bg-gray-100 rounded" />
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
        <div className="flex items-center gap-3">
          <div className="h-3 w-32 bg-gray-200 rounded" />
        </div>
        <div className="flex items-center gap-1">
          <div className="h-7 w-7 bg-gray-200 rounded" />
          <div className="h-7 w-7 bg-gray-200 rounded" />
          <div className="h-7 w-7 bg-gray-200 rounded" />
        </div>
      </div>
    </div>
  )
}
