CREATE TABLE favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_favorite UNIQUE (user_id, item_id, item_type)
);

CREATE INDEX idx_favorites_user_type ON favorites(user_id, item_type);
