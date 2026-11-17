package exception;

/**
 * Lançada quando o CPF informado não é válido (formato incorreto,
 * quantidade de dígitos errada ou dígitos verificadores inconsistentes).
 *
 * <p>É uma exceção não verificada (unchecked), pois representa erro
 * de dados de entrada detectado nas validações de construtores e setters.</p>
 */
public class CpfInvalidoException extends DadoInvalidoException {

    public CpfInvalidoException(String cpf) {
        super("CPF inválido: " + cpf);
    }
}
