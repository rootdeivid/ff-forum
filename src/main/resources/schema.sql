-- FF Forum - Schema H2
-- Tabela de apoio para o dropdown dinamico
CREATE TABLE IF NOT EXISTS classe (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(40) NOT NULL,
  tipo VARCHAR(20)
);

-- Recurso principal: usuario / aventureiro
CREATE TABLE IF NOT EXISTS usuario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(45) NOT NULL,
  apelido VARCHAR(45),
  email VARCHAR(100) NOT NULL UNIQUE,
  senha VARCHAR(100) NOT NULL,
  data_nascimento DATE NOT NULL,
  raca VARCHAR(20) NOT NULL,
  classe_id INT,
  data_center VARCHAR(30),
  nivel INT,
  aceita_termos BOOLEAN NOT NULL,
  FOREIGN KEY (classe_id) REFERENCES classe(id)
);
