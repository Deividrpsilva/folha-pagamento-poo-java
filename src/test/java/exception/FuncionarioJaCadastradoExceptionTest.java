package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioJaCadastradoExceptionTest {

    @Test
    @DisplayName("Deve guardar a mensagem informada")
    void deveGuardarMensagem() {
        FuncionarioJaCadastradoException e = new FuncionarioJaCadastradoException("duplicado");
        assertEquals("duplicado", e.getMessage());
    }

    @Test
    @DisplayName("Deve montar mensagem padrão a partir do código")
    void deveMontarMensagemPeloCodigo() {
        FuncionarioJaCadastradoException e = new FuncionarioJaCadastradoException(10);
        assertEquals("Já existe um funcionário cadastrado com o código 10.", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser uma exceção verificada (checked)")
    void deveSerChecked() {
        Exception e = new FuncionarioJaCadastradoException(1);
        assertFalse(e instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada")
    void devePoderSerLancada() {
        assertThrows(FuncionarioJaCadastradoException.class, () -> {
            throw new FuncionarioJaCadastradoException(3);
        });
    }
}
