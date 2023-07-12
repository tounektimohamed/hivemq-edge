import { datadogRum } from '@datadog/browser-rum'

datadogRum.init({
  applicationId: import.meta.env.VITE_MONITORING_DATADOG_APP,
  clientToken: import.meta.env.VITE_MONITORING_DATADOG_TOKEN,
  site: 'datadoghq.eu',
  service: 'hivemq-edge-frontend',
  env: 'dev',
  // Specify a version number to identify the deployed version of your application in Datadog
  // version: '1.0.0',
  sessionSampleRate: 100,
  sessionReplaySampleRate: 20,
  trackUserInteractions: true,
  trackResources: true,
  trackLongTasks: true,
  defaultPrivacyLevel: 'mask-user-input',
})

datadogRum.startSessionReplayRecording()
