# StockPilot : frontend

React 19 + TypeScript + Vite. Voir le [README principal](../README.md) pour l'installation complète.

```bash
npm install
npm run dev      # http://localhost:5173, /api est proxifié vers http://localhost:8080
npm run lint     # oxlint
npm test         # Vitest + Testing Library
npm run build    # typecheck + build de production
```

Organisation de `src/` :

| Dossier | Contenu |
|---|---|
| `api/` | Client HTTP typé (`http.ts` traduit les erreurs `problem+json` en messages lisibles) |
| `lib/` | Logique pure et testée : filtrage, statistiques, formatage |
| `hooks/` | `useStockData` : chargement et rafraîchissement des données |
| `components/` | Composants de présentation et formulaires |
