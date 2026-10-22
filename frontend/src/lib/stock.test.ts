import { describe, expect, it } from 'vitest'
import { makeProduct } from '../test/fixtures'
import {
  computeStats,
  DEFAULT_FILTERS,
  filterProducts,
  isLowStock,
  LOW_STOCK_THRESHOLD,
  normalizeText,
} from './stock'

const products = [
  makeProduct({ id: 1, name: 'Écran 27 pouces', sku: 'ECRA-027', quantity: 12 }),
  makeProduct({
    id: 2,
    name: 'Souris sans fil',
    sku: 'SOUR-001',
    quantity: 4,
    category: { id: 2, name: 'Périphériques' },
  }),
  makeProduct({ id: 3, name: 'Chaise', sku: 'CHAI-010', quantity: LOW_STOCK_THRESHOLD, category: null }),
]

describe('normalizeText', () => {
  it('met en minuscules et retire les accents', () => {
    expect(normalizeText('Écran Électrique')).toBe('ecran electrique')
  })
})

describe('isLowStock', () => {
  it('considère le seuil comme inclus', () => {
    expect(isLowStock({ quantity: LOW_STOCK_THRESHOLD })).toBe(true)
    expect(isLowStock({ quantity: LOW_STOCK_THRESHOLD + 1 })).toBe(false)
  })
})

describe('filterProducts', () => {
  it('retourne tout sans filtre', () => {
    expect(filterProducts(products, DEFAULT_FILTERS)).toHaveLength(3)
  })

  it('cherche sans tenir compte des accents ni de la casse', () => {
    const result = filterProducts(products, { ...DEFAULT_FILTERS, search: 'ECRAN' })
    expect(result.map((p) => p.id)).toEqual([1])
  })

  it('cherche aussi dans le SKU et le nom de catégorie', () => {
    expect(filterProducts(products, { ...DEFAULT_FILTERS, search: 'sour-001' })).toHaveLength(1)
    expect(filterProducts(products, { ...DEFAULT_FILTERS, search: 'périph' })).toHaveLength(1)
  })

  it('filtre par catégorie', () => {
    const result = filterProducts(products, { ...DEFAULT_FILTERS, categoryId: '2' })
    expect(result.map((p) => p.id)).toEqual([2])
  })

  it('filtre par état du stock', () => {
    const low = filterProducts(products, { ...DEFAULT_FILTERS, stockStatus: 'low' })
    const available = filterProducts(products, { ...DEFAULT_FILTERS, stockStatus: 'available' })
    expect(low.map((p) => p.id)).toEqual([2, 3])
    expect(available.map((p) => p.id)).toEqual([1])
  })

  it('combine les filtres', () => {
    const result = filterProducts(products, {
      search: 'souris',
      categoryId: '2',
      stockStatus: 'low',
    })
    expect(result.map((p) => p.id)).toEqual([2])
  })
})

describe('computeStats', () => {
  it('calcule références, unités et stock faible', () => {
    expect(computeStats(products)).toEqual({ references: 3, units: 21, lowStock: 2 })
  })

  it('gère une liste vide', () => {
    expect(computeStats([])).toEqual({ references: 0, units: 0, lowStock: 0 })
  })
})
