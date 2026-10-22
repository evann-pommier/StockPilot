/** Erreur renvoyée par l'API, avec le statut HTTP (0 si le serveur est injoignable). */
export class ApiError extends Error {
  readonly status: number

  constructor(message: string, status: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

async function extractMessage(response: Response, fallback: string): Promise<string> {
  try {
    // Le backend répond en RFC 9457 (problem+json) : le champ "detail" est lisible tel quel.
    const body: unknown = await response.json()
    if (body && typeof body === 'object' && 'detail' in body) {
      const { detail } = body as { detail: unknown }
      if (typeof detail === 'string' && detail.length > 0) {
        return detail
      }
    }
  } catch {
    // Corps absent ou non JSON : on retombe sur le message générique.
  }
  return `${fallback} (HTTP ${response.status})`
}

/**
 * Appel JSON typé. Lève une {@link ApiError} avec un message prêt à afficher.
 */
export async function request<T>(
  url: string,
  init: RequestInit | undefined,
  fallbackMessage: string,
): Promise<T> {
  let response: Response
  try {
    response = await fetch(url, init)
  } catch {
    throw new ApiError('Le serveur est injoignable. Vérifiez que le backend est démarré.', 0)
  }

  if (!response.ok) {
    throw new ApiError(await extractMessage(response, fallbackMessage), response.status)
  }

  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

export function jsonBody(method: 'POST' | 'PUT', payload: unknown): RequestInit {
  return {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  }
}
