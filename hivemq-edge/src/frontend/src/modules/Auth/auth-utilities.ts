import { Dispatch, SetStateAction } from 'react'

import { ApiBearerToken } from '@/api/__generated__'
import { parseJWT, verifyJWT } from '@/api/utils.ts'

export const LOCALSTORAGE_AUTH_TOKEN = 'auth'

export const verifyStoredToken = (
  authToken: string | undefined,
  setAuthToken: Dispatch<SetStateAction<string | undefined>>,
  login: (newUser: ApiBearerToken, callback: VoidFunction) => void,
  setLoading: Dispatch<SetStateAction<boolean>>
) => {
  if (!authToken) {
    setAuthToken(undefined)
    setLoading(false)
    return
  }

  const parsedToken = parseJWT(authToken)
  if (!parsedToken) {
    setAuthToken(undefined)
    setLoading(false)
    return
  }

  const isValid = verifyJWT(parsedToken)
  if (!isValid) {
    setAuthToken(undefined)
    setLoading(false)
    return
  }

  consoleLog(authToken, 'from localStorage')
  login({ token: authToken }, () => {
    setLoading(false)
  })
}

export const consoleLog = (token: string | undefined, context: string) => {
  console.groupCollapsed(
    '%c[dev] Token %c %s %s',
    'color:lightblue;font-weight:bold;',
    'color:lightcoral;',
    token?.slice(-6),
    `(${context})`
  )
  console.log('Token')
  console.log(token)
  console.groupEnd()
}
