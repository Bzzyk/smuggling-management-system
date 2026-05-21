# Podział pracy w projekcie

## Temat projektu

**System zarządzania przemytem papierosów**

Projekt ma charakter fikcyjny i edukacyjny. Celem projektu jest przygotowanie aplikacji webowej w Javie oraz relacyjnej bazy danych spełniającej wymagania przedmiotów:

- Projektowanie aplikacji WWW w języku Java,
- Systemy baz danych.

Projekt realizowany jest przez trzy osoby. Podział pracy został przygotowany tak, aby każda osoba miała porównywalny wkład zarówno w część bazodanową, jak i w część aplikacji Java/Spring.

---

# 1. Ogólny podział modułów

| Osoba | Główny obszar odpowiedzialności |
|---|---|
| Kamil Osakowicz | Użytkownicy, role, zlecenia, podstawowa konfiguracja bezpieczeństwa, historia zmian |
| Osoba 2 | Transporty, trasy, pojazdy, przypisanie przemytników, bezpieczeństwo modułu transportowego |
| Osoba 3 | Ładunki, magazyny, płatności, raporty, bezpieczeństwo modułu magazynowo-finansowego |

Każda osoba odpowiada za:

- część tabel w bazie danych,
- widoki SQL,
- funkcje SQL,
- procedury SQL,
- triggery SQL,
- encje JPA,
- repozytoria,
- serwisy,
- kontrolery MVC,
- REST API,
- widoki Thymeleaf,
- walidację formularzy,
- część zabezpieczeń Spring Security,
- dokumentację swojego modułu,
- prezentację swojego zakresu prac.

---

# 2. Podział części bazodanowej

## 2.1. Kamil Osakowicz

### Tabele

Kamil odpowiada za przygotowanie tabel związanych z użytkownikami, rolami, zleceniami oraz historią zmian:

- `users`
- `roles`
- `user_roles`
- `orders`
- `order_statuses`
- `audit_logs`

### Opis tabel

- `users` — przechowuje dane użytkowników systemu.
- `roles` — przechowuje role użytkowników, np. `ADMIN`, `BOSS`, `SMUGGLER`, `ACCOUNTANT`.
- `user_roles` — tabela łącząca użytkowników z rolami.
- `orders` — przechowuje zlecenia przemytu.
- `order_statuses` — słownik statusów zleceń.
- `audit_logs` — przechowuje historię zmian wykonywanych w systemie.

### Widoki SQL

- `v_user_permissions` — widok pokazujący użytkowników oraz przypisane im role.
- `v_active_orders` — widok pokazujący aktywne zlecenia.

### Funkcje SQL

- `count_orders_for_user(user_id)` — funkcja licząca liczbę zleceń przypisanych do danego użytkownika.
- `check_user_role(user_id, role_name)` — funkcja sprawdzająca, czy użytkownik posiada określoną rolę.

### Procedury SQL

- `create_order(...)` — procedura tworząca nowe zlecenie.
- `change_order_status(...)` — procedura zmieniająca status zlecenia.

### Triggery SQL

- `trg_audit_orders` — trigger zapisujący historię zmian w tabeli zleceń.
- `trg_audit_users` — trigger zapisujący historię zmian w tabeli użytkowników.

### Uwagi

Kamil przygotowuje ogólną tabelę `audit_logs`, ale nie odpowiada za wszystkie triggery audytowe w całym projekcie. Każda osoba przygotowuje triggery historii zmian dla własnych tabel.

---

## 2.2. Osoba 2

### Tabele

Osoba 2 odpowiada za przygotowanie tabel związanych z transportami, trasami, pojazdami oraz przypisaniem przemytników:

- `transports`
- `transport_statuses`
- `routes`
- `route_difficulty_levels`
- `smuggler_assignments`
- `vehicles`

### Opis tabel

- `transports` — przechowuje dane transportów.
- `transport_statuses` — słownik statusów transportu.
- `routes` — przechowuje trasy transportu.
- `route_difficulty_levels` — słownik poziomów trudności lub ryzyka trasy.
- `smuggler_assignments` — przechowuje przypisania przemytników do transportów.
- `vehicles` — przechowuje dane pojazdów.

### Widoki SQL

- `v_active_transports` — widok pokazujący aktywne transporty.
- `v_smuggler_workload` — widok pokazujący obciążenie przemytników transportami.

### Funkcje SQL

- `calculate_route_risk(route_id)` — funkcja obliczająca poziom ryzyka danej trasy.
- `count_active_transports_for_smuggler(user_id)` — funkcja licząca aktywne transporty przypisane do danego przemytnika.

### Procedury SQL

- `assign_smuggler_to_transport(...)` — procedura przypisująca przemytnika do transportu.
- `change_transport_status(...)` — procedura zmieniająca status transportu.

### Triggery SQL

- `trg_audit_transports` — trigger zapisujący historię zmian transportów.
- `trg_check_transport_dates` — trigger sprawdzający poprawność dat transportu.

---

## 2.3. Osoba 3

### Tabele

Osoba 3 odpowiada za przygotowanie tabel związanych z ładunkami, magazynami, płatnościami i raportami:

- `cargo`
- `cargo_types`
- `warehouses`
- `warehouse_stock`
- `payments`
- `payment_statuses`

### Opis tabel

- `cargo` — przechowuje informacje o ładunkach.
- `cargo_types` — słownik typów ładunków.
- `warehouses` — przechowuje dane magazynów.
- `warehouse_stock` — przechowuje aktualny stan magazynowy.
- `payments` — przechowuje dane płatności, kosztów i zysków.
- `payment_statuses` — słownik statusów płatności.

### Widoki SQL

- `v_warehouse_stock` — widok pokazujący aktualny stan magazynów.
- `v_profit_report` — widok pokazujący raport zysków i kosztów.

### Funkcje SQL

- `calculate_cargo_value(cargo_id)` — funkcja obliczająca wartość ładunku.
- `calculate_order_profit(order_id)` — funkcja obliczająca zysk ze zlecenia.

### Procedury SQL

- `add_cargo_to_warehouse(...)` — procedura dodająca ładunek do magazynu.
- `register_payment(...)` — procedura rejestrująca płatność.

### Triggery SQL

- `trg_check_warehouse_capacity` — trigger sprawdzający, czy magazyn nie przekracza pojemności.
- `trg_audit_payments` — trigger zapisujący historię zmian płatności.

---

# 3. Podział części Java / Spring Boot

## 3.1. Kamil Osakowicz

### Moduły aplikacji

Kamil odpowiada za moduły:

- `security`
- `user`
- `role`
- `order`
- `audit`

### Encje JPA

- `User`
- `Role`
- `Order`
- `OrderStatus`
- `AuditLog`

### Repozytoria

- `UserRepository`
- `RoleRepository`
- `OrderRepository`
- `OrderStatusRepository`
- `AuditLogRepository`

### Serwisy

- `UserService`
- `RoleService`
- `OrderService`
- `AuditService`

### Kontrolery MVC

- `UserController`
- `RoleController`
- `OrderController`
- `AuthController`

### Kontrolery REST

- `UserRestController`
- `OrderRestController`

### Zakres funkcjonalny

Kamil odpowiada za:

- konfigurację projektu Spring Boot,
- podłączenie aplikacji do bazy danych,
- logowanie użytkowników,
- rejestrację użytkowników,
- obsługę ról,
- panel administratora,
- listę użytkowników,
- zarządzanie rolami,
- CRUD zleceń,
- zmianę statusu zlecenia,
- obsługę sesji,
- obsługę ciasteczek,
- podstawową konfigurację Spring Security,
- REST API dla zleceń i użytkowników,
- podstawową historię zmian w systemie.

---

## 3.2. Osoba 2

### Moduły aplikacji

Osoba 2 odpowiada za moduły:

- `transport`
- `route`
- `vehicle`
- `smugglerAssignment`

### Encje JPA

- `Transport`
- `TransportStatus`
- `Route`
- `RouteDifficultyLevel`
- `Vehicle`
- `SmugglerAssignment`

### Repozytoria

- `TransportRepository`
- `TransportStatusRepository`
- `RouteRepository`
- `RouteDifficultyLevelRepository`
- `VehicleRepository`
- `SmugglerAssignmentRepository`

### Serwisy

- `TransportService`
- `RouteService`
- `VehicleService`
- `SmugglerAssignmentService`

### Kontrolery MVC

- `TransportController`
- `RouteController`
- `VehicleController`
- `SmugglerAssignmentController`

