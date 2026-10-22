import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import { makeProduct } from '../test/fixtures'
import { MovementForm } from './MovementForm'

const products = [makeProduct({ id: 1, name: 'Clavier', quantity: 3 })]

async function fillAndSubmit(type: 'IN' | 'OUT', quantity: string) {
  const user = userEvent.setup()
  await user.selectOptions(screen.getByLabelText(/produit/i), '1')
  await user.selectOptions(screen.getByLabelText(/type de mouvement/i), type)
  const input = screen.getByLabelText(/quantité/i)
  await user.clear(input)
  await user.type(input, quantity)
  await user.click(screen.getByRole('button', { name: /enregistrer le mouvement/i }))
}

describe('MovementForm', () => {
  it('refuse une sortie supérieure au stock sans appeler l\'API', async () => {
    const onSubmit = vi.fn()
    render(<MovementForm products={products} onSubmit={onSubmit} onCancel={() => {}} />)

    await fillAndSubmit('OUT', '10')

    expect(await screen.findByRole('alert')).toHaveTextContent('Stock insuffisant : 3 unité(s)')
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('soumet une entrée valide', async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined)
    render(<MovementForm products={products} onSubmit={onSubmit} onCancel={() => {}} />)

    await fillAndSubmit('IN', '7')

    expect(onSubmit).toHaveBeenCalledWith(1, 'IN', 7)
  })

  it('affiche l\'erreur renvoyée par le serveur', async () => {
    const onSubmit = vi.fn().mockRejectedValue(new Error('Produit introuvable.'))
    render(<MovementForm products={products} onSubmit={onSubmit} onCancel={() => {}} />)

    await fillAndSubmit('IN', '2')

    expect(await screen.findByRole('alert')).toHaveTextContent('Produit introuvable.')
  })
})
