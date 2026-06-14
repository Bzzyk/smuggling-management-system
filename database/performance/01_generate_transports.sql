-- Generates a larger synthetic dataset for performance tests on transports.
-- Change params.row_count to 10000, 100000 or 1000000 depending on the test.

WITH params AS (
    SELECT 10000::int AS row_count
),
dict AS (
    INSERT INTO roles (name, description)
    VALUES ('BOSS', 'Performance test boss role')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description
    RETURNING id
),
boss_user AS (
    INSERT INTO users (first_name, last_name, username, password_hash, email, enabled)
    VALUES ('Performance', 'Boss', 'perf_boss', '{noop}password', 'perf_boss@example.test', true)
    ON CONFLICT (username) DO UPDATE SET email = EXCLUDED.email
    RETURNING id
),
boss_role AS (
    SELECT id FROM roles WHERE name = 'BOSS'
),
boss_user_role AS (
    INSERT INTO user_roles (user_id, role_id)
    SELECT boss_user.id, boss_role.id
    FROM boss_user, boss_role
    ON CONFLICT DO NOTHING
    RETURNING user_id
),
order_status AS (
    INSERT INTO order_statuses (name, description)
    VALUES ('NOWE', 'Performance test order status')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description
    RETURNING id
),
transport_status AS (
    INSERT INTO transport_statuses (name, description)
    VALUES ('ZAPLANOWANY', 'Performance test planned status')
    ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description
    RETURNING id
),
difficulty AS (
    INSERT INTO route_difficulty_levels (name, risk_level, description)
    VALUES ('STANDARDOWA', 2, 'Performance test difficulty')
    ON CONFLICT (name) DO UPDATE SET risk_level = EXCLUDED.risk_level
    RETURNING id
),
routes_seed AS (
    INSERT INTO routes (name, start_point, end_point, distance_km, difficulty_level_id, active)
    SELECT
        'PERF_ROUTE_' || gs,
        'Start ' || gs,
        'End ' || gs,
        100 + gs,
        (SELECT id FROM difficulty),
        true
    FROM generate_series(1, 20) gs
    WHERE NOT EXISTS (
        SELECT 1 FROM routes r WHERE r.name = 'PERF_ROUTE_' || gs
    )
    RETURNING id
),
vehicles_seed AS (
    INSERT INTO vehicles (registration_number, brand, model, vehicle_type, load_capacity, available, active)
    SELECT
        'PERF' || gs,
        'PerfBrand',
        'PerfModel',
        'VAN',
        1500,
        true,
        true
    FROM generate_series(1, 200) gs
    WHERE NOT EXISTS (
        SELECT 1 FROM vehicles v WHERE v.registration_number = 'PERF' || gs
    )
    RETURNING id
),
orders_seed AS (
    INSERT INTO orders (title, description, planned_date, status_id, created_by_user_id, responsible_user_id, estimated_profit)
    SELECT
        'PERF_ORDER_' || gs,
        'Generated order for performance tests',
        CURRENT_DATE + (gs % 365),
        (SELECT id FROM order_status),
        (SELECT id FROM boss_user),
        (SELECT id FROM boss_user),
        0
    FROM params, generate_series(1, GREATEST(1, row_count / 10)) gs
    WHERE NOT EXISTS (
        SELECT 1 FROM orders o WHERE o.title = 'PERF_ORDER_' || gs
    )
    RETURNING id
)
INSERT INTO transports (
    order_id,
    route_id,
    vehicle_id,
    status_id,
    start_location,
    destination,
    transport_date,
    planned_arrival_date,
    description
)
SELECT
    o.id,
    r.id,
    v.id,
    (SELECT id FROM transport_status),
    'Start ' || gs,
    'Destination ' || gs,
    CURRENT_DATE + (gs % 365),
    CURRENT_DATE + ((gs % 365) + 1),
    'Generated transport for performance tests'
FROM params
JOIN generate_series(1, row_count) gs ON true
JOIN LATERAL (
    SELECT id FROM orders
    WHERE title LIKE 'PERF_ORDER_%'
    ORDER BY id
    OFFSET (gs - 1) % GREATEST(1, (SELECT COUNT(*) FROM orders WHERE title LIKE 'PERF_ORDER_%'))
    LIMIT 1
) o ON true
JOIN LATERAL (
    SELECT id FROM routes
    WHERE name LIKE 'PERF_ROUTE_%'
    ORDER BY id
    OFFSET (gs - 1) % GREATEST(1, (SELECT COUNT(*) FROM routes WHERE name LIKE 'PERF_ROUTE_%'))
    LIMIT 1
) r ON true
JOIN LATERAL (
    SELECT id FROM vehicles
    WHERE registration_number LIKE 'PERF%'
    ORDER BY id
    OFFSET (gs - 1) % GREATEST(1, (SELECT COUNT(*) FROM vehicles WHERE registration_number LIKE 'PERF%'))
    LIMIT 1
) v ON true;
