CREATE TABLE IF NOT EXISTS portfolio_snapshot (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    date       DATE           NOT NULL UNIQUE,
    value      DECIMAL(12, 2) NOT NULL,
    created_at DATE           NOT NULL DEFAULT (CURRENT_DATE)
);
