package model;

import exception.DadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioCLTTest {

    private static final String CPF = "52998224725";

    @Test
    @DisplayName("Remuneração = salário-base + benefícios")
    void deveCalcularRemuneracao() {
        FuncionarioCLT clt = new FuncionarioCLT(1, "Ana", CPF, 3000, 600);
        assertEquals(3600.0, clt.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Remuneração sem benefícios deve ser igual ao salário-base")
    void remuneracaoSemBeneficios() {
        FuncionarioCLT clt = new FuncionarioCLT(1, "Ana", CPF, 2500, 0);
        assertEquals(2500.0, clt.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Tributos = 11% do salário-base")
    void deveCalcularTributos() {
        FuncionarioCLT clt = new FuncionarioCLT(1, "Ana", CPF, 3000, 600);
        assertEquals(330.0, clt.calcularTributos(), 0.001);
    }

    @Test
    @DisplayName("Deve rejeitar benefícios negativos no construtor e no setter")
    void deveRejeitarBeneficiosNegativos() {
        assertThrows(DadoInvalidoException.class, () -> new FuncionarioCLT(1, "Ana", CPF, 3000, -1));
        FuncionarioCLT clt = new FuncionarioCLT(1, "Ana", CPF, 3000, 100);
        assertThrows(DadoInvalidoException.class, () -> clt.setBeneficios(-10));
        assertEquals(100, clt.getBeneficios());
    }

    @Test
    @DisplayName("Deve rejeitar salário-base negativo")
    void deveRejeitarSalarioNegativo() {
        assertThrows(DadoInvalidoException.class, () -> new FuncionarioCLT(1, "Ana", CPF, -3000, 0));
    }

    @Test
    @DisplayName("Alterar benefícios deve refletir na remuneração")
    void alterarBeneficios() {
        FuncionarioCLT clt = new FuncionarioCLT(1, "Ana", CPF, 3000, 0);
        clt.setBeneficios(400);
        assertEquals(3400.0, clt.calcularRemuneracao(), 0.001);
    }

    @Test
    @DisplayName("Deve ser Tributavel e Identificavel, com tipo CLT")
    void contratos() {
        FuncionarioCLT clt = new FuncionarioCLT(9, "Ana", CPF, 3000, 0);
        assertInstanceOf(Tributavel.class, clt);
        assertInstanceOf(Identificavel.class, clt);
        assertEquals("CLT", clt.getTipo());
    }

    @Test
    @DisplayName("exibirDados deve mostrar benefícios, tributos e líquido")
    void exibirDados() {
        String dados = new FuncionarioCLT(1, "Ana", CPF, 3000, 600).exibirDados();
        assertTrue(dados.contains("Benefícios"));
        assertTrue(dados.contains("R$ 600,00"));
        assertTrue(dados.contains("R$ 330,00"));   // tributos
        assertTrue(dados.contains("R$ 3.270,00")); // líquido
    }
}
