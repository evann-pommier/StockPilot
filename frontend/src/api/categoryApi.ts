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


export async function createCategory(name: string): Promise<Category> {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name }),
  });

  if (!response.ok) {
    throw new Error(
        `Erreur lors de la création de la catégorie : ${response.status}`,
    );
  }

  return await response.json() as Category;
}
