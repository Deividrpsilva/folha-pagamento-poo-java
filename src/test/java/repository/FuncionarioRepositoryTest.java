package repository;

import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioRepositoryTest {

    private FuncionarioRepository repository;
    private Funcionario ana;
    private Funcionario bruno;

    @BeforeEach
    void setUp() {
        repository = new FuncionarioRepository();
        ana = new FuncionarioCLT(1, "Ana", "52998224725", 3000, 500);
        bruno = new FuncionarioPJ(2, "Bruno", "11144477735", 2000, 2);
    }

    @Test
    @DisplayName("Repositório novo deve estar vazio")
    void repositorioNovoVazio() {
        assertEquals(0, repository.quantidade());
        assertTrue(repository.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("Deve salvar e recuperar funcionário pelo código")
    void deveSalvarERecuperar() {
        repository.salvar(ana);
        Optional<Funcionario> encontrado = repository.buscarPorCodigo(1);
        assertTrue(encontrado.isPresent());
        assertSame(ana, encontrado.get());
    }

    @Test
    @DisplayName("Busca por código inexistente deve retornar Optional vazio")
    void buscaInexistente() {
        assertTrue(repository.buscarPorCodigo(99).isEmpty());
    }

    @Test
    @DisplayName("Deve listar na ordem de cadastro")
    void deveListarNaOrdem() {
        repository.salvar(bruno);
        repository.salvar(ana);
        assertEquals(List.of(bruno, ana), repository.listarTodos());
    }

    @Test
    @DisplayName("A lista retornada não deve expor o armazenamento interno")
    void listaNaoExpoeInterno() {
        repository.salvar(ana);
        repository.listarTodos().clear();
        assertEquals(1, repository.quantidade());
    }

    @Test
    @DisplayName("Deve informar se um código existe")
    void deveVerificarExistencia() {
        repository.salvar(ana);
        assertTrue(repository.existe(1));
        assertFalse(repository.existe(2));
    }

    @Test
    @DisplayName("Deve remover funcionário existente e retornar true")
    void deveRemover() {
        repository.salvar(ana);
        assertTrue(repository.remover(1));
        assertFalse(repository.existe(1));
        assertEquals(0, repository.quantidade());
    }

    @Test
    @DisplayName("Remover inexistente deve retornar false")
    void removerInexistente() {
        assertFalse(repository.remover(42));
    }

    @Test
    @DisplayName("Não deve aceitar funcionário nulo")
    void naoAceitaNulo() {
        assertThrows(NullPointerException.class, () -> repository.salvar(null));
    }
}
