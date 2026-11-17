package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorCpfTest {

    @ParameterizedTest
    @ValueSource(strings = {"52998224725", "529.982.247-25", "111.444.777-35", " 526.018.159-06 "})
    @DisplayName("Deve aceitar CPFs válidos, com ou sem máscara")
    void deveAceitarCpfsValidos(String cpf) {
        assertTrue(ValidadorCpf.isValido(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224724", "12345678900", "1234567890", "123456789012", "abc.def.ghi-jk"})
    @DisplayName("Deve rejeitar CPFs com dígito verificador errado ou formato inválido")
    void deveRejeitarCpfsInvalidos(String cpf) {
        assertFalse(ValidadorCpf.isValido(cpf));
    }

    @ParameterizedTest
    @ValueSource(strings = {"00000000000", "11111111111", "999.999.999-99"})
    @DisplayName("Deve rejeitar sequências de dígitos repetidos")
    void deveRejeitarSequenciasRepetidas(String cpf) {
        assertFalse(ValidadorCpf.isValido(cpf));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Deve rejeitar CPF nulo ou vazio")
    void deveRejeitarNuloOuVazio(String cpf) {
        assertFalse(ValidadorCpf.isValido(cpf));
    }

    @Test
    @DisplayName("Deve normalizar removendo a máscara")
    void deveNormalizar() {
        assertEquals("52998224725", ValidadorCpf.normalizar("529.982.247-25"));
        assertNull(ValidadorCpf.normalizar(null));
    }

    @Test
    @DisplayName("Deve formatar no padrão 000.000.000-00")
    void deveFormatar() {
        assertEquals("529.982.247-25", ValidadorCpf.formatar("52998224725"));
    }
}
