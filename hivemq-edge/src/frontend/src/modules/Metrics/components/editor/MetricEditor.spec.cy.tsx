/// <reference types="cypress" />

import { MetricList } from '@/api/__generated__'
import { mockBridgeId } from '@/api/hooks/useGetBridges/__handlers__'
import { MOCK_METRICS } from '@/api/hooks/useGetMetrics/__handlers__'

import { ChartType } from '@/modules/Metrics/types.ts'
import MetricSelector from './MetricEditor.tsx'

describe('MetricEditor', () => {
  beforeEach(() => {
    cy.viewport(800, 800)
    cy.intercept('/api/v1/metrics', { items: MOCK_METRICS } as MetricList).as('getMetrics')
  })

  describe('Basic selector - default chart', () => {
    it('should render the selector', () => {
      const onSubmit = cy.stub().as('onSubmit')
      cy.mountWithProviders(
        <MetricSelector
          filter={mockBridgeId}
          onSubmit={onSubmit}
          selectedChart={ChartType.SAMPLE}
          selectedMetrics={[MOCK_METRICS[0].name as string, MOCK_METRICS[0].name as string]}
        />,
      )
      cy.get('div#react-select-2-placeholder').should('contain.text', 'Select...')
      cy.get("button[type='submit']").should('be.disabled')
      cy.get('input#metrics-select').click()

      cy.get('div#react-select-2-listbox').find("[role='option']").should('have.length', 10)
      cy.get('div#react-select-2-listbox')
        .find("[role='option']")
        .eq(3)
        .should('have.text', '[Forward] Publish success (count)')
        .should('have.attr', 'aria-disabled', 'true')

      cy.get('div#react-select-2-listbox').find("[role='option']").eq(5).click()
      cy.get("button[type='submit']").should('not.be.disabled')
      cy.get("button[type='submit']").click()
      cy.get('@onSubmit').should('have.been.calledWithMatch', {
        selectedTopic: {
          label: '[Local] Publish failed (count)',
          value: 'com.hivemq.edge.bridge.bridge-id-01.local.publish.failed.count',
          isDisabled: false,
        },
      })
    })

    it('should be accessible', () => {
      cy.injectAxe()
      cy.mountWithProviders(
        <MetricSelector
          filter={mockBridgeId}
          onSubmit={cy.stub()}
          selectedChart={ChartType.SAMPLE}
          selectedMetrics={[MOCK_METRICS[0].name as string, MOCK_METRICS[0].name as string]}
        />,
      )
      cy.checkAccessibility()
      cy.percySnapshot('Component: MetricEditor')
    })
  })
})
