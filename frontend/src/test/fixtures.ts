import type { Product } from '../api/types'

export function makeProduct(overrides: Partial<Product> = {}): Product {
  return {
    id: 1,
    sku: 'CLAV-001',
    name: 'Clavier mécanique',
    description: null,
    price: 89.9,
    quantity: 25,
    category: { id: 1, name: 'Informatique' },
    ...overrides,
  }
}
