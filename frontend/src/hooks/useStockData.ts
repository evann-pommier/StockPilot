import { useCallback, useEffect, useState } from 'react'
import { getCategories } from '../api/categoryApi'
import { getProducts } from '../api/productApi'
import { getStockMovements } from '../api/stockMovementApi'
import type { Category, Product, StockMovement } from '../api/types'

interface StockData {
  products: Product[]
  categories: Category[]
  movements: StockMovement[]
}

const EMPTY: StockData = { products: [], categories: [], movements: [] }

async function fetchAll(): Promise<StockData> {
  const [products, categories, movements] = await Promise.all([
    getProducts(),
    getCategories(),
    getStockMovements(),
  ])
  return { products, categories, movements }
}

function toMessage(err: unknown): string {
  return err instanceof Error ? err.message : 'Impossible de charger les données.'
}

/** Charge produits, catégories et mouvements, et permet de les rafraîchir après une écriture. */
export function useStockData() {
  const [data, setData] = useState<StockData>(EMPTY)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    fetchAll()
      .then((result) => {
        if (!cancelled) {
          setData(result)
          setError('')
        }
      })
      .catch((err: unknown) => {
        if (!cancelled) setError(toMessage(err))
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [])

  /** Recharge les données sans repasser par l'état « chargement » (évite le clignotement). */
  const reload = useCallback(async () => {
    try {
      setData(await fetchAll())
      setError('')
    } catch (err) {
      setError(toMessage(err))
    }
  }, [])

  return { ...data, loading, error, reload }
}
