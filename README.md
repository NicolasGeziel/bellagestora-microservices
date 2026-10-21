# BellaGestora

A **BellaGestora** é uma plataforma SaaS de gestão desenvolvida para profissionais da beleza e trabalhadores autônomos. A plataforma permite gerenciar procedimentos, agendamentos e usuários em um único sistema.

O projeto evoluiu de uma arquitetura monolítica para uma **arquitetura de microsserviços orientada a eventos**, com o objetivo de melhorar a escalabilidade, a organização do código e a separação de responsabilidades entre os serviços.

Toda a infraestrutura é executada com Docker e Docker Compose, incluindo as aplicações, os bancos de dados e o sistema de mensageria. Cada serviço possui responsabilidades específicas e seu próprio banco de dados, promovendo maior isolamento e facilitando a manutenção do sistema.

## Tecnologias e Arquitetura

### Back-end — Microsserviços

- **Java 21** — Linguagem de programação.
- **Spring Boot 3** — Desenvolvimento das APIs e dos microsserviços.
- **Spring Cloud Gateway** — Ponto de entrada único para as requisições do front-end e responsável pelo roteamento entre os serviços.
- **Spring Security e JWT** — Autenticação e proteção dos recursos da aplicação por meio de tokens.
- **RabbitMQ** — Comunicação assíncrona entre os microsserviços por meio de mensagens e eventos.
- **PostgreSQL** — Banco de dados relacional, com bancos isolados por serviço.
- **Maven** — Gerenciamento de dependências e compilação dos projetos Java.

### Front-end

- **Angular** — Desenvolvimento da interface da aplicação.
- **Standalone Components** — Organização modular dos componentes, sem a necessidade de declarar todos eles em módulos tradicionais.
- **Design responsivo** — Interface adaptável a diferentes tamanhos de tela.
- **Nginx** — Servidor web utilizado para disponibilizar o front-end em produção.

### Infraestrutura e DevOps

- **Docker** — Empacotamento e execução das aplicações em contêineres.
- **Docker Compose** — Orquestração dos serviços, bancos de dados e sistema de mensageria.

## Estrutura dos Microsserviços

O back-end foi dividido em três serviços principais, cada um com uma responsabilidade bem definida.

### 1. API Gateway — `/api-gateway`

**Porta: `8082`**

É o ponto de entrada das requisições do front-end para o back-end.

Responsabilidades:
- Receber as requisições enviadas pelo front-end.
- Encaminhar cada requisição para o microsserviço correspondente.
- Centralizar as configurações de CORS (*Cross-Origin Resource Sharing*).
- Evitar que o cliente precise acessar diretamente as portas internas dos serviços.

### 2. Auth Service — `/auth-service`

**Porta: `8081`**

É responsável pelo gerenciamento da autenticação e dos usuários.

Responsabilidades:
- Realizar o cadastro de usuários.
- Validar as credenciais durante o login.
- Gerar tokens JWT para autenticação.
- Publicar eventos de criação de usuários no RabbitMQ.
- Gerenciar seu próprio banco de dados, chamado `db_auth`.

### 3. Core Service — `/core-service`

**Porta: `8080`**

É responsável pelas principais regras de negócio da plataforma.

Responsabilidades:
- Gerenciar o catálogo de procedimentos.
- Gerenciar os agendamentos.
- Aplicar as regras de controle de acesso.
- Validar os tokens JWT recebidos nas requisições.
- Consumir eventos do RabbitMQ para manter uma cópia local dos dados de usuários necessários às operações do serviço.
- Gerenciar seu próprio banco de dados, chamado `db_core`.

Essa separação permite que cada serviço gerencie seus próprios dados e responsabilidades, reduzindo o acoplamento entre os componentes da aplicação.

## Funcionalidades Principais

### Autenticação e Segurança

- Cadastro e login de usuários.
- Geração e validação de tokens JWT.
- Autenticação sem necessidade de manter uma sessão de usuário no servidor (*stateless*).
- Proteção de endpoints por meio do Spring Security.

### Comunicação Assíncrona

- Integração entre microsserviços utilizando RabbitMQ.
- Publicação e consumo de eventos.
- Replicação dos dados necessários para consulta de usuários entre os serviços, reduzindo a dependência de chamadas diretas entre eles.

### Controle de Acesso — RBAC

O sistema utiliza RBAC (*Role-Based Access Control*), um modelo de controle de acesso baseado em papéis.

- **ADMIN:** pode gerenciar os procedimentos e controlar os agendamentos.
- **USER:** pode consultar os procedimentos e solicitar agendamentos.

As permissões são aplicadas de acordo com o papel de cada usuário.

### Catálogo de Procedimentos

- Cadastro, consulta, atualização e exclusão de procedimentos (CRUD).
- Gerenciamento dos procedimentos pelo administrador.
- Barra de pesquisa dinâmica para facilitar a localização dos procedimentos.

### Gestão de Agendamentos

- Clientes podem solicitar agendamentos.
- Administradores podem confirmar ou cancelar solicitações.
- O controle das alterações de status segue as permissões definidas para cada tipo de usuário.

## Como Executar o Projeto

A aplicação utiliza Docker Compose para iniciar e integrar os microsserviços, os bancos de dados PostgreSQL, o RabbitMQ e o front-end.

### Pré-requisitos

Antes de começar, verifique se você possui as seguintes ferramentas instaladas:

