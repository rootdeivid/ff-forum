# FF Forum — Projeto Individual Web (Frontend Vanilla + Spring Boot + JdbcTemplate + H2)

Tema: **FF Forum / Aventureiro** — cadastro de aventureiros de Eorzea.

## Estrutura
```
ff-forum/
├── src/                      → Backend Spring Boot (API REST)
│   ├── main/java/sptech/school/ff_forum/
│   │   ├── FfForumApplication.java
│   │   └── controller/{UsuarioController, ClasseController}.java
│   └── main/resources/{application.properties, schema.sql, data.sql}
├── frontend/                 → Frontend Vanilla (HTML/CSS/JS) - ATIVO
│   ├── cadastro.html         → Formulário completo (texto, numérico, data, radio, checkbox, select dinâmico)
│   ├── login.html
│   ├── index.html
│   ├── css/ js/ assets/
│   └── README.md
├── FF-Projeto/               → Original preservado (agora só frontend, backend Node removido)
│   └── public/               → cópia original
├── FFProject---BD/           → Modelagem e SQL original (referência)
└── pom.xml
```

## Como executar o backend
Pré-requisito: Java 21, Maven 3.9+

```bash
# na raiz ff-forum/
./mvnw spring-boot:run
# ou mvn spring-boot:run
```
- API em `http://localhost:8080`
- H2 Console em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:ffdb`, user `sa`, senha vazia)
- Banco em memória com `schema.sql` + `data.sql` carregados automaticamente.

## Como executar o frontend
- Com Live Server (VS Code): botão "Go Live" apontando para `frontend/cadastro.html`
  - Ex: `http://127.0.0.1:5500/frontend/index.html`
- Ou: `npx serve frontend` / `python -m http.server` dentro de `frontend/`
- O frontend consome `http://localhost:8080/api/*` (CORS liberado com `@CrossOrigin(origins="*")`)

## Endpoints da API

Base: `http://localhost:8080`

| Método | URL | Descrição | Status |
|--------|-----|-----------|--------|
| GET | `/api/classes` | Lista classes para dropdown dinâmico | 200 OK, 204 No Content |
| GET | `/api/usuarios` | Lista todos os usuários | 200, 204 |
| GET | `/api/usuarios/{id}` | Busca por ID | 200, 404 Not Found |
| POST | `/api/usuarios` | Cadastra usuário | 201 Created, 400 Bad Request |
| POST | `/api/usuarios/login` | Autentica | 200, 400, 401 Unauthorized |

### Exemplos

**GET /api/classes (dropdown)**
```bash
curl http://localhost:8080/api/classes
# 200 [{"ID":1,"NOME":"Mago","TIPO":"Magia"}, ...]
```

**POST /api/usuarios**
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome":"Deivid",
    "apelido":"Mywan",
    "email":"deivid@email.com",
    "senha":"senha123",
    "dataNascimento":"2000-01-15",
    "raca":"Au Ra",
    "classeId":5,
    "dataCenter":"Light",
    "nivel":80,
    "aceitaTermos":true
  }'
# 201 {"id":1,"mensagem":"Cadastro realizado com sucesso"}
# 400 {"erro":"Email ja cadastrado"} ou {"erro":"Nivel deve ser entre 1 e 100"}
```

**POST /api/usuarios/login**
```bash
curl -X POST http://localhost:8080/api/usuarios/login \
  -H "Content-Type: application/json" \
  -d '{"email":"deivid@email.com","senha":"senha123"}'
# 200 {"ID":1,"NOME":"Deivid","EMAIL":"deivid@email.com"}
# 401 {"erro":"Email ou senha invalidos"}
```

**GET /api/usuarios/1**
```bash
curl http://localhost:8080/api/usuarios/1
# 200 { ... }
# 404 {"erro":"Usuario nao encontrado"}
```

## Persistência
- H2 em memória (relacional, atende requisito). Tabelas:
  - `classe(id, nome, tipo)` — apoio para select
  - `usuario(id, nome, apelido, email UNIQUE, senha, data_nascimento, raca, classe_id FK, data_center, nivel, aceita_termos)`
- Script: `src/main/resources/schema.sql` e `data.sql`
- Acesso via `JdbcTemplate` (sem JPA) — simples, conforme restrição.

## Validação
- **Front**: `cadastro.html` valida obrigatórios, formatos, `@` no email, senha >=6 e confirmação, data não futura, radio selecionado, classe selecionada, nivel 1-100, checkbox termos. Bloqueia `fetch` se inválido.
- **Back**: `UsuarioController.validar()` replica todas as regras + verifica FK `classe_id` existe + email único. Mesmo via Postman/curl, retorna `400`.

## CORS
`@CrossOrigin(origins="*")` nos controllers. Frontend (Live Server porta 5500) → Backend (8080) sem bloqueio.

## Integração
- Frontend usa `fetch()` nativo com `API_URL = "http://localhost:8080"`
- Dropdown em `cadastro.html` populado via `GET /api/classes` no `onload`.

## Remoção do backend Node
`FF-Projeto/src`, `app.js`, `package.json` removidos. `FF-Projeto/README.md` explica. Frontend ativo é `frontend/`.
# ff-forum
