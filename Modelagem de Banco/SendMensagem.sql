
CREATE TABLE Discentes (
                discentes_pk INT AUTO_INCREMENT NOT NULL,
                discentes_matricula INT NOT NULL,
                discentes_nome VARCHAR(255) NOT NULL,
                PRIMARY KEY (discentes_pk)
);


CREATE TABLE Docente (
                docente_pk INT AUTO_INCREMENT NOT NULL,
                docente_nome VARCHAR(255) NOT NULL,
                docente_usuario VARCHAR(30) NOT NULL,
                docente_senha VARCHAR(255) NOT NULL,
                PRIMARY KEY (docente_pk)
);


CREATE TABLE Coordenacao (
                coordenacao_pk INT AUTO_INCREMENT NOT NULL,
                coord_nome VARCHAR(255) NOT NULL,
                docente_pk INT NOT NULL,
                PRIMARY KEY (coordenacao_pk)
);


CREATE TABLE Disciplina (
                disciplina_pk INT AUTO_INCREMENT NOT NULL,
                disciplina_nome VARCHAR(255) NOT NULL,
                coordenacao_pk INT NOT NULL,
                PRIMARY KEY (disciplina_pk)
);


CREATE TABLE Ministra (
                ministra_pk INT AUTO_INCREMENT NOT NULL,
                docente_pk INT NOT NULL,
                disciplina_pk INT NOT NULL,
                PRIMARY KEY (ministra_pk)
);


CREATE TABLE Estuda (
                estuda_pk INT NOT NULL,
                discentes_pk INT NOT NULL,
                disciplina_pk INT NOT NULL,
                PRIMARY KEY (estuda_pk)
);


ALTER TABLE Estuda ADD CONSTRAINT discentes_estuda_fk
FOREIGN KEY (discentes_pk)
REFERENCES Discentes (discentes_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Ministra ADD CONSTRAINT docente_ministra_fk
FOREIGN KEY (docente_pk)
REFERENCES Docente (docente_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Coordenacao ADD CONSTRAINT docente_coordenacao_fk
FOREIGN KEY (docente_pk)
REFERENCES Docente (docente_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Disciplina ADD CONSTRAINT coordenacao_disciplina_fk
FOREIGN KEY (coordenacao_pk)
REFERENCES Coordenacao (coordenacao_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Estuda ADD CONSTRAINT disciplina_estuda_fk
FOREIGN KEY (disciplina_pk)
REFERENCES Disciplina (disciplina_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE Ministra ADD CONSTRAINT disciplina_ministra_fk
FOREIGN KEY (disciplina_pk)
REFERENCES Disciplina (disciplina_pk)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

--
-- Extraindo dados da tabela `Docente`
--

INSERT INTO `Docente` (`docente_pk`, `docente_nome`, `docente_usuario`, `docente_senha`) VALUES
(1, 'Marcelo Quinta', 'marcelo', 'marcelo'),
(2, 'Edmundo Spoto', 'edmundo', 'edmundo');