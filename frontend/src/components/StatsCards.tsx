import type { StockStats } from '../lib/stock'

export function StatsCards({ stats }: { stats: StockStats }) {
  return (
    <section className="dashboard" aria-label="Indicateurs clés">
      <div className="stat-card">
        <span className="stat-label">Références produits : </span>
        <strong className="stat-value">{stats.references}</strong>
      </div>
      <div className="stat-card">
        <span className="stat-label">Unités en stock : </span>
        <strong className="stat-value">{stats.units}</strong>
      </div>
      <div className="stat-card">
        <span className="stat-label">Stock faible : </span>
        <strong className="stat-value">{stats.lowStock}</strong>
      </div>
    </section>
  )
}
