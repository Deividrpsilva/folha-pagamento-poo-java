package service;

import exception.DadoInvalidoException;
import exception.FuncionarioJaCadastradoException;
import exception.FuncionarioNaoEncontradoException;
import model.Funcionario;
import model.Tributavel;
import repository.FuncionarioRepository;
import util.ValidadorCpf;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Camada de serviço: aplica as regras de negócio e coordena as operações
 * sobre os funcionários, delegando o armazenamento ao repositório.
 */
public class FuncionarioService {

    private static final Locale PT_BR = new Locale("pt", "BR");

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repositório não pode ser nulo.");
    }

    // ------------------------------------------------------------------ cadastro

    /**
     * Cadastra um novo funcionário.
     *
     * @throws FuncionarioJaCadastradoException se o código ou o CPF já estiverem em uso
     * @throws DadoInvalidoException            se o funcionário for nulo
     */
    public void cadastrar(Funcionario funcionario) throws FuncionarioJaCadastradoException {
        if (funcionario == null) {
            throw new DadoInvalidoException("Funcionário é obrigatório.");
        }
        if (repository.existe(funcionario.getCodigo())) {
            throw new FuncionarioJaCadastradoException(funcionario.getCodigo());
        }
        boolean cpfEmUso = repository.listarTodos().stream()
                .anyMatch(f -> f.getCpf().equals(funcionario.getCpf()));
        if (cpfEmUso) {
            throw new FuncionarioJaCadastradoException(
                    "Já existe um funcionário cadastrado com o CPF " + ValidadorCpf.formatar(funcionario.getCpf()) + ".");
        }
        repository.salvar(funcionario);
    }

    // ------------------------------------------------------------------ consultas

    public List<Funcionario> listarTodos() {
        return repository.listarTodos();
    }

    /**
     * Busca pelo código identificador.
     */
    public Funcionario buscarFuncionario(int codigo) throws FuncionarioNaoEncontradoException {
        return repository.buscarPorCodigo(codigo)
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(codigo));
    }

    /**
     * Sobrecarga: busca pelo CPF (aceita com ou sem máscara).
     */
    public Funcionario buscarFuncionario(String cpf) throws FuncionarioNaoEncontradoException {
        if (cpf == null || cpf.isBlank()) {
            throw new DadoInvalidoException("CPF é obrigatório.");
        }
        String normalizado = ValidadorCpf.normalizar(cpf);
        return repository.listarTodos().stream()
                .filter(f -> f.getCpf().equals(normalizado))
                .findFirst()
                .orElseThrow(() -> new FuncionarioNaoEncontradoException(
                        "Funcionário com CPF " + cpf + " não encontrado."));
    }

    /**
     * Sobrecarga: busca pelo nome.
     *
     * @param buscaExata {@code true} compara o nome inteiro; {@code false} procura
     *                   o trecho dentro do nome. Em ambos os casos a comparação
     *                   ignora maiúsculas/minúsculas e acentos.
     * @return lista (nunca vazia) com os funcionários encontrados
     */
    public List<Funcionario> buscarFuncionario(String nome, boolean buscaExata)
            throws FuncionarioNaoEncontradoException {
        if (nome == null || nome.isBlank()) {
            throw new DadoInvalidoException("Nome é obrigatório.");
        }
        String alvo = simplificar(nome);
        List<Funcionario> encontrados = repository.listarTodos().stream()
                .filter(f -> buscaExata
                        ? simplificar(f.getNome()).equals(alvo)
                        : simplificar(f.getNome()).contains(alvo))
                .toList();
        if (encontrados.isEmpty()) {
            throw new FuncionarioNaoEncontradoException("Nenhum funcionário encontrado com o nome \"" + nome + "\".");
        }
        return encontrados;
    }

    // ------------------------------------------------------------------ remoção

    public void remover(int codigo) throws FuncionarioNaoEncontradoException {
        if (!repository.remover(codigo)) {
            throw new FuncionarioNaoEncontradoException(codigo);
        }
    }

    // ------------------------------------------------------------------ cálculos e relatórios

    public double calcularRemuneracao(int codigo) throws FuncionarioNaoEncontradoException {
        return buscarFuncionario(codigo).calcularRemuneracao();
    }

    public String exibirDados(int codigo) throws FuncionarioNaoEncontradoException {
        return buscarFuncionario(codigo).exibirDados();
    }

    /**
     * Soma a remuneração bruta de todos os funcionários (polimorfismo:
     * cada objeto executa sua própria regra de cálculo).
     */
    public double calcularTotalFolha() {
        return repository.listarTodos().stream()
                .mapToDouble(Funcionario::calcularRemuneracao)
                .sum();
    }

    /**
     * Soma os tributos de todos os funcionários tributáveis.
     */
    public double calcularTotalTributos() {
        return repository.listarTodos().stream()
                .filter(Tributavel.class::isInstance)
                .map(Tributavel.class::cast)
                .mapToDouble(Tributavel::calcularTributos)
                .sum();
    }

    /**
     * Gera um relatório tabular com todos os funcionários e seus salários.
     */
    public String gerarRelatorio() {
        List<Funcionario> todos = repository.listarTodos();
        StringBuilder sb = new StringBuilder();
        String linha = "-".repeat(92) + "\n";
        sb.append("RELATÓRIO DA FOLHA DE PAGAMENTO\n").append(linha);
        sb.append(String.format("%-6s %-25s %-13s %15s %13s %15s%n",
                "Cód.", "Nome", "Tipo", "Remuneração", "Tributos", "Líquido"));
        sb.append(linha);
        if (todos.isEmpty()) {
            sb.append("Nenhum funcionário cadastrado.\n");
        }
        for (Funcionario f : todos) {
            double bruto = f.calcularRemuneracao();
            double tributos = (f instanceof Tributavel t) ? t.calcularTributos() : 0.0;
            sb.append(String.format(PT_BR, "%-6d %-25s %-13s %15s %13s %15s%n",
                    f.getCodigo(), abreviar(f.getNome(), 25), f.getTipo(),
                    moeda(bruto), moeda(tributos), moeda(bruto - tributos)));
        }
        sb.append(linha);
        double totalBruto = calcularTotalFolha();
        double totalTributos = calcularTotalTributos();
        sb.append(String.format("%-46s %15s %13s %15s%n", "TOTAL (" + todos.size() + " funcionário(s))",
                moeda(totalBruto), moeda(totalTributos), moeda(totalBruto - totalTributos)));
        return sb.toString();
    }

    // ------------------------------------------------------------------ auxiliares

    private static String moeda(double valor) {
        return String.format(PT_BR, "R$ %,.2f", valor);
    }

    private static String abreviar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + "…";
    }

    /** Remove acentos e converte para minúsculas, para comparação de nomes. */
    private static String simplificar(String texto) {
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(PT_BR);
    }
}
