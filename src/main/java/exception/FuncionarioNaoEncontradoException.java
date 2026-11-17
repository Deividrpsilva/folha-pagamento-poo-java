package exception;

/**
 * Lançada quando se tenta buscar, remover ou consultar um funcionário
 * que não está cadastrado no sistema.
 *
 * <p>É uma exceção verificada (checked): obriga as camadas chamadoras
 * a tratá-la ou repassá-la explicitamente.</p>
 */
public class FuncionarioNaoEncontradoException extends Exception {

    private static final long serialVersionUID = 1L;

    public FuncionarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria a exceção com mensagem padrão a partir do código procurado.
     */
    public FuncionarioNaoEncontradoException(int codigo) {
        this("Funcionário com código " + codigo + " não encontrado.");
    }
}
