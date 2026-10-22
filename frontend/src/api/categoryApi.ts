import { jsonBody, request } from './http'
import type { Category } from './types'

const API_URL = '/api/categories'

export function getCategories(): Promise<Category[]> {
  return request<Category[]>(API_URL, undefined, 'Erreur lors du chargement des catégories')
}

export function createCategory(name: string): Promise<Category> {
  return request<Category>(
    API_URL,
    jsonBody('POST', { name }),
    'Erreur lors de la création de la catégorie',
  )
}
