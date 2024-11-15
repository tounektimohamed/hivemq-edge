import { FC, useEffect, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Button, Card, CardBody, CardHeader, CardProps, Heading, HStack, List, ListItem } from '@chakra-ui/react'
import { LuWand } from 'react-icons/lu'

import { useGetTagSchemas } from '@/api/hooks/useDomainModel/useGetTagSchemas.ts'
import ErrorMessage from '@/components/ErrorMessage.tsx'
import LoaderSpinner from '@/components/Chakra/LoaderSpinner.tsx'
import { filterSupportedProperties } from '@/components/rjsf/MqttTransformation/utils/data-type.utils.ts'
import MappingInstruction from '@/components/rjsf/MqttTransformation/components/mapping/MappingInstruction.tsx'
import { FlatJSONSchema7, getPropertyListFrom } from '@/components/rjsf/MqttTransformation/utils/json-schema.utils.ts'
import { FieldMapping } from '@/modules/Mappings/types.ts'

interface MappingEditorProps extends Omit<CardProps, 'onChange'> {
  topic: string | undefined
  showTransformation?: boolean
  mapping?: FieldMapping[]
  onChange?: (v: FieldMapping[] | undefined) => void
  onSchemaReady?: (v: FlatJSONSchema7[]) => void
}

const MappingEditor: FC<MappingEditorProps> = ({
  topic,
  showTransformation = false,
  mapping,
  onChange,
  onSchemaReady,
  ...props
}) => {
  const { t } = useTranslation('components')
  const { data, isLoading, isError, error, isSuccess } = useGetTagSchemas(topic ? [topic] : [])

  const properties = useMemo(() => {
    const allProperties = data ? getPropertyListFrom(data) : []
    return allProperties.filter(filterSupportedProperties)
  }, [data])

  useEffect(() => {
    if (properties.length) onSchemaReady?.(properties)
    // TODO[NVL] Infinite re-rendering prevented by removing callback from dependencies; check if side effects
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [properties, properties.length])

  return (
    <Card {...props} size="sm">
      <CardHeader as={HStack} justifyContent="space-between">
        <Heading as="h3" size="sm">
          {t('rjsf.MqttTransformationField.mapping.header')}
        </Heading>
        <Button data-testid="auto-mapping" size="sm" leftIcon={<LuWand />} isDisabled>
          {t('rjsf.MqttTransformationField.mapping.auto.aria-label')}
        </Button>
      </CardHeader>
      <CardBody maxH="58vh" overflowY="scroll">
        {isLoading && <LoaderSpinner />}
        {isError && error && <ErrorMessage message={error.message} />}
        {!isSuccess && !isError && !isLoading && (
          <ErrorMessage message={t('rjsf.MqttTransformationField.destination.prompt')} status="info" />
        )}
        {isSuccess && (
          <List>
            {properties.map((property) => {
              const instruction = mapping ? mapping.findIndex((e) => e.destination.propertyPath === property.key) : -1
              return (
                <ListItem key={property.key}>
                  <MappingInstruction
                    showTransformation={showTransformation}
                    property={property}
                    mapping={instruction !== -1 ? mapping?.[instruction] : undefined}
                    onChange={(source, destination) => {
                      let newMappings = [...(mapping || [])]
                      if (source) {
                        const newItem: FieldMapping = {
                          source: { propertyPath: source },
                          destination: { propertyPath: destination },
                        }
                        if (instruction !== -1) {
                          newMappings[instruction] = newItem
                        } else newMappings.push(newItem)
                      } else {
                        newMappings = newMappings.filter((mapped) => mapped.destination.propertyPath !== destination)
                      }

                      onChange?.(newMappings)
                    }}
                  />
                </ListItem>
              )
            })}
          </List>
        )}
      </CardBody>
    </Card>
  )
}

export default MappingEditor
