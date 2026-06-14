# Wymagania bazy danych

## Schemat i odtworzenie bazy

Schemat bazy znajduje sie w katalogu `database`. Glowny plik
`database/01_schema.sql` tworzy tabele, klucze glowne, klucze obce, ograniczenia
`NOT NULL`, `UNIQUE`, `CHECK` oraz indeksy. Kolejne pliki `02-08.sql` uzupelniaja
slowniki, widoki, funkcje, procedury, triggery oraz role bazodanowe.

Kolejnosc uruchomienia jest opisana w `database/README.md`.

## Relacje i ERD

Model danych jest opisany w `docs/06-model-danych.md`, a diagram ERD znajduje sie
w `docs/diagram.svg`. Najwazniejsze relacje:

- `users` - `roles` przez tabele `user_roles`,
- `users` - `smuggler_profiles` jako relacja jeden-do-jednego,
- `orders` - `transports` jako relacja jeden-do-wielu,
- `transports` - `vehicles`, `routes`, `transport_statuses`,
- `transports` - `smuggler_assignments` - `smuggler_profiles`,
- `cargo` - `cargo_types`, `orders`, `transports`, `warehouses`,
- `warehouse_stock` - `warehouses` i `cargo`,
- `payments` - `orders` i `payment_statuses`.

## Indeksy

Indeksy podstawowe sa tworzone w `database/01_schema.sql`, m.in. dla kolumn:

- `orders.status_id`, `orders.created_by_user_id`, `orders.planned_date`,
- `transports.order_id`, `transports.status_id`, `transports.transport_date`,
- `cargo.order_id`, `cargo.transport_id`, `cargo.warehouse_id`,
- `warehouse_stock.warehouse_id`, `warehouse_stock.cargo_id`,
- `payments.order_id`, `payments.status_id`, `payments.payment_date`.

Dodatkowe indeksy do testow wydajnosciowych sa w
`database/performance/03_indexes.sql`.

## Widoki, funkcje, procedury i triggery

Projekt zawiera logike po stronie PostgreSQL:

- widoki w `database/04_views.sql`, np. `v_available_vehicles`,
  `v_available_cargo`, `v_active_transports`, `v_transport_details`,
  `v_warehouse_stock`, `v_profit_report`,
- funkcje w `database/05_functions.sql`, np.
  `calculate_transport_risk_score`, `estimate_transport_operational_cost`,
  `calculate_transport_estimated_profit`, `calculate_order_profit`,
- procedury w `database/06_procedures.sql`, np.
  `create_order`, `change_order_status`, `assign_smuggler_to_transport`,
  `assign_vehicle_to_transport`, `assign_cargo_to_transport`,
  `change_transport_status`, `add_cargo_to_warehouse`, `register_payment`,
- triggery w `database/07_triggers.sql`, np. audyt zlecen/uzytkownikow/platnosci,
  walidacja zmian statusu transportu, kontrola pojemnosci magazynu i odswiezanie
  przewidywanego zysku zlecen.

## ORM

Aplikacja korzysta ze Spring Data JPA/Hibernate. Encje sa w pakietach
`pl.edu.pb.smuggling.*.model`, a repozytoria dziedzicza po `JpaRepository`.
Przyklady encji: `User`, `Role`, `SmugglingOrder`, `Transport`, `Cargo`,
`Warehouse`, `WarehouseStock`, `Payment`.

Czesc raportowa i selektory transportowe korzystaja dodatkowo z `JdbcTemplate`,
poniewaz odczytuja dane z widokow SQL oraz funkcji PostgreSQL.

## Autoryzacja

Spring Security jest skonfigurowany w `SecurityConfig`. Uzytkownicy i role sa
ladowane z bazy przez `CustomUserDetailsService`. Dostep do zasobow jest
ograniczany adnotacjami `@PreAuthorize`.

Glowne role:

- `ADMIN` - uzytkownicy, role, audyt, pelny dostep administracyjny,
- `BOSS` - zlecenia, transporty, trasy, pojazdy i czesc raportow,
- `SMUGGLER` - odczyt przypisanych transportow i wybranych slownikow,
- `ACCOUNTANT` - platnosci, magazyny i raporty finansowe.

## Paginacja

Stronicowanie jest dostepne dla glownych list REST:

- `GET /api/orders?page=0&size=20&sort=id&dir=asc`,
- `GET /api/users?page=0&size=20&sort=id&dir=asc`,
- `GET /api/transports`,
- `GET /api/routes`,
- `GET /api/vehicles`,
- `GET /api/audit-logs`,
- `GET /api/auth-logs`.

## Swagger/OpenAPI

Dokumentacja OpenAPI jest udostepniana przez Springdoc. Po uruchomieniu aplikacji
Swagger UI jest dostepny pod adresem:

```text
http://localhost:8080/swagger-ui/index.html
```

Alternatywny adres:

```text
http://localhost:8080/swagger-ui.html
```

Proste requesty do recznego sprawdzenia API sa w pliku `backend/requests.http`.

## Testy wydajnosciowe

Opis testow wydajnosciowych dla tabeli `transports` znajduje sie w
`docs/09-testy-wydajnosciowe.md`. Skrypty SQL sa w katalogu
`database/performance`.