### Kontrolery REST

- `TransportRestController`
- `RouteRestController`
- `VehicleRestController`

### Zakres funkcjonalny

Osoba 2 odpowiada za:

- CRUD transportów,
- CRUD tras,
- CRUD pojazdów,
- przypisywanie przemytnika do transportu,
- zmianę statusu transportu,
- sortowanie transportów,
- filtrowanie transportów po dacie i statusie,
- walidację formularzy transportów, tras i pojazdów,
- REST API dla transportów, tras i pojazdów,
- zabezpieczenie dostępu do modułu transportów,
- historię zmian w module transportowym.

---

## 3.3. Osoba 3

### Moduły aplikacji

Osoba 3 odpowiada za moduły:

- `cargo`
- `warehouse`
- `payment`
- `report`
- `external`

### Encje JPA

- `Cargo`
- `CargoType`
- `Warehouse`
- `WarehouseStock`
- `Payment`
- `PaymentStatus`

### Repozytoria

- `CargoRepository`
- `CargoTypeRepository`
- `WarehouseRepository`
- `WarehouseStockRepository`
- `PaymentRepository`
- `PaymentStatusRepository`

### Serwisy

- `CargoService`
- `WarehouseService`
- `PaymentService`
- `ReportService`
- `CurrencyService`

### Kontrolery MVC

- `CargoController`
- `WarehouseController`
- `PaymentController`
- `ReportController`

### Kontrolery REST

- `CargoRestController`
- `WarehouseRestController`
- `PaymentRestController`
- `ReportRestController`

### Klient REST

- `NbpClient`

### Zakres funkcjonalny

Osoba 3 odpowiada za:

- CRUD ładunków,
- CRUD magazynów,
- obsługę stanu magazynowego,
- CRUD płatności,
- raport zysków i strat,
- raport stanu magazynowego,
- raport ryzyka,
- REST API dla ładunków, magazynów, płatności i raportów,
- klienta REST do zewnętrznego API, np. NBP API,
- zabezpieczenie dostępu do raportów i płatności,
- historię zmian w module magazynowo-finansowym.

---

# 4. Podział Spring Security i kontroli dostępu

Spring Security oraz kontrola dostępu zostały podzielone między członków zespołu. Kamil odpowiada za konfigurację podstawową bezpieczeństwa aplikacji, natomiast każda osoba odpowiada za zabezpieczenie własnego modułu biznesowego.

Dzięki temu bezpieczeństwo nie jest realizowane tylko przez jedną osobę, lecz jest częścią pracy każdego członka zespołu.

## 4.1. Kamil Osakowicz — konfiguracja podstawowa bezpieczeństwa

Kamil odpowiada za podstawową konfigurację Spring Security oraz mechanizmy logowania użytkowników.

### Zakres

- konfiguracja `SecurityConfig`,
- konfiguracja formularza logowania,
- konfiguracja wylogowania,
- obsługa rejestracji użytkowników,
- haszowanie haseł za pomocą BCrypt,
- pobieranie użytkownika z bazy danych przez `CustomUserDetailsService`,
- przygotowanie klas związanych z użytkownikiem zalogowanym,
- podstawowe role użytkowników,
- podstawowe reguły dostępu do stron publicznych i administracyjnych.

### Przykładowe reguły

- `/login` — dostępne dla wszystkich,
- `/register` — dostępne dla wszystkich,
- `/` — dostępne dla wszystkich,
- `/admin/**` — dostępne tylko dla roli `ADMIN`,
- `/users/**` — dostępne tylko dla roli `ADMIN`,
- `/orders/**` — dostępne dla ról `ADMIN` oraz `BOSS`.

Kamil nie odpowiada za pełne zabezpieczenie wszystkich modułów aplikacji. Każdy członek zespołu zabezpiecza swój moduł biznesowy.

---

## 4.2. Osoba 2 — kontrola dostępu do modułu transportowego

Osoba 2 odpowiada za zabezpieczenie modułów związanych z transportami, trasami, pojazdami oraz przypisaniem przemytników.

### Zakres

