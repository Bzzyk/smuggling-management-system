# Wymagania bazy danych

## Schemat i odtworzenie bazy

Schemat bazy znajduje się w katalogu `database`. Główny plik
`database/01_schema.sql` tworzy tabele, klucze główne, klucze obce, ograniczenia
`NOT NULL`, `UNIQUE`, `CHECK` oraz indeksy. Kolejne pliki `02-08.sql` uzupełniają
słowniki, widoki, funkcje, procedury, triggery oraz role bazodanowe.

Kolejność uruchomienia jest opisana w `database/README.md`.

## Relacje i ERD

Model danych jest opisany w `docs/06-model-danych.md`, a diagram ERD znajduje się
w `docs/diagram.svg`.

Najważniejsze relacje:

- `users` - `roles` przez tabelę `user_roles`,
- `users` - `smuggler_profiles` jako relacja jeden-do-jednego,
- `orders` - `transports` jako relacja jeden-do-wielu,
- `transports` - `vehicles`, `routes`, `transport_statuses`,
- `transports` - `smuggler_assignments` - `smuggler_profiles`,
- `cargo` - `cargo_types`, `orders`, `transports`, `warehouses`,
- `warehouse_stock` - `warehouses` i `cargo`,
- `payments` - `orders` i `payment_statuses`.

## Indeksy

Indeksy podstawowe są tworzone w `database/01_schema.sql`, m.in. dla kolumn:

- `orders.status_id`, `orders.created_by_user_id`, `orders.planned_date`,
- `transports.order_id`, `transports.status_id`, `transports.transport_date`,
- `cargo.order_id`, `cargo.transport_id`, `cargo.warehouse_id`,
- `warehouse_stock.warehouse_id`, `warehouse_stock.cargo_id`,
- `payments.order_id`, `payments.status_id`, `payments.payment_date`.

Dodatkowe indeksy do testów wydajnościowych są w
`database/performance/03_indexes.sql`.

## Widoki, funkcje, procedury i triggery

Projekt zawiera logikę po stronie PostgreSQL:

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
- triggery w `database/07_triggers.sql`, np. audyt zleceń/użytkowników/płatności,
  walidacja zmian statusu transportu, kontrola pojemności magazynu i odświeżanie
  przewidywanego zysku zleceń.

## ORM

Aplikacja korzysta ze Spring Data JPA/Hibernate. Encje są w pakietach
`pl.edu.pb.smuggling.*.model`, a repozytoria dziedziczą po `JpaRepository`.

Przykładowe encje:

- `User`,
- `Role`,
- `SmugglingOrder`,
- `Transport`,
- `Cargo`,
- `Warehouse`,
- `WarehouseStock`,
- `Payment`.

Część raportowa i selektory transportowe korzystają dodatkowo z `JdbcTemplate`,
ponieważ odczytują dane z widoków SQL oraz funkcji PostgreSQL.

## Autoryzacja

Spring Security jest skonfigurowany w `SecurityConfig`. Użytkownicy i role są
ładowane z bazy przez `CustomUserDetailsService`. Dostęp do zasobów jest
ograniczany adnotacjami `@PreAuthorize`.

Główne role:

- `ADMIN` - użytkownicy, role, audyt, pełny dostęp administracyjny,
- `BOSS` - zlecenia, transporty, trasy, pojazdy i część raportów,
- `SMUGGLER` - odczyt przypisanych transportów i wybranych słowników,
- `ACCOUNTANT` - płatności, magazyny i raporty finansowe.

## Paginacja

Stronicowanie jest dostępne dla głównych list REST:

- `GET /api/orders?page=0&size=20&sort=id&dir=asc`,
- `GET /api/users?page=0&size=20&sort=id&dir=asc`,
- `GET /api/transports`,
- `GET /api/routes`,
- `GET /api/vehicles`,
- `GET /api/audit-logs`,
- `GET /api/auth-logs`.

Endpointy stronicowane zwracają obiekt `Page`, a właściwe rekordy są w polu
`content`.

## Swagger/OpenAPI

Dokumentacja OpenAPI jest udostępniana przez Springdoc. Po uruchomieniu aplikacji
Swagger UI jest dostępny pod adresem:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Proste requesty do ręcznego sprawdzenia API są w pliku `backend/requests.http`.

## Testy wydajnościowe

Opis testów wydajnościowych dla tabeli `transports` znajduje się w
`docs/09-testy-wydajnosciowe.md`. Skrypty SQL są w katalogu
`database/performance`.
