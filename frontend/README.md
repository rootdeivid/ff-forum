# Frontend - FF Forum

Frontend Vanilla (HTML5, CSS3, JS) - sem frameworks.

## Páginas
- `index.html` - Home + lista de aventureiros (GET /api/usuarios)
- `cadastro.html` - Formulário completo de cadastro (POST /api/usuarios)
- `login.html` - Login (POST /api/usuarios/login)

## Tipos de entrada (requisito)
- Texto: nome, apelido, email
- Numérico: nivel (1-100)
- Data: data_nascimento
- Radio: raca (Hyur, Miqo'te, Elezen, Roegadyn, Lalafell, Au Ra, Hrothgar, Viera)
- Checkbox: aceita_termos, newsletter
- Dropdown (select) dinâmico: classe - carregado via `GET http://localhost:8080/api/classes` com `fetch()`

## Como executar
1. Inicie o backend em `http://localhost:8080` (ver README raiz)
2. Abra este diretório com **Live Server** (VS Code) ou `npx serve .`
   - Ex: `http://127.0.0.1:5500/frontend/cadastro.html` ou `http://127.0.0.1:5500/frontend/index.html`
3. O navegador fará requisições para `http://localhost:8080/api/*` (CORS já configurado no Spring)

## Validação front-end
Feita em `cadastro.html: validarFront()` antes do `fetch` - verifica obrigatórios, formatos, tamanhos, datas.

## Integração
- `API_URL = "http://localhost:8080"`
- Usa `fetch()` nativo.
