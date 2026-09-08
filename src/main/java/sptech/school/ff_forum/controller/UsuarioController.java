package sptech.school.ff_forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Map<String, Object>> usuarios = jdbcTemplate.queryForList(
                "SELECT u.id, u.nome, u.apelido, u.email, u.data_nascimento, u.raca, u.classe_id, c.nome as classe_nome, u.data_center, u.nivel FROM usuario u LEFT JOIN classe c ON u.classe_id = c.id ORDER BY u.id"
        );
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                "SELECT u.id, u.nome, u.apelido, u.email, u.data_nascimento, u.raca, u.classe_id, c.nome as classe_nome, u.data_center, u.nivel FROM usuario u LEFT JOIN classe c ON u.classe_id = c.id WHERE u.id = ?", id
        );
        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Usuario nao encontrado"));
        }
        return ResponseEntity.ok(resultado.get(0));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestParam String nome,
            @RequestParam(required = false) String apelido,
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String dataNascimento,
            @RequestParam String raca,
            @RequestParam Integer classeId,
            @RequestParam(required = false) String dataCenter,
            @RequestParam Integer nivel,
            @RequestParam Boolean aceitaTermos) {

        // validacoes simples, linha a linha
        if (nome == null || nome.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Nome obrigatorio"));
        if (nome.length() < 3) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Nome deve ter pelo menos 3 caracteres"));
        if (email == null || email.isBlank() || !email.contains("@")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Email invalido"));
        if (senha == null || senha.length() < 6) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Senha deve ter pelo menos 6 caracteres"));
        if (dataNascimento == null || dataNascimento.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Data de nascimento obrigatoria"));
        LocalDate dataNasc;
        try {
            dataNasc = LocalDate.parse(dataNascimento);
            if (dataNasc.isAfter(LocalDate.now())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Data de nascimento nao pode ser no futuro"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Data de nascimento invalida (use YYYY-MM-DD)"));
        }
        if (raca == null || raca.isBlank()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Raca obrigatoria"));
        if (!List.of("Hyur","Miqo'te","Elezen","Roegadyn","Lalafell","Au Ra","Hrothgar","Viera").contains(raca)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Raca invalida"));
        }
        if (classeId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Classe obrigatoria"));
        if (nivel == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Nivel obrigatorio"));
        if (nivel < 1 || nivel > 100) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Nivel deve ser entre 1 e 100"));
        if (aceitaTermos == null || !aceitaTermos) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Voce deve aceitar os termos"));

        Integer countClasse = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM classe WHERE id = ?", Integer.class, classeId);
        if (countClasse == null || countClasse == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Classe invalida"));
        }

        try {
            jdbcTemplate.update(
                    "INSERT INTO usuario (nome, apelido, email, senha, data_nascimento, raca, classe_id, data_center, nivel, aceita_termos) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    nome, apelido, email, senha, java.sql.Date.valueOf(dataNasc), raca, classeId, dataCenter, nivel, aceitaTermos
            );
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Email ja cadastrado"));
        }

        Integer id = jdbcTemplate.queryForObject("SELECT id FROM usuario WHERE email = ?", Integer.class, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id, "mensagem", "Cadastro realizado com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", "Email e senha sao obrigatorios"));
        }
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                "SELECT id, nome, email FROM usuario WHERE email = ? AND senha = ?", email, senha
        );
        if (resultado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Email ou senha invalidos"));
        }
        return ResponseEntity.ok(resultado.get(0));
    }
}
