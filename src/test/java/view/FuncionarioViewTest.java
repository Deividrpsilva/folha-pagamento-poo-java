package view;

import model.FuncionarioCLT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.FuncionarioRepository;
import service.FuncionarioService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa a camada de apresentação simulando a digitação do usuário.
 * Cada linha do texto de entrada corresponde a um "Enter".
 */
class FuncionarioViewTest {

    private FuncionarioService service;

    @BeforeEach
    void setUp() {
        service = new FuncionarioService(new FuncionarioRepository());
    }

    /** Executa a view com a entrada simulada e devolve tudo o que foi impresso. */
    private String executar(String... linhas) {
        String entrada = String.join("\n", linhas) + "\n";
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        new FuncionarioView(service, new Scanner(entrada), saida).iniciar();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Deve sair ao escolher a opção 0")
    void deveSair() {
        String saida = executar("0");
        assertTrue(saida.contains("FOLHA DE PAGAMENTO"));
        assertTrue(saida.contains("Encerrando o sistema"));
    }

    @Test
    @DisplayName("Deve encerrar sem erro quando a entrada termina")
    void deveEncerrarNoFimDaEntrada() {
        assertDoesNotThrow(() -> executar("2"));
    }

    @Test
    @DisplayName("Deve cadastrar um CLT pelo menu (aceitando vírgula decimal)")
    void deveCadastrarClt() {
        String saida = executar("1", "1", "10", "Ana Souza", "529.982.247-25", "3.000,00", "500", "0");
        assertTrue(saida.contains("Funcionário cadastrado com sucesso!"));
        assertEquals(1, service.listarTodos().size());
        assertEquals(3500.0, service.calcularTotalFolha(), 0.001);
    }

    @Test
    @DisplayName("Deve cadastrar PJ e Comissionado pelo menu")
    void deveCadastrarPjEComissionado() {
        executar("1", "2", "20", "Bruno", "11144477735", "1000", "2",
                 "1", "3", "30", "Carla", "52601815906", "2000", "5", "10000",
                 "0");
        assertEquals(2, service.listarTodos().size());
        assertEquals(4500.0, service.calcularTotalFolha(), 0.001);
    }

    @Test
    @DisplayName("Deve tratar CPF inválido sem encerrar o programa")
    void deveTratarCpfInvalido() {
        String saida = executar("1", "1", "10", "Ana", "123", "1000", "0", "0");
        assertTrue(saida.contains("Dado inválido: CPF inválido: 123"));
        assertTrue(saida.contains("Encerrando"));
        assertTrue(service.listarTodos().isEmpty());
    }

    @Test
    @DisplayName("Deve tratar código duplicado (FuncionarioJaCadastradoException)")
    void deveTratarDuplicado() throws Exception {
        service.cadastrar(new FuncionarioCLT(10, "Ana", "52998224725", 1000, 0));
        String saida = executar("1", "1", "10", "Outra", "11144477735", "1000", "0", "0");
        assertTrue(saida.contains("Erro: Já existe um funcionário cadastrado com o código 10."));
    }

    @Test
    @DisplayName("Deve tratar busca de inexistente (FuncionarioNaoEncontradoException)")
    void deveTratarNaoEncontrado() {
        String saida = executar("3", "99", "0");
        assertTrue(saida.contains("Erro: Funcionário com código 99 não encontrado."));
    }

    @Test
    @DisplayName("Deve tratar remoção de inexistente")
    void deveTratarRemocaoInexistente() {
        String saida = executar("6", "5", "0");
        assertTrue(saida.contains("Erro: Funcionário com código 5 não encontrado."));
    }

    @Test
    @DisplayName("Deve tratar número digitado incorretamente")
    void deveTratarNumeroInvalido() {
        String saida = executar("3", "abc", "0");
        assertTrue(saida.contains("Dado inválido: \"abc\" não é um número inteiro."));
    }

    @Test
    @DisplayName("Deve tratar valores negativos")
    void deveTratarNegativo() {
        String saida = executar("1", "1", "10", "Ana", "52998224725", "-100", "0", "0");
        assertTrue(saida.contains("Dado inválido: Salário-base não pode ser negativo."));
    }

    @Test
    @DisplayName("Deve listar, buscar, calcular, exibir, remover e gerar relatório")
    void fluxoCompleto() throws Exception {
        service.cadastrar(new FuncionarioCLT(1, "Ana Souza", "52998224725", 3000, 500));
        String saida = executar(
                "2",                 // listar
                "4", "52998224725",  // buscar por CPF
                "5", "ana", "n",     // buscar por nome (parcial)
                "7", "1",            // remuneração
                "8", "1",            // dados completos
                "9",                 // relatório
                "10",                // total
                "6", "1",            // remover
                "2",                 // listar vazio
                "0");
        assertTrue(saida.contains("[1] Ana Souza (CLT) - R$ 3.500,00"));
        assertTrue(saida.contains("Remuneração: R$ 3.500,00"));
        assertTrue(saida.contains("Benefícios"));
        assertTrue(saida.contains("RELATÓRIO DA FOLHA DE PAGAMENTO"));
        assertTrue(saida.contains("Total da folha: R$ 3.500,00"));
        assertTrue(saida.contains("Funcionário removido com sucesso!"));
        assertTrue(saida.contains("Nenhum funcionário cadastrado."));
    }

    @Test
    @DisplayName("Deve avisar sobre opção e tipo inválidos")
    void opcaoInvalida() {
        String saida = executar("42", "1", "9", "0");
        assertTrue(saida.contains("Opção inválida."));
        assertTrue(saida.contains("Tipo inválido."));
    }
}
