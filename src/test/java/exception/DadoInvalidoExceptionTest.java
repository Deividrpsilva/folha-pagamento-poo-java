package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DadoInvalidoExceptionTest {

    @Test
    @DisplayName("Deve guardar a mensagem informada")
    void deveGuardarMensagem() {
        DadoInvalidoException e = new DadoInvalidoException("Nome é obrigatório.");
        assertEquals("Nome é obrigatório.", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser uma exceção não verificada (unchecked)")
    void deveSerUnchecked() {
        Exception e = new DadoInvalidoException("x");
        assertInstanceOf(RuntimeException.class, e);
    }
}
