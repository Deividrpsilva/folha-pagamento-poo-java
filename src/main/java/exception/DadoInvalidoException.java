package exception;

/**
 * Lançada quando um dado obrigatório não é informado ou quando um valor
 * está fora do intervalo permitido (por exemplo, salário negativo ou
 * percentual de comissão fora da faixa de 0 a 100).
 */
public class DadoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DadoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
