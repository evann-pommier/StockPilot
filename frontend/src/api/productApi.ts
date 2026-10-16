
const API_URL = '/api/products';

export interface Product {
  id: number;
  name: string;
  sku: string;
  description?: string;
  price: number;
  quantity: number;
  category?: {
    id: number;
    name: string;
  };
}

export type CreateProductData = {
  name: string;
  sku: string;
  description: string;
  price: number;
  quantity: number;
  category: {
    id: number;
  };
};

export async function getProducts(): Promise<Product[]> {
  const response = await fetch(API_URL);

  if (!response.ok) {
    throw new Error(
        `Erreur lors du chargement des produits : ${response.status}`,
    );
  }

  return (await response.json()) as Product[];
}

export async function createProduct(
    product: CreateProductData,
): Promise<Product> {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(product),
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(
        message || `Erreur lors de la création : ${response.status}`,
    );
  }

  return (await response.json()) as Product;
}


export async function updateProduct(
    id: number,
    product: CreateProductData,
): Promise<Product> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(product),
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(
        message || `Erreur lors de la modification : ${response.status}`,
    );
  }

  return (await response.json()) as Product;
}

export async function deleteProduct(id: number): Promise<void> {
  const response = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(
        message || `Erreur lors de la suppression : ${response.status}`,
    );
  }
}
