import { FC } from 'react'
import { useTranslation } from 'react-i18next'
import { usePostTopicSamples } from '@/api/hooks/useTopicOntology/useGetTopicSamples.tsx'
import { Button, Card, CardBody, CardFooter, CardHeader, Heading, HStack, Text } from '@chakra-ui/react'

interface TopicSamplerProps {
  topic: string
}

const TopicSampler: FC<TopicSamplerProps> = ({ topic }) => {
  const { t } = useTranslation()
  const topicSampler = usePostTopicSamples()

  return (
    <Card size="sm">
      <CardHeader as={HStack} justifyContent="space-between">
        <Heading size="md">{topic}</Heading>
      </CardHeader>
      <CardBody>
        <Text>{t('workspace.topicWheel.topicSampler.title')} </Text>
        <Text>{t('workspace.topicWheel.topicSampler.description')} </Text>
      </CardBody>
      <CardFooter>
        <Button
          onClick={() => topicSampler.mutateAsync({ adapter: 'sss', topic: 'ss' })}
          isLoading={topicSampler.isPending}
          loadingText={t('workspace.topicWheel.topicSampler.sampler.loader')}
        >
          {t('workspace.topicWheel.topicSampler.sampler.cta')}
        </Button>
      </CardFooter>
    </Card>
  )
}

export default TopicSampler