import exception.FuncionarioJaCadastradoException;
import model.FuncionarioCLT;
import model.FuncionarioComissionado;
import model.FuncionarioPJ;
import repository.FuncionarioRepository;
import service.FuncionarioService;
import view.FuncionarioView;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação: monta as camadas (repository → service → view),
 * carrega alguns funcionários de exemplo e inicia o menu.
 */
public class Main {

    public static void main(String[] args) {
        FuncionarioRepository repository = new FuncionarioRepository();
        FuncionarioService service = new FuncionarioService(repository);

        carregarDadosDeExemplo(service);

        PrintStream saida = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        try (Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8)) {
            new FuncionarioView(service, entrada, saida).iniciar();
        }
    }

    private static void carregarDadosDeExemplo(FuncionarioService service) {
        try {
            service.cadastrar(new FuncionarioCLT(1, "Ana Souza", "529.982.247-25", 3000, 500));
            service.cadastrar(new FuncionarioPJ(2, "Bruno Lima", "111.444.777-35", 2000, 3));
            service.cadastrar(new FuncionarioComissionado(3, "Carla Mendes", "526.018.159-06", 2000, 5, 20000));
        } catch (FuncionarioJaCadastradoException e) {
            System.err.println("Falha ao carregar dados de exemplo: " + e.getMessage());
        }
    }
}
