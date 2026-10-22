import { jsonBody, request } from './http'
import type { Product, ProductInput } from './types'

const API_URL = '/api/products'

export function getProducts(): Promise<Product[]> {
  return request<Product[]>(API_URL, undefined, 'Erreur lors du chargement des produits')
}

export function createProduct(product: ProductInput): Promise<Product> {
  return request<Product>(API_URL, jsonBody('POST', product), 'Erreur lors de la création')
}

export function updateProduct(id: number, product: ProductInput): Promise<Product> {
  return request<Product>(
    `${API_URL}/${id}`,
    jsonBody('PUT', product),
    'Erreur lors de la modification',
  )
}

export function deleteProduct(id: number): Promise<void> {
  return request<void>(`${API_URL}/${id}`, { method: 'DELETE' }, 'Erreur lors de la suppression')
}
