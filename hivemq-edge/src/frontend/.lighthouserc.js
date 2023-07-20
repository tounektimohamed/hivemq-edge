const TARGET_PERFORMANCE = 0.9
const TARGET_ACCEPTABILITY = 1
const TARGET_BEST_PRACTICE = 1
const TARGET_SEO = 0.8

module.exports = {
  ci: {
    collect: {
      // collect options here
      staticDistDir: 'hivemq-edge/src/frontend/dist',
      url: ['http://localhost/app'],
      isSinglePageApplication: true,
      numberOfRuns: 1,
      settings: {
        preset: 'desktop',
        onlyCategories: ['performance', 'accessibility', 'best-practices', 'seo'],
        skipAudits: ['uses-http2'],
        chromeFlags: '--no-sandbox',
      },
    },
    assert: {
      // assert options here
      // preset: 'lighthouse:recommended',
      assertions: {
        'categories:performance': ['error', { minScore: TARGET_PERFORMANCE, aggregationMethod: 'median-run' }],
        'categories:accessibility': ['error', { minScore: TARGET_ACCEPTABILITY, aggregationMethod: 'pessimistic' }],
        'categories:best-practices': ['error', { minScore: TARGET_BEST_PRACTICE, aggregationMethod: 'pessimistic' }],
        'categories:seo': ['error', { minScore: TARGET_SEO, aggregationMethod: 'pessimistic' }],
      },
    },
    upload: {
      // upload options here
    },
    server: {
      // server options here
    },
    wizard: {
      // wizard options here
    },
  },
}
