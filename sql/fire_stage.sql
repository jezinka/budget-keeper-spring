CREATE TABLE IF NOT EXISTS fire_stage (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    threshold        DECIMAL(12, 2) NOT NULL UNIQUE,
    first_crossed_at DATE           NULL
);

INSERT INTO fire_stage (threshold) VALUES
    (15000),
    (75000),
    (150000),
    (225000),
    (300000),
    (375000),
    (450000),
    (525000),
    (600000),
    (675000),
    (750000),
    (825000),
    (900000),
    (975000),
    (1050000),
    (1125000),
    (1200000),
    (1275000),
    (1350000),
    (1425000),
    (1500000)
ON DUPLICATE KEY UPDATE threshold = threshold;

-- Retroactively fill first_crossed_at from existing portfolio_snapshot data
UPDATE fire_stage fs
SET fs.first_crossed_at = (
    SELECT MIN(ps.date)
    FROM portfolio_snapshot ps
    WHERE ps.value >= fs.threshold
)
WHERE fs.first_crossed_at IS NULL;
