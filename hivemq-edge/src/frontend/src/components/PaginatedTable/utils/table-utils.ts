import { SortDirection } from '@tanstack/react-table'
import { AriaAttributes } from 'react'

export const getAriaSort = (canSort: boolean, isSorted: false | SortDirection): AriaAttributes['aria-sort'] => {
  if (!canSort) {
    return undefined
  }
  if (isSorted === 'asc') {
    return 'ascending'
  }
  if (isSorted === 'desc') {
    return 'descending'
  }
  // if (header.column.getIsSorted() === false) return "none"

  return 'none'
}
