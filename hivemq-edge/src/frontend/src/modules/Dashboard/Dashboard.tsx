import { AbsoluteCenter, Box } from '@chakra-ui/react'
import { SkipNavContent } from '@chakra-ui/skip-nav'
import { FC } from 'react'
import { Navigate, Outlet } from 'react-router-dom'

import LoaderSpinner from '@/components/Chakra/LoaderSpinner.tsx'

import { useAuth } from '../Auth/hooks/useAuth.ts'

const Dashboard: FC = () => {
  const { credentials, isLoading } = useAuth()

  if (isLoading) {
    return (
      <Box position="relative" h="100vh">
        <AbsoluteCenter p="4" axis="both">
          <LoaderSpinner />
        </AbsoluteCenter>
      </Box>
    )
  }
  if (!credentials) {
    return <Navigate to="/login" state={{ from: location }} />
  }

  return (
    <>
      <SkipNavContent />
      <Outlet />
    </>
  )
}

export default Dashboard
