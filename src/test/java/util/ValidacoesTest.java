package util;

import exception.DadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidacoesTest {

    @Test
    @DisplayName("obrigatorio: deve retornar o texto sem espaços nas extremidades")
    void obrigatorioDeveRetornarTextoAparado() {
        assertEquals("Ana", Validacoes.obrigatorio("  Ana  ", "Nome"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("obrigatorio: deve lançar exceção para texto nulo, vazio ou em branco")
    void obrigatorioDeveLancarExcecao(String valor) {
        DadoInvalidoException e = assertThrows(DadoInvalidoException.class,
                () -> Validacoes.obrigatorio(valor, "Nome"));
        assertEquals("Nome é obrigatório.", e.getMessage());
    }

    @Test
    @DisplayName("naoNegativo: deve aceitar zero e positivos")
    void naoNegativoDeveAceitarZeroEPositivos() {
        assertEquals(0.0, Validacoes.naoNegativo(0.0, "Valor"));
        assertEquals(10.5, Validacoes.naoNegativo(10.5, "Valor"));
        assertEquals(3, Validacoes.naoNegativo(3, "Qtd"));
    }

    @Test
    @DisplayName("naoNegativo: deve lançar exceção para negativos e NaN")
    void naoNegativoDeveLancarExcecao() {
        assertThrows(DadoInvalidoException.class, () -> Validacoes.naoNegativo(-0.01, "Valor"));
        assertThrows(DadoInvalidoException.class, () -> Validacoes.naoNegativo(Double.NaN, "Valor"));
        assertThrows(DadoInvalidoException.class, () -> Validacoes.naoNegativo(-1, "Qtd"));
    }

    @Test
    @DisplayName("positivo: deve aceitar maior que zero e rejeitar zero ou negativo")
    void positivo() {
        assertEquals(1, Validacoes.positivo(1, "Código"));
        assertThrows(DadoInvalidoException.class, () -> Validacoes.positivo(0, "Código"));
        assertThrows(DadoInvalidoException.class, () -> Validacoes.positivo(-5, "Código"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, 0.5, 50, 100})
    @DisplayName("percentual: deve aceitar valores entre 0 e 100")
    void percentualValido(double valor) {
        assertEquals(valor, Validacoes.percentual(valor, "Percentual"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 100.01, 150, Double.NaN})
    @DisplayName("percentual: deve lançar exceção fora da faixa 0–100")
    void percentualInvalido(double valor) {
        assertThrows(DadoInvalidoException.class, () -> Validacoes.percentual(valor, "Percentual"));
    }
}
