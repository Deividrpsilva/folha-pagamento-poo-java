package model;

import exception.CpfInvalidoException;
import exception.DadoInvalidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa as regras comuns definidas na classe abstrata {@link Funcionario}
 * e o comportamento polimórfico de {@link Funcionario#calcularRemuneracao()}.
 */
class FuncionarioTest {

    static final String CPF_VALIDO = "52998224725";

    /** Subclasse mínima usada só para testar a classe abstrata isoladamente. */
    static class FuncionarioFake extends Funcionario {
        FuncionarioFake(int codigo, String nome, String cpf, double salarioBase) {
            super(codigo, nome, cpf, salarioBase);
        }
        @Override public double calcularRemuneracao() { return getSalarioBase(); }
        @Override public String getTipo() { return "Fake"; }
        @Override protected String detalhesEspecificos() { return ""; }
    }

    @Nested
    @DisplayName("Validações comuns (construtor e setters)")
    class ValidacoesComuns {

        @Test
        @DisplayName("Deve criar funcionário com dados válidos e CPF normalizado")
        void deveCriarComDadosValidos() {
            Funcionario f = new FuncionarioFake(1, "  Ana Souza ", "529.982.247-25", 1000);
            assertEquals(1, f.getCodigo());
            assertEquals("Ana Souza", f.getNome());
            assertEquals(CPF_VALIDO, f.getCpf());
            assertEquals(1000, f.getSalarioBase());
        }

        @Test
        @DisplayName("Deve rejeitar código zero ou negativo")
        void deveRejeitarCodigoInvalido() {
            assertThrows(DadoInvalidoException.class, () -> new FuncionarioFake(0, "Ana", CPF_VALIDO, 1000));
            assertThrows(DadoInvalidoException.class, () -> new FuncionarioFake(-1, "Ana", CPF_VALIDO, 1000));
        }

        @Test
        @DisplayName("Deve rejeitar nome não informado")
        void deveRejeitarNomeVazio() {
            assertThrows(DadoInvalidoException.class, () -> new FuncionarioFake(1, null, CPF_VALIDO, 1000));
            assertThrows(DadoInvalidoException.class, () -> new FuncionarioFake(1, " ", CPF_VALIDO, 1000));
        }

        @Test
        @DisplayName("Deve rejeitar CPF não informado")
        void deveRejeitarCpfVazio() {
            assertThrows(DadoInvalidoException.class, () -> new FuncionarioFake(1, "Ana", "", 1000));
        }

        @Test
        @DisplayName("Deve lançar CpfInvalidoException para CPF inválido")
        void deveRejeitarCpfInvalido() {
            assertThrows(CpfInvalidoException.class, () -> new FuncionarioFake(1, "Ana", "12345678900", 1000));
        }

        @Test
        @DisplayName("Deve rejeitar salário-base negativo")
        void deveRejeitarSalarioNegativo() {
            assertThrows(DadoInvalidoException.class, () -> new FuncionarioFake(1, "Ana", CPF_VALIDO, -1));
        }

        @Test
        @DisplayName("Setters devem validar e não alterar o estado quando o valor é inválido")
        void settersDevemValidar() {
            Funcionario f = new FuncionarioFake(1, "Ana", CPF_VALIDO, 1000);
            assertThrows(DadoInvalidoException.class, () -> f.setNome(""));
            assertThrows(CpfInvalidoException.class, () -> f.setCpf("11111111111"));
            assertThrows(DadoInvalidoException.class, () -> f.setSalarioBase(-50));
            assertEquals("Ana", f.getNome());
            assertEquals(CPF_VALIDO, f.getCpf());
            assertEquals(1000, f.getSalarioBase());
        }
    }

    @Nested
    @DisplayName("Igualdade e representação")
    class IgualdadeERepresentacao {

        @Test
        @DisplayName("Funcionários com o mesmo código devem ser iguais")
        void equalsPorCodigo() {
            Funcionario a = new FuncionarioFake(5, "Ana", CPF_VALIDO, 1000);
            Funcionario b = new FuncionarioFake(5, "Bruno", "11144477735", 2000);
            Funcionario c = new FuncionarioFake(6, "Ana", CPF_VALIDO, 1000);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
            assertNotEquals(a, null);
        }

        @Test
        @DisplayName("exibirDados deve conter código, nome, CPF formatado e remuneração")
        void exibirDados() {
            String dados = new FuncionarioFake(1, "Ana", CPF_VALIDO, 1500).exibirDados();
            assertTrue(dados.contains("Ana"));
            assertTrue(dados.contains("529.982.247-25"));
            assertTrue(dados.contains("R$ 1.500,00"));
        }

        @Test
        @DisplayName("toString deve resumir código, nome, tipo e remuneração")
        void toStringResumo() {
            assertEquals("[1] Ana (Fake) - R$ 1.500,00",
                    new FuncionarioFake(1, "Ana", CPF_VALIDO, 1500).toString());
        }
    }

    @Nested
    @DisplayName("Polimorfismo por sobreposição")
    class Polimorfismo {

        @Test
        @DisplayName("Referência do tipo Funcionario deve executar a implementação do objeto real")
        void deveExecutarImplementacaoDoObjetoReal() {
            Funcionario funcionario1 = new FuncionarioCLT(1, "Ana", CPF_VALIDO, 3000, 500);
            Funcionario funcionario2 = new FuncionarioPJ(2, "Bruno", "11144477735", 2000, 3);
            Funcionario funcionario3 = new FuncionarioComissionado(3, "Carla", "52601815906", 2000, 5, 20000);

            assertEquals(3500.0, funcionario1.calcularRemuneracao(), 0.001);
            assertEquals(6000.0, funcionario2.calcularRemuneracao(), 0.001);
            assertEquals(3000.0, funcionario3.calcularRemuneracao(), 0.001);
        }

        @Test
        @DisplayName("Cada subclasse deve informar seu próprio tipo")
        void cadaSubclasseTemSeuTipo() {
            List<Funcionario> lista = List.of(
                    new FuncionarioCLT(1, "Ana", CPF_VALIDO, 3000, 500),
                    new FuncionarioPJ(2, "Bruno", "11144477735", 2000, 3),
                    new FuncionarioComissionado(3, "Carla", "52601815906", 2000, 5, 20000));

            assertEquals(List.of("CLT", "PJ", "Comissionado"),
                    lista.stream().map(Funcionario::getTipo).toList());
        }
    }
}
