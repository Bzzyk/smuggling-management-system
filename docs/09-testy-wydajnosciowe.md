# Testy wydajnosciowe tabeli transports

## Cel

Celem testow jest sprawdzenie, jak tabela `transports` zachowuje sie przy
wiekszej liczbie rekordow oraz czy indeksy wspieraja najczestsze zapytania:
filtrowanie po statusie, dacie transportu i zleceniu.

## Scenariusze danych

Skrypt `database/performance/01_generate_transports.sql` generuje dane testowe z
uzyciem `generate_series`. Domyslnie tworzy 10000 transportow. Aby wykonac testy
dla wiekszych wolumenow, nalezy zmienic wartosc w CTE `params`:

- 10000 rekordow - szybki test lokalny,
- 100000 rekordow - test sredni,
- 1000000 rekordow - test duzy, uruchamiac na mocniejszej bazie.

Przyklad:

```sql
WITH params AS (
    SELECT 100000 AS row_count
)
```

## Kolejnosc testu

1. Odtworzyc baze skryptami `database/01-08.sql`.
2. Uruchomic generator:

```bash
psql -d smuggling -f database/performance/01_generate_transports.sql
```

3. Wykonac pomiary bazowe:

```bash
psql -d smuggling -f database/performance/02_explain_transports.sql
```

4. Dodac indeksy optymalizacyjne:

```bash
psql -d smuggling -f database/performance/03_indexes.sql
```

5. Ponownie wykonac `02_explain_transports.sql` i porownac wyniki.

## Zapytania EXPLAIN ANALYZE

Skrypt `02_explain_transports.sql` sprawdza:

- liste transportow filtrowanych po `status_id`,
- zakres dat po `transport_date`,
- transporty dla konkretnego `order_id`,
- polaczenie `transports` z `orders` i `transport_statuses`,
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

Po dodaniu indeksow zapytania filtrujace po `status_id`, `transport_date` i
`order_id` powinny korzystac z `Index Scan` albo `Bitmap Index Scan` zamiast
pelnego `Seq Scan` dla duzych zbiorow. Najwieksza roznica powinna byc widoczna
dla 100000 i 1000000 rekordow.

W raporcie z testu nalezy zapisac:

| Liczba rekordow | Zapytanie | Przed indeksami | Po indeksach | Wniosek |
|---:|---|---:|---:|---|
| 10000 | status + data | | | |
| 100000 | status + data | | | |
| 1000000 | status + data | | | |
