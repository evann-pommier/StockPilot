import type { Product } from '../api/types'
import { formatPrice } from '../lib/format'
import { isLowStock } from '../lib/stock'

interface Props {
  products: Product[]
  onEdit: (product: Product) => void
  onDelete: (product: Product) => void
}

export function ProductTable({ products, onEdit, onDelete }: Props) {
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Produit</th>
            <th>Description</th>
            <th>SKU</th>
            <th>Catégorie</th>
            <th>Prix</th>
            <th>Quantité</th>
            <th>État du stock</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product) => {
            const low = isLowStock(product)
            return (
              <tr key={product.id}>
                <td>
                  <strong>{product.name}</strong>
                </td>
                <td>{product.description || '—'}</td>
                <td>{product.sku}</td>
                <td>{product.category?.name ?? 'Sans catégorie'}</td>
                <td>{formatPrice(product.price)}</td>
                <td>{product.quantity}</td>
                <td>
                  <span className={`stock-badge ${low ? 'stock-low' : 'stock-normal'}`}>
                    {low ? 'Stock faible' : 'Disponible'}
                  </span>
                </td>
                <td>
                  <div className="action-buttons">
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() => onEdit(product)}
                    >
                      Modifier
                    </button>
                    <button
                      type="button"
                      className="danger-button"
                      onClick={() => onDelete(product)}
                    >
                      Supprimer
                    </button>
                  </div>
                </td>
              </tr>
            )
          })}
        </tbody>
      </table>
    </div>
  )
}
