package repository;

import model.Funcionario;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Armazena os funcionários em memória.
 *
 * <p>Usa um {@link LinkedHashMap} indexado pelo código, o que garante busca
 * por código em tempo constante e mantém a ordem de cadastro na listagem.</p>
 *
 * <p>O repositório não aplica regras de negócio (como duplicidade de CPF):
 * essa responsabilidade é da camada de serviço.</p>
 */
public class FuncionarioRepository {

    private final Map<Integer, Funcionario> funcionarios = new LinkedHashMap<>();

    /**
     * Salva (ou substitui) um funcionário, usando o código como chave.
     */
    public void salvar(Funcionario funcionario) {
        Objects.requireNonNull(funcionario, "Funcionário não pode ser nulo.");
        funcionarios.put(funcionario.getCodigo(), funcionario);
    }

    public Optional<Funcionario> buscarPorCodigo(int codigo) {
        return Optional.ofNullable(funcionarios.get(codigo));
    }

    /**
     * @return cópia da lista de funcionários, na ordem de cadastro
     *         (alterar a lista retornada não afeta o repositório)
     */
    public List<Funcionario> listarTodos() {
        return new ArrayList<>(funcionarios.values());
    }

    public boolean existe(int codigo) {
        return funcionarios.containsKey(codigo);
    }

    /**
     * @return {@code true} se havia um funcionário com esse código e ele foi removido
     */
    public boolean remover(int codigo) {
        return funcionarios.remove(codigo) != null;
    }

    public int quantidade() {
        return funcionarios.size();
    }
}
