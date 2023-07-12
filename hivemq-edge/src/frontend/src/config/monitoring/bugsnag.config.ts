import Bugsnag from '@bugsnag/js'
import BugsnagPluginReact from '@bugsnag/plugin-react'

Bugsnag.start({
  apiKey: import.meta.env.VITE_MONITORING_BUGSNAG,
  plugins: [new BugsnagPluginReact()],
  appVersion: import.meta.env.VITE_APP_VERSION,
})

Bugsnag.notify(new Error('Conditional Test Error'))
