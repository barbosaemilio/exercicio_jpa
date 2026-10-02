package br.com.barbosa.dao;

import br.com.barbosa.domain.Produto;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ProdutoDAO {

    private final EntityManager em;

    public ProdutoDAO(EntityManager em) {
        this.em = em;
    }

    public Produto salvar(Produto produto) {
        try {
            em.getTransaction().begin();
            em.persist(produto);
            em.getTransaction().commit();
            return produto;
        } catch (Exception e) {
            rollbackSeNecessario();
            throw e;
        }
    }

    public Produto buscarPorId(Long id) {
        return em.find(Produto.class, id);
    }

    public List<Produto> listarTodos() {
        return em.createQuery("select p from Produto p order by p.id", Produto.class)
                .getResultList();
    }

    public Produto atualizar(Produto produto) {
        try {
            em.getTransaction().begin();
            Produto atualizado = em.merge(produto);
            em.getTransaction().commit();
            return atualizado;
        } catch (Exception e) {
            rollbackSeNecessario();
            throw e;
        }
    }

    public void remover(Long id) {
        try {
            em.getTransaction().begin();
            Produto produto = em.find(Produto.class, id);
            if (produto != null) {
                em.remove(produto);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            rollbackSeNecessario();
            throw e;
        }
    }

    private void rollbackSeNecessario() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}