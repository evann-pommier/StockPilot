const API_URL = '/api/products';

export interface Product {
  id: number;
  name: string;
  sku: string;
  quantity: number;
}

export async function getProducts(): Promise<Product[]> {
  const response = await fetch(API_URL);

  if (!response.ok) {
    throw new Error(
      `Erreur lors du chargement des produits : ${response.status}`,
    );
  }

  return await response.json() as Promise<Product[]>;
}