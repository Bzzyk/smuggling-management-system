# Testy wydajnościowe tabeli `transports`

## Cel

Celem testów jest sprawdzenie, jak tabela `transports` zachowuje się przy
większej liczbie rekordów oraz czy indeksy wspierają najczęstsze zapytania:
filtrowanie po statusie, dacie transportu i zleceniu.

## Scenariusze danych

Skrypt `database/performance/01_generate_transports.sql` generuje dane testowe z
użyciem `generate_series`. Domyślnie tworzy 10000 transportów.

Aby wykonać testy dla większych wolumenów, należy zmienić wartość w CTE
`params`:

- 10000 rekordów - szybki test lokalny,
- 100000 rekordów - test średni,
- 1000000 rekordów - test duży, uruchamiany tylko na mocniejszej bazie testowej.

Przykład:

```sql
WITH params AS (
    SELECT 100000 AS row_count
)
```

Generator jest przeznaczony do lokalnej lub testowej bazy danych. Nie należy
uruchamiać go na bazie produkcyjnej ani na współdzielonej bazie hostowanej bez
zgody zespołu.

## Kolejność testu

1. Odtworzyć bazę skryptami `database/01-08.sql`.
2. Uruchomić generator danych:

```bash
psql -d smuggling -f database/performance/01_generate_transports.sql
```

3. Wykonać pomiary bazowe:

```bash
psql -d smuggling -f database/performance/02_explain_transports.sql
```

4. Dodać indeksy optymalizacyjne:

```bash
psql -d smuggling -f database/performance/03_indexes.sql
```

5. Ponownie wykonać `02_explain_transports.sql` i porównać wyniki.

## Zapytania `EXPLAIN ANALYZE`

Skrypt `02_explain_transports.sql` sprawdza:

- listę transportów filtrowanych po `status_id`,
- zakres dat po `transport_date`,
- transporty dla konkretnego `order_id`,
- połączenie `transports` z `orders` i `transport_statuses`,
- wariant podobny do listy REST: status + zakres dat + sortowanie.

## Proponowane indeksy

Skrypt `03_indexes.sql` dodaje:

```sql
CREATE INDEX IF NOT EXISTS idx_transports_status_date
ON transports(status_id, transport_date);

CREATE INDEX IF NOT EXISTS idx_transports_order_status
ON transports(order_id, status_id);

CREATE INDEX IF NOT EXISTS idx_transports_date_id
ON transports(transport_date, id);

CREATE INDEX IF NOT EXISTS idx_smuggler_assignments_smuggler_active
ON smuggler_assignments(smuggler_id, active);
```

## Oczekiwane wnioski

Po dodaniu indeksów zapytania filtrujące po `status_id`, `transport_date` i
`order_id` powinny korzystać z `Index Scan` albo `Bitmap Index Scan` zamiast
pełnego `Seq Scan` dla dużych zbiorów. Największa różnica powinna być widoczna
dla 100000 i 1000000 rekordów.

W raporcie z testu należy zapisać:

| Liczba rekordów | Zapytanie | Przed indeksami | Po indeksach | Wniosek |
|---:|---|---:|---:|---|
| 10000 | status + data | | | |
| 100000 | status + data | | | |
| 1000000 | status + data | | | |
