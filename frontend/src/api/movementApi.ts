const API_URL = '/api/movements';

export interface StockMovement {
  id: number;
  type: 'IN' | 'OUT' | 'ADJUSTMENT';
  quantity: number;
}

export async function getMovements(): Promise<StockMovement[]> {
  const response = await fetch(API_URL);

  if (!response.ok) {
    throw new Error(
      `Erreur lors du chargement des mouvements : ${response.status}`,
    );
  }

  return await response.json() as Promise<StockMovement[]>;
}