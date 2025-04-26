-- Таблица для Epic
CREATE TABLE epic
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50),
    start_time  TIMESTAMP,
    duration    BIGINT, -- Хранится в секундах
    end_time    TIMESTAMP,
    type        VARCHAR(50)  NOT NULL
);

-- Таблица для Subtask
CREATE TABLE subtask
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    epic_id     BIGINT,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50),
    start_time  TIMESTAMP,
    duration    BIGINT, -- Хранится в секундах
    end_time    TIMESTAMP,
    type        VARCHAR(50)  NOT NULL,
    FOREIGN KEY (epic_id) REFERENCES epic (id) ON DELETE CASCADE
);

-- Таблица для Task
CREATE TABLE task
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50),
    start_time  TIMESTAMP,
    duration    BIGINT, -- Хранится в секундах
    end_time    TIMESTAMP,
    type        VARCHAR(50)  NOT NULL
);

-- Опционально: Добавление индексов для оптимизации
CREATE INDEX idx_epic_status ON epic (status);
CREATE INDEX idx_subtask_epic_id ON subtask (epic_id);
CREATE INDEX idx_task_status ON task (status);