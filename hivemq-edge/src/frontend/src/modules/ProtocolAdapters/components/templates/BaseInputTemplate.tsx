import { Templates } from '@rjsf/chakra-ui'
import { BaseInputTemplateProps } from '@rjsf/utils'
import { FC } from 'react'

import { TopicInputTemplate } from './__internals/TopicInputTemplate.tsx'

export const BaseInputTemplate: FC<BaseInputTemplateProps> = (props) => {
  const { BaseInputTemplate } = Templates
  const { schema } = props

  if (schema.format === 'mqtt-topic') {
    return <TopicInputTemplate {...props} />
  }
  // @ts-ignore
  return <BaseInputTemplate {...props} />
}
