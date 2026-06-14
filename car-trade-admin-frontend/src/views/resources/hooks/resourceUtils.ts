/** 资源列表操作工具函数 */

/**
 * 在列表中交换相邻条目以实现排序
 * @param list - 待操作的数组副本来源
 * @param index - 当前条目索引
 * @param direction - 移动方向：-1 向前，1 向后
 * @returns 交换后的新数组；索引越界时返回 null
 */
export function moveListItem<T>(list: T[], index: number, direction: -1 | 1): T[] | null {
  const newIndex = index + direction
  if (newIndex < 0 || newIndex >= list.length) return null
  const copy = [...list]
  const [item] = copy.splice(index, 1)
  copy.splice(newIndex, 0, item)
  return copy
}
