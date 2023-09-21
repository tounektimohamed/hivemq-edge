import { describe, it, expect, vi } from 'vitest'
import { verifyStoredToken } from '@/modules/Auth/auth-utilities.ts'
import { MOCK_JWT } from '@/__test-utils__/mocks.ts'

describe('processToken', () => {
  const mockSetAuthToken = vi.fn()
  const mockLogin = vi.fn()
  const mockSetLoading = vi.fn()

  it('should not accept an empty token', () => {
    verifyStoredToken(undefined, mockSetAuthToken, mockLogin, mockSetLoading)
    expect(mockSetLoading).toHaveBeenCalledWith(false)
    expect(mockSetAuthToken).toHaveBeenCalledWith(undefined)
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('should not accept a non-compliant token', () => {
    verifyStoredToken('incorrect token', mockSetAuthToken, mockLogin, mockSetLoading)
    expect(mockSetLoading).toHaveBeenCalledWith(false)
    expect(mockSetAuthToken).toHaveBeenCalledWith(undefined)
    expect(mockLogin).not.toHaveBeenCalled()
  })

  it('should not accept an old token', () => {
    verifyStoredToken(MOCK_JWT, mockSetAuthToken, mockLogin, mockSetLoading)
    expect(mockSetLoading).toHaveBeenCalledWith(false)
    expect(mockSetAuthToken).toHaveBeenCalledWith(undefined)
    expect(mockLogin).not.toHaveBeenCalled()
  })
})
