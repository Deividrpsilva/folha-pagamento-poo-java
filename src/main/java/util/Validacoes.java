package util;

import exception.DadoInvalidoException;

/**
 * Validações reutilizáveis pelos construtores e setters das entidades.
 * Centraliza as regras para evitar repetição de código (DRY).
 */
public final class Validacoes {

    private Validacoes() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Garante que um texto obrigatório foi informado (não nulo e não vazio).
     *
     * @return o texto sem espaços nas extremidades
     */
    public static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadoInvalidoException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    /**
     * Garante que um valor numérico não é negativo.
     */
    public static double naoNegativo(double valor, String campo) {
        if (Double.isNaN(valor) || valor < 0) {
            throw new DadoInvalidoException(campo + " não pode ser negativo.");
        }
        return valor;
    }

    /**
     * Garante que um inteiro não é negativo.
     */
    public static int naoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new DadoInvalidoException(campo + " não pode ser negativo.");
        }
        return valor;
    }

    /**
     * Garante que um inteiro é estritamente positivo.
     */
    public static int positivo(int valor, String campo) {
        if (valor <= 0) {
            throw new DadoInvalidoException(campo + " deve ser maior que zero.");
        }
        return valor;
    }

    /**
     * Garante que um percentual está entre 0 e 100 (inclusive).
     */
    public static double percentual(double valor, String campo) {
        if (Double.isNaN(valor) || valor < 0 || valor > 100) {
            throw new DadoInvalidoException(campo + " deve estar entre 0 e 100.");
        }
        return valor;
    }
}
