package model;

import exception.CpfInvalidoException;
import util.ValidadorCpf;
import util.Validacoes;

import java.util.Locale;
import java.util.Objects;

/**
 * Classe abstrata que representa um funcionário da empresa.
 *
 * <p>Concentra os dados comuns a todas as categorias (código, nome, CPF e
 * salário-base) e declara o método abstrato {@link #calcularRemuneracao()},
 * que cada subclasse sobrescreve com sua própria regra (polimorfismo por
 * sobreposição).</p>
 */
public abstract class Funcionario implements Identificavel {

    private final int codigo;
    private String nome;
    private String cpf;
    private double salarioBase;

    /**
     * @throws exception.DadoInvalidoException se algum dado obrigatório faltar ou for inválido
     * @throws CpfInvalidoException            se o CPF for inválido
     */
    protected Funcionario(int codigo, String nome, String cpf, double salarioBase) {
        this.codigo = Validacoes.positivo(codigo, "Código");
        setNome(nome);
        setCpf(cpf);
        setSalarioBase(salarioBase);
    }

    /**
     * Calcula a remuneração bruta do funcionário conforme a regra da sua categoria.
     */
    public abstract double calcularRemuneracao();

    /**
     * @return o nome da categoria do funcionário (ex.: "CLT", "PJ")
     */
    public abstract String getTipo();

    /**
     * Detalhes específicos da categoria, usados na exibição dos dados completos.
     */
    protected abstract String detalhesEspecificos();

    /**
     * Monta um texto com todos os dados do funcionário.
     */
    public String exibirDados() {
        StringBuilder sb = new StringBuilder();
        sb.append("Código.........: ").append(codigo).append('\n');
        sb.append("Tipo...........: ").append(getTipo()).append('\n');
        sb.append("Nome...........: ").append(nome).append('\n');
        sb.append("CPF............: ").append(ValidadorCpf.formatar(cpf)).append('\n');
        sb.append(detalhesEspecificos());
        sb.append("Remuneração....: ").append(moeda(calcularRemuneracao())).append('\n');
        if (this instanceof Tributavel tributavel) {
            sb.append("Tributos.......: ").append(moeda(tributavel.calcularTributos())).append('\n');
            sb.append("Valor líquido..: ")
              .append(moeda(calcularRemuneracao() - tributavel.calcularTributos())).append('\n');
        }
        return sb.toString();
    }

    /** Formata um valor como moeda brasileira (R$ 1.234,56). */
    protected static String moeda(double valor) {
        return String.format(new Locale("pt", "BR"), "R$ %,.2f", valor);
    }

    @Override
    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Validacoes.obrigatorio(nome, "Nome");
    }

    /** @return o CPF somente com dígitos */
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        Validacoes.obrigatorio(cpf, "CPF");
        if (!ValidadorCpf.isValido(cpf)) {
            throw new CpfInvalidoException(cpf);
        }
        this.cpf = ValidadorCpf.normalizar(cpf);
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = Validacoes.naoNegativo(salarioBase, "Salário-base");
    }

    /** Dois funcionários são iguais quando possuem o mesmo código. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Funcionario outro)) return false;
        return codigo == outro.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", codigo, nome, getTipo(), moeda(calcularRemuneracao()));
    }
}
