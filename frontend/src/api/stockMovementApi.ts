import { jsonBody, request } from './http'
import type { MovementType, StockMovement } from './types'

const API_URL = '/api/movements'

/** Historique complet, du plus récent au plus ancien (ordre fourni par l'API). */
export function getStockMovements(): Promise<StockMovement[]> {
  return request<StockMovement[]>(API_URL, undefined, 'Erreur lors du chargement des mouvements')
}

export function createStockMovement(
  productId: number,
  type: Exclude<MovementType, 'ADJUSTMENT'>,
  quantity: number,
): Promise<StockMovement> {
  return request<StockMovement>(
    API_URL,
    jsonBody('POST', { productId, type, quantity }),
    'Erreur lors du mouvement de stock',
  )
}
