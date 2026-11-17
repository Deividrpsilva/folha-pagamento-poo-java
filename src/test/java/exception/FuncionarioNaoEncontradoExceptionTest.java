package exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioNaoEncontradoExceptionTest {

    @Test
    @DisplayName("Deve guardar a mensagem informada")
    void deveGuardarMensagem() {
        FuncionarioNaoEncontradoException e = new FuncionarioNaoEncontradoException("não achei");
        assertEquals("não achei", e.getMessage());
    }

    @Test
    @DisplayName("Deve montar mensagem padrão a partir do código")
    void deveMontarMensagemPeloCodigo() {
        FuncionarioNaoEncontradoException e = new FuncionarioNaoEncontradoException(42);
        assertEquals("Funcionário com código 42 não encontrado.", e.getMessage());
    }

    @Test
    @DisplayName("Deve ser uma exceção verificada (checked)")
    void deveSerChecked() {
        Exception e = new FuncionarioNaoEncontradoException(1);
        assertFalse(e instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada")
    void devePoderSerLancada() {
        FuncionarioNaoEncontradoException e = assertThrows(FuncionarioNaoEncontradoException.class, () -> {
            throw new FuncionarioNaoEncontradoException(7);
        });
        assertTrue(e.getMessage().contains("7"));
    }
}
