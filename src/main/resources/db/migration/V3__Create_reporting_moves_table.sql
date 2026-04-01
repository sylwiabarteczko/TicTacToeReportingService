CREATE TABLE reporting_moves (
    id       BIGSERIAL PRIMARY KEY,
    game_id  BIGINT NOT NULL REFERENCES reporting_games(id),
    moved_at TIMESTAMP NOT NULL
);