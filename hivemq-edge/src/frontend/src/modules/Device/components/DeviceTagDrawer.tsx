import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import {
  Button,
  Drawer,
  DrawerBody,
  DrawerCloseButton,
  DrawerContent,
  DrawerFooter,
  DrawerHeader,
  DrawerOverlay,
  Text,
  useDisclosure,
} from '@chakra-ui/react'
import { LuFileCog } from 'react-icons/lu'

import { DomainTagList } from '@/api/__generated__'
import IconButton from '@/components/Chakra/IconButton.tsx'
import DeviceTagForm from '@/modules/Device/components/DeviceTagForm.tsx'
import { ManagerContextType } from '@/modules/Mappings/types.ts'

interface DeviceTagDrawerProps {
  isDisabled?: boolean
  context: ManagerContextType
  onSubmit?: (data: DomainTagList | undefined) => void
}

const DeviceTagDrawer: FC<DeviceTagDrawerProps> = ({ context, onSubmit, isDisabled = false }) => {
  const { t } = useTranslation()
  const { isOpen, onOpen, onClose } = useDisclosure()

  const onHandleSubmit = (data: DomainTagList | undefined) => {
    onSubmit?.(data)
    onClose()
  }

  return (
    <>
      <IconButton
        variant="primary"
        aria-label={t('device.drawer.tagList.cta.edit')}
        icon={<LuFileCog />}
        isDisabled={isDisabled}
        onClick={onOpen}
      />
      <Drawer isOpen={isOpen} placement="right" size="md" onClose={onClose} closeOnOverlayClick={false}>
        <DrawerOverlay />
        <DrawerContent>
          <DrawerCloseButton />
          <DrawerHeader>
            <Text> {t('device.drawer.tagEditor.title')}</Text>
          </DrawerHeader>

          <DrawerBody>
            <DeviceTagForm context={context} onSubmit={onHandleSubmit} />
          </DrawerBody>

          <DrawerFooter>
            <Button variant="primary" type="submit" form="domainTags-instance-form">
              {t('unifiedNamespace.submit.label')}
            </Button>
          </DrawerFooter>
        </DrawerContent>
      </Drawer>
    </>
  )
}

export default DeviceTagDrawer
