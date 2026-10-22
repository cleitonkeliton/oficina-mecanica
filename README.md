# Oficina Mecânica

Sistema web de uma oficina mecânica, em Java com Spring Boot e MySQL. Tem quatro partes:

| Parte | Endereço | Quem usa | O que faz |
|---|---|---|---|
| **Site (tela principal)** | `/` | Qualquer pessoa | Serviços com valores, sobre a oficina, diferenciais, horários, formas de pagamento, endereço, telefone, WhatsApp e redes sociais |
| **Administração** | `/admin` | Administrador | Painel com a situação dos carros; edita as informações do site; cadastra serviços e preços; cria usuários da equipe. Também tem acesso a tudo da recepção |
| **Recepção** | `/recepcao` | Recepção e administrador | Carros na oficina e a situação de cada um; registra a entrada de veículos; cadastra clientes e veículos; lança serviços e peças; atualiza a situação |
| **Área do cliente** | `/area-cliente/login` | Dono do carro | Entra com o CPF e a senha que a recepção passou e acompanha a situação do carro, o orçamento, a previsão de entrega e o histórico |

## Situações do carro

A recepção muda a situação na ordem de serviço (ou direto no painel) e pode deixar uma observação. O cliente vê na hora, com uma barra de etapas e a linha do tempo:

`Aguardando avaliação` → `Em orçamento` → `Aguardando aprovação do cliente` → `Aguardando peças` → `Em manutenção` → `Pronto para retirada` → `Entregue` (ou `Cancelado`)

## Como rodar

Precisa de **Java 21 ou mais novo** (o projeto também roda no JDK 24), **Maven** e **MySQL 8**.

1. Crie o banco e o usuário no MySQL (só na primeira vez):

   ```sql
   CREATE DATABASE oficina_mecanica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER 'usuario_oficina'@'localhost' IDENTIFIED BY 'Qweasd@32';
   GRANT ALL PRIVILEGES ON oficina_mecanica.* TO 'usuario_oficina'@'localhost';
   ```

   As tabelas são criadas automaticamente quando o sistema sobe. Se o banco já tinha as tabelas da versão anterior, elas são aproveitadas: clientes e serviços continuam lá (os serviços antigos passam a usar a descrição como nome). As tabelas antigas `veiculo`, `ordem_servico` e `ordem_servico_servico` não são mais usadas.

2. Rode o sistema:

   ```bash
   mvn spring-boot:run
   ```

   Ou, pelo IntelliJ, execute a classe `com.oficina.OficinaApplication`.

3. Abra <http://localhost:8080>.

### Primeiro acesso

Na primeira vez, o sistema cria o texto padrão do site, alguns serviços de exemplo e dois usuários:

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Recepção | `recepcao` | `recepcao123` |

**Troque essas senhas** logo depois de entrar (link "Trocar senha" no topo). Depois, em *Informações do site*, coloque o nome, endereço, telefone e WhatsApp de verdade da oficina, e em *Serviços e preços* ajuste os valores.

### Acesso do cliente

No cadastro do cliente (Recepção → Clientes), preencha a senha de acesso ou use o botão "Gerar senha" (8 letras e números). O cliente entra em "Acompanhar meu carro" com o **CPF** e essa senha.

Se a recepção remover o acesso ou trocar a senha, a sessão que o cliente tiver aberta cai na hora. O mesmo vale para usuários da equipe desativados ou com a senha redefinida pelo administrador. Depois de 5 senhas erradas seguidas, aquele login fica bloqueado por 15 minutos.

### Configuração

Os dados do banco estão em `src/main/resources/application.properties`. Para trocar sem mexer no arquivo, use variáveis de ambiente:

| Variável | Para quê | Padrão |
|---|---|---|
| `DB_URL` | Endereço JDBC do MySQL | `jdbc:mysql://localhost:3306/oficina_mecanica?...` |
| `DB_USERNAME` | Usuário do banco | `usuario_oficina` |
| `DB_PASSWORD` | Senha do banco | `Qweasd@32` |
| `PORT` | Porta do site | `8080` |
| `ADMIN_SENHA` / `RECEPCAO_SENHA` | Senhas dos usuários criados no primeiro start | `admin123` / `recepcao123` |
| `OFICINA_FUSO` | Fuso horário das datas e horas | `America/Sao_Paulo` |

## Testes

```bash
mvn test
```

Os testes não precisam do MySQL: cobrem as regras de CPF e placa, as regras da ordem de serviço (entrada, mudança de situação, travas, limites), a troca de dono do veículo, o bloqueio de login e o controle de acesso e de sessão de cada perfil.

## Estrutura do código

```
src/main/java/com/oficina
├── config/       segurança (login e permissões) e dados iniciais
├── controller/   telas: site, login, admin, recepção e área do cliente
├── form/         formulários das telas
├── model/        entidades do banco (Cliente, Veiculo, OrdemServico, Servico...)
├── repository/   acesso ao banco (Spring Data JPA)
├── security/     usuário logado
├── service/      regras do sistema
└── util/         CPF, placa e textos
src/main/resources
├── templates/    páginas HTML (Thymeleaf)
└── static/       CSS, JavaScript e imagens
```

## Conceitos usados

- **Programação orientada a objetos**: entidades com encapsulamento (`Cliente`, `Veiculo`, `OrdemServico`), enums com comportamento (`StatusServico` sabe sua etapa e se está finalizado).
- **Camadas**: controller (tela) → service (regras) → repository (banco).
- **Tratamento de exceções**: `RegraNegocioException` leva a mensagem de erro até o campo certo do formulário.
- **Segurança**: senhas criptografadas com BCrypt, proteção CSRF nos formulários, permissões por perfil, bloqueio após senhas erradas, sessão derrubada quando o acesso muda, e o cliente só enxerga as próprias ordens.
- **Concorrência**: a ordem de serviço é travada enquanto alguém a altera, assim duas pessoas mexendo ao mesmo tempo não apagam a mudança uma da outra.
