import { createContext, FunctionComponent, PropsWithChildren, useEffect, useState } from 'react'

import { ApiBearerToken } from '@/api/__generated__'
import { useLocalStorage } from '@/hooks/useLocalStorage/useLocalStorage.ts'

import { LOCALSTORAGE_AUTH_TOKEN, consoleLog, verifyStoredToken } from './auth-utilities.ts'

interface AuthContextType {
  credentials: ApiBearerToken | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (user: ApiBearerToken, callback: VoidFunction) => void
  logout: (callback: VoidFunction) => void
}

export const AuthContext = createContext<AuthContextType | null>(null)

export const AuthProvider: FunctionComponent<PropsWithChildren> = ({ children }) => {
  const [credentials, setCredentials] = useState<ApiBearerToken | null>(null)
  const [isLoading, setLoading] = useState(true)
  const [isAuthenticated, setAuthenticated] = useState(false)

  const [storedToken, setStoredToken] = useLocalStorage<string | undefined>(LOCALSTORAGE_AUTH_TOKEN, undefined)

  useEffect(() => {
    verifyStoredToken(storedToken, setStoredToken, login, setLoading)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const login = (newUser: ApiBearerToken, callback: VoidFunction) => {
    consoleLog(newUser.token, 'login')
    setCredentials(newUser)
    setStoredToken(newUser.token)
    setAuthenticated(true)
    setLoading(false)
    callback()
  }

  const logout = (callback: VoidFunction) => {
    consoleLog(undefined, 'logout')

    setCredentials(null)
    setStoredToken(undefined)
    setAuthenticated(false)
    setLoading(false)
    callback()
  }

  return (
    <AuthContext.Provider value={{ credentials, login, logout, isLoading, isAuthenticated }}>
      {children}
    </AuthContext.Provider>
  )
}
