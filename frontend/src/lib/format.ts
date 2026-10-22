import type { MovementType } from '../api/types'

export function formatPrice(price: number): string {
  return Number(price).toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' })
}

export function formatDateTime(isoDate: string): string {
  return new Date(isoDate).toLocaleString('fr-FR', { dateStyle: 'short', timeStyle: 'short' })
}

export const MOVEMENT_LABELS: Record<MovementType, string> = {
  IN: 'Entrée',
  OUT: 'Sortie',
  ADJUSTMENT: 'Ajustement',
}
