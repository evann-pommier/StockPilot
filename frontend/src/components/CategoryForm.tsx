import { useState, type SyntheticEvent } from 'react'

interface Props {
  onSubmit: (name: string) => Promise<void>
  onCancel: () => void
}

export function CategoryForm({ onSubmit, onCancel }: Props) {
  const [name, setName] = useState('')
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  async function handleSubmit(event: SyntheticEvent<HTMLFormElement>) {
    event.preventDefault()
    const trimmed = name.trim()

    if (!trimmed) {
      setError('Le nom de la catégorie est obligatoire.')
      return
    }

    setSaving(true)
    setError('')
    try {
      await onSubmit(trimmed)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur lors de la création de la catégorie.')
      setSaving(false)
    }
  }

  return (
    <section className="form-section">
      <h2>Nouvelle catégorie</h2>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="new-category-name">Nom de la catégorie</label>
          <input
            id="new-category-name"
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="Ex. : Informatique"
            required
          />
        </div>

        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        <div className="form-actions">
          <button type="submit" className="primary-button" disabled={saving}>
            {saving ? 'Création...' : 'Créer la catégorie'}
          </button>
          <button type="button" className="secondary-button" onClick={onCancel}>
            Annuler
          </button>
        </div>
      </form>
    </section>
  )
}
