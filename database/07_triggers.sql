-- =========================================================
-- System zarzadzania przemytem papierosow
-- Plik: database/07_triggers.sql
-- Baza: PostgreSQL
-- =========================================================

-- =========================================================
-- UZYTKOWNICY, ROLE, ZLECENIA, HISTORIA ZMIAN
-- =========================================================


-- AUDIT_ORDERS


-- Trigger zapisuje zmiany w tabeli orders do audit_logs.

CREATE OR REPLACE FUNCTION audit_orders_func()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_logs (table_name, record_id, action, changed_by_user_id, old_value, new_value)
        VALUES ('orders', NEW.id, 'INSERT', NULL, NULL, row_to_json(NEW)::TEXT);
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_logs (table_name, record_id, action, changed_by_user_id, old_value, new_value)
        VALUES ('orders', NEW.id, 'UPDATE', NULL, row_to_json(OLD)::TEXT, row_to_json(NEW)::TEXT);
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_logs (table_name, record_id, action, changed_by_user_id, old_value, new_value)
        VALUES ('orders', OLD.id, 'DELETE', NULL, row_to_json(OLD)::TEXT, NULL);
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS trg_audit_orders ON orders;

CREATE TRIGGER trg_audit_orders
AFTER INSERT OR UPDATE OR DELETE ON orders
FOR EACH ROW
EXECUTE FUNCTION audit_orders_func();


-- AUDIT_USERS


-- Trigger zapisuje zmiany w tabeli users do audit_logs.

CREATE OR REPLACE FUNCTION audit_users_func()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO audit_logs (table_name, record_id, action, changed_by_user_id, old_value, new_value)
        VALUES ('users', NEW.id, 'INSERT', NULL, NULL, row_to_json(NEW)::TEXT);
        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_logs (table_name, record_id, action, changed_by_user_id, old_value, new_value)
        VALUES ('users', NEW.id, 'UPDATE', NULL, row_to_json(OLD)::TEXT, row_to_json(NEW)::TEXT);
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO audit_logs (table_name, record_id, action, changed_by_user_id, old_value, new_value)
        VALUES ('users', OLD.id, 'DELETE', NULL, row_to_json(OLD)::TEXT, NULL);
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS trg_audit_users ON users;

CREATE TRIGGER trg_audit_users
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW
EXECUTE FUNCTION audit_users_func();


-- =========================================================
-- TRANSPORTY, TRASY, POJAZDY, PRZYPISANIE PRZEMYTNIKOW
-- =========================================================


-- SET_TRANSPORT_UPDATED_AT


-- Trigger automatycznie ustawia pole updated_at przy kazdej zmianie transportu.

CREATE OR REPLACE FUNCTION set_transport_updated_at()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_set_transport_updated_at ON transports;

CREATE TRIGGER trg_set_transport_updated_at
BEFORE UPDATE ON transports
FOR EACH ROW
EXECUTE FUNCTION set_transport_updated_at();


-- VALIDATE_TRANSPORT_STATUS_CHANGE


-- Trigger pilnuje dozwolonych przejsc statusow transportu takze przy recznym
-- UPDATE na tabeli transports.

CREATE OR REPLACE FUNCTION validate_transport_status_change()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_old_status VARCHAR(30);
    v_new_status VARCHAR(30);
BEGIN
    IF NEW.status_id = OLD.status_id THEN
        RETURN NEW;
    END IF;

    SELECT name
    INTO v_old_status
    FROM transport_statuses
    WHERE id = OLD.status_id;

    SELECT name
    INTO v_new_status
    FROM transport_statuses
    WHERE id = NEW.status_id;

    IF v_old_status IN ('DOSTARCZONY', 'NIEUDANY', 'ANULOWANY') THEN
        RAISE EXCEPTION 'Nie mozna zmienic statusu zakonczonego transportu %', NEW.id;
    END IF;

    IF v_old_status = 'ZAPLANOWANY'
       AND v_new_status NOT IN ('W_DRODZE', 'ANULOWANY') THEN
        RAISE EXCEPTION 'Nieprawidlowa zmiana statusu z % na %', v_old_status, v_new_status;
    END IF;

    IF v_old_status = 'W_DRODZE'
       AND v_new_status NOT IN ('DOSTARCZONY', 'NIEUDANY', 'ANULOWANY') THEN
        RAISE EXCEPTION 'Nieprawidlowa zmiana statusu z % na %', v_old_status, v_new_status;
    END IF;

    IF v_new_status = 'W_DRODZE' AND NEW.vehicle_id IS NULL THEN
        RAISE EXCEPTION 'Nie mozna rozpoczac transportu % bez przypisanego pojazdu', NEW.id;
    END IF;

    IF v_new_status = 'W_DRODZE'
       AND NOT EXISTS (
           SELECT 1
           FROM smuggler_assignments
           WHERE transport_id = NEW.id
             AND active = TRUE
       ) THEN
        RAISE EXCEPTION 'Nie mozna rozpoczac transportu % bez przypisanego przemytnika', NEW.id;
    END IF;

    IF v_new_status = 'W_DRODZE'
       AND NOT EXISTS (
           SELECT 1
           FROM cargo
           WHERE transport_id = NEW.id
       ) THEN
        RAISE EXCEPTION 'Nie mozna rozpoczac transportu % bez przypisanego ladunku', NEW.id;
    END IF;

    IF v_new_status = 'W_DRODZE'
       AND EXISTS (
           SELECT 1
           FROM vehicles v
           WHERE v.id = NEW.vehicle_id
             AND v.load_capacity < (
                 SELECT COALESCE(SUM(c.packages_count), 0)
                 FROM cargo c
                 WHERE c.transport_id = NEW.id
             )
       ) THEN
        RAISE EXCEPTION 'Nie mozna rozpoczac transportu %. Ladunek przekracza ladownosc pojazdu', NEW.id;
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_validate_transport_status_change ON transports;

CREATE TRIGGER trg_validate_transport_status_change
BEFORE UPDATE OF status_id ON transports
FOR EACH ROW
EXECUTE FUNCTION validate_transport_status_change();


-- VALIDATE_SMUGGLER_ASSIGNMENT


-- Trigger zabezpiecza przypisanie przemytnika takze wtedy, gdy ktos ominie
-- procedure assign_smuggler_to_transport i wykona INSERT/UPDATE recznie.

CREATE OR REPLACE FUNCTION validate_smuggler_assignment()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_transport_status VARCHAR(30);
    v_smuggler_active BOOLEAN;
BEGIN
    IF NEW.active = FALSE THEN
        RETURN NEW;
    END IF;

    SELECT ts.name
    INTO v_transport_status
    FROM transports t
    JOIN transport_statuses ts
        ON ts.id = t.status_id
    WHERE t.id = NEW.transport_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Transport o id % nie istnieje', NEW.transport_id;
    END IF;

    IF v_transport_status <> 'ZAPLANOWANY' THEN
        RAISE EXCEPTION 'Nie mozna aktywnie przypisac przemytnika do transportu o statusie %', v_transport_status;
    END IF;

    SELECT sp.active
    INTO v_smuggler_active
    FROM smuggler_profiles sp
    JOIN users u
        ON u.id = sp.user_id
    WHERE sp.user_id = NEW.smuggler_id
      AND u.enabled = TRUE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Aktywny przemytnik o id % nie istnieje', NEW.smuggler_id;
    END IF;

    IF v_smuggler_active = FALSE THEN
        RAISE EXCEPTION 'Przemytnik o id % jest nieaktywny', NEW.smuggler_id;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM smuggler_assignments sa
        JOIN transports t
            ON t.id = sa.transport_id
        JOIN transport_statuses ts
            ON ts.id = t.status_id
        WHERE sa.smuggler_id = NEW.smuggler_id
          AND sa.active = TRUE
          AND ts.name IN ('ZAPLANOWANY', 'W_DRODZE')
          AND sa.id <> COALESCE(NEW.id, -1)
    ) THEN
        RAISE EXCEPTION 'Przemytnik o id % ma juz aktywny transport', NEW.smuggler_id;
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_validate_smuggler_assignment ON smuggler_assignments;

CREATE TRIGGER trg_validate_smuggler_assignment
BEFORE INSERT OR UPDATE OF transport_id, smuggler_id, active ON smuggler_assignments
FOR EACH ROW
EXECUTE FUNCTION validate_smuggler_assignment();


-- CLOSE_TRANSPORT_ASSIGNMENTS_AND_UPDATE_STATS


-- Trigger po zakonczeniu transportu aktualizuje statystyki przypisanych
-- przemytnikow i dezaktywuje ich przypisania do tego transportu.

CREATE OR REPLACE FUNCTION close_transport_assignments_and_update_stats()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_old_status VARCHAR(30);
    v_new_status VARCHAR(30);
