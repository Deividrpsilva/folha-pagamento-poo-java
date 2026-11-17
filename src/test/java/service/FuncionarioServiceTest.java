package service;

import exception.DadoInvalidoException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import repository.FuncionarioRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioServiceTest {

    private FuncionarioService service;
    private FuncionarioCLT ana;
    private FuncionarioPJ bruno;
    private FuncionarioComissionado carla;

    @BeforeEach
    void setUp() {
        service = new FuncionarioService(new FuncionarioRepository());
        ana = new FuncionarioCLT(1, "Ana Souza", "52998224725", 3000, 500);          // 3.500 bruto, 330 tributos
        bruno = new FuncionarioPJ(2, "Bruno Lima", "11144477735", 2000, 3);            // 6.000 bruto, 0 tributos
        carla = new FuncionarioComissionado(3, "Carla Souza", "52601815906", 2000, 5, 20000); // 3.000 bruto, 330 tributos
    }

    private void cadastrarTodos() throws FuncionarioJaCadastradoException {
        service.cadastrar(ana);
        service.cadastrar(bruno);
        service.cadastrar(carla);
    }

    @Test
    @DisplayName("Não deve aceitar repositório nulo")
    void repositorioNulo() {
        assertThrows(NullPointerException.class, () -> new FuncionarioService(null));
    }

    @Nested
    @DisplayName("Cadastro")
    class Cadastro {

        @Test
        @DisplayName("Deve cadastrar funcionário")
        void deveCadastrar() throws Exception {
            service.cadastrar(ana);
            assertEquals(List.of(ana), service.listarTodos());
        }

        @Test
        @DisplayName("Deve lançar FuncionarioJaCadastradoException para código duplicado")
        void codigoDuplicado() throws Exception {
            service.cadastrar(ana);
            Funcionario outro = new FuncionarioPJ(1, "Outro", "08301661305", 100, 1);
            FuncionarioJaCadastradoException e = assertThrows(FuncionarioJaCadastradoException.class,
                    () -> service.cadastrar(outro));
            assertTrue(e.getMessage().contains("código 1"));
            assertEquals(1, service.listarTodos().size());
        }

        @Test
        @DisplayName("Deve lançar FuncionarioJaCadastradoException para CPF duplicado")
        void cpfDuplicado() throws Exception {
            service.cadastrar(ana);
            Funcionario mesmoCpf = new FuncionarioPJ(99, "Outro", "529.982.247-25", 100, 1);
            FuncionarioJaCadastradoException e = assertThrows(FuncionarioJaCadastradoException.class,
                    () -> service.cadastrar(mesmoCpf));
            assertTrue(e.getMessage().contains("529.982.247-25"));
        }

        @Test
        @DisplayName("Deve rejeitar funcionário nulo")
        void nulo() {
            assertThrows(DadoInvalidoException.class, () -> service.cadastrar(null));
        }
    }

    @Nested
    @DisplayName("Consulta (sobrecarga de buscarFuncionario)")
    class Consulta {

        @BeforeEach
        void popular() throws Exception {
            cadastrarTodos();
        }

        @Test
        @DisplayName("Deve listar todos na ordem de cadastro")
        void listarTodos() {
            assertEquals(List.of(ana, bruno, carla), service.listarTodos());
        }

        @Test
        @DisplayName("buscarFuncionario(int): deve encontrar pelo código")
        void buscarPorCodigo() throws Exception {
            assertSame(bruno, service.buscarFuncionario(2));
        }

        @Test
        @DisplayName("buscarFuncionario(int): deve lançar exceção para código inexistente")
        void buscarPorCodigoInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario(99));
        }

        @Test
        @DisplayName("buscarFuncionario(String): deve encontrar pelo CPF com ou sem máscara")
        void buscarPorCpf() throws Exception {
            assertSame(ana, service.buscarFuncionario("52998224725"));
            assertSame(ana, service.buscarFuncionario("529.982.247-25"));
        }

        @Test
        @DisplayName("buscarFuncionario(String): deve lançar exceção para CPF não cadastrado")
        void buscarPorCpfInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario("08301661305"));
        }

        @Test
        @DisplayName("buscarFuncionario(String): deve rejeitar CPF vazio")
        void buscarPorCpfVazio() {
            assertThrows(DadoInvalidoException.class, () -> service.buscarFuncionario(" "));
        }

        @Test
        @DisplayName("buscarFuncionario(String, true): busca exata ignorando maiúsculas e acentos")
        void buscarPorNomeExato() throws Exception {
            assertEquals(List.of(ana), service.buscarFuncionario("ana souza", true));
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario("Souza", true));
        }

        @Test
        @DisplayName("buscarFuncionario(String, false): busca parcial pode retornar vários")
        void buscarPorNomeParcial() throws Exception {
            assertEquals(List.of(ana, carla), service.buscarFuncionario("souza", false));
        }

        @Test
        @DisplayName("buscarFuncionario(String, boolean): deve ignorar acentos")
        void buscarIgnorandoAcentos() throws Exception {
            service.cadastrar(new FuncionarioCLT(4, "João Conceição", "18609139034", 1000, 0));
            assertEquals(1, service.buscarFuncionario("joao conceicao", true).size());
        }

        @Test
        @DisplayName("buscarFuncionario(String, boolean): deve lançar exceção se ninguém for encontrado")
        void buscarPorNomeInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario("Zé", false));
        }

        @Test
        @DisplayName("buscarFuncionario(String, boolean): deve rejeitar nome vazio")
        void buscarPorNomeVazio() {
            assertThrows(DadoInvalidoException.class, () -> service.buscarFuncionario("", false));
        }

        @Test
        @DisplayName("exibirDados: deve retornar os dados completos do funcionário")
        void exibirDados() throws Exception {
            String dados = service.exibirDados(3);
            assertTrue(dados.contains("Carla Souza"));
            assertTrue(dados.contains("Comissionado"));
        }

        @Test
        @DisplayName("exibirDados: deve lançar exceção para inexistente")
        void exibirDadosInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.exibirDados(50));
        }
    }

    @Nested
    @DisplayName("Remoção")
    class Remocao {

        @Test
        @DisplayName("Deve remover funcionário existente")
        void deveRemover() throws Exception {
            cadastrarTodos();
            service.remover(2);
            assertEquals(List.of(ana, carla), service.listarTodos());
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.buscarFuncionario(2));
        }

        @Test
        @DisplayName("Deve lançar FuncionarioNaoEncontradoException ao remover inexistente")
        void removerInexistente() {
            FuncionarioNaoEncontradoException e = assertThrows(FuncionarioNaoEncontradoException.class,
                    () -> service.remover(77));
            assertTrue(e.getMessage().contains("77"));
        }

        @Test
        @DisplayName("Após remover, deve ser possível recadastrar o mesmo código")
        void recadastrarAposRemover() throws Exception {
            service.cadastrar(ana);
            service.remover(1);
            assertDoesNotThrow(() -> service.cadastrar(ana));
        }
    }

    @Nested
    @DisplayName("Cálculos e relatório")
    class Calculos {

        @Test
        @DisplayName("calcularRemuneracao(codigo) deve usar a regra de cada categoria (polimorfismo)")
        void remuneracaoPolimorfica() throws Exception {
            cadastrarTodos();
            assertEquals(3500.0, service.calcularRemuneracao(1), 0.001);
            assertEquals(6000.0, service.calcularRemuneracao(2), 0.001);
            assertEquals(3000.0, service.calcularRemuneracao(3), 0.001);
        }

        @Test
        @DisplayName("calcularRemuneracao deve lançar exceção para inexistente")
        void remuneracaoInexistente() {
            assertThrows(FuncionarioNaoEncontradoException.class, () -> service.calcularRemuneracao(9));
        }

        @Test
        @DisplayName("Total da folha = soma das remunerações brutas")
        void totalFolha() throws Exception {
            cadastrarTodos();
            assertEquals(12500.0, service.calcularTotalFolha(), 0.001);
        }

        @Test
        @DisplayName("Total da folha sem funcionários deve ser zero")
        void totalFolhaVazia() {
            assertEquals(0.0, service.calcularTotalFolha());
        }

        @Test
        @DisplayName("Total de tributos considera apenas funcionários Tributavel")
        void totalTributos() throws Exception {
            cadastrarTodos();
            assertEquals(660.0, service.calcularTotalTributos(), 0.001);
        }

        @Test
        @DisplayName("Relatório deve conter todos os funcionários e os totais")
        void relatorio() throws Exception {
            cadastrarTodos();
            String relatorio = service.gerarRelatorio();
            assertTrue(relatorio.contains("Ana Souza"));
            assertTrue(relatorio.contains("Bruno Lima"));
            assertTrue(relatorio.contains("Carla Souza"));
            assertTrue(relatorio.contains("R$ 12.500,00"));
            assertTrue(relatorio.contains("R$ 660,00"));
            assertTrue(relatorio.contains("R$ 11.840,00"));
            assertTrue(relatorio.contains("3 funcionário(s)"));
        }

        @Test
        @DisplayName("Relatório vazio deve avisar que não há funcionários")
        void relatorioVazio() {
            assertTrue(service.gerarRelatorio().contains("Nenhum funcionário cadastrado."));
        }
    }
}
