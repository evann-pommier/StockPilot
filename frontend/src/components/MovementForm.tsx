import { useState, type SyntheticEvent } from 'react'
import type { MovementType, Product } from '../api/types'

export type EditableMovementType = Exclude<MovementType, 'ADJUSTMENT'>

interface Props {
  products: Product[]
  onSubmit: (productId: number, type: EditableMovementType, quantity: number) => Promise<void>
  onCancel: () => void
}

export function MovementForm({ products, onSubmit, onCancel }: Props) {
  const [productId, setProductId] = useState('')
  const [type, setType] = useState<EditableMovementType>('IN')
  const [quantity, setQuantity] = useState('1')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    const product = products.find((item) => item.id === Number(productId))
    if (!product) {
      setError('Sélectionne un produit.')
      return
    }

    const parsedQuantity = Number(quantity)
    if (!Number.isInteger(parsedQuantity) || parsedQuantity <= 0) {
      setError('La quantité doit être un entier supérieur à zéro.')
      return
    }

    if (type === 'OUT' && parsedQuantity > product.quantity) {
      setError(`Stock insuffisant : ${product.quantity} unité(s) disponible(s).`)
      return
    }

    setSaving(true)
    try {
      await onSubmit(product.id, type, parsedQuantity)
    } catch (err) {
      setError(
        err instanceof Error ? err.message : 'Une erreur est survenue lors du mouvement de stock.',
      )
      setSaving(false)
    }
  }

  return (
    <section className="products-section form-section">
      <h2>Mouvement de stock</h2>
      <p className="form-intro">Enregistre une entrée ou une sortie de stock.</p>

      <form onSubmit={handleSubmit}>
        <div className="form-grid">
          <label className="full-width">
            Produit *
            <select
              required
              value={productId}
              onChange={(event) => setProductId(event.target.value)}
            >
              <option value="">Choisir un produit</option>
              {products.map((product) => (
                <option key={product.id} value={product.id}>
                  {product.name} — Stock : {product.quantity}
                </option>
              ))}
            </select>
          </label>

          <label>
            Type de mouvement *
            <select
              value={type}
              onChange={(event) => setType(event.target.value as EditableMovementType)}
            >
              <option value="IN">Entrée de stock</option>
              <option value="OUT">Sortie de stock</option>
            </select>
          </label>

          <label>
            Quantité *
            <input
              required
              type="number"
              min="1"
              step="1"
              value={quantity}
              onChange={(event) => setQuantity(event.target.value)}
            />
          </label>
        </div>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        <div className="form-actions">
          <button type="button" className="secondary-button" onClick={onCancel}>
            Annuler
          </button>
          <button
            type="submit"
            className="primary-button"
            disabled={saving || products.length === 0}
          >
            {saving ? 'Enregistrement...' : 'Enregistrer le mouvement'}
          </button>
        </div>
      </form>
    </section>
  )
}
