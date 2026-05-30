-- =========================================================
-- System zarzadzania przemytem papierosow
-- Plik: database/02_dictionaries.sql
-- Dane slownikowe
-- =========================================================


-- ROLES

INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administrator systemu zarzadzajacy uzytkownikami i rolami'),
    ('BOSS', 'Uzytkownik tworzacy zlecenia i zarzadzajacy transportami'),
    ('SMUGGLER', 'Uzytkownik przypisywany do transportow'),
    ('ACCOUNTANT', 'Uzytkownik zarzadzajacy platnosciami i raportami finansowymi')
ON CONFLICT (name) DO NOTHING;


-- ORDER_STATUSES

INSERT INTO order_statuses (name, description) VALUES
    ('NOWE', 'Nowo utworzone zlecenie'),
    ('W_TRAKCIE', 'Zlecenie jest w trakcie realizacji'),
    ('ZREALIZOWANE', 'Zlecenie zostalo zakonczone'),
    ('ANULOWANE', 'Zlecenie zostalo anulowane')
ON CONFLICT (name) DO NOTHING;


-- TRANSPORT_STATUSES

INSERT INTO transport_statuses (name, description) VALUES
    ('ZAPLANOWANY', 'Transport zostal zaplanowany'),
    ('W_DRODZE', 'Transport jest w trakcie realizacji'),
    ('DOSTARCZONY', 'Transport zostal dostarczony'),
    ('NIEUDANY', 'Transport zakonczyl sie niepowodzeniem'),
    ('ANULOWANY', 'Transport zostal anulowany')
ON CONFLICT (name) DO NOTHING;


-- ROUTE_DIFFICULTY_LEVELS

INSERT INTO route_difficulty_levels (name, risk_level, description) VALUES
    ('LATWA', 1, 'Trasa o niskim poziomie ryzyka'),
    ('STANDARDOWA', 2, 'Trasa o umiarkowanym poziomie ryzyka'),
    ('TRUDNA', 3, 'Trasa wymagajaca doswiadczenia'),
    ('BARDZO_TRUDNA', 4, 'Trasa o wysokim poziomie ryzyka'),
    ('EKSTREMALNA', 5, 'Trasa o najwyzszym poziomie ryzyka')
ON CONFLICT (name) DO NOTHING;


-- CARGO_TYPES

INSERT INTO cargo_types (name, description) VALUES
    ('PAPIEROSY', 'Ladunek papierosow'),
    ('TYTON', 'Ladunek tytoniu'),
    ('MIESZANY', 'Ladunek mieszany')
ON CONFLICT (name) DO NOTHING;


-- PAYMENT_STATUSES

INSERT INTO payment_statuses (name, description) VALUES
    ('OCZEKUJACA', 'Platnosc oczekuje na realizacje'),
    ('ZAPLACONA', 'Platnosc zostala zrealizowana'),
    ('ANULOWANA', 'Platnosc zostala anulowana')
ON CONFLICT (name) DO NOTHING;
