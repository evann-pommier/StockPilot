import { useState, type SyntheticEvent } from 'react'
import type { Category, Product, ProductInput } from '../api/types'

interface Props {
  categories: Category[]
  /** Produit à modifier ; `null` pour une création. */
  product: Product | null
  onSubmit: (input: ProductInput) => Promise<void>
  onCancel: () => void
}

export function ProductForm({ categories, product, onSubmit, onCancel }: Props) {
  const editing = product !== null

  const [name, setName] = useState(product?.name ?? '')
  const [sku, setSku] = useState(product?.sku ?? '')
  const [description, setDescription] = useState(product?.description ?? '')
  const [price, setPrice] = useState(product ? String(product.price) : '')
  const [quantity, setQuantity] = useState(product ? String(product.quantity) : '0')
  const [categoryId, setCategoryId] = useState(product?.category ? String(product.category.id) : '')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')

    if (!categoryId) {
      setError('Sélectionne une catégorie.')
      return
    }

    const parsedPrice = Number(price)
    const parsedQuantity = Number(quantity)

    if (!Number.isFinite(parsedPrice) || parsedPrice < 0) {
      setError('Le prix doit être un nombre positif ou nul.')
      return
    }

    if (!Number.isInteger(parsedQuantity) || parsedQuantity < 0) {
      setError('La quantité doit être un entier positif ou nul.')
      return
    }

    setSaving(true)
    try {
      await onSubmit({
        name: name.trim(),
        sku: sku.trim(),
        description: description.trim(),
        price: parsedPrice,
        quantity: parsedQuantity,
        categoryId: Number(categoryId),
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur est survenue.')
      setSaving(false)
    }
  }

  return (
    <section className="products-section form-section">
      <h2>{editing ? 'Modifier le produit' : 'Nouveau produit'}</h2>
      <p className="form-intro">Renseigne les informations du produit ci-dessous.</p>

      <form onSubmit={handleSubmit}>
        <div className="form-grid">
          <label>
            Nom du produit *
            <input
              required
              value={name}
              onChange={(event) => setName(event.target.value)}
              placeholder="Clavier mécanique"
            />
          </label>

          <label>
            SKU *
            <input
              required
              value={sku}
              onChange={(event) => setSku(event.target.value)}
              placeholder="CLAV-002"
            />
          </label>

          <label className="full-width">
            Description
            <textarea
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              placeholder="Description du produit"
              rows={3}
            />
          </label>

          <label>
            Prix (€) *
            <input
              required
              type="number"
              min="0"
              step="0.01"
              value={price}
              onChange={(event) => setPrice(event.target.value)}
              placeholder="79.99"
            />
          </label>

          <label>
            {editing ? 'Quantité en stock' : 'Quantité initiale *'}
            <input
              required
              type="number"
              min="0"
              step="1"
              value={quantity}
              disabled={editing}
              onChange={(event) => setQuantity(event.target.value)}
            />
            {editing && (
              <small>Le stock se modifie via les mouvements (entrée / sortie).</small>
            )}
          </label>

          <label className="full-width">
            Catégorie *
            <select
              required
              value={categoryId}
              onChange={(event) => setCategoryId(event.target.value)}
            >
              <option value="">Choisir une catégorie</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
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
          <button type="submit" className="primary-button" disabled={saving}>
            {saving
              ? 'Enregistrement...'
              : editing
                ? 'Enregistrer les modifications'
                : 'Créer le produit'}
          </button>
        </div>
      </form>
    </section>
  )
}
