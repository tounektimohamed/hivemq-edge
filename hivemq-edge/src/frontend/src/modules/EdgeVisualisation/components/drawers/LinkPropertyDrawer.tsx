import { Box, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerHeader, Text } from '@chakra-ui/react'
import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { Node } from 'reactflow'

import { Adapter, Bridge } from '@/api/__generated__'
import Metrics from '@/modules/Metrics/Metrics.tsx'

import { NodeTypes } from '../../types.ts'
import { getDefaultMetricsFor } from '../../utils/nodes-utils.ts'

interface LinkPropertyDrawerProps {
  nodeId: string
  selectedNode: Node<Bridge | Adapter>
  isOpen: boolean
  onClose: () => void
  onEditEntity: () => void
}
const LinkPropertyDrawer: FC<LinkPropertyDrawerProps> = ({ nodeId, isOpen, selectedNode, onClose }) => {
  const { t } = useTranslation()

  return (
    <Drawer isOpen={isOpen} placement="right" size={'md'} onClose={onClose}>
      {/*<DrawerOverlay />*/}
      <DrawerContent>
        <DrawerCloseButton />

        <DrawerHeader>
          <Box>
            <Text>{t('workspace.observability.header', { context: selectedNode.type })}</Text>
            <Text>
              {t('workspace.device.type', { context: selectedNode.type })}: {selectedNode.data.id}
            </Text>
          </Box>
        </DrawerHeader>
        <DrawerBody>
          <Metrics
            nodeId={nodeId}
            type={selectedNode.type as NodeTypes}
            id={selectedNode.data.id}
            initMetrics={getDefaultMetricsFor(selectedNode)}
          />
        </DrawerBody>
      </DrawerContent>
    </Drawer>
  )
}

export default LinkPropertyDrawer
