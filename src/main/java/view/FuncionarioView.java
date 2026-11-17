package view;

import exception.DadoInvalidoException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import service.FuncionarioService;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Camada de apresentação: menu de console que interage com o usuário.
 *
 * <p>É aqui que as exceções de negócio são <b>tratadas</b> e convertidas em
 * mensagens amigáveis, sem interromper o programa. A entrada ({@link Scanner})
 * e a saída ({@link PrintStream}) são injetadas para permitir testes
 * automatizados simulando o usuário.</p>
 */
public class FuncionarioView {

    private static final Locale PT_BR = new Locale("pt", "BR");

    private final FuncionarioService service;
    private final Scanner entrada;
    private final PrintStream saida;

    public FuncionarioView(FuncionarioService service, Scanner entrada, PrintStream saida) {
        this.service = service;
        this.entrada = entrada;
        this.saida = saida;
    }

    /**
     * Exibe o menu em laço até o usuário escolher "0 - Sair"
     * (ou até a entrada terminar).
     */
    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirMenu();
            try {
                String opcao = lerLinha("Opção: ");
                executando = processarOpcao(opcao);
            } catch (NoSuchElementException fimDaEntrada) {
                executando = false;
            }
        }
        saida.println("Encerrando o sistema. Até logo!");
    }

    private void exibirMenu() {
        saida.println();
        saida.println("===== FOLHA DE PAGAMENTO =====");
        saida.println(" 1 - Cadastrar funcionário");
        saida.println(" 2 - Listar funcionários");
        saida.println(" 3 - Buscar por código");
        saida.println(" 4 - Buscar por CPF");
        saida.println(" 5 - Buscar por nome");
        saida.println(" 6 - Remover funcionário");
        saida.println(" 7 - Calcular remuneração");
        saida.println(" 8 - Exibir dados completos");
        saida.println(" 9 - Relatório da folha");
        saida.println("10 - Total da folha de pagamento");
        saida.println(" 0 - Sair");
    }

    /**
     * Executa a opção escolhida, tratando as exceções de negócio.
     *
     * @return {@code false} quando o usuário pede para sair
     */
    boolean processarOpcao(String opcao) {
        try {
            switch (opcao) {
                case "1" -> cadastrar();
                case "2" -> listar();
                case "3" -> saida.println(service.buscarFuncionario(lerInteiro("Código: ")));
                case "4" -> saida.println(service.buscarFuncionario(lerLinha("CPF: ")));
                case "5" -> buscarPorNome();
                case "6" -> remover();
                case "7" -> calcularRemuneracao();
                case "8" -> saida.print(service.exibirDados(lerInteiro("Código: ")));
                case "9" -> saida.print(service.gerarRelatorio());
                case "10" -> saida.println("Total da folha: " + moeda(service.calcularTotalFolha()));
                case "0" -> { return false; }
                default -> saida.println("Opção inválida.");
            }
        } catch (FuncionarioNaoEncontradoException | FuncionarioJaCadastradoException e) {
            saida.println("Erro: " + e.getMessage());
        } catch (DadoInvalidoException e) {
            saida.println("Dado inválido: " + e.getMessage());
        }
        return true;
    }

    private void cadastrar() throws FuncionarioJaCadastradoException {
        saida.println("Tipo: 1 - CLT | 2 - PJ | 3 - Comissionado");
        String tipo = lerLinha("Tipo: ");
        if (!tipo.matches("[123]")) {
            saida.println("Tipo inválido.");
            return;
        }
        int codigo = lerInteiro("Código: ");
        String nome = lerLinha("Nome: ");
        String cpf = lerLinha("CPF: ");

        Funcionario funcionario = switch (tipo) {
            case "1" -> new FuncionarioCLT(codigo, nome, cpf,
                    lerDecimal("Salário-base: "), lerDecimal("Benefícios: "));
            case "2" -> new FuncionarioPJ(codigo, nome, cpf,
                    lerDecimal("Valor por projeto: "), lerInteiro("Quantidade de projetos: "));
            default -> new FuncionarioComissionado(codigo, nome, cpf,
                    lerDecimal("Salário-base: "), lerDecimal("Percentual de comissão (0-100): "),
                    lerDecimal("Total de vendas: "));
        };
        service.cadastrar(funcionario);
        saida.println("Funcionário cadastrado com sucesso!");
    }

    private void listar() {
        List<Funcionario> todos = service.listarTodos();
        if (todos.isEmpty()) {
            saida.println("Nenhum funcionário cadastrado.");
            return;
        }
        todos.forEach(saida::println);
    }

    private void buscarPorNome() throws FuncionarioNaoEncontradoException {
        String nome = lerLinha("Nome: ");
        boolean exata = lerLinha("Busca exata? (s/n): ").trim().equalsIgnoreCase("s");
        service.buscarFuncionario(nome, exata).forEach(saida::println);
    }

    private void remover() throws FuncionarioNaoEncontradoException {
        service.remover(lerInteiro("Código: "));
        saida.println("Funcionário removido com sucesso!");
    }

    private void calcularRemuneracao() throws FuncionarioNaoEncontradoException {
        int codigo = lerInteiro("Código: ");
        saida.println("Remuneração: " + moeda(service.calcularRemuneracao(codigo)));
    }

    // ------------------------------------------------------------------ leitura

    private String lerLinha(String rotulo) {
        saida.print(rotulo);
        return entrada.nextLine().trim();
    }

    private int lerInteiro(String rotulo) {
        String valor = lerLinha(rotulo);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("\"" + valor + "\" não é um número inteiro.");
        }
    }

    /** Lê número decimal aceitando vírgula ou ponto (ex.: 1500,50 ou 1500.50). */
    private double lerDecimal(String rotulo) {
        String valor = lerLinha(rotulo);
        try {
            return Double.parseDouble(normalizarDecimal(valor));
        } catch (NumberFormatException e) {
            throw new DadoInvalidoException("\"" + valor + "\" não é um número válido.");
        }
    }

    private static String normalizarDecimal(String valor) {
        // "1.500,50" -> "1500.50"; "1500,50" -> "1500.50"; "1500.50" -> "1500.50"
        if (valor.contains(",")) {
            return valor.replace(".", "").replace(",", ".");
        }
        return valor;
    }

    private static String moeda(double valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }
}
