-- =========================================================
-- System zarządzania przemytem papierosów
-- Plik: database/01_schema.sql
-- Baza: PostgreSQL
-- =========================================================

-- Uwaga:
-- Projekt ma charakter fikcyjny i edukacyjny.
-- Ten plik zawiera początkowy schemat relacyjnej bazy danych.

-- =========================================================
-- CZĘŚĆ 1: UŻYTKOWNICY, ROLE, ZLECENIA, HISTORIA ZMIAN
-- Odpowiedzialny: Kamil Osakowicz
-- =========================================================

-- =========================
-- USERS
-- =========================

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,

    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(80) NOT NULL,

    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,

    email VARCHAR(120) UNIQUE,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_users_username_length
        CHECK (LENGTH(username) BETWEEN 3 AND 50),

    CONSTRAINT chk_users_first_name_length
        CHECK (LENGTH(first_name) BETWEEN 2 AND 50),

    CONSTRAINT chk_users_last_name_length
        CHECK (LENGTH(last_name) BETWEEN 2 AND 80)
);

-- =========================
-- ROLES
-- =========================

CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,

    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),

    CONSTRAINT chk_roles_name
        CHECK (name IN ('ADMIN', 'BOSS', 'SMUGGLER', 'ACCOUNTANT'))
);

-- =========================
-- USER_ROLES
-- =========================

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

-- =========================
-- ORDER_STATUSES
-- =========================

CREATE TABLE IF NOT EXISTS order_statuses (
    id SERIAL PRIMARY KEY,

    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),

    CONSTRAINT chk_order_statuses_name
        CHECK (name IN ('NOWE', 'W_TRAKCIE', 'ZREALIZOWANE', 'ANULOWANE'))
);

-- =========================
-- ORDERS
-- =========================

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,

    title VARCHAR(100) NOT NULL,
    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    planned_date DATE,
    completed_at TIMESTAMP,

    status_id INT NOT NULL,
    created_by_user_id INT NOT NULL,
    responsible_user_id INT,

    estimated_profit NUMERIC(12, 2) DEFAULT 0,

    CONSTRAINT fk_orders_status
        FOREIGN KEY (status_id)
        REFERENCES order_statuses(id),

    CONSTRAINT fk_orders_created_by
        FOREIGN KEY (created_by_user_id)
        REFERENCES users(id),

    CONSTRAINT fk_orders_responsible_user
        FOREIGN KEY (responsible_user_id)
        REFERENCES users(id),

    CONSTRAINT chk_orders_title_length
        CHECK (LENGTH(title) BETWEEN 3 AND 100),

    CONSTRAINT chk_orders_estimated_profit
        CHECK (estimated_profit >= 0)
);

-- =========================
-- AUDIT_LOGS
-- =========================

CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,

    table_name VARCHAR(80) NOT NULL,
    record_id INT NOT NULL,

    action VARCHAR(20) NOT NULL,
    changed_by_user_id INT,

    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    old_value TEXT,
    new_value TEXT,

    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (changed_by_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_audit_logs_action
        CHECK (action IN ('INSERT', 'UPDATE', 'DELETE'))
);

-- =========================================================
-- CZĘŚĆ 2: TRANSPORTY, TRASY, POJAZDY, PRZYPISANIE PRZEMYTNIKÓW
-- Odpowiedzialny: Osoba 2
-- =========================================================

-- =========================
-- TRANSPORT_STATUSES
-- =========================

CREATE TABLE IF NOT EXISTS transport_statuses (
    id SERIAL PRIMARY KEY,

    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),

    CONSTRAINT chk_transport_statuses_name
        CHECK (name IN ('ZAPLANOWANY', 'W_DRODZE', 'DOSTARCZONY', 'NIEUDANY', 'ANULOWANY'))
);

-- =========================
-- ROUTE_DIFFICULTY_LEVELS
-- =========================

CREATE TABLE IF NOT EXISTS route_difficulty_levels (
    id SERIAL PRIMARY KEY,

    name VARCHAR(30) NOT NULL UNIQUE,
    risk_level INT NOT NULL,
    description VARCHAR(255),

    CONSTRAINT chk_route_difficulty_risk
        CHECK (risk_level BETWEEN 1 AND 5)
);

-- =========================
-- ROUTES
-- =========================

