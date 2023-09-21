import { Dispatch, SetStateAction } from 'react'

import { ApiBearerToken } from '@/api/__generated__'
import { parseJWT, verifyJWT } from '@/api/utils.ts'

export const processToken = (
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

  login({ token: authToken }, () => {
    setLoading(false)
    consoleLog(authToken, 'from localStorage')
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
