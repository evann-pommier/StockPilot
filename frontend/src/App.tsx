import {useEffect, useState} from 'react';
import './App.css'
import {
    createProduct,
    updateProduct,
    getProducts,
    type Product,
    type CreateProductData,
} from './api/productApi'
import {getCategories, type Category} from './api/categoryApi'
import * as React from "react";
import {
    createStockMovement,
    getStockMovements,
    type StockMovement,
} from './api/stockMovementApi'

function App() {
    const [products, setProducts] = useState<Product[]>([])
    const [movements, setMovements] = useState<StockMovement[]>([])
    const [movementHistoryError, setMovementHistoryError] = useState('')
    const [categories, setCategories] = useState<Category[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [showForm, setShowForm] = useState(false)
    const [saving, setSaving] = useState(false)
    const [editingProduct, setEditingProduct] = useState<Product | null>(null)
    const [showMovementForm, setShowMovementForm] = useState(false)
    const [movementProductId, setMovementProductId] = useState('')
    const [movementType, setMovementType] = useState<'IN' | 'OUT'>('IN')
    const [movementQuantity, setMovementQuantity] = useState('1')
    const [movementError, setMovementError] = useState('')
    const [movementSuccess, setMovementSuccess] = useState('')
    const [savingMovement, setSavingMovement] = useState(false)
    const [formError, setFormError] = useState('')
    const [success, setSuccess] = useState('')

    const [name, setName] = useState('')
    const [sku, setSku] = useState('')
    const [description, setDescription] = useState('')
    const [price, setPrice] = useState('')
    const [quantity, setQuantity] = useState('0')
    const [categoryId, setCategoryId] = useState('')

    async function loadData() {
        setLoading(true)
        setError('')

        try {
            const [productsData, categoriesData, movementsData] =
                await Promise.all([
                    getProducts(),
                    getCategories(),
                    getStockMovements(),
                ])

            setProducts(productsData)
            setCategories(categoriesData)
            setMovements(movementsData)
            setMovementHistoryError('')
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : 'Impossible de charger les données.'
            )
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        void loadData()
    }, [])

    function resetForm() {
        setName('')
        setSku('')
        setDescription('')
        setPrice('')
        setQuantity('0')
        setCategoryId('')
        setFormError('')
    }

    function handleEditProduct(product: Product) {
        setEditingProduct(product)
        setName(product.name)
        setSku(product.sku)
        setDescription(product.description ?? '')
        setPrice(String(product.price))
        setQuantity(String(product.quantity))
        setCategoryId(product.category ? String(product.category.id) : '')
        setFormError('')
        setSuccess('')
        setShowForm(true)
    }

    async function handleCreateMovement(
        event: Parameters<
            NonNullable<React.ComponentProps<'form'>['onSubmit']>
        >[0],
    ) {
        event.preventDefault()
        setMovementError('')
        setMovementSuccess('')

        if (!movementProductId) {
            setMovementError('Sélectionne un produit.')
            return
        }

        const parsedQuantity = Number(movementQuantity)

        if (!Number.isInteger(parsedQuantity) || parsedQuantity <= 0) {
            setMovementError('La quantité doit être un entier supérieur à zéro.')
            return
        }

        const product = products.find(
            (item) => item.id === Number(movementProductId),
        )

        if (!product) {
            setMovementError('Le produit sélectionné est introuvable.')
            return
        }

        if (movementType === 'OUT' && parsedQuantity > product.quantity) {
            setMovementError(
                `Stock insuffisant : ${product.quantity} unité(s) disponible(s).`,
            )
            return
        }

        setSavingMovement(true)

        try {
            await createStockMovement(
                product.id,
                movementType,
                parsedQuantity,
            )

            await loadData()

            setMovementSuccess(
                movementType === 'IN'
                    ? 'Entrée de stock enregistrée avec succès.'
                    : 'Sortie de stock enregistrée avec succès.',
            )

            setMovementQuantity('1')
            setShowMovementForm(false)
        } catch (err) {
            setMovementError(
                err instanceof Error
                    ? err.message
                    : 'Une erreur est survenue lors du mouvement de stock.',
            )
        } finally {
            setSavingMovement(false)
        }
    }


    async function handleCreateProduct(
        event: Parameters<
            NonNullable<React.ComponentProps<'form'>['onSubmit']>
        >[0],
    ) {
        event.preventDefault()
        setFormError('')
        setSuccess('')

        if (!categoryId) {
            setFormError('Sélectionne une catégorie.')
            return
        }

        const parsedPrice = Number(price)
        const parsedQuantity = Number(quantity)

        if (!Number.isFinite(parsedPrice) || parsedPrice < 0) {
            setFormError('Le prix doit être un nombre positif ou nul.')
            return
        }

        if (!Number.isInteger(parsedQuantity) || parsedQuantity < 0) {
            setFormError('La quantité doit être un entier positif ou nul.')
            return
        }

        const productData: CreateProductData = {
            name: name.trim(),
            sku: sku.trim(),
            description: description.trim(),
            price: parsedPrice,
            quantity: parsedQuantity,
            category: {id: Number(categoryId)},
        }

        setSaving(true)

        try {
            if (editingProduct) {
                await updateProduct(editingProduct.id, productData)
                setSuccess('Produit modifié avec succès.')
            } else {
                await createProduct(productData)
                setSuccess('Produit créé avec succès.')
            }

            await loadData()
            resetForm()
            setEditingProduct(null)
            setShowForm(false)
        } catch (err) {
            setFormError(
                err instanceof Error
                    ? err.message
                    : 'Une erreur est survenue lors de la création.',
            )
        } finally {
            setSaving(false)
        }
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

                <button
                    className="primary-button"
                    onClick={() => {
                        setShowForm(!showForm)
                        setFormError('')
                        setSuccess('')
                    }}
                >
                    {showForm ? 'Annuler' : '+ Nouveau produit'}
                </button>

                <button
                    type="button"
                    className="secondary-button"
                    onClick={() => {
                        setShowMovementForm(!showMovementForm)
                        setMovementError('')
                        setMovementSuccess('')
                    }}
                >
                    {showMovementForm ? 'Annuler le mouvement' : 'Mouvement de stock'}
                </button>

            </header>

            {showForm && (
                <section className="products-section form-section">
                    <h2>{editingProduct ? 'Modifier le produit' : 'Nouveau produit'}</h2>
                    <p className="form-intro">
                        Renseigne les informations du produit ci-dessous.
                    </p>

                    <form onSubmit={handleCreateProduct}>
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
                                Quantité initiale *
                                <input
                                    required
                                    type="number"
                                    min="0"
                                    step="1"
                                    value={quantity}
                                    onChange={(event) => setQuantity(event.target.value)}
                                />
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

                        {formError && <p className="error-message">{formError}</p>}

                        <div className="form-actions">
                            <button
                                type="button"
                                className="secondary-button"
                                onClick={() => {
                                    setShowForm(false)
                                    setEditingProduct(null)
                                    resetForm()
                                }}
                            >
                                Annuler
                            </button>
                            <button
                                type="submit"
                                className="primary-button"
                                disabled={saving}
                            >
                                {saving
                                    ? 'Enregistrement...'
                                    : editingProduct
                                        ? 'Enregistrer les modifications'
                                        : 'Créer le produit'}
                            </button>
                        </div>
                    </form>
                </section>
            )}


            {showMovementForm && (
                <section className="products-section form-section">
                    <h2>Mouvement de stock</h2>
                    <p className="form-intro">
                        Enregistre une entrée ou une sortie de stock.
                    </p>

                    <form onSubmit={handleCreateMovement}>
                        <div className="form-grid">
                            <label className="full-width">
                                Produit *
                                <select
                                    required
                                    value={movementProductId}
                                    onChange={(event) =>
                                        setMovementProductId(event.target.value)
                                    }
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
                                    value={movementType}
                                    onChange={(event) =>
                                        setMovementType(event.target.value as 'IN' | 'OUT')
                                    }
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
                                    value={movementQuantity}
                                    onChange={(event) =>
                                        setMovementQuantity(event.target.value)
                                    }
                                />
                            </label>
                        </div>

                        {movementError && (
                            <p className="error-message">{movementError}</p>
                        )}

                        {movementSuccess && (
                            <p className="success-message">{movementSuccess}</p>
                        )}

                        <div className="form-actions">
                            <button
                                type="button"
                                className="secondary-button"
                                onClick={() => {
                                    setShowMovementForm(false)
                                    setMovementError('')
                                }}
                            >
                                Annuler
                            </button>

                            <button
                                type="submit"
                                className="primary-button"
                                disabled={savingMovement || products.length === 0}
                            >
                                {savingMovement
                                    ? 'Enregistrement...'
                                    : 'Enregistrer le mouvement'}
                            </button>
                        </div>
                    </form>
                </section>
            )}

            {success && <p className="success-message">{success}</p>}

            <section className="dashboard">
                <div className="stat-card">
                    <span className="stat-label">Références produits</span>
                    <strong className="stat-value">{products.length}</strong>
                </div>

                <div className="stat-card">
                    <span className="stat-label">Unités en stock</span>
                    <strong className="stat-value">
                        {products.reduce((total, product) => total + product.quantity, 0)}
                    </strong>
                </div>

                <div className="stat-card">
                    <span className="stat-label">Stock faible</span>
                    <strong className="stat-value">
                        {products.filter((product) => product.quantity <= 5).length}
                    </strong>
                </div>
            </section>

            <section className="products-section">
                <div className="section-heading">
                    <div>
                        <h2>Vos produits</h2>
                        <p>Consultez les quantités disponibles et les prix.</p>
                    </div>
                </div>

                {loading && <p className="message">Chargement des produits...</p>}
                {error && <p className="error-message">{error}</p>}

                {!loading && !error && products.length === 0 && (
                    <p className="message">Aucun produit pour le moment.</p>
                )}

                {!loading && !error && products.length > 0 && (
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
                            {products.map((product) => (
                                <tr key={product.id}>
                                    <td>
                                        <strong>{product.name}</strong>
                                    </td>
                                    <td>
                                        {product.description || '—'}
                                    </td>
                                    <td>{product.sku}</td>
                                    <td>{product.category?.name ?? 'Sans catégorie'}</td>
                                    <td>
                                        {Number(product.price).toLocaleString('fr-FR', {
                                            style: 'currency',
                                            currency: 'EUR',
                                        })}
                                    </td>
                                    <td>{product.quantity}</td>
                                    <td>
                                        <span
                                            className={`stock-badge ${
                                                product.quantity <= 5 ? 'stock-low' : 'stock-normal'
                                            }`}
                                        >
                                            {product.quantity <= 5 ? 'Stock faible' : 'Disponible'}
                                        </span>
                                    </td>
                                    <td>
                                        <button
                                            type="button"
                                            className="secondary-button"
                                            onClick={() => handleEditProduct(product)}
                                        >
                                            Modifier
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            <section className="dashboard-section">
                <h2>Historique des mouvements</h2>

                {movementHistoryError && (
                    <p className="error-message">{movementHistoryError}</p>
                )}

                {!movementHistoryError && movements.length === 0 && (
                    <p>Aucun mouvement de stock enregistré pour le moment.</p>
                )}

                {movements.length > 0 && (
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
                            {[...movements].reverse().map((movement) => (
                                <tr key={movement.id}>
                                    <td>{movement.id}</td>
                                    <td>
                                        {new Date(movement.createdAt).toLocaleString('fr-FR', {
                                            dateStyle: 'short',
                                            timeStyle: 'short',
                                        })}
                                    </td>
                                    <td>
                                        {movement.product?.name ?? 'Produit inconnu'}
                                    </td>
                                    <td>
                                            <span
                                                className={`stock-badge ${
                                                    movement.type === 'IN'
                                                        ? 'stock-normal'
                                                        : 'stock-low'
                                                }`}
                                            >
                                                {movement.type === 'IN'
                                                    ? 'Entrée'
                                                    : movement.type === 'OUT'
                                                        ? 'Sortie'
                                                        : 'Ajustement'}
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
        </main>
    )
}

export default App
