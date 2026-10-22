import type { Product } from '../api/types'

/** En dessous (ou égal) de ce seuil, un produit est considéré en stock faible. */
export const LOW_STOCK_THRESHOLD = 5

export type StockStatus = 'all' | 'available' | 'low'

export interface ProductFilters {
  search: string
  categoryId: string
  stockStatus: StockStatus
}

export const DEFAULT_FILTERS: ProductFilters = {
  search: '',
  categoryId: 'all',
  stockStatus: 'all',
}

export function isLowStock(product: Pick<Product, 'quantity'>): boolean {
  return product.quantity <= LOW_STOCK_THRESHOLD
}

/** Minuscules et sans accents : « Écran » et « ecran » se correspondent. */
export function normalizeText(text: string): string {
  return text
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
}

export function filterProducts(products: Product[], filters: ProductFilters): Product[] {
  const search = normalizeText(filters.search.trim())

  return products.filter((product) => {
    const matchesSearch =
      normalizeText(product.name).includes(search) ||
      normalizeText(product.sku).includes(search) ||
      normalizeText(product.category?.name ?? '').includes(search)

    const matchesCategory =
      filters.categoryId === 'all' || String(product.category?.id ?? '') === filters.categoryId

    const matchesStatus =
      filters.stockStatus === 'all' ||
      (filters.stockStatus === 'low' && isLowStock(product)) ||
      (filters.stockStatus === 'available' && !isLowStock(product))

    return matchesSearch && matchesCategory && matchesStatus
  })
}

export interface StockStats {
  references: number
  units: number
  lowStock: number
}

export function computeStats(products: Product[]): StockStats {
  return {
    references: products.length,
    units: products.reduce((total, product) => total + product.quantity, 0),
    lowStock: products.filter(isLowStock).length,
  }
}
