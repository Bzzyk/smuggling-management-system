-- =========================================================
-- System zarzadzania przemytem papierosow
-- Plik: database/04_views.sql
-- Baza: PostgreSQL
-- =========================================================

-- =========================================================
-- TRANSPORTY, TRASY, POJAZDY, PRZYPISANIE PRZEMYTNIKOW
-- =========================================================


-- AVAILABLE_SMUGGLERS


-- Dostepni przemytnicy
-- Przemytnik jest dostepny, jesli konto i profil sa aktywne oraz nie ma
-- aktywnego przypisania do transportu o statusie ZAPLANOWANY albo W_DRODZE.

CREATE OR REPLACE VIEW v_available_smugglers AS
SELECT
    u.id AS smuggler_id,
    u.first_name,
    u.last_name,
    u.username,
    sp.experience_level,
    sp.completed_transports_count,
    sp.failed_transports_count,
    CASE
        WHEN (sp.completed_transports_count + sp.failed_transports_count) = 0 THEN NULL
        ELSE ROUND(
            sp.completed_transports_count * 100.0
            / (sp.completed_transports_count + sp.failed_transports_count),
            2
        )
    END AS success_rate_percent
FROM users u
JOIN smuggler_profiles sp
    ON sp.user_id = u.id
WHERE u.enabled = TRUE
  AND sp.active = TRUE
  AND NOT EXISTS (
      SELECT 1
      FROM smuggler_assignments sa
      JOIN transports t
          ON t.id = sa.transport_id
      JOIN transport_statuses ts
          ON ts.id = t.status_id
      WHERE sa.smuggler_id = u.id
        AND sa.active = TRUE
        AND ts.name IN ('ZAPLANOWANY', 'W_DRODZE')
  );


-- AVAILABLE_VEHICLES


-- Dostepne pojazdy
-- Pojazd jest dostepny, jesli ma flage available oraz nie jest przypisany do
-- aktywnego transportu.

CREATE OR REPLACE VIEW v_available_vehicles AS
SELECT
    v.id AS vehicle_id,
    v.registration_number,
    v.brand,
    v.model,
    v.vehicle_type,
    v.load_capacity
FROM vehicles v
WHERE v.available = TRUE
  AND NOT EXISTS (
      SELECT 1
      FROM transports t
      JOIN transport_statuses ts
          ON ts.id = t.status_id
      WHERE t.vehicle_id = v.id
        AND ts.name IN ('ZAPLANOWANY', 'W_DRODZE')
  );


-- ACTIVE_TRANSPORTS


-- Aktywne transporty
-- Widok pokazuje transporty, ktore sa zaplanowane albo aktualnie w drodze.

CREATE OR REPLACE VIEW v_active_transports AS
SELECT
    t.id AS transport_id,
    ts.name AS transport_status,
    o.id AS order_id,
    o.title AS order_title,
    r.id AS route_id,
    r.name AS route_name,
    r.start_point,
    r.end_point,
    r.distance_km,
    rdl.name AS difficulty_name,
    rdl.risk_level,
    v.id AS vehicle_id,
    v.registration_number,
    v.brand AS vehicle_brand,
    v.model AS vehicle_model,
    t.transport_date,
    t.planned_arrival_date,
    t.created_at,
    t.updated_at
FROM transports t
JOIN transport_statuses ts
    ON ts.id = t.status_id
JOIN orders o
    ON o.id = t.order_id
LEFT JOIN routes r
    ON r.id = t.route_id
LEFT JOIN route_difficulty_levels rdl
    ON rdl.id = r.difficulty_level_id
LEFT JOIN vehicles v
    ON v.id = t.vehicle_id
WHERE ts.name IN ('ZAPLANOWANY', 'W_DRODZE');


-- TRANSPORT_DETAILS


-- Szczegoly transportow
-- Pelniejszy widok transportu z trasa, pojazdem, statusem i przypisanymi
-- przemytnikami.

