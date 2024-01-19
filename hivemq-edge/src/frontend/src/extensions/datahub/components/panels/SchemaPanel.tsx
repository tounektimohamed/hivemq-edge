import { FC, useCallback, useMemo } from 'react'
import { Node } from 'reactflow'
import { IChangeEvent } from '@rjsf/core'
import { Card, CardBody } from '@chakra-ui/react'
import { CustomValidator } from '@rjsf/utils'

// import { Parser } from 'acorn'
// import { customizeValidator } from '@rjsf/validator-ajv8'
// import { parse } from 'protobufjs'

import { PanelProps, SchemaData } from '../../types.ts'
import useDataHubDraftStore from '../../hooks/useDataHubDraftStore.ts'
import { MOCK_SCHEMA_SCHEMA } from '../../api/specs/SchemaData.ts'
import { ReactFlowSchemaForm, datahubRJSFWidgets } from '../helpers'

export const SchemaPanel: FC<PanelProps> = ({ selectedNode, onClose }) => {
  const { nodes, onUpdateNodes } = useDataHubDraftStore()
  // const [fields, setFields] = useState<string[] | null>(null)

  const data = useMemo(() => {
    const adapterNode = nodes.find((e) => e.id === selectedNode) as Node<SchemaData> | undefined
    return adapterNode ? adapterNode.data : null
  }, [selectedNode, nodes])

  const onFormSubmit = useCallback(
    (data: IChangeEvent) => {
      const { formData } = data
      onUpdateNodes(selectedNode, formData)
      onClose?.()
    },
    [selectedNode, onUpdateNodes, onClose]
  )

  const customValidate: CustomValidator<SchemaData> = (formData, errors) => {
    if (!formData) return errors

    // TODO[NVL] Consider live validation
    // if (type === SchemaType.JAVASCRIPT && schemaSource) {
    //   try {
    //     const program = Parser.parse(schemaSource, { ecmaVersion: 'latest' })
    //   } catch (e) {
    //     errors.schemaSource?.addError((e as SyntaxError).message)
    //   }
    // }
    // if (type === SchemaType.JSON && schemaSource) {
    //   try {
    //     const validator = customizeValidator()
    //     const parsed: RJSFSchema = JSON.parse(schemaSource)
    //     const validated = validator.validateFormData(undefined, { ...jj, required: [] })
    //   } catch (e) {
    //     errors.schemaSource?.addError((e as SyntaxError).message)
    //     // setFields(null)
    //   }
    // }
    // if (type === SchemaType.PROTO && schemaSource) {
    //   try {
    //     const parsed = parse(schemaSource)
    //   } catch (e) {
    //     errors.schemaSource?.addError((e as SyntaxError).message)
    //     // setFields(null)
    //   }
    // }

    return errors
  }

  return (
    <Card>
      <CardBody>
        <ReactFlowSchemaForm
          schema={MOCK_SCHEMA_SCHEMA.schema}
          uiSchema={MOCK_SCHEMA_SCHEMA.uiSchema}
          formData={data}
          widgets={datahubRJSFWidgets}
          customValidate={customValidate}
          onSubmit={onFormSubmit}
          onChange={() => console.log('changed')}
          onError={() => console.log('errors')}
        />
      </CardBody>
    </Card>
  )
}