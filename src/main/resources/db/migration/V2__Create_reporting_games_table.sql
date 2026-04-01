CREATE TABLE reporting_games (
    id         BIGINT PRIMARY KEY,
    user_id    BIGINT REFERENCES reporting_users(id),
    created_at TIMESTAMP NOT NULL,
    finished   BOOLEAN NOT NULL DEFAULT FALSE,
    is_ai      BOOLEAN NOT NULL DEFAULT FALSE
);