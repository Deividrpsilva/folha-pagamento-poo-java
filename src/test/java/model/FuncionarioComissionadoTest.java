package model;

import exception.DadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioComissionadoTest {

    private static final String CPF = "52601815906";

    @Test
    @DisplayName("Comissão = total de vendas × percentual / 100")
    void deveCalcularComissao() {
        FuncionarioComissionado c = new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, 30000);
        assertEquals(1500.0, c.calcularComissao(), 0.001);
    }

    @Test
    @DisplayName("Remuneração = salário-base + comissão")
    void deveCalcularRemuneracao() {
        FuncionarioComissionado c = new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, 30000);
        assertEquals(3500.0, c.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Sem vendas, a remuneração deve ser apenas o salário-base")
    void semVendas() {
        FuncionarioComissionado c = new FuncionarioComissionado(1, "Carla", CPF, 2000, 10, 0);
        assertEquals(2000.0, c.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Tributos = 11% da remuneração total")
    void deveCalcularTributos() {
        FuncionarioComissionado c = new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, 30000);
        assertEquals(385.0, c.calcularTributos(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 100.5, 200})
    @DisplayName("Deve rejeitar percentual de comissão fora de 0–100")
    void deveRejeitarPercentualInvalido(double percentual) {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioComissionado(1, "Carla", CPF, 2000, percentual, 1000));
    }

    @Test
    @DisplayName("Deve aceitar os limites 0% e 100%")
    void deveAceitarLimites() {
        assertEquals(2000.0, new FuncionarioComissionado(1, "C", CPF, 2000, 0, 5000).calcularRemuneracao(), 0.001);
        assertEquals(7000.0, new FuncionarioComissionado(1, "C", CPF, 2000, 100, 5000).calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Deve rejeitar total de vendas negativo")
    void deveRejeitarVendasNegativas() {
        assertThrows(DadoInvalidoException.class,
                () -> new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, -100));
    }

    @Test
    @DisplayName("Setters devem validar e preservar o valor anterior em caso de erro")
    void settersDevemValidar() {
        FuncionarioComissionado c = new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, 1000);
        assertThrows(DadoInvalidoException.class, () -> c.setPercentualComissao(101));
        assertThrows(DadoInvalidoException.class, () -> c.setTotalVendas(-1));
        assertEquals(5, c.getPercentualComissao());
        assertEquals(1000, c.getTotalVendas());
    }

    @Test
    @DisplayName("Deve ser Tributavel e Identificavel, com tipo Comissionado")
    void contratos() {
        FuncionarioComissionado c = new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, 1000);
        assertInstanceOf(Tributavel.class, c);
        assertInstanceOf(Identificavel.class, c);
        assertEquals("Comissionado", c.getTipo());
    }

    @Test
    @DisplayName("exibirDados deve mostrar vendas, percentual e comissão")
    void exibirDados() {
        String dados = new FuncionarioComissionado(1, "Carla", CPF, 2000, 5, 30000).exibirDados();
        assertTrue(dados.contains("R$ 30.000,00"));
        assertTrue(dados.contains("Comissão.......: R$ 1.500,00"));
        assertTrue(dados.contains("Tributos"));
    }
}