CREATE TABLE IF NOT EXISTS routes (
    id SERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    start_point VARCHAR(100) NOT NULL,
    end_point VARCHAR(100) NOT NULL,

    distance_km NUMERIC(8, 2),
    difficulty_level_id INT NOT NULL,

    description TEXT,

    CONSTRAINT fk_routes_difficulty_level
        FOREIGN KEY (difficulty_level_id)
        REFERENCES route_difficulty_levels(id),

    CONSTRAINT chk_routes_name_length
        CHECK (LENGTH(name) BETWEEN 3 AND 100),

    CONSTRAINT chk_routes_distance
        CHECK (distance_km IS NULL OR distance_km > 0)
);

-- =========================
-- VEHICLES
-- =========================

CREATE TABLE IF NOT EXISTS vehicles (
    id SERIAL PRIMARY KEY,

    registration_number VARCHAR(20) NOT NULL UNIQUE,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(30) NOT NULL,

    load_capacity INT NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_vehicle_type
        CHECK (vehicle_type IN ('SAMOCHOD_OSOBOWY', 'BUS', 'CIEZAROWKA', 'VAN')),

    CONSTRAINT chk_vehicle_load_capacity
        CHECK (load_capacity > 0)
);

-- =========================
-- TRANSPORTS
-- =========================