- zabezpieczenie kontrolerów MVC dla transportów,
- zabezpieczenie kontrolerów MVC dla tras,
- zabezpieczenie kontrolerów MVC dla pojazdów,
- zabezpieczenie endpointów REST związanych z transportami,
- zastosowanie adnotacji `@PreAuthorize` w swoim module,
- ograniczenie dostępu do transportów na podstawie roli użytkownika,
- ukrywanie przycisków w widokach Thymeleaf zależnie od roli.

### Przykładowe reguły

- `ADMIN` i `BOSS` mogą dodawać, edytować i usuwać transporty,
- `SMUGGLER` może przeglądać tylko przypisane do siebie transporty,
- `SMUGGLER` może zmienić status przypisanego do siebie transportu,
- `SMUGGLER` nie może usuwać transportów,
- użytkownik bez odpowiedniej roli nie ma dostępu do modułu transportów.

---

## 4.3. Osoba 3 — kontrola dostępu do modułu magazynowo-finansowego i raportów

Osoba 3 odpowiada za zabezpieczenie modułów związanych z ładunkami, magazynami, płatnościami oraz raportami.

### Zakres

- zabezpieczenie kontrolerów MVC dla ładunków,
- zabezpieczenie kontrolerów MVC dla magazynów,
- zabezpieczenie kontrolerów MVC dla płatności,
- zabezpieczenie kontrolerów MVC dla raportów,
- zabezpieczenie endpointów REST związanych z ładunkami, płatnościami i raportami,
- zastosowanie adnotacji `@PreAuthorize` w swoim module,
- ograniczenie dostępu do raportów finansowych,
- ukrywanie przycisków w widokach Thymeleaf zależnie od roli.

### Przykładowe reguły

- `ADMIN` i `BOSS` mają dostęp do raportów,
- `ACCOUNTANT` ma dostęp do płatności i raportów finansowych,
- `SMUGGLER` nie ma dostępu do płatności ani raportów finansowych,
- `BOSS` może zarządzać magazynami,
- użytkownik bez odpowiedniej roli nie ma dostępu do modułu raportów.

---

# 5. Podział frontendu / Thymeleaf

Frontend jest podzielony według modułów biznesowych. Każda osoba odpowiada za widoki Thymeleaf, formularze, komunikaty walidacyjne oraz elementy widoczne zależnie od roli w swoim module.

## 5.1. Kamil Osakowicz

### Widoki

- `templates/layout.html`
- `templates/index.html`
- `templates/login.html`
- `templates/register.html`
- `templates/users/list.html`
- `templates/users/form.html`
- `templates/users/details.html`
- `templates/roles/list.html`
- `templates/roles/form.html`
- `templates/orders/list.html`
- `templates/orders/form.html`
- `templates/orders/details.html`

### Zakres

Kamil odpowiada za:

- główny layout aplikacji,
- menu nawigacyjne,
- stronę główną,
- stronę logowania,
- stronę rejestracji,
- listę użytkowników,
- formularze użytkowników,
- zarządzanie rolami,
- listę zleceń,
- formularz zlecenia,
- szczegóły zlecenia,
- komunikaty błędów walidacji,
- ukrywanie przycisków zależnie od roli.

---

## 5.2. Osoba 2

### Widoki

- `templates/transports/list.html`
- `templates/transports/form.html`
- `templates/transports/details.html`
- `templates/transports/confirm-delete.html`
- `templates/routes/list.html`
- `templates/routes/form.html`
- `templates/routes/details.html`
- `templates/vehicles/list.html`
- `templates/vehicles/form.html`
- `templates/vehicles/details.html`

### Zakres

Osoba 2 odpowiada za:

- listę transportów,
- formularz dodawania i edycji transportu,
- szczegóły transportu,
- potwierdzenie usuwania transportu,
- listę tras,
- formularz tras,
- szczegóły trasy,
- listę pojazdów,
- formularz pojazdu,
- sortowanie transportów,
- filtrowanie transportów,
- komunikaty błędów walidacji,
- ukrywanie przycisków zależnie od roli.

---

## 5.3. Osoba 3

### Widoki

- `templates/cargo/list.html`
- `templates/cargo/form.html`
- `templates/cargo/details.html`
- `templates/warehouses/list.html`
- `templates/warehouses/form.html`
- `templates/warehouses/details.html`
- `templates/payments/list.html`
- `templates/payments/form.html`
- `templates/payments/details.html`
- `templates/reports/profit.html`
- `templates/reports/warehouse-stock.html`
- `templates/reports/risk.html`

