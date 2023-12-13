import { useContext } from 'react'
import { useLocation } from 'react-router-dom'
import { UNSAFE_NavigationContext as NavigationContext } from 'react-router-dom'

export function useRouterNavigation() {
  const location = useLocation()
  const { navigator } = useContext(NavigationContext)
  return (path: string) => {
    const toPathname = navigator.encodeLocation?.(path).pathname ?? path
    const locationPathname = location.pathname
    return (
      locationPathname === toPathname ||
      (locationPathname.startsWith(toPathname) && locationPathname.charAt(toPathname.length) === '/')
    )
  }
}
