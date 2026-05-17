# NexBill — Billing Service

Microserviço responsável pelo gerenciamento de faturamento do ecossistema **NexBill**.

O projeto foi desenvolvido com foco em:

- Clean Architecture
- Domain-Driven Design (DDD)
- Event-Driven Architecture
- Separação de responsabilidades
- Escalabilidade e manutenibilidade

---

# 📚 Tecnologias

| Tecnologia | Descrição |
| --- | --- |
| Java 17 | Linguagem principal |
| Spring Boot 4.0.6 | Framework backend |
| Spring Web MVC | APIs REST |
| Spring Data JPA | Persistência |
| PostgreSQL 16 | Banco de dados |
| RabbitMQ | Mensageria |
| Docker | Containers |
| Maven | Gerenciamento de dependências |

---

# 🏗️ Arquitetura

O projeto segue princípios de **Clean Architecture**, mantendo as regras de negócio desacopladas de frameworks, banco de dados e detalhes externos.

```
presentation
    ↓
application
    ↓
domain
    ↑
infra
```

---

# 📂 Estrutura do Projeto

```
src/main/java/com/nexbill/billing_service
│
├── application/
│   ├── event/
│   └── usecase/
│
├── domain/
│   ├── aggregate/
│   ├── entity/
│   ├── enums/
│   ├── event/
│   ├── repository/
│   └── valueobject/
│
├── infra/
│   ├── cache/
│   ├── integration/
│   ├── messaging/
│   └── persistence/
│
└── presentation/
```

---

# 📌 Camadas

## Domain

Camada central da aplicação.

Responsável por:

- regras de negócio
- aggregates
- entidades
- value objects
- domain events
- contratos de repositório

A camada de domínio não depende de frameworks externos.

---

## Application

Responsável pelos casos de uso da aplicação.

Contém:

- orchestration dos fluxos
- use cases
- publishers de eventos
- processors

Exemplo:

```
CreateInvoiceUseCase
```

---

## Infra

Responsável pelas implementações externas.

Contém:

- persistência JPA
- RabbitMQ
- integrações externas
- adapters

---

## Presentation

Responsável pela entrada HTTP da aplicação.

Contém:

- controllers REST
- DTOs
- validações de request

---

# 💰 Modelo de Domínio

## Invoice (Aggregate Root)

Representa uma fatura do sistema.

### Responsabilidades

- gerenciar expenditures
- controlar status
- validar fluxo de criação
- calcular total da invoice
- registrar domain events

---

## Expenditure

Representa um gasto associado à invoice.

### Responsabilidades

- validar dados
- representar valores monetários
- controlar status individual

---

## Money (Value Object)

Representa um valor monetário imutável.

### Características

- escala fixa de 2 casas decimais
- HALF_EVEN rounding
- operações seguras
- evita inconsistências monetárias

---

# 🔄 Fluxo de Criação de Invoice

```
HTTP Request
    ↓
Controller
    ↓
CreateInvoiceUseCase
    ↓
Invoice Aggregate
    ↓
Repository Interface
    ↓
Persistence Adapter
    ↓
PostgreSQL
    ↓
Domain Event
    ↓
RabbitMQ
    ↓
ERP Processor
```

---

# 📡 API REST

## Criar Invoice

### Endpoint

```
POST /v1/invoice
```

---

## Exemplo de Request

```
{
  "type":"WHOLESALE",
  "source":"ENERGY",
  "period":"2025-05",
  "createdBy":"joao",
  "expenditures": [
    {
      "name":"Energia",
      "description":"Consumo mensal",
      "price":1200.50
    }
  ]
}
```

---

## Response

```
202 Accepted
```

---

# 🐘 PostgreSQL

## Configuração padrão

| Configuração | Valor |
| --- | --- |
| Porta | 3003 |
| Database | billing_db |
| User | billing_user |
| Password | billing_pass |

---

# 🐇 RabbitMQ

## Configuração padrão

| Configuração | Valor |
| --- | --- |
| AMQP Port | 5672 |
| Management Port | 15672 |
| User | admin |
| Password | admin |

---

# ⚙️ Variáveis de Ambiente

O projeto utiliza variáveis de ambiente para configuração local.

## Exemplo `.env`

```
APP_NAME=billing-service
APP_PORT=8080

DB_HOST=localhost
DB_PORT=3003
DB_NAME=billing_db
DB_USERNAME=billing_user
DB_PASSWORD=billing_pass

RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin
```

---

# 🚀 Executando o Projeto

## 1. Subir infraestrutura

```
docker compose up-d
```

---

## 2. Rodar aplicação

Linux/Mac:

```
./mvnw spring-boot:run
```

Windows:

```
mvnw.cmd spring-boot:run
```

---

# 🧪 Testes

Executar testes:

```
./mvnw test
```

---

# 📌 Convenções do Projeto

## REST

Todos os endpoints seguem:

```
/v1/
```

---

## Use Cases

Padrão utilizado:

```
application.usecase.<contexto>.<acao>
```

Exemplo:

```
application.usecase.invoice.create
```

---

## Persistência

Use cases dependem de abstrações do domínio, nunca diretamente de:

- JPA
- Hibernate
- Spring Data

---

## Eventos

Fluxo assíncrono planejado:

```
Domain Event
    ↓
Publisher
    ↓
RabbitMQ
    ↓
Consumer
    ↓
ERP Processor
```

---

# 📈 Roadmap

- [ ]  Integração completa com RabbitMQ
- [ ]  Retry para integração ERP
- [ ]  Outbox Pattern
- [ ]  Observabilidade
- [ ]  Testes de integração
- [ ]  CI/CD
- [ ]  Kubernetes deployment

---

# 👨‍💻 Autor

João Ribeiro

---

# 📄 Licença

Projeto desenvolvido para fins de estudo, aprendizado e evolução arquitetural utilizando microsserviços e Clean Architecture.