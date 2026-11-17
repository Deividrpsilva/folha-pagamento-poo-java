package model;

import util.Validacoes;

/**
 * Funcionário que recebe salário-base mais comissão sobre suas vendas.
 *
 * <p>Remuneração = salário-base + (total de vendas × percentual de comissão / 100).</p>
 * <p>Tributos: desconto simplificado de 11% sobre a remuneração total
 * (salário-base + comissão), pois a comissão integra o salário.</p>
 */
public class FuncionarioComissionado extends Funcionario implements Tributavel {

    /** Alíquota simplificada de desconto sobre a remuneração total. */
    public static final double ALIQUOTA_DESCONTOS = 0.11;

    private double percentualComissao;
    private double totalVendas;

    /**
     * @param percentualComissao percentual entre 0 e 100 (ex.: 5 significa 5%)
     */
    public FuncionarioComissionado(int codigo, String nome, String cpf, double salarioBase,
                                   double percentualComissao, double totalVendas) {
        super(codigo, nome, cpf, salarioBase);
        setPercentualComissao(percentualComissao);
        setTotalVendas(totalVendas);
    }

    /** @return o valor da comissão sobre as vendas */
    public double calcularComissao() {
        return totalVendas * percentualComissao / 100.0;
    }

    @Override
    public double calcularRemuneracao() {
        return getSalarioBase() + calcularComissao();
    }

    @Override
    public double calcularTributos() {
        return calcularRemuneracao() * ALIQUOTA_DESCONTOS;
    }

    @Override
    public String getTipo() {
        return "Comissionado";
    }

    @Override
    protected String detalhesEspecificos() {
        return "Salário-base...: " + moeda(getSalarioBase()) + '\n'
             + "Total vendas...: " + moeda(totalVendas) + '\n'
             + "% Comissão.....: " + String.format("%.2f%%", percentualComissao) + '\n'
             + "Comissão.......: " + moeda(calcularComissao()) + '\n';
    }

    public double getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(double percentualComissao) {
        this.percentualComissao = Validacoes.percentual(percentualComissao, "Percentual de comissão");
    }

    public double getTotalVendas() {
        return totalVendas;
    }

    public void setTotalVendas(double totalVendas) {
        this.totalVendas = Validacoes.naoNegativo(totalVendas, "Total de vendas");
    }
}
