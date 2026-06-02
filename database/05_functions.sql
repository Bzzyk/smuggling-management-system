-- =========================================================
-- System zarzadzania przemytem papierosow
-- Plik: database/05_functions.sql
-- Baza: PostgreSQL
-- =========================================================

-- =========================================================
-- TRANSPORTY, TRASY, POJAZDY, PRZYPISANIE PRZEMYTNIKOW
-- =========================================================


-- CALCULATE_TRANSPORT_RISK_SCORE


-- Funkcja oblicza punktowy poziom ryzyka transportu w skali 0-100.
-- Na wynik wplywa trudnosc trasy, dystans, typ pojazdu oraz doswiadczenie
-- i skutecznosc przypisanych przemytnikow.

CREATE OR REPLACE FUNCTION calculate_transport_risk_score(
    p_transport_id INT
)
RETURNS NUMERIC(5, 2)
LANGUAGE plpgsql
AS $$
DECLARE
    v_risk_level INT;
    v_distance_km NUMERIC(8, 2);
    v_vehicle_type VARCHAR(30);
    v_base_score NUMERIC(8, 2);
    v_distance_modifier NUMERIC(8, 2) := 0;
    v_vehicle_modifier NUMERIC(8, 2) := 0;
    v_smuggler_modifier NUMERIC(8, 2) := 0;
    v_result NUMERIC(8, 2);
BEGIN
    SELECT
        rdl.risk_level,
        COALESCE(r.distance_km, 0),
        v.vehicle_type
    INTO
        v_risk_level,
        v_distance_km,
        v_vehicle_type
    FROM transports t
    JOIN routes r
        ON r.id = t.route_id
    JOIN route_difficulty_levels rdl
        ON rdl.id = r.difficulty_level_id
    LEFT JOIN vehicles v
        ON v.id = t.vehicle_id
    WHERE t.id = p_transport_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Transport o id % nie istnieje albo nie ma przypisanej trasy', p_transport_id;
    END IF;

    v_base_score := v_risk_level * 15;

    v_distance_modifier := CASE
        WHEN v_distance_km >= 500 THEN 20
        WHEN v_distance_km >= 300 THEN 12
        WHEN v_distance_km >= 150 THEN 6
        ELSE 0
    END;

    v_vehicle_modifier := CASE v_vehicle_type
        WHEN 'SAMOCHOD_OSOBOWY' THEN 4
        WHEN 'VAN' THEN 8
        WHEN 'BUS' THEN 10
        WHEN 'CIEZAROWKA' THEN 16
        ELSE 8
    END;

    SELECT COALESCE(SUM(
        CASE sp.experience_level
            WHEN 'EXPERT' THEN -12
            WHEN 'SENIOR' THEN -8
            WHEN 'REGULAR' THEN -4
            WHEN 'JUNIOR' THEN 4
            ELSE 0
        END
        +
        CASE
            WHEN (sp.completed_transports_count + sp.failed_transports_count) = 0 THEN 0
            WHEN sp.completed_transports_count * 100.0
                 / (sp.completed_transports_count + sp.failed_transports_count) >= 90 THEN -8
            WHEN sp.completed_transports_count * 100.0
                 / (sp.completed_transports_count + sp.failed_transports_count) >= 75 THEN -4
            WHEN sp.completed_transports_count * 100.0
                 / (sp.completed_transports_count + sp.failed_transports_count) < 50 THEN 8
            ELSE 0
        END
    ), 0)
    INTO v_smuggler_modifier
    FROM smuggler_assignments sa
    JOIN smuggler_profiles sp
        ON sp.user_id = sa.smuggler_id
    WHERE sa.transport_id = p_transport_id
      AND sa.active = TRUE;

    v_result := v_base_score
        + v_distance_modifier
        + v_vehicle_modifier
        + v_smuggler_modifier;

    RETURN ROUND(LEAST(GREATEST(v_result, 0), 100), 2);
END;
$$;


-- ESTIMATE_TRANSPORT_OPERATIONAL_COST


-- Funkcja oblicza szacunkowy koszt operacyjny transportu.
-- Na wynik wplywa dystans, typ pojazdu, ryzyko trasy oraz doswiadczenie
-- przypisanych przemytnikow. Nie jest to finalne rozliczenie finansowe.

