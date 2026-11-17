package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que todas as classes concretas implementam {@link Identificavel}
 * (por herança de {@link Funcionario}) e retornam o código correto.
 */
class IdentificavelTest {

    @Test
    @DisplayName("Todas as classes concretas devem ser Identificavel e retornar seu código")
    void todasAsConcretasSaoIdentificaveis() {
        List<Identificavel> itens = List.of(
                new FuncionarioCLT(10, "Ana", "52998224725", 3000, 0),
                new FuncionarioPJ(20, "Bruno", "11144477735", 1000, 1),
                new FuncionarioComissionado(30, "Carla", "52601815906", 2000, 5, 0));

        assertEquals(List.of(10, 20, 30), itens.stream().map(Identificavel::getCodigo).toList());
    }

    @Test
    @DisplayName("Funcionario deve implementar Identificavel diretamente")
    void funcionarioImplementaIdentificavel() {
        assertTrue(Identificavel.class.isAssignableFrom(Funcionario.class));
    }
}