CREATE TABLE IF NOT EXISTS transports (
    id SERIAL PRIMARY KEY,

    order_id INT NOT NULL,
    route_id INT,
    vehicle_id INT,

    status_id INT NOT NULL,

    start_location VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,

    transport_date DATE NOT NULL,
    planned_arrival_date DATE,

    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_transports_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transports_route
        FOREIGN KEY (route_id)
        REFERENCES routes(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_transports_vehicle
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicles(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_transports_status
        FOREIGN KEY (status_id)
        REFERENCES transport_statuses(id),

    CONSTRAINT chk_transport_dates
        CHECK (
            planned_arrival_date IS NULL
            OR planned_arrival_date >= transport_date
        )
);

-- =========================
-- SMUGGLER_ASSIGNMENTS
-- przypisanie przemytników do transportów
-- =========================

CREATE TABLE IF NOT EXISTS smuggler_assignments (
    id SERIAL PRIMARY KEY,

    transport_id INT NOT NULL,
    smuggler_id INT NOT NULL,

    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    note VARCHAR(255),

    CONSTRAINT fk_smuggler_assignments_transport
        FOREIGN KEY (transport_id)
        REFERENCES transports(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_smuggler_assignments_user
        FOREIGN KEY (smuggler_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_smuggler_transport
        UNIQUE (transport_id, smuggler_id)
);

-- =========================================================
-- CZĘŚĆ 3: ŁADUNKI, MAGAZYNY, PŁATNOŚCI, RAPORTY
-- Odpowiedzialny: Osoba 3
-- =========================================================

-- =========================
-- CARGO_TYPES
-- =========================

CREATE TABLE IF NOT EXISTS cargo_types (
    id SERIAL PRIMARY KEY,

    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),

    CONSTRAINT chk_cargo_types_name
        CHECK (name IN ('PAPIEROSY', 'TYTON', 'MIESZANY'))
);

-- =========================
-- WAREHOUSES
-- =========================

CREATE TABLE IF NOT EXISTS warehouses (
    id SERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL UNIQUE,
    location VARCHAR(150) NOT NULL,

    max_capacity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_warehouses_name_length
        CHECK (LENGTH(name) BETWEEN 3 AND 100),

    CONSTRAINT chk_warehouses_capacity
        CHECK (max_capacity > 0)
);

-- =========================
-- CARGO
-- =========================

CREATE TABLE IF NOT EXISTS cargo (
    id SERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    cargo_type_id INT NOT NULL,

    packages_count INT NOT NULL,
    estimated_value NUMERIC(12, 2) NOT NULL,

    order_id INT,
    transport_id INT,
    warehouse_id INT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_cargo_type
        FOREIGN KEY (cargo_type_id)
        REFERENCES cargo_types(id),

    CONSTRAINT fk_cargo_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_cargo_transport
        FOREIGN KEY (transport_id)
        REFERENCES transports(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_cargo_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_cargo_name_length
        CHECK (LENGTH(name) BETWEEN 3 AND 100),

    CONSTRAINT chk_cargo_packages_count
        CHECK (packages_count > 0),

    CONSTRAINT chk_cargo_estimated_value
        CHECK (estimated_value >= 0)
);

-- =========================
-- WAREHOUSE_STOCK
-- stan magazynowy
-- =========================

CREATE TABLE IF NOT EXISTS warehouse_stock (
    id SERIAL PRIMARY KEY,

    warehouse_id INT NOT NULL,
    cargo_id INT NOT NULL,

    quantity INT NOT NULL,
    added_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_warehouse_stock_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_warehouse_stock_cargo
        FOREIGN KEY (cargo_id)
        REFERENCES cargo(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_warehouse_cargo
        UNIQUE (warehouse_id, cargo_id),

    CONSTRAINT chk_warehouse_stock_quantity
        CHECK (quantity > 0)
);

-- =========================
-- PAYMENT_STATUSES
-- =========================

CREATE TABLE IF NOT EXISTS payment_statuses (
    id SERIAL PRIMARY KEY,

    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),

    CONSTRAINT chk_payment_statuses_name
        CHECK (name IN ('OCZEKUJACA', 'ZAPLACONA', 'ANULOWANA'))
);

-- =========================
-- PAYMENTS
-- =========================

CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,

    order_id INT NOT NULL,

    amount NUMERIC(12, 2) NOT NULL,
    payment_type VARCHAR(30) NOT NULL,

    status_id INT NOT NULL,
    payment_date DATE,

    description TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_payments_status
        FOREIGN KEY (status_id)
        REFERENCES payment_statuses(id),

    CONSTRAINT chk_payments_amount
        CHECK (amount > 0),

    CONSTRAINT chk_payments_type
        CHECK (payment_type IN ('KOSZT', 'PRZYCHOD', 'PROWIZJA'))
);

-- =========================================================
-- INDEKSY
-- =========================================================

-- USERS / ROLES / ORDERS

CREATE INDEX IF NOT EXISTS idx_users_username
ON users(username);

CREATE INDEX IF NOT EXISTS idx_users_email
ON users(email);

CREATE INDEX IF NOT EXISTS idx_orders_status_id
ON orders(status_id);

CREATE INDEX IF NOT EXISTS idx_orders_created_by_user_id
ON orders(created_by_user_id);

CREATE INDEX IF NOT EXISTS idx_orders_responsible_user_id
ON orders(responsible_user_id);

CREATE INDEX IF NOT EXISTS idx_orders_created_at
ON orders(created_at);

CREATE INDEX IF NOT EXISTS idx_orders_planned_date
ON orders(planned_date);

CREATE INDEX IF NOT EXISTS idx_audit_logs_table_record
ON audit_logs(table_name, record_id);

CREATE INDEX IF NOT EXISTS idx_audit_logs_changed_at
ON audit_logs(changed_at);

-- TRANSPORTS / ROUTES / VEHICLES

CREATE INDEX IF NOT EXISTS idx_transports_order_id
ON transports(order_id);

CREATE INDEX IF NOT EXISTS idx_transports_status_id
ON transports(status_id);

CREATE INDEX IF NOT EXISTS idx_transports_transport_date
ON transports(transport_date);

CREATE INDEX IF NOT EXISTS idx_transports_route_id
ON transports(route_id);

CREATE INDEX IF NOT EXISTS idx_transports_vehicle_id
ON transports(vehicle_id);

CREATE INDEX IF NOT EXISTS idx_smuggler_assignments_transport_id
ON smuggler_assignments(transport_id);

CREATE INDEX IF NOT EXISTS idx_smuggler_assignments_smuggler_id
ON smuggler_assignments(smuggler_id);

CREATE INDEX IF NOT EXISTS idx_routes_difficulty_level_id
ON routes(difficulty_level_id);

CREATE INDEX IF NOT EXISTS idx_vehicles_registration_number
ON vehicles(registration_number);

-- CARGO / WAREHOUSES / PAYMENTS

CREATE INDEX IF NOT EXISTS idx_cargo_type_id
ON cargo(cargo_type_id);

CREATE INDEX IF NOT EXISTS idx_cargo_order_id
ON cargo(order_id);

CREATE INDEX IF NOT EXISTS idx_cargo_transport_id
ON cargo(transport_id);

CREATE INDEX IF NOT EXISTS idx_cargo_warehouse_id
ON cargo(warehouse_id);

CREATE INDEX IF NOT EXISTS idx_warehouse_stock_warehouse_id
ON warehouse_stock(warehouse_id);

CREATE INDEX IF NOT EXISTS idx_warehouse_stock_cargo_id
ON warehouse_stock(cargo_id);

CREATE INDEX IF NOT EXISTS idx_payments_order_id
ON payments(order_id);

CREATE INDEX IF NOT EXISTS idx_payments_status_id
ON payments(status_id);

CREATE INDEX IF NOT EXISTS idx_payments_payment_date
ON payments(payment_date);