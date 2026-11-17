package model;

import util.Validacoes;

/**
 * Prestador de serviços contratado como Pessoa Jurídica.
 *
 * <p>Remuneração = valor por projeto × quantidade de projetos realizados.</p>
 *
 * <p>O PJ não possui salário-base (fica registrado como zero) e <b>não</b>
 * implementa {@link Tributavel}: nesta modelagem, o recolhimento dos tributos
 * é responsabilidade da própria pessoa jurídica que emite a nota fiscal, e
 * não um desconto feito pela empresa na folha.</p>
 */
public class FuncionarioPJ extends Funcionario {

    private double valorPorProjeto;
    private int quantidadeProjetos;

    public FuncionarioPJ(int codigo, String nome, String cpf, double valorPorProjeto, int quantidadeProjetos) {
        super(codigo, nome, cpf, 0.0);
        setValorPorProjeto(valorPorProjeto);
        setQuantidadeProjetos(quantidadeProjetos);
    }

    @Override
    public double calcularRemuneracao() {
        return valorPorProjeto * quantidadeProjetos;
    }

    @Override
    public String getTipo() {
        return "PJ";
    }

    @Override
    protected String detalhesEspecificos() {
        return "Valor/projeto..: " + moeda(valorPorProjeto) + '\n'
             + "Qtd. projetos..: " + quantidadeProjetos + '\n';
    }

    public double getValorPorProjeto() {
        return valorPorProjeto;
    }

    public void setValorPorProjeto(double valorPorProjeto) {
        this.valorPorProjeto = Validacoes.naoNegativo(valorPorProjeto, "Valor por projeto");
    }

    public int getQuantidadeProjetos() {
        return quantidadeProjetos;
    }

    public void setQuantidadeProjetos(int quantidadeProjetos) {
        this.quantidadeProjetos = Validacoes.naoNegativo(quantidadeProjetos, "Quantidade de projetos");
    }
}
