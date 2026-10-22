import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import type { StockMovement } from '../api/types'
import { MovementHistory } from './MovementHistory'

const movements: StockMovement[] = [
  {
    id: 2,
    type: 'OUT',
    quantity: 3,
    createdAt: '2026-10-16T14:30:00',
    product: { id: 1, sku: 'CLAV-001', name: 'Clavier' },
  },
  {
    id: 1,
    type: 'IN',
    quantity: 10,
    createdAt: '2026-10-15T09:00:00',
    product: { id: 1, sku: 'CLAV-001', name: 'Clavier' },
  },
]

describe('MovementHistory', () => {
  it('affiche le nom du produit et non « Produit inconnu »', () => {
    render(<MovementHistory movements={movements} />)
    expect(screen.getAllByText('Clavier')).toHaveLength(2)
    expect(screen.queryByText(/produit inconnu/i)).not.toBeInTheDocument()
  })

  it('conserve l\'ordre de l\'API (plus récent en premier)', () => {
    render(<MovementHistory movements={movements} />)
    const rows = screen.getAllByRole('row').slice(1)
    expect(rows[0]).toHaveTextContent('Sortie')
    expect(rows[1]).toHaveTextContent('Entrée')
  })

  it('affiche un message quand il n\'y a aucun mouvement', () => {
    render(<MovementHistory movements={[]} />)
    expect(screen.getByText(/aucun mouvement/i)).toBeInTheDocument()
  })
})
