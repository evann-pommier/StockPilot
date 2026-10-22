import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiError, request } from './http'

function mockFetch(response: Response | Error) {
  const fn = vi.fn(() =>
    response instanceof Error ? Promise.reject(response) : Promise.resolve(response),
  )
  vi.stubGlobal('fetch', fn)
  return fn
}

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('request', () => {
  it('retourne le JSON en cas de succès', async () => {
    mockFetch(new Response(JSON.stringify({ id: 1 }), { status: 200 }))
    await expect(request<{ id: number }>('/api/x', undefined, 'échec')).resolves.toEqual({ id: 1 })
  })

  it('retourne undefined pour un 204', async () => {
    mockFetch(new Response(null, { status: 204 }))
    await expect(request<void>('/api/x', { method: 'DELETE' }, 'échec')).resolves.toBeUndefined()
  })

  it('utilise le champ "detail" du problem+json comme message', async () => {
    mockFetch(
      new Response(JSON.stringify({ status: 409, detail: 'Stock insuffisant.' }), { status: 409 }),
    )
    const error = await request('/api/x', undefined, 'échec').catch((e: unknown) => e)
    expect(error).toBeInstanceOf(ApiError)
    expect((error as ApiError).message).toBe('Stock insuffisant.')
    expect((error as ApiError).status).toBe(409)
  })

  it('retombe sur le message générique si le corps n\'est pas du JSON', async () => {
    mockFetch(new Response('<html lang="fr">oups</html>', { status: 500 }))
    await expect(request('/api/x', undefined, 'Erreur serveur')).rejects.toThrow(
      'Erreur serveur (HTTP 500)',
    )
  })

  it('signale un serveur injoignable', async () => {
    mockFetch(new TypeError('Failed to fetch'))
    const error = await request('/api/x', undefined, 'échec').catch((e: unknown) => e)
    expect((error as ApiError).status).toBe(0)
    expect((error as ApiError).message).toMatch(/injoignable/)
  })
})
