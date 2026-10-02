package br.com.barbosa;

import br.com.barbosa.dao.ProdutoDAO;
import br.com.barbosa.domain.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ProdutoPU");
        EntityManager em = emf.createEntityManager();

        try {
            ProdutoDAO dao = new ProdutoDAO(em);

            Produto produto = dao.salvar(new Produto("Teclado Mecânico", new BigDecimal("249.90")));
            System.out.println("Salvo: " + produto);

            produto.setPreco(new BigDecimal("199.90"));
            dao.atualizar(produto);
            System.out.println("Atualizado: " + dao.buscarPorId(produto.getId()));

            System.out.println("Todos: " + dao.listarTodos());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}