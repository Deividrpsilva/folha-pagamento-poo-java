# Sistema de Gerenciamento de Funcionários e Folha de Pagamento

Projeto da atividade avaliativa de **Programação Orientada a Objetos em Java**.
O sistema cadastra funcionários de diferentes categorias, calcula a remuneração
de cada um, gera o relatório da folha e calcula o total pago pela empresa.

Foi desenvolvido com **TDD** (ciclo *Red → Green → Refactor*), usando **JUnit 5**
e **Maven**, com o código organizado **em camadas**.

---

## Funcionalidades

| # | Funcionalidade | Onde está |
|---|----------------|-----------|
| 1 | Cadastrar funcionários (CLT, PJ ou Comissionado) | `FuncionarioService.cadastrar` |
| 2 | Listar todos os funcionários | `FuncionarioService.listarTodos` |
| 3 | Buscar por código, CPF ou nome | `FuncionarioService.buscarFuncionario` (sobrecarga) |
| 4 | Remover funcionário | `FuncionarioService.remover` |
| 5 | Calcular a remuneração de um funcionário | `FuncionarioService.calcularRemuneracao` |
| 6 | Exibir os dados completos | `FuncionarioService.exibirDados` |
| 7 | Relatório com todos os funcionários e salários | `FuncionarioService.gerarRelatorio` |
| 8 | Total da folha de pagamento | `FuncionarioService.calcularTotalFolha` |

Ao iniciar, o programa já carrega três funcionários de exemplo (um de cada
categoria) para facilitar a demonstração.

---

## Regras de remuneração

| Categoria | Remuneração bruta | Tributos/descontos (`Tributavel`) |
|-----------|-------------------|-----------------------------------|
| **CLT** | salário-base + benefícios | 11% sobre o salário-base |
| **PJ** | valor por projeto × quantidade de projetos | não se aplica* |
| **Comissionado** | salário-base + (vendas × % comissão ÷ 100) | 11% sobre a remuneração total |

\* O PJ **não** implementa `Tributavel`: nesta modelagem, quem recolhe os tributos
é a própria pessoa jurídica que emite a nota fiscal, e não a empresa na folha.
É o caso de "implementar a interface somente onde fizer sentido".

> As alíquotas são simplificações didáticas, não a tabela oficial do INSS/IR.

---

## Como os conceitos de POO foram aplicados

**Encapsulamento** — todos os atributos são `private`; os setters e construtores
validam os dados (CPF, campos obrigatórios, valores negativos, percentual de 0 a 100).
O código do funcionário é `final` (não muda depois de criado), e os setters são
`final` para que nenhuma subclasse possa burlar as validações.

**Herança e abstração** — `Funcionario` é abstrata (código, nome, CPF, salário-base)
e declara o método abstrato `calcularRemuneracao()`. As subclasses
`FuncionarioCLT`, `FuncionarioPJ` e `FuncionarioComissionado` implementam cada uma
a sua regra.

**Interfaces** — `Identificavel` (`getCodigo()`), implementada por `Funcionario`
e herdada por todas as classes concretas; `Tributavel` (`calcularTributos()`),
implementada por CLT e Comissionado.

**Polimorfismo por sobreposição** — com uma referência do tipo `Funcionario`, o
Java executa a versão de `calcularRemuneracao()` do objeto real:

```java
Funcionario f1 = new FuncionarioCLT(1, "Ana", "529.982.247-25", 3000, 500);
Funcionario f2 = new FuncionarioPJ(2, "Bruno", "111.444.777-35", 2000, 3);
Funcionario f3 = new FuncionarioComissionado(3, "Carla", "526.018.159-06", 2000, 5, 20000);

System.out.println(f1.calcularRemuneracao()); // 3500.0
System.out.println(f2.calcularRemuneracao()); // 6000.0
System.out.println(f3.calcularRemuneracao()); // 3000.0
```

**Polimorfismo por sobrecarga** — na `FuncionarioService`:

```java
buscarFuncionario(int codigo);                    // pelo código
buscarFuncionario(String cpf);                    // pelo CPF (com ou sem máscara)
buscarFuncionario(String nome, boolean buscaExata); // pelo nome (exato ou parcial)
```

A busca por nome ignora maiúsculas/minúsculas e acentos ("joao" encontra "João").

---

## Tratamento de exceções

| Exceção | Tipo | Quando é lançada |
|---------|------|------------------|
| `FuncionarioNaoEncontradoException` | checked | buscar, remover ou consultar funcionário inexistente |
| `FuncionarioJaCadastradoException` | checked | cadastrar código ou CPF já existentes |
| `DadoInvalidoException` | unchecked | dado obrigatório ausente, valor negativo, percentual fora de 0–100, número digitado errado |
| `CpfInvalidoException` | unchecked (subclasse de `DadoInvalidoException`) | CPF com formato ou dígitos verificadores inválidos |

As exceções são **lançadas** no model e no service e **tratadas** na view, que
mostra uma mensagem amigável e continua o programa sem encerrar.

---

## Estrutura do projeto

```
src/
├── main/java/
│   ├── model/        Funcionario, FuncionarioCLT, FuncionarioPJ,
│   │                 FuncionarioComissionado, Tributavel, Identificavel
│   ├── repository/   FuncionarioRepository   (armazenamento em memória — LinkedHashMap)
│   ├── service/      FuncionarioService      (regras de negócio)
│   ├── exception/    exceções personalizadas
│   ├── view/         FuncionarioView         (menu de console)
│   ├── util/         ValidadorCpf, Validacoes (validações reutilizáveis)
│   └── Main.java     monta as camadas e inicia o menu
└── test/java/        uma classe de teste para cada classe de produção
    ├── model/  repository/  service/  exception/  view/  util/
```

| Camada | Responsabilidade |
|--------|------------------|
| Model | Entidades e regras de cálculo de cada categoria |
| Repository | Guardar e recuperar funcionários |
| Service | Regras de negócio (duplicidade, buscas, folha, relatório) |
| View | Interação com o usuário e tratamento das exceções |
| Exception | Situações excepcionais do sistema |

---

## Como executar

Pré-requisitos: **Java 17+** e **Maven 3.8+**.

```bash
# compilar e executar o menu
mvn compile exec:java

# ou gerar o .jar e executar
mvn package
java -jar target/folha-pagamento-1.0.0.jar
```

No IntelliJ, Eclipse ou NetBeans: importe a pasta como **projeto Maven** e
execute a classe `Main`.

## Como executar os testes

```bash
mvn test
```

Na IDE: clique com o botão direito em `src/test/java` → *Run All Tests*.

São **137 testes** cobrindo: cálculos de todas as categorias, validações (casos de
sucesso e de falha), lançamento das exceções personalizadas, comportamento
polimórfico, interfaces, repositório, serviço e o menu (simulando a digitação
do usuário).

---

## Histórico de desenvolvimento (TDD)

Os commits mostram o ciclo do TDD para cada parte do sistema:

- `test: ... (RED)` — os testes são escritos primeiro e falham, porque a classe ainda não existe;
- `feat: ... (GREEN)` — a implementação mínima faz os testes passarem;
- `refactor: ...` — melhoria do código mantendo todos os testes verdes.

Veja com `git log --oneline`.
