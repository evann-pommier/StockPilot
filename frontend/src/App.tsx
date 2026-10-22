import { useMemo, useState } from 'react'
import './App.css'
import { createCategory } from './api/categoryApi'
import { createProduct, deleteProduct, updateProduct } from './api/productApi'
import { createStockMovement } from './api/stockMovementApi'
import type { Product, ProductInput } from './api/types'
import { CategoryForm } from './components/CategoryForm'
import { MovementForm, type EditableMovementType } from './components/MovementForm'
import { MovementHistory } from './components/MovementHistory'
import { ProductFilters } from './components/ProductFilters'
import { ProductForm } from './components/ProductForm'
import { ProductTable } from './components/ProductTable'
import { StatsCards } from './components/StatsCards'
import { useStockData } from './hooks/useStockData'
import { computeStats, DEFAULT_FILTERS, filterProducts } from './lib/stock'

/** Un seul panneau de saisie ouvert à la fois. */
type Panel = 'product' | 'category' | 'movement' | null

function App() {
  const { products, categories, movements, loading, error, reload } = useStockData()

  const [panel, setPanel] = useState<Panel>(null)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [filters, setFilters] = useState(DEFAULT_FILTERS)
  const [success, setSuccess] = useState('')
  const [actionError, setActionError] = useState('')

  const filteredProducts = useMemo(
    () => filterProducts(products, filters),
    [products, filters],
  )
  const stats = useMemo(() => computeStats(products), [products])

  function openPanel(next: Exclude<Panel, null>) {
    setSuccess('')
    setActionError('')
    setEditingProduct(null)
    setPanel((current) => (current === next ? null : next))
  }

  function closePanel() {
    setPanel(null)
    setEditingProduct(null)
  }

  function handleEdit(product: Product) {
    setSuccess('')
    setActionError('')
    setEditingProduct(product)
    setPanel('product')
  }

  async function handleDelete(product: Product) {
    if (!window.confirm(`Voulez-vous vraiment supprimer le produit « ${product.name} » ?`)) {
      return
    }

    setSuccess('')
    setActionError('')
    try {
      await deleteProduct(product.id)
      setSuccess(`Le produit « ${product.name} » a été supprimé.`)
      await reload()
    } catch (err) {
      setActionError(err instanceof Error ? err.message : 'Erreur lors de la suppression.')
    }
  }

  async function handleSaveProduct(input: ProductInput) {
    if (editingProduct) {
      await updateProduct(editingProduct.id, input)
      setSuccess('Produit modifié avec succès.')
    } else {
      await createProduct(input)
      setSuccess('Produit créé avec succès.')
    }
    await reload()
    closePanel()
  }

  async function handleCreateCategory(name: string) {
    await createCategory(name)
    setSuccess('Catégorie créée avec succès !')
    await reload()
    closePanel()
  }

  async function handleCreateMovement(
    productId: number,
    type: EditableMovementType,
    quantity: number,
  ) {
    await createStockMovement(productId, type, quantity)
    setSuccess(
      type === 'IN'
        ? 'Entrée de stock enregistrée avec succès.'
        : 'Sortie de stock enregistrée avec succès.',
    )
    await reload()
    closePanel()
  }

  return (
    <main className="stockpilot">
      <header className="app-header">
        <div>
          <p className="eyebrow">GESTION D'INVENTAIRE</p>
          <h1>StockPilot</h1>
          <p className="subtitle">
            Gérez vos produits et suivez votre stock en un seul endroit.
          </p>
        </div>

        <div className="header-actions">
          <button type="button" className="primary-button" onClick={() => openPanel('product')}>
            {panel === 'product' && !editingProduct ? 'Annuler' : '+ Nouveau produit'}
          </button>
          <button type="button" className="primary-button" onClick={() => openPanel('category')}>
            {panel === 'category' ? 'Annuler catégorie' : '+ Nouvelle catégorie'}
          </button>
          <button type="button" className="secondary-button" onClick={() => openPanel('movement')}>
            {panel === 'movement' ? 'Annuler le mouvement' : 'Mouvement de stock'}
          </button>
        </div>
      </header>

      {panel === 'product' && (
        <ProductForm
          key={editingProduct?.id ?? 'new'}
          categories={categories}
          product={editingProduct}
          onSubmit={handleSaveProduct}
          onCancel={closePanel}
        />
      )}
      {panel === 'category' && (
        <CategoryForm onSubmit={handleCreateCategory} onCancel={closePanel} />
      )}
      {panel === 'movement' && (
        <MovementForm products={products} onSubmit={handleCreateMovement} onCancel={closePanel} />
      )}

      {success && (
        <p className="success-message" role="status">
          {success}
        </p>
      )}
      {actionError && (
        <p className="error-message" role="alert">
          {actionError}
        </p>
      )}

      <StatsCards stats={stats} />

      <section className="products-section">
        <div className="section-heading">
          <div>
            <h2>Vos produits</h2>
            <p>Consultez les quantités disponibles et les prix.</p>
          </div>
        </div>

        <ProductFilters categories={categories} filters={filters} onChange={setFilters} />

        {loading && <p className="message">Chargement des produits...</p>}
        {error && (
          <p className="error-message" role="alert">
            {error}
          </p>
        )}

        {!loading && !error && products.length === 0 && (
          <p className="message">Aucun produit pour le moment.</p>
        )}
        {!loading && !error && products.length > 0 && filteredProducts.length === 0 && (
          <p className="message">Aucun produit ne correspond à votre recherche.</p>
        )}
        {!loading && !error && filteredProducts.length > 0 && (
          <ProductTable
            products={filteredProducts}
            onEdit={handleEdit}
            onDelete={handleDelete}
          />
        )}
      </section>

      <MovementHistory movements={movements} />
    </main>
  )
}

export default App
