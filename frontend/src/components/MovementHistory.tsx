import type { StockMovement } from '../api/types'
import { formatDateTime, MOVEMENT_LABELS } from '../lib/format'

/** Historique affiché dans l'ordre reçu de l'API : du plus récent au plus ancien. */
export function MovementHistory({ movements }: { movements: StockMovement[] }) {
  return (
    <section className="dashboard-section">
      <h2>Historique des mouvements</h2>

      {movements.length === 0 ? (
        <p>Aucun mouvement de stock enregistré pour le moment.</p>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Date et heure</th>
                <th>Produit</th>
                <th>Type</th>
                <th>Quantité</th>
              </tr>
            </thead>
            <tbody>
              {movements.map((movement) => (
                <tr key={movement.id}>
                  <td>{movement.id}</td>
                  <td>{formatDateTime(movement.createdAt)}</td>
                  <td>{movement.product.name}</td>
                  <td>
                    <span
                      className={`stock-badge ${movement.type === 'IN' ? 'stock-normal' : 'stock-low'}`}
                    >
                      {MOVEMENT_LABELS[movement.type]}
                    </span>
                  </td>
                  <td>{movement.quantity}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
