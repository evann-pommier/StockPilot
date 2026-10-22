export type MovementType = 'IN' | 'OUT' | 'ADJUSTMENT'

export interface Category {
  id: number
  name: string
  description?: string | null
}

export interface Product {
  id: number
  sku: string
  name: string
  description?: string | null
  price: number
  quantity: number
  category: { id: number; name: string } | null
}

/** Corps envoyé à l'API pour créer ou modifier un produit. */
export interface ProductInput {
  sku: string
  name: string
  description: string
  price: number
  quantity: number
  categoryId: number
}

export interface StockMovement {
  id: number
  type: MovementType
  quantity: number
  createdAt: string
  product: { id: number; sku: string; name: string }
}
