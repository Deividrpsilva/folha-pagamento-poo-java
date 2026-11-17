package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CpfInvalidoExceptionTest {

    @Test
    @DisplayName("Deve incluir o CPF recebido na mensagem")
    void deveIncluirCpfNaMensagem() {
        CpfInvalidoException e = new CpfInvalidoException("123");
        assertEquals("CPF inválido: 123", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser um tipo de DadoInvalidoException (unchecked)")
    void deveSerDadoInvalido() {
        RuntimeException e = new CpfInvalidoException("x");
        assertInstanceOf(DadoInvalidoException.class, e);
    }
}
