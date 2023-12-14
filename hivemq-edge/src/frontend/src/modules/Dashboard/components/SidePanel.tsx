import { Box, Button, Flex, Image, Text } from '@chakra-ui/react'
import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { FiLogOut } from 'react-icons/fi'
import { useNavigate } from 'react-router-dom'

import { useGetConfiguration } from '@/api/hooks/useFrontendServices/useGetConfiguration.tsx'
import logo from '@/assets/edge/03-hivemq-industrial-edge-vert.svg'
import { useAuth } from '@/modules/Auth/hooks/useAuth.ts'
import NotificationBadge from '@/modules/Notifications/NotificationBadge.tsx'

import useGetNavItems from '../hooks/useGetNavItems.tsx'
import NavLinksBlock from './NavLinksBlock.tsx'

const SidePanel: FC = () => {
  const { data: configuration } = useGetConfiguration()
  const { data: items } = useGetNavItems()
  const auth = useAuth()
  const navigate = useNavigate()
  const { t } = useTranslation()

  return (
    <nav>
      <Flex flexDirection="column" w={256} h={'100%'} bgColor={'#f5f5f5'} overflow={'auto'}>
        <Box p={4} m={'auto'} mb={4}>
          <Image src={logo} alt={t('branding.company') as string} boxSize="200px" />
          {configuration && (
            <Text data-testid="edge-release" fontSize="xs" textAlign={'center'}>
              [ {configuration.environment?.properties?.version} ]
            </Text>
          )}
        </Box>

        <Box m={4} mb={8}>
          <NotificationBadge />
        </Box>

        <Flex flexDirection="column" flex={1}>
          {items.map(({ title, items }) => (
            <NavLinksBlock key={title} title={title} items={items} />
          ))}
        </Flex>

        <Flex flexDirection="column" flex={1} />
        <Flex p={4} flexDirection={'column'} alignItems={'flex-start'} ml={2}>
          <Button leftIcon={<FiLogOut />} variant="link" onClick={() => auth.logout(() => navigate('/login'))}>
            {t('translation:action.logout')}
          </Button>
        </Flex>
      </Flex>
    </nav>
  )
}

export default SidePanel
