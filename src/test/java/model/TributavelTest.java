package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica o comportamento polimórfico da interface {@link Tributavel}
 * nas categorias em que ela se aplica.
 */
class TributavelTest {

    @Test
    @DisplayName("Cada implementação deve aplicar sua própria regra de tributos")
    void cadaImplementacaoTemSuaRegra() {
        Tributavel clt = new FuncionarioCLT(1, "Ana", "52998224725", 1000, 500);
        Tributavel comissionado = new FuncionarioComissionado(2, "Carla", "52601815906", 1000, 10, 5000);

        // CLT: 11% somente sobre o salário-base (benefícios não entram)
        assertEquals(110.0, clt.calcularTributos(), 0.001);
        // Comissionado: 11% sobre base + comissão (1000 + 500)
        assertEquals(165.0, comissionado.calcularTributos(), 0.001);
    }

    @Test
    @DisplayName("Somente CLT e Comissionado implementam Tributavel")
    void apenasCategoriasTributaveis() {
        assertTrue(Tributavel.class.isAssignableFrom(FuncionarioCLT.class));
        assertTrue(Tributavel.class.isAssignableFrom(FuncionarioComissionado.class));
        assertFalse(Tributavel.class.isAssignableFrom(FuncionarioPJ.class));
    }

    @Test
    @DisplayName("Soma de tributos de uma lista polimórfica")
    void somaPolimorfica() {
        List<Tributavel> lista = List.of(
                new FuncionarioCLT(1, "Ana", "52998224725", 2000, 0),
                new FuncionarioComissionado(2, "Carla", "52601815906", 1000, 0, 0));
        double total = lista.stream().mapToDouble(Tributavel::calcularTributos).sum();
        assertEquals(330.0, total, 0.001);
    }
}
