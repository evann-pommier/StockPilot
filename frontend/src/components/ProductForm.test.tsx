import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { makeProduct } from '../test/fixtures'
import { ProductForm } from './ProductForm'

const categories = [{ id: 1, name: 'Informatique' }]

describe('ProductForm', () => {
  it('envoie un ProductInput complet à la création', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn().mockResolvedValue(undefined)
    render(<ProductForm categories={categories} product={null} onSubmit={onSubmit} onCancel={() => {}} />)

    await user.type(screen.getByLabelText(/nom du produit/i), '  Souris ')
    await user.type(screen.getByLabelText(/sku/i), 'SOUR-001')
    await user.type(screen.getByLabelText(/prix/i), '34.5')
    await user.selectOptions(screen.getByLabelText(/catégorie/i), '1')
    await user.click(screen.getByRole('button', { name: /créer le produit/i }))

    expect(onSubmit).toHaveBeenCalledWith({
      name: 'Souris',
      sku: 'SOUR-001',
      description: '',
      price: 34.5,
      quantity: 0,
      categoryId: 1,
    })
  })

  it('verrouille la quantité en modification', () => {
    render(
      <ProductForm
        categories={categories}
        product={makeProduct({ quantity: 9 })}
        onSubmit={vi.fn()}
        onCancel={() => {}}
      />,
    )

    expect(screen.getByLabelText(/quantité en stock/i)).toBeDisabled()
    expect(screen.getByText(/mouvements/i)).toBeInTheDocument()
  })

  it('affiche le message du serveur en cas d\'échec', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn().mockRejectedValue(new Error('Un produit utilisant ce SKU existe déjà.'))
    render(
      <ProductForm
        categories={categories}
        product={makeProduct()}
        onSubmit={onSubmit}
        onCancel={() => {}}
      />,
    )

    await user.click(screen.getByRole('button', { name: /enregistrer les modifications/i }))

    expect(await screen.findByRole('alert')).toHaveTextContent('existe déjà')
  })
})
