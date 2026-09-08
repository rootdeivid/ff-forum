package sptech.school.ff_forum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/classes")
public class ClasseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        List<Map<String, Object>> classes = jdbcTemplate.queryForList(
                "SELECT id, nome, tipo FROM classe ORDER BY nome"
        );
        if (classes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(classes);
    }
}