CREATE OR REPLACE VIEW v_transport_details AS
SELECT
    t.id AS transport_id,
    ts.name AS transport_status,
    o.id AS order_id,
    o.title AS order_title,
    t.start_location,
    t.destination,
    t.transport_date,
    t.planned_arrival_date,
    r.id AS route_id,
    r.name AS route_name,
    r.distance_km,
    rdl.name AS difficulty_name,
    rdl.risk_level,
    v.id AS vehicle_id,
    v.registration_number,
    v.brand AS vehicle_brand,
    v.model AS vehicle_model,
    v.vehicle_type,
    STRING_AGG(
        CONCAT(u.first_name, ' ', u.last_name, ' (', sp.experience_level, ')'),
        ', '
        ORDER BY u.last_name, u.first_name
    ) FILTER (WHERE u.id IS NOT NULL) AS assigned_smugglers,
    t.description,
    t.created_at,
    t.updated_at
FROM transports t
JOIN transport_statuses ts
    ON ts.id = t.status_id
JOIN orders o
    ON o.id = t.order_id
LEFT JOIN routes r
    ON r.id = t.route_id
LEFT JOIN route_difficulty_levels rdl
    ON rdl.id = r.difficulty_level_id
LEFT JOIN vehicles v
    ON v.id = t.vehicle_id
LEFT JOIN smuggler_assignments sa
    ON sa.transport_id = t.id
   AND sa.active = TRUE
LEFT JOIN smuggler_profiles sp
    ON sp.user_id = sa.smuggler_id
LEFT JOIN users u
    ON u.id = sp.user_id
GROUP BY
    t.id,
    ts.name,
    o.id,
    o.title,
    r.id,
    r.name,
    r.distance_km,
    rdl.name,
    rdl.risk_level,
    v.id,
    v.registration_number,
    v.brand,
    v.model,
    v.vehicle_type;


-- ROUTE_SUMMARY


-- Podsumowanie tras
-- Pomocniczy widok do wyboru trasy i oceny jej trudnosci.

CREATE OR REPLACE VIEW v_route_summary AS
SELECT
    r.id AS route_id,
    r.name,
    r.start_point,
    r.end_point,
    r.distance_km,
    rdl.name AS difficulty_name,
    rdl.risk_level,
    CASE
        WHEN rdl.risk_level <= 2 THEN 'NISKIE'
        WHEN rdl.risk_level = 3 THEN 'SREDNIE'
        ELSE 'WYSOKIE'
    END AS risk_category,
    r.description
FROM routes r
JOIN route_difficulty_levels rdl
    ON rdl.id = r.difficulty_level_id;

-- v_warehouse_stock: Pokazuje aktualny stan magazynów ze szczegółami ładunku
CREATE OR REPLACE VIEW v_warehouse_stock AS
SELECT 
    ws.id AS stock_id,
    w.name AS warehouse_name,
    w.location,
    c.name AS cargo_name,
    ct.name AS cargo_type,
    ws.quantity,
    c.estimated_value,
    ws.added_at
FROM warehouse_stock ws
JOIN warehouses w ON ws.warehouse_id = w.id
JOIN cargo c ON ws.cargo_id = c.id
JOIN cargo_types ct ON c.cargo_type_id = ct.id;

-- v_profit_report: Raport zysków i kosztów dla poszczególnych zleceń
CREATE OR REPLACE VIEW v_profit_report AS
SELECT 
    p.order_id,
    SUM(CASE WHEN p.payment_type = 'PRZYCHOD' THEN p.amount ELSE 0 END) AS total_revenue,
    SUM(CASE WHEN p.payment_type IN ('KOSZT', 'PROWIZJA') THEN p.amount ELSE 0 END) AS total_costs,
    -- Zysk netto (Przychody - Koszty - Prowizje) z opłaconych płatności
    SUM(CASE WHEN p.payment_type = 'PRZYCHOD' THEN p.amount ELSE 0 END) -
    SUM(CASE WHEN p.payment_type IN ('KOSZT', 'PROWIZJA') THEN p.amount ELSE 0 END) AS net_profit
FROM payments p
JOIN payment_statuses ps ON p.status_id = ps.id
WHERE ps.name = 'ZAPLACONA'
GROUP BY p.order_id;