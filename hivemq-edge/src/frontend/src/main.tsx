import React from 'react'
import ReactDOM from 'react-dom/client'
import { setupWorker } from 'msw'

import { handlers } from '@/__test-utils__/msw/handlers.ts'
import '@/config/i18n.config'
import MainApp from '@/modules/App/MainApp.tsx'

if (import.meta.env.VITE_MONITORING_BUGSNAG) {
  import(/* webpackChunkName: "hivemq-monitoring" */ '@/config/monitoring/bugsnag.config.ts')
}
if (import.meta.env.VITE_MONITORING_SENTRY) {
  import(/* webpackChunkName: "hivemq-monitoring" */ '@/config/monitoring/sentry.config.ts')
}
if (import.meta.env.VITE_MONITORING_DATADOG_APP) {
  import(/* webpackChunkName: "hivemq-monitoring" */ '@/config/monitoring/datadog.config.ts')
}

if (import.meta.env.MODE === 'development') {
  import(/* webpackChunkName: "hivemq-dev-chunk" */ './__test-utils__/dev-console')

  if (import.meta.env.VITE_FLAG_MOCK_SERVER === 'true') {
    const worker = setupWorker(...handlers)
    worker.start({ onUnhandledRequest: 'bypass' })
    worker.printHandlers()
  }
}

const body = document.querySelector('body')
if (body) {
  body.dataset['appVersion'] = import.meta.env.VITE_APP_VERSION
}

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <MainApp />
  </React.StrictMode>
)
