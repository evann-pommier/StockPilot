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

function App() {
    const [products, setProducts] = useState<Product[]>([])
    const [categories, setCategories] = useState<Category[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [showForm, setShowForm] = useState(false)
    const [saving, setSaving] = useState(false)
    const [editingProduct, setEditingProduct] = useState<Product | null>(null)
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
            const [productData, categoryData] = await Promise.all([
                getProducts(),
                getCategories(),
            ])
            setProducts(productData)
            setCategories(categoryData)
        } catch {
            setError(
                'Impossible de charger les données. Vérifie que Spring Boot est démarré.',
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
                                        {product.description && (
                                            <span className="product-description">
                          {product.description}
                        </span>
                                        )}
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
        </main>
    )
}

export default App