### Zakres

Osoba 3 odpowiada za:

- listę ładunków,
- formularz ładunku,
- szczegóły ładunku,
- listę magazynów,
- formularz magazynu,
- szczegóły magazynu,
- listę płatności,
- formularz płatności,
- szczegóły płatności,
- raport zysków i strat,
- raport stanu magazynowego,
- raport ryzyka,
- widok z danymi z klienta REST,
- komunikaty błędów walidacji,
- ukrywanie przycisków zależnie od roli.

---

# 6. Podział REST API

## 6.1. Kamil Osakowicz

Kamil odpowiada za REST API związane ze zleceniami i użytkownikami:

- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `PUT /api/orders/{id}`
- `DELETE /api/orders/{id}`
- `GET /api/users`
- `GET /api/users/{id}`

---

## 6.2. Osoba 2

Osoba 2 odpowiada za REST API związane z transportami, trasami i pojazdami:

- `GET /api/transports`
- `GET /api/transports/{id}`
- `POST /api/transports`
- `PUT /api/transports/{id}`
- `DELETE /api/transports/{id}`
- `GET /api/routes`
- `GET /api/routes/{id}`
- `GET /api/vehicles`
- `GET /api/vehicles/{id}`

---

## 6.3. Osoba 3

Osoba 3 odpowiada za REST API związane z ładunkami, magazynami, płatnościami i raportami:

- `GET /api/cargo`
- `GET /api/cargo/{id}`
- `POST /api/cargo`
- `PUT /api/cargo/{id}`
- `DELETE /api/cargo/{id}`
- `GET /api/warehouses`
- `GET /api/payments`
- `GET /api/reports/profit`
- `GET /api/reports/warehouse-stock`
- `GET /api/reports/risk`

---

# 7. Podział walidacji formularzy

Każda osoba odpowiada za walidację formularzy w swoim module.

## 7.1. Kamil Osakowicz

Kamil odpowiada za walidację:

- loginu,
- hasła,
- imienia,
- nazwiska,
- roli użytkownika,
- tytułu zlecenia,
- daty zlecenia,
- statusu zlecenia.

### Przykładowe reguły walidacji

- login musi mieć od 3 do 20 znaków,
- hasło musi mieć co najmniej 5 znaków,
- imię i nazwisko nie mogą być puste,
- tytuł zlecenia musi mieć od 3 do 50 znaków,
- data planowanego zlecenia nie może być pusta,
- status zlecenia jest wymagany.

---

## 7.2. Osoba 2

Osoba 2 odpowiada za walidację:

- numeru rejestracyjnego pojazdu,
- daty transportu,
- miejsca startowego,
- miejsca docelowego,
- statusu transportu,
- poziomu ryzyka trasy,
- przypisanego przemytnika.

### Przykładowe reguły walidacji

- numer rejestracyjny pojazdu jest wymagany,
- data transportu jest wymagana,
- miejsce startowe nie może być puste,
- miejsce docelowe nie może być puste,
- poziom ryzyka musi mieścić się w określonym zakresie,
- pojazd musi zostać przypisany do transportu,
- przemytnik musi zostać przypisany do transportu.

---

## 7.3. Osoba 3

Osoba 3 odpowiada za walidację:

- nazwy ładunku,
- typu ładunku,
- liczby paczek,
- wartości ładunku,
- pojemności magazynu,
- kwoty płatności,
- statusu płatności.

### Przykładowe reguły walidacji

- nazwa ładunku nie może być pusta,
- typ ładunku jest wymagany,
- liczba paczek musi być większa od 0,
- wartość ładunku musi być większa od 0,
- pojemność magazynu musi być większa od 0,
- kwota płatności musi być większa od 0,
- status płatności jest wymagany.

---

# 8. Podział dokumentacji

## 8.1. Kamil Osakowicz

Kamil odpowiada za przygotowanie:

- `README.md`
- `docs/01-opis-projektu.md`
- `docs/04-podzial-pracy.md`
- `docs/07-bezpieczenstwo.md`
- części pliku `docs/08-api-rest.md` dotyczącej użytkowników i zleceń.

### Zakres opisu

- cel projektu,
- technologie,
- instrukcja uruchomienia,
- podział pracy,
- role użytkowników,
- logowanie,
- rejestracja,
- podstawowa konfiguracja Spring Security,
- sesja i ciasteczka,
- uprawnienia,
- historia zmian.

---

## 8.2. Osoba 2

Osoba 2 odpowiada za przygotowanie:

- części pliku `docs/06-model-danych.md` dotyczącej transportów, tras i pojazdów,
- części pliku `docs/08-api-rest.md` dotyczącej transportów, tras i pojazdów,
- `docs/09-modul-transportow.md`.

### Zakres opisu

- transporty,
- trasy,
- pojazdy,
- przypisanie przemytników,
- sortowanie,
- filtrowanie,
- bezpieczeństwo modułu transportowego,
- widoki SQL dla transportów,
- funkcje, procedury i triggery związane z transportami.

---

## 8.3. Osoba 3

Osoba 3 odpowiada za przygotowanie:

- części pliku `docs/06-model-danych.md` dotyczącej ładunków, magazynów i płatności,
- części pliku `docs/08-api-rest.md` dotyczącej ładunków, magazynów, płatności i raportów,
- `docs/10-raporty-i-platnosci.md`.

### Zakres opisu

- ładunki,
- magazyny,
- płatności,
- raporty,
- bezpieczeństwo modułu magazynowo-finansowego,
- klient REST,
- API NBP,
- widoki raportowe,
- funkcje, procedury i triggery związane z magazynami oraz płatnościami.

---

# 9. Podział prezentacji postępów

## 9.1. Prezentacja 20-30%

### Kamil Osakowicz pokazuje

- repozytorium projektu,
- strukturę katalogów,
- opis projektu,
- podział pracy,
- pierwsze tabele `users`, `roles`, `orders`,
- pierwszą konfigurację Spring Boot,
- szkic logowania i Spring Security.

### Osoba 2 pokazuje

- tabele `transports`, `routes`, `vehicles`,
- pierwszy CRUD transportów,
- pierwsze widoki transportów,
- plan sortowania i filtrowania,
- plan zabezpieczenia modułu transportów,
- plan przypisywania przemytników do transportów.

### Osoba 3 pokazuje

- tabele `cargo`, `warehouses`, `payments`,
- pierwszy CRUD ładunków lub magazynów,
- plan raportów,
- plan klienta REST,
- plan zabezpieczenia modułu raportów i płatności.

---

## 9.2. Prezentacja 60-70%

### Kamil Osakowicz pokazuje

- działające logowanie,
- rejestrację,
- role użytkowników,
- zarządzanie użytkownikami,
- CRUD zleceń,
- REST API zleceń,
- historię zmian dla użytkowników i zleceń.

### Osoba 2 pokazuje

- CRUD transportów,
- CRUD tras,
- CRUD pojazdów,
- przypisanie przemytnika do transportu,
- sortowanie i filtrowanie,
- zabezpieczenie modułu transportów,
- REST API transportów,
- historię zmian dla transportów.

### Osoba 3 pokazuje

- CRUD ładunków,
- CRUD magazynów,
- CRUD płatności,
- raport zysków,
- raport stanu magazynowego,
- klient REST NBP,
- zabezpieczenie modułu raportów,
- REST API raportów,
- historię zmian dla płatności i magazynów.

---

# 10. Podsumowanie równości podziału

Podział pracy został przygotowany tak, aby każda osoba miała porównywalny zakres obowiązków.

Każda osoba odpowiada za:

- 6 tabel w bazie danych,
- 2 widoki SQL,
- 2 funkcje SQL,
- 2 procedury SQL,
- 2 triggery SQL,
- kilka encji JPA,
- repozytoria,
- serwisy,
- kontrolery MVC,
- kontrolery REST,
- widoki Thymeleaf,
- walidację formularzy,
- część Spring Security,
- część dokumentacji,
- część prezentacji.

Bezpieczeństwo aplikacji zostało podzielone w taki sposób, że Kamil odpowiada za konfigurację podstawową logowania, rejestracji, użytkowników i ról, natomiast pozostali członkowie zespołu odpowiadają za zabezpieczenie swoich modułów biznesowych.

Dzięki temu projekt jest podzielony równomiernie zarówno pod kątem przedmiotu **Systemy baz danych**, jak i pod kątem przedmiotu **Projektowanie aplikacji WWW w języku Java**.