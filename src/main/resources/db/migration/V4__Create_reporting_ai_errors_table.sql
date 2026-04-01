CREATE TABLE reporting_ai_errors (
    id       BIGSERIAL PRIMARY KEY,
    game_id  BIGINT REFERENCES reporting_games(id),
    error_at TIMESTAMP NOT NULL,
    message  TEXT
);