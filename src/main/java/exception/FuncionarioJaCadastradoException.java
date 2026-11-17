package exception;

/**
 * Lançada quando se tenta cadastrar um funcionário cujo código
 * identificador ou CPF já pertence a outro funcionário cadastrado.
 */
public class FuncionarioJaCadastradoException extends Exception {

    public FuncionarioJaCadastradoException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria a exceção com mensagem padrão a partir do código duplicado.
     */
    public FuncionarioJaCadastradoException(int codigo) {
        this("Já existe um funcionário cadastrado com o código " + codigo + ".");
    }
}
