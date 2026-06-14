# Baza danych

Ten katalog zawiera skrypty SQL potrzebne do odtworzenia struktury bazy
PostgreSQL używanej przez aplikację.

## Kolejność uruchamiania

Pliki należy uruchamiać w tej kolejności:

| Kolejność | Plik | Opis |
|---:|---|---|
| 1 | `01_schema.sql` | Tworzy tabele, klucze główne, klucze obce, ograniczenia `NOT NULL`, `UNIQUE`, `CHECK` oraz podstawowe indeksy. |
| 2 | `02_dictionaries.sql` | Wstawia wartości słownikowe: role, statusy zleceń, statusy transportów, poziomy trudności tras, typy ładunków i statusy płatności. |
| 3 | `03_sample_data.sql` | Miejsce na ręczne dane testowe SQL. Aktualnie zawiera tylko komentarz. Nie jest wymagany do podstawowego uruchomienia schematu. |
| 4 | `04_views.sql` | Tworzy widoki pomocnicze i raportowe, np. `v_available_vehicles`, `v_available_cargo`, `v_active_transports`, `v_profit_report`. |
| 5 | `05_functions.sql` | Tworzy funkcje SQL/PLpgSQL do zliczania, sprawdzania ról, liczenia ryzyka i zysku. |
| 6 | `06_procedures.sql` | Tworzy procedury do obsługi zleceń, statusów, przypisań, magazynów i płatności. |
| 7 | `07_triggers.sql` | Tworzy funkcje triggerów i triggery do audytu, walidacji, pojemności magazynu i odświeżania zysku. |
| 8 | `08_roles_permissions.sql` | Definiuje role i uprawnienia bazodanowe używane w projekcie. |

## Wymagania

- PostgreSQL 14 lub nowszy,
- użytkownik bazy z prawami do tworzenia tabel, indeksów, widoków, funkcji,
  procedur i triggerów,
- aplikacja po stronie Hibernate używa trybu walidacji schematu:
  `spring.jpa.hibernate.ddl-auto=validate`.

## Odtworzenie bazy

Przykład dla `psql`:

```bash
createdb smuggling
psql -d smuggling -f database/01_schema.sql
psql -d smuggling -f database/02_dictionaries.sql
psql -d smuggling -f database/03_sample_data.sql
psql -d smuggling -f database/04_views.sql
psql -d smuggling -f database/05_functions.sql
psql -d smuggling -f database/06_procedures.sql
psql -d smuggling -f database/07_triggers.sql
psql -d smuggling -f database/08_roles_permissions.sql
```

Na istniejącej bazie pliki też należy uruchamiać od `01` do `08`. Część obiektów
używa `IF NOT EXISTS` albo `CREATE OR REPLACE`, ale uruchomienie skryptów na
współdzielonej bazie powinno być wykonane świadomie.

## Dane testowe

Plik `03_sample_data.sql` nie zawiera dużych insertów. Dane przykładowe mogą być
tworzone przez aplikację, np. przez seedery w pakiecie
`pl.edu.pb.smuggling.seed`.

## Testy wydajnościowe

Skrypty do lokalnych testów wydajnościowych są w katalogu
`database/performance`:

- `01_generate_transports.sql` - generuje większy zestaw danych dla tabeli
  `transports`,
- `02_explain_transports.sql` - uruchamia zapytania `EXPLAIN ANALYZE`,
- `03_indexes.sql` - dodaje proponowane indeksy optymalizacyjne.

Te skrypty są przeznaczone do lokalnej lub testowej bazy danych.
