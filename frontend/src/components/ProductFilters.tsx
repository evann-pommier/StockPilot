import type { Category } from '../api/types'
import type { ProductFilters as Filters, StockStatus } from '../lib/stock'

interface Props {
  categories: Category[]
  filters: Filters
  onChange: (filters: Filters) => void
}

export function ProductFilters({ categories, filters, onChange }: Props) {
  return (
    <>
      <div className="search-container">
        <input
          type="search"
          aria-label="Rechercher un produit"
          placeholder="Rechercher un produit par nom, SKU ou catégorie..."
          value={filters.search}
          onChange={(event) => onChange({ ...filters, search: event.target.value })}
        />
      </div>

      <div className="filter-container">
        <label htmlFor="category-filter">Catégorie :</label>
        <select
          id="category-filter"
          value={filters.categoryId}
          onChange={(event) => onChange({ ...filters, categoryId: event.target.value })}
        >
          <option value="all">Toutes les catégories</option>
          {categories.map((category) => (
            <option key={category.id} value={String(category.id)}>
              {category.name}
            </option>
          ))}
        </select>
      </div>

      <div className="filter-container">
        <label htmlFor="stock-status-filter">État du stock :</label>
        <select
          id="stock-status-filter"
          value={filters.stockStatus}
          onChange={(event) =>
            onChange({ ...filters, stockStatus: event.target.value as StockStatus })
          }
        >
          <option value="all">Tous les stocks</option>
          <option value="available">Disponible</option>
          <option value="low">Stock faible</option>
        </select>
      </div>
    </>
  )
}
