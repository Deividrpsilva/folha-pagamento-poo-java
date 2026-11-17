package model;

import exception.DadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioPJTest {

    private static final String CPF = "11144477735";

    @Test
    @DisplayName("Remuneração = valor por projeto × quantidade de projetos")
    void deveCalcularRemuneracaoPorProjetos() {
        FuncionarioPJ pj = new FuncionarioPJ(1, "Bruno", CPF, 2500, 4);
        assertEquals(10000.0, pj.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Sem projetos, a remuneração deve ser zero")
    void semProjetos() {
        FuncionarioPJ pj = new FuncionarioPJ(1, "Bruno", CPF, 2500, 0);
        assertEquals(0.0, pj.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Salário-base do PJ deve ser zero")
    void salarioBaseZero() {
        assertEquals(0.0, new FuncionarioPJ(1, "Bruno", CPF, 2500, 4).getSalarioBase());
    }

    @Test
    @DisplayName("Deve rejeitar valor por projeto negativo")
    void deveRejeitarValorNegativo() {
        assertThrows(DadoInvalidoException.class, () -> new FuncionarioPJ(1, "Bruno", CPF, -1, 2));
    }

    @Test
    @DisplayName("Deve rejeitar quantidade de projetos negativa")
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(DadoInvalidoException.class, () -> new FuncionarioPJ(1, "Bruno", CPF, 1000, -2));
    }

    @Test
    @DisplayName("Setters devem validar e atualizar o cálculo")
    void settersDevemValidar() {
        FuncionarioPJ pj = new FuncionarioPJ(1, "Bruno", CPF, 1000, 1);
        pj.setQuantidadeProjetos(3);
        pj.setValorPorProjeto(1500);
        assertEquals(4500.0, pj.calcularRemuneracao(), 0.001);
        assertThrows(DadoInvalidoException.class, () -> pj.setValorPorProjeto(-5));
        assertThrows(DadoInvalidoException.class, () -> pj.setQuantidadeProjetos(-1));
    }

    @Test
    @DisplayName("PJ não deve ser Tributavel, mas deve ser Identificavel")
    void contratos() {
        FuncionarioPJ pj = new FuncionarioPJ(1, "Bruno", CPF, 1000, 1);
        assertFalse(pj instanceof Tributavel);
        assertInstanceOf(Identificavel.class, pj);
        assertEquals("PJ", pj.getTipo());
    }

    @Test
    @DisplayName("exibirDados deve mostrar dados de projeto e não mostrar tributos")
    void exibirDados() {
        String dados = new FuncionarioPJ(1, "Bruno", CPF, 2500, 4).exibirDados();
        assertTrue(dados.contains("Qtd. projetos..: 4"));
        assertTrue(dados.contains("R$ 10.000,00"));
        assertFalse(dados.contains("Tributos"));
    }
}
