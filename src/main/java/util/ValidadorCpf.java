package util;

/**
 * Utilitário para validação e normalização de CPF.
 *
 * <p>Aceita CPF com ou sem máscara (ex.: "529.982.247-25" ou "52998224725")
 * e verifica os dois dígitos verificadores pelo algoritmo oficial.</p>
 */
public final class ValidadorCpf {

    private ValidadorCpf() {
        // classe utilitária: não deve ser instanciada
    }

    /**
     * Remove pontos, traços e espaços, mantendo apenas os dígitos.
     *
     * @return somente os dígitos, ou {@code null} se a entrada for nula
     */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return null;
        }
        return cpf.replaceAll("[.\\-\\s]", "");
    }

    /**
     * Verifica se o CPF é válido.
     */
    public static boolean isValido(String cpf) {
        String digitos = normalizar(cpf);
        if (digitos == null || !digitos.matches("\\d{11}")) {
            return false;
        }
        // Sequências repetidas (000..., 111...) passam no cálculo, mas são inválidas.
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }
        int dv1 = calcularDigito(digitos, 9);
        int dv2 = calcularDigito(digitos, 10);
        return dv1 == Character.getNumericValue(digitos.charAt(9))
                && dv2 == Character.getNumericValue(digitos.charAt(10));
    }

    /**
     * Formata 11 dígitos no padrão 000.000.000-00.
     */
    public static String formatar(String cpf) {
        String d = normalizar(cpf);
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    private static int calcularDigito(String digitos, int quantidade) {
        int soma = 0;
        int peso = quantidade + 1;
        for (int i = 0; i < quantidade; i++) {
            soma += Character.getNumericValue(digitos.charAt(i)) * peso--;
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}