CREATE OR REPLACE FUNCTION estimate_transport_operational_cost(
    p_transport_id INT
)
RETURNS NUMERIC(12, 2)
LANGUAGE plpgsql
AS $$
DECLARE
    v_distance_km NUMERIC(8, 2);
    v_risk_level INT;
    v_vehicle_type VARCHAR(30);
    v_rate_per_km NUMERIC(8, 2);
    v_risk_multiplier NUMERIC(8, 2);
    v_base_fee NUMERIC(8, 2);
    v_smuggler_fee NUMERIC(12, 2);
    v_result NUMERIC(12, 2);
BEGIN
    SELECT
        COALESCE(r.distance_km, 0),
        rdl.risk_level,
        v.vehicle_type
    INTO
        v_distance_km,
        v_risk_level,
        v_vehicle_type
    FROM transports t
    JOIN routes r
        ON r.id = t.route_id
    JOIN route_difficulty_levels rdl
        ON rdl.id = r.difficulty_level_id
    LEFT JOIN vehicles v
        ON v.id = t.vehicle_id
    WHERE t.id = p_transport_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Transport o id % nie istnieje albo nie ma przypisanej trasy', p_transport_id;
    END IF;

    v_rate_per_km := CASE v_vehicle_type
        WHEN 'SAMOCHOD_OSOBOWY' THEN 2.50
        WHEN 'VAN' THEN 3.50
        WHEN 'BUS' THEN 4.00
        WHEN 'CIEZAROWKA' THEN 6.00
        ELSE 3.50
    END;

    v_risk_multiplier := 1 + (v_risk_level - 1) * 0.15;

    v_base_fee := CASE
        WHEN v_vehicle_type = 'CIEZAROWKA' THEN 500
        WHEN v_vehicle_type = 'BUS' THEN 350
        WHEN v_vehicle_type = 'VAN' THEN 300
        ELSE 200
    END;

    SELECT COALESCE(SUM(
        CASE sp.experience_level
            WHEN 'JUNIOR' THEN 150
            WHEN 'REGULAR' THEN 250
            WHEN 'SENIOR' THEN 400
            WHEN 'EXPERT' THEN 600
            ELSE 250
        END
    ), 0)
    INTO v_smuggler_fee
    FROM smuggler_assignments sa
    JOIN smuggler_profiles sp
        ON sp.user_id = sa.smuggler_id
    WHERE sa.transport_id = p_transport_id
      AND sa.active = TRUE;

    v_result := v_base_fee
        + (v_distance_km * v_rate_per_km * v_risk_multiplier)
        + v_smuggler_fee;

    RETURN ROUND(v_result, 2);
END;
$$;

-- calculate_cargo_value: Zwraca szacowaną wartość ładunku na podstawie jego ID
CREATE OR REPLACE FUNCTION calculate_cargo_value(p_cargo_id INT)
RETURNS NUMERIC(12, 2) 
LANGUAGE plpgsql 
AS $$
DECLARE
    v_value NUMERIC(12, 2);
BEGIN
    SELECT estimated_value INTO v_value 
    FROM cargo 
    WHERE id = p_cargo_id;
    
    RETURN COALESCE(v_value, 0.00);
END;
$$;

-- calculate_order_profit: Oblicza zysk ze zlecenia (tylko opłacone transakcje)
CREATE OR REPLACE FUNCTION calculate_order_profit(p_order_id INT)
RETURNS NUMERIC(12, 2) 
LANGUAGE plpgsql 
AS $$
DECLARE
    v_profit NUMERIC(12, 2);
BEGIN
    SELECT 
        COALESCE(SUM(CASE WHEN payment_type = 'PRZYCHOD' THEN amount ELSE 0 END), 0) -
        COALESCE(SUM(CASE WHEN payment_type IN ('KOSZT', 'PROWIZJA') THEN amount ELSE 0 END), 0)
    INTO v_profit
    FROM payments p
    JOIN payment_statuses ps ON p.status_id = ps.id
    WHERE p.order_id = p_order_id AND ps.name = 'ZAPLACONA';
    
    RETURN v_profit;
END;
$$;