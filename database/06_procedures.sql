-- =========================================================
-- System zarzadzania przemytem papierosow
-- Plik: database/06_procedures.sql
-- Baza: PostgreSQL
-- =========================================================

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