BEGIN
    SELECT name
    INTO v_old_status
    FROM transport_statuses
    WHERE id = OLD.status_id;

    SELECT name
    INTO v_new_status
    FROM transport_statuses
    WHERE id = NEW.status_id;

    IF v_old_status = v_new_status THEN
        RETURN NEW;
    END IF;

    IF v_old_status IN ('DOSTARCZONY', 'NIEUDANY', 'ANULOWANY') THEN
        RETURN NEW;
    END IF;

    IF v_new_status = 'DOSTARCZONY' THEN
        UPDATE smuggler_profiles sp
        SET completed_transports_count = completed_transports_count + 1
        FROM smuggler_assignments sa
        WHERE sa.smuggler_id = sp.user_id
          AND sa.transport_id = NEW.id
          AND sa.active = TRUE;
    ELSIF v_new_status = 'NIEUDANY' THEN
        UPDATE smuggler_profiles sp
        SET failed_transports_count = failed_transports_count + 1
        FROM smuggler_assignments sa
        WHERE sa.smuggler_id = sp.user_id
          AND sa.transport_id = NEW.id
          AND sa.active = TRUE;
    END IF;

    IF v_new_status IN ('DOSTARCZONY', 'NIEUDANY', 'ANULOWANY') THEN
        UPDATE smuggler_assignments
        SET active = FALSE
        WHERE transport_id = NEW.id
          AND active = TRUE;
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_close_transport_assignments_and_update_stats ON transports;

CREATE TRIGGER trg_close_transport_assignments_and_update_stats
AFTER UPDATE OF status_id ON transports
FOR EACH ROW
EXECUTE FUNCTION close_transport_assignments_and_update_stats();

-- Pojemność magazynu
CREATE OR REPLACE FUNCTION check_warehouse_capacity_func()
RETURNS TRIGGER 
LANGUAGE plpgsql 
AS $$
DECLARE
    v_max_capacity INT;
    v_current_quantity INT;
BEGIN
    -- Sprawdzenie maksymalnej pojemności magazynu
    SELECT max_capacity INTO v_max_capacity FROM warehouses WHERE id = NEW.warehouse_id;
    
    -- Obliczenie aktualnego zapasu z pominięciem edytowanego rekordu (dla UPDATE)
    SELECT COALESCE(SUM(quantity), 0) INTO v_current_quantity
    FROM warehouse_stock
    WHERE warehouse_id = NEW.warehouse_id AND id IS DISTINCT FROM NEW.id;
    
    IF (v_current_quantity + NEW.quantity) > v_max_capacity THEN
        RAISE EXCEPTION 'Błąd: Przekroczono pojemność magazynu. Maksymalna: %, Próba dodania: %', 
            v_max_capacity, (v_current_quantity + NEW.quantity);
    END IF;
    
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_check_warehouse_capacity ON warehouse_stock;

CREATE TRIGGER trg_check_warehouse_capacity
BEFORE INSERT OR UPDATE ON warehouse_stock
FOR EACH ROW EXECUTE FUNCTION check_warehouse_capacity_func();


-- Audyt płatności

-- Tabela wymagana do zapisywania audytu płatności
CREATE TABLE IF NOT EXISTS payments_audit (
    id SERIAL PRIMARY KEY,
    payment_id INT NOT NULL,
    action_type VARCHAR(10) NOT NULL, -- INSERT, UPDATE, DELETE
    old_status_id INT,
    new_status_id INT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION audit_payments_func()
RETURNS TRIGGER 
LANGUAGE plpgsql 
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO payments_audit(payment_id, action_type, new_status_id)
        VALUES (NEW.id, 'INSERT', NEW.status_id);
        RETURN NEW;
        
    ELSIF TG_OP = 'UPDATE' THEN
        -- Rejestrujemy tylko zmianę statusu, by nie śmiecić w bazie
        IF OLD.status_id IS DISTINCT FROM NEW.status_id THEN
            INSERT INTO payments_audit(payment_id, action_type, old_status_id, new_status_id)
            VALUES (NEW.id, 'UPDATE', OLD.status_id, NEW.status_id);
        END IF;
        RETURN NEW;
        
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO payments_audit(payment_id, action_type, old_status_id)
        VALUES (OLD.id, 'DELETE', OLD.status_id);
        RETURN OLD;
    END IF;
    
    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS trg_audit_payments ON payments;

CREATE TRIGGER trg_audit_payments
AFTER INSERT OR UPDATE OR DELETE ON payments
FOR EACH ROW EXECUTE FUNCTION audit_payments_func();
