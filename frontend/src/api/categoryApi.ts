const API_URL = '/api/categories';

export interface Category {
  id: number;
  name: string;
}

export async function getCategories(): Promise<Category[]> {
  const response = await fetch(API_URL);

  if (!response.ok) {
    throw new Error(
      `Erreur lors du chargement des catégories : ${response.status}`,
    );
  }

  return await response.json() as Promise<Category[]>;
}