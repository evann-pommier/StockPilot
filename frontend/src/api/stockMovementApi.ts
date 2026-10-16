
const API_URL = '/api/movements';

export type MovementType = 'IN' | 'OUT' | 'ADJUSTMENT';

export interface StockMovement {
    id: number;
    type: MovementType;
    quantity: number;
    createdAt: string;
    product?: { id: number; name: string; sku: string };
}

export async function createStockMovement(
    productId: number,
    type: 'IN' | 'OUT',
    quantity: number,
): Promise<StockMovement> {
    const params = new URLSearchParams({
        productId: String(productId),
        type,
        quantity: String(quantity),
    });

    const response = await fetch(`${API_URL}?${params.toString()}`, {
        method: 'POST',
    });

    if (!response.ok) {
        const message = await response.text();
        throw new Error(
            message || `Erreur lors du mouvement de stock : ${response.status}`,
        );
    }

    return (await response.json()) as StockMovement;
}

export async function getStockMovements(): Promise<StockMovement[]> {
    const response = await fetch(API_URL);

    if (!response.ok) {
        throw new Error(
            `Erreur lors du chargement des mouvements : ${response.status}`,
        );
    }

    return (await response.json()) as StockMovement[];
}
