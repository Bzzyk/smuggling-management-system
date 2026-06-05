-- =========================================================
-- System zarzadzania przemytem papierosow
-- Plik: database/06_procedures.sql
-- Baza: PostgreSQL
-- =========================================================

-- =========================================================
-- UZYTKOWNICY, ROLE, ZLECENIA
-- =========================================================


-- CREATE_ORDER


-- Procedura tworzy zlecenie z domyslnym statusem NOWE.

CREATE OR REPLACE PROCEDURE create_order(
    p_title VARCHAR,
    p_description TEXT,
    p_planned_date DATE,
    p_created_by_user_id INT,
    p_responsible_user_id INT DEFAULT NULL,
    p_estimated_profit NUMERIC DEFAULT 0
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_id INT;
BEGIN
    SELECT id
    INTO v_status_id
    FROM order_statuses
    WHERE name = 'NOWE';

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Status zlecenia NOWE nie istnieje';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM users
        WHERE id = p_created_by_user_id
    ) THEN
        RAISE EXCEPTION 'Uzytkownik tworzacy o id % nie istnieje', p_created_by_user_id;
    END IF;

    IF p_responsible_user_id IS NOT NULL
       AND NOT EXISTS (
           SELECT 1
           FROM users
           WHERE id = p_responsible_user_id
       ) THEN
        RAISE EXCEPTION 'Uzytkownik odpowiedzialny o id % nie istnieje', p_responsible_user_id;
    END IF;

    INSERT INTO orders (
        title,
        description,
        planned_date,
        status_id,
        created_by_user_id,
        responsible_user_id,
        estimated_profit
    )
    VALUES (
        p_title,
        p_description,
        p_planned_date,
        v_status_id,
        p_created_by_user_id,
        p_responsible_user_id,
        COALESCE(p_estimated_profit, 0)
    );
END;
$$;


-- CHANGE_ORDER_STATUS


-- Procedura zmienia status zlecenia na podstawie nazwy statusu.

CREATE OR REPLACE PROCEDURE change_order_status(
    p_order_id INT,
    p_status_name VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_id INT;
    v_current_status VARCHAR(30);
BEGIN
    SELECT os.name
    INTO v_current_status
    FROM orders o
    JOIN order_statuses os
        ON os.id = o.status_id
    WHERE o.id = p_order_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Zlecenie o id % nie istnieje', p_order_id;
    END IF;

    SELECT id
    INTO v_status_id
    FROM order_statuses
    WHERE name = p_status_name;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Status zlecenia % nie istnieje', p_status_name;
    END IF;

    IF p_status_name NOT IN ('NOWE', 'W_TRAKCIE', 'ZREALIZOWANE', 'ANULOWANE') THEN
        RAISE EXCEPTION 'Nieprawidlowy status zlecenia: %', p_status_name;
    END IF;

    IF v_current_status = p_status_name THEN
        RETURN;
    END IF;

    UPDATE orders
    SET status_id = v_status_id,
        completed_at = CASE
            WHEN p_status_name IN ('ZREALIZOWANE', 'ANULOWANE') THEN CURRENT_TIMESTAMP
            WHEN p_status_name IN ('NOWE', 'W_TRAKCIE') THEN NULL
            ELSE completed_at
        END
    WHERE id = p_order_id;
END;
$$;


-- =========================================================
-- TRANSPORTY, TRASY, POJAZDY, PRZYPISANIE PRZEMYTNIKOW
-- =========================================================


-- ASSIGN_SMUGGLER_TO_TRANSPORT


-- Procedura przypisuje przemytnika do transportu.
-- Sprawdza, czy transport istnieje, czy przemytnik jest aktywny oraz czy nie
-- ma juz innego aktywnego transportu.

CREATE OR REPLACE PROCEDURE assign_smuggler_to_transport(
    p_transport_id INT,
    p_smuggler_id INT,
    p_note VARCHAR DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_transport_status VARCHAR(30);
    v_smuggler_active BOOLEAN;
BEGIN
    SELECT ts.name
    INTO v_transport_status
    FROM transports t
    JOIN transport_statuses ts
        ON ts.id = t.status_id
    WHERE t.id = p_transport_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Transport o id % nie istnieje', p_transport_id;
    END IF;

    IF v_transport_status <> 'ZAPLANOWANY' THEN
        RAISE EXCEPTION 'Nie mozna przypisac przemytnika do transportu o statusie %', v_transport_status;
    END IF;

    SELECT sp.active
    INTO v_smuggler_active
    FROM smuggler_profiles sp
    JOIN users u
        ON u.id = sp.user_id
    WHERE sp.user_id = p_smuggler_id
      AND u.enabled = TRUE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Aktywny przemytnik o id % nie istnieje', p_smuggler_id;
    END IF;

    IF v_smuggler_active = FALSE THEN
        RAISE EXCEPTION 'Przemytnik o id % jest nieaktywny', p_smuggler_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM smuggler_assignments sa
        JOIN transports t
            ON t.id = sa.transport_id
        JOIN transport_statuses ts
            ON ts.id = t.status_id
        WHERE sa.smuggler_id = p_smuggler_id
          AND sa.active = TRUE
          AND ts.name IN ('ZAPLANOWANY', 'W_DRODZE')
    ) THEN
        RAISE EXCEPTION 'Przemytnik o id % ma juz aktywny transport', p_smuggler_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM smuggler_assignments sa
        WHERE sa.transport_id = p_transport_id
          AND sa.smuggler_id = p_smuggler_id
          AND sa.active = FALSE
    ) THEN
        UPDATE smuggler_assignments
        SET active = TRUE,
            assigned_at = CURRENT_TIMESTAMP,
            note = p_note
        WHERE transport_id = p_transport_id
          AND smuggler_id = p_smuggler_id;
    ELSE
        INSERT INTO smuggler_assignments (
            transport_id,
            smuggler_id,
            note
        )
        VALUES (
            p_transport_id,
            p_smuggler_id,
            p_note
        );
    END IF;