- [Git](https://git-scm.com/)
- [Java 21 (JDK)](https://adoptium.net/)
- [Maven](https://maven.apache.org/) ou o Maven Wrapper disponibilizado pelo projeto.
- [Node.js e npm](https://nodejs.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) ou Docker Engine com Docker Compose.

### 1. Compilar os microsserviços Java

Antes de iniciar os contêineres, compile os três projetos Java para gerar os arquivos `.jar`.

Abra um terminal dentro de cada uma das pastas abaixo e execute o comando:

```bash
mvn clean package -DskipTests
```

Pastas que devem ser compiladas:

- `/api-gateway`
- `/auth-service`
- `/core-service`

**Importante:** esse comando ignora a execução dos testes automatizados. Ele é útil para gerar os arquivos `.jar` rapidamente, mas não substitui a execução dos testes antes de disponibilizar o sistema em produção.

### 2. Configurar as credenciais

Na raiz do projeto, no mesmo nível do arquivo `docker-compose.yml`, crie um arquivo chamado `.env`.

Adicione as seguintes variáveis:

```dotenv
DB_PASSWORD=sua_senha_segura_do_postgres
RABBITMQ_PASSWORD=sua_senha_segura_do_rabbitmq
```

Substitua os valores de exemplo por senhas seguras.

**Atenção:** não compartilhe suas credenciais e não envie o arquivo `.env` para o repositório Git. Adicione esse arquivo ao `.gitignore` para evitar o envio acidental das senhas.

As variáveis e os nomes utilizados devem corresponder às configurações definidas no `docker-compose.yml` e nos arquivos de configuração dos serviços.

### 3. Iniciar a aplicação

Abra um terminal na raiz do projeto, onde está localizado o arquivo `docker-compose.yml`, e execute:

```
docker compose up -d --build
```
Esse comando solicita ao Docker Compose que construa as imagens necessárias e inicie os serviços em segundo plano.
Na primeira inicialização, aguarde até que os bancos de dados, o RabbitMQ e as aplicações estejam prontos para receber requisições.

### 4. Acessar a aplicação

Após a inicialização dos serviços, os endereços esperados são:

| Componente | Endereço | Descrição |
|---|---|---|
| Front-end | [http://localhost:4200](http://localhost:4200) | Interface da aplicação Angular |
| API Gateway | [http://localhost:8082](http://localhost:8082) | Ponto de entrada do back-end |
| RabbitMQ | [http://localhost:15672](http://localhost:15672) | Painel de gerenciamento da mensageria |
| PostgreSQL | `localhost:5433` | Acesso externo ao banco de dados, se configurado |

**Credenciais do RabbitMQ:** o usuário e a senha dependem das configurações definidas no `docker-compose.yml` e no arquivo `.env`. O exemplo de configuração utiliza o usuário `admin` e a senha definida em `RABBITMQ_PASSWORD`.

**Observação:** as portas e os endereços acima dependem do mapeamento configurado no Docker Compose. O PostgreSQL, por exemplo, precisa ter a porta `5433` do computador mapeada para a porta interna do contêiner para aceitar conexões externas nesse endereço.

## Execução Local para Desenvolvimento

Se você quiser depurar o código diretamente pela IDE, sem executar as aplicações Spring Boot dentro de contêineres, siga estas etapas.

### 1. Iniciar as dependências

Verifique se o PostgreSQL e o RabbitMQ estão em execução e acessíveis pelas aplicações.

Você pode utilizar instalações locais ou iniciar apenas essas dependências por meio do Docker.

### 2. Configurar as variáveis de ambiente

Configure as variáveis necessárias nas configurações de execução da sua IDE, como:

```dotenv
DB_PASSWORD=sua_senha_do_banco
RABBITMQ_PASSWORD=sua_senha_do_rabbitmq
```

Verifique também as demais variáveis exigidas pelos arquivos de configuração de cada serviço, incluindo URLs dos bancos de dados, nomes dos bancos e configurações de JWT, quando aplicável.

### 3. Iniciar os microsserviços

Execute as três aplicações Spring Boot pela IDE, nesta ordem sugerida:

1. `Auth Service` — porta `8081`.
2. `Core Service` — porta `8080`.
3. `API Gateway` — porta `8082`.

Aguarde a inicialização de cada serviço e confira os logs caso ocorra algum erro de conexão ou configuração.

### 4. Iniciar o front-end

Abra um terminal na pasta `/frontend` e execute:

```bash
npm install
npm start
```

Depois que o servidor de desenvolvimento do Angular iniciar, acesse:

[http://localhost:4200](http://localhost:4200)

Verifique se a URL utilizada pelo front-end aponta para o API Gateway e se as configurações de CORS permitem as requisições do ambiente local.

## Encerrando a Aplicação

Para parar os contêineres iniciados pelo Docker Compose, execute na raiz do projeto:

```bash
docker compose down
```

Esse comando interrompe e remove os contêineres criados pelo projeto, mas normalmente mantém os volumes persistentes dos bancos de dados.

Para apagar também os volumes associados ao ambiente, utilize:

```bash
docker compose down -v
```

**Atenção:** o segundo comando pode excluir permanentemente os dados armazenados nos volumes do projeto. Utilize-o somente quando tiver certeza de que não precisa preservar esses dados.

## Objetivo do Projeto

A BellaGestora foi desenvolvida para aplicar conceitos e tecnologias utilizados no desenvolvimento de sistemas distribuídos, incluindo:

- Arquitetura de microsserviços.
- Comunicação assíncrona orientada a eventos.
- APIs REST com Spring Boot.
- Autenticação e autorização com Spring Security e JWT.
- Bancos de dados isolados por serviço.
- Containerização e orquestração com Docker Compose.
- Desenvolvimento de interfaces web com Angular.

O projeto busca oferecer uma base organizada e escalável para a evolução de uma plataforma de gestão voltada a profissionais autônomos da área da beleza.
