package model;

import util.Validacoes;

/**
 * Funcionário contratado pelo regime CLT.
 *
 * <p>Remuneração = salário-base + adicional de benefícios.</p>
 * <p>Tributos: desconto simplificado de {@value #ALIQUOTA_DESCONTOS} (11%)
 * sobre o salário-base, a título de contribuição previdenciária. Os benefícios
 * não sofrem desconto nesta simplificação.</p>
 */
public class FuncionarioCLT extends Funcionario implements Tributavel {

    /** Alíquota simplificada de desconto previdenciário. */
    public static final double ALIQUOTA_DESCONTOS = 0.11;

    private double beneficios;

    public FuncionarioCLT(int codigo, String nome, String cpf, double salarioBase, double beneficios) {
        super(codigo, nome, cpf, salarioBase);
        setBeneficios(beneficios);
    }

    @Override
    public double calcularRemuneracao() {
        return getSalarioBase() + beneficios;
    }

    @Override
    public double calcularTributos() {
        return getSalarioBase() * ALIQUOTA_DESCONTOS;
    }

    @Override
    public String getTipo() {
        return "CLT";
    }

    @Override
    protected String detalhesEspecificos() {
        return "Salário-base...: " + moeda(getSalarioBase()) + '\n'
             + "Benefícios.....: " + moeda(beneficios) + '\n';
    }

    public double getBeneficios() {
        return beneficios;
    }

    public void setBeneficios(double beneficios) {
        this.beneficios = Validacoes.naoNegativo(beneficios, "Benefícios");
    }
}
