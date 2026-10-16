import './App.css';

function App() {
  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon">S</div>
          <span>StockPilot</span>
        </div>

        <p className="nav-label">MENU PRINCIPAL</p>

        <nav className="navigation">
          <a className="nav-item active" href="#dashboard">
            <span>▦</span> Tableau de bord
          </a>
          <a className="nav-item" href="#products">
            <span>□</span> Produits
          </a>
          <a className="nav-item" href="#categories">
            <span>▤</span> Catégories
          </a>
          <a className="nav-item" href="#movements">
            <span>↕</span> Mouvements de stock
          </a>
        </nav>

        <div className="sidebar-footer">
          <div className="avatar">SP</div>
          <div>
            <strong>Administrateur</strong>
            <small>Gestionnaire de stock</small>
          </div>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <h1>Tableau de bord</h1>
            <p>Vue d'ensemble de votre inventaire</p>
          </div>
          <div className="status">
            <span className="status-dot" />
            Interface de démonstration
          </div>
        </header>

        <section className="welcome">
          <div>
            <p className="eyebrow">VOTRE ESPACE DE GESTION</p>
            <h2>Bienvenue sur StockPilot 👋</h2>
            <p>Suivez vos stocks et gérez vos produits depuis un seul endroit.</p>
          </div>
          <div className="welcome-icon">📦</div>
        </section>

        <section className="stats-grid">
          <article className="stat-card">
            <div className="stat-top">
              <span>Total des produits</span>
              <span className="stat-icon blue">▦</span>
            </div>
            <strong>—</strong>
            <small>En attente de connexion à l'API</small>
          </article>

          <article className="stat-card">
            <div className="stat-top">
              <span>Catégories</span>
              <span className="stat-icon purple">▤</span>
            </div>
            <strong>—</strong>
            <small>Catégories enregistrées</small>
          </article>

          <article className="stat-card">
            <div className="stat-top">
              <span>Stock faible</span>
              <span className="stat-icon orange">!</span>
            </div>
            <strong>—</strong>
            <small>Produits à réapprovisionner</small>
          </article>

          <article className="stat-card">
            <div className="stat-top">
              <span>Mouvements</span>
              <span className="stat-icon green">↕</span>
            </div>
            <strong>—</strong>
            <small>Historique des mouvements</small>
          </article>
        </section>

        <section className="content-card">
          <div className="section-heading">
            <div>
              <h2>Activité récente</h2>
              <p>Les derniers mouvements de votre inventaire</p>
            </div>
            <span className="coming-soon">Bientôt disponible</span>
          </div>

          <div className="empty-state">
            <div className="empty-icon">📋</div>
            <h3>Aucune donnée à afficher</h3>
            <p>
              Les statistiques et les mouvements apparaîtront ici
              lorsque le frontend sera connecté au backend.
            </p>
          </div>
        </section>
      </main>
    </div>
  );
}

export default App;