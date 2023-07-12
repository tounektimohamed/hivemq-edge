import * as Sentry from '@sentry/react'

Sentry.init({
  dsn: 'https://d78a3d0dfacc4967bfbd1da84e844d3e@o4505516882853888.ingest.sentry.io/4505516886130688',
  integrations: [
    new Sentry.BrowserTracing({
      //   // Set 'tracePropagationTargets' to control for which URLs distributed tracing should be enabled
      tracePropagationTargets: [],
    }),
    // new Sentry.Replay(),
  ],
  // Performance Monitoring
  tracesSampleRate: 1.0, // Capture 100% of the transactions, reduce in production!
  // Session Replay
  replaysSessionSampleRate: 0.1, // This sets the sample rate at 10%. You may want to change it to 100% while in development and then sample at a lower rate in production.
  replaysOnErrorSampleRate: 1.0, // If you're not already sampling the entire session, change the sample rate to 100% when sampling sessions where errors occur.
})

Sentry.captureException(new Error('Conditional Test Error'))
