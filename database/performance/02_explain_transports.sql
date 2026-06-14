-- EXPLAIN ANALYZE queries for transports performance tests.

EXPLAIN ANALYZE
SELECT *
FROM transports
WHERE status_id = (
    SELECT id FROM transport_statuses WHERE name = 'ZAPLANOWANY'
)
ORDER BY transport_date DESC
LIMIT 50;

EXPLAIN ANALYZE
SELECT *
FROM transports
WHERE transport_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30 days'
ORDER BY transport_date ASC, id ASC
LIMIT 100;

EXPLAIN ANALYZE
SELECT *
FROM transports
WHERE order_id = (
    SELECT id FROM orders WHERE title LIKE 'PERF_ORDER_%' ORDER BY id LIMIT 1
)
ORDER BY id DESC;

EXPLAIN ANALYZE
SELECT
    t.id,
    t.transport_date,
    o.title,
    ts.name AS status_name
FROM transports t
JOIN orders o ON o.id = t.order_id
JOIN transport_statuses ts ON ts.id = t.status_id
WHERE ts.name = 'ZAPLANOWANY'
  AND t.transport_date >= CURRENT_DATE
ORDER BY t.transport_date DESC
LIMIT 100;

EXPLAIN ANALYZE
SELECT
    t.id,
    t.order_id,
    t.status_id,
    t.transport_date
FROM transports t
WHERE t.status_id = (
    SELECT id FROM transport_statuses WHERE name = 'ZAPLANOWANY'
)
  AND t.transport_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '90 days'
ORDER BY t.transport_date ASC, t.id ASC
LIMIT 100 OFFSET 0;