END;
$$;


-- ASSIGN_VEHICLE_TO_TRANSPORT


-- Procedura przypisuje pojazd do transportu.
-- Sprawdza, czy pojazd jest dostepny i czy nie jest uzywany w innym aktywnym
-- transporcie.

CREATE OR REPLACE PROCEDURE assign_vehicle_to_transport(
    p_transport_id INT,
    p_vehicle_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_transport_status VARCHAR(30);
    v_vehicle_available BOOLEAN;
BEGIN
    SELECT ts.name
    INTO v_transport_status
    FROM transports t
    JOIN transport_statuses ts
        ON ts.id = t.status_id
    WHERE t.id = p_transport_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Transport o id % nie istnieje', p_transport_id;
    END IF;

    IF v_transport_status <> 'ZAPLANOWANY' THEN
        RAISE EXCEPTION 'Nie mozna przypisac pojazdu do transportu o statusie %', v_transport_status;
    END IF;

    SELECT available
    INTO v_vehicle_available
    FROM vehicles
    WHERE id = p_vehicle_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Pojazd o id % nie istnieje', p_vehicle_id;
    END IF;

    IF v_vehicle_available = FALSE THEN
        RAISE EXCEPTION 'Pojazd o id % jest oznaczony jako niedostepny', p_vehicle_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM transports t
        JOIN transport_statuses ts
            ON ts.id = t.status_id
        WHERE t.vehicle_id = p_vehicle_id
          AND t.id <> p_transport_id
          AND ts.name IN ('ZAPLANOWANY', 'W_DRODZE')
    ) THEN
        RAISE EXCEPTION 'Pojazd o id % jest juz przypisany do aktywnego transportu', p_vehicle_id;
    END IF;

    UPDATE transports
    SET vehicle_id = p_vehicle_id
    WHERE id = p_transport_id;
END;
$$;


-- CHANGE_TRANSPORT_STATUS


-- Procedura zmienia status transportu na podstawie nazwy statusu.

CREATE OR REPLACE PROCEDURE change_transport_status(
    p_transport_id INT,
    p_status_name VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_status_id INT;
    v_current_status VARCHAR(30);
    v_vehicle_id INT;
BEGIN
    SELECT id
    INTO v_status_id
    FROM transport_statuses
    WHERE name = p_status_name;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Status transportu % nie istnieje', p_status_name;
    END IF;

    SELECT
        ts.name,
        t.vehicle_id
    INTO
        v_current_status,
        v_vehicle_id
    FROM transports t
    JOIN transport_statuses ts
        ON ts.id = t.status_id
    WHERE t.id = p_transport_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Transport o id % nie istnieje', p_transport_id;
    END IF;

    IF v_current_status IN ('DOSTARCZONY', 'NIEUDANY', 'ANULOWANY') THEN
        RAISE EXCEPTION 'Nie mozna zmienic statusu zakonczonego transportu %', p_transport_id;
    END IF;

    IF v_current_status = 'ZAPLANOWANY'
       AND p_status_name NOT IN ('W_DRODZE', 'ANULOWANY') THEN
        RAISE EXCEPTION 'Nieprawidlowa zmiana statusu z % na %', v_current_status, p_status_name;
    END IF;

    IF v_current_status = 'W_DRODZE'
       AND p_status_name NOT IN ('DOSTARCZONY', 'NIEUDANY', 'ANULOWANY') THEN
        RAISE EXCEPTION 'Nieprawidlowa zmiana statusu z % na %', v_current_status, p_status_name;
    END IF;

    IF p_status_name = 'W_DRODZE' AND v_vehicle_id IS NULL THEN
        RAISE EXCEPTION 'Nie mozna rozpoczac transportu % bez przypisanego pojazdu', p_transport_id;
    END IF;

    IF p_status_name = 'W_DRODZE'
       AND NOT EXISTS (
           SELECT 1
           FROM smuggler_assignments
           WHERE transport_id = p_transport_id
             AND active = TRUE
       ) THEN
        RAISE EXCEPTION 'Nie mozna rozpoczac transportu % bez przypisanego przemytnika', p_transport_id;
    END IF;

    UPDATE transports
    SET status_id = v_status_id
    WHERE id = p_transport_id;
END;
$$;

-- add_cargo_to_warehouse: Dodaje ładunek do magazynu (jeśli istnieje, zwiększa ilość)
CREATE OR REPLACE PROCEDURE add_cargo_to_warehouse(
    p_warehouse_id INT,
    p_cargo_id INT,
    p_quantity INT
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Użycie mechanizmu UPSERT
    INSERT INTO warehouse_stock (warehouse_id, cargo_id, quantity)
    VALUES (p_warehouse_id, p_cargo_id, p_quantity)
    ON CONFLICT (warehouse_id, cargo_id) 
    DO UPDATE SET quantity = warehouse_stock.quantity + EXCLUDED.quantity;
    
    -- Opcjonalnie: uaktualnienie głównej tabeli cargo
    UPDATE cargo SET warehouse_id = p_warehouse_id WHERE id = p_cargo_id;
END;
$$;

-- register_payment: Szybka rejestracja płatności
CREATE OR REPLACE PROCEDURE register_payment(
    p_order_id INT,
    p_amount NUMERIC,
    p_payment_type VARCHAR,
    p_status_id INT,
    p_description TEXT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO payments (order_id, amount, payment_type, status_id, payment_date, description)
    VALUES (p_order_id, p_amount, p_payment_type, p_status_id, CURRENT_DATE, p_description);
END;
$$;
