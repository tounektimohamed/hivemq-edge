import { useMutation } from '@tanstack/react-query'
import type { BehaviorPolicy } from '@/api/__generated__'
import { useHttpClient } from '@/api/hooks/useHttpClient/useHttpClient.ts'

interface UpdateDataPolicyProps {
  policyId: string
  requestBody: BehaviorPolicy
}

export const useUpdateBehaviorPolicy = () => {
  const appClient = useHttpClient()

  return useMutation({
    mutationFn: (data: UpdateDataPolicyProps) => {
      return appClient.dataHubBehaviorPolicies.updateBehaviorPolicy(data.policyId, data.requestBody)
    },
  })
}