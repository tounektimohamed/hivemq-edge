import { Button, Flex } from '@chakra-ui/react'
import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { BiAddToQueue } from 'react-icons/bi'
import { Outlet, useNavigate } from 'react-router-dom'

import { useListBridges } from '@/api/hooks/useGetBridges/useListBridges.tsx'
import PageContainer from '@/components/PageContainer.tsx'
import Bridges from '@/modules/Bridges/Bridges.tsx'
import { BridgeProvider } from '@/modules/Bridges/hooks/useBridgeConfig.tsx'

const BridgePage: FC = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const { isLoading, isError } = useListBridges()

  return (
    <PageContainer
      title={t('bridge.title') as string}
      subtitle={t('bridge.description') as string}
      cta={
        <Flex height={'100%'} justifyContent={'flex-end'} alignItems={'flex-end'} pb={6}>
          <Button
            variant="outline"
            leftIcon={<BiAddToQueue />}
            onClick={() => navigate('/mqtt-bridges/new')}
            isDisabled={isLoading || isError}
          >
            {t('bridge.action.add')}
          </Button>
        </Flex>
      }
    >
      <BridgeProvider>
        <Bridges />
        <Outlet />
      </BridgeProvider>
    </PageContainer>
  )
}

export default BridgePage
