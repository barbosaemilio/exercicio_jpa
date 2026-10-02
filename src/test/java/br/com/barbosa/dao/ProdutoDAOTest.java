package br.com.barbosa.dao;

import br.com.barbosa.domain.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoDAOTest {

    private static EntityManagerFactory emf;

    private EntityManager em;
    private ProdutoDAO dao;

    @BeforeAll
    static void iniciarFabrica() {
        emf = Persistence.createEntityManagerFactory("ProdutoTestPU");
    }

    @AfterAll
    static void fecharFabrica() {
        emf.close();
    }

    @BeforeEach
    void prepararCenario() {
        em = emf.createEntityManager();
        dao = new ProdutoDAO(em);

        // garante que cada teste começa com a tabela vazia
        em.getTransaction().begin();
        em.createQuery("delete from Produto").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void encerrarCenario() {
        em.close();
    }

    @Test
    void deveSalvarProdutoEGerarId() {
        Produto produto = new Produto("Mouse Gamer", new BigDecimal("89.90"));

        Produto salvo = dao.salvar(produto);

        assertNotNull(salvo.getId());
    }

    @Test
    void deveBuscarProdutoPorId() {
        Produto salvo = dao.salvar(new Produto("Monitor 24", new BigDecimal("899.00")));
        em.clear(); // força a busca no banco, e não no cache do EntityManager

        Produto encontrado = dao.buscarPorId(salvo.getId());

        assertNotNull(encontrado);
        assertEquals("Monitor 24", encontrado.getNome());
        assertEquals(0, new BigDecimal("899.00").compareTo(encontrado.getPreco()));
    }

    @Test
    void deveRetornarNuloQuandoIdNaoExiste() {
        assertNull(dao.buscarPorId(999999L));
    }

    @Test
    void deveAtualizarProduto() {
        Produto salvo = dao.salvar(new Produto("Headset", new BigDecimal("150.00")));

        salvo.setNome("Headset Pro");
        salvo.setPreco(new BigDecimal("199.90"));
        dao.atualizar(salvo);
        em.clear();

        Produto atualizado = dao.buscarPorId(salvo.getId());
        assertEquals("Headset Pro", atualizado.getNome());
        assertEquals(0, new BigDecimal("199.90").compareTo(atualizado.getPreco()));
    }

    @Test
    void deveRemoverProduto() {
        Produto salvo = dao.salvar(new Produto("Webcam", new BigDecimal("120.00")));

        dao.remover(salvo.getId());
        em.clear();

        assertNull(dao.buscarPorId(salvo.getId()));
    }

    @Test
    void deveListarTodosOsProdutos() {
        dao.salvar(new Produto("Produto A", new BigDecimal("10.00")));
        dao.salvar(new Produto("Produto B", new BigDecimal("20.00")));

        List<Produto> produtos = dao.listarTodos();

        assertEquals(2, produtos.size());
    }

    @Test
    void naoDeveSalvarProdutoSemNome() {
        Produto semNome = new Produto(null, new BigDecimal("50.00"));

        assertThrows(Exception.class, () -> dao.salvar(semNome));
    }
}