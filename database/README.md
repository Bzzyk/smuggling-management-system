# Database setup

This directory contains the SQL scripts required to recreate the PostgreSQL
database structure used by the application.

Run the files in this order:

| Order | File | Purpose |
|---|---|---|
| 1 | `01_schema.sql` | Creates tables, primary keys, foreign keys, `NOT NULL`, `UNIQUE`, `CHECK` constraints and base indexes. |
| 2 | `02_dictionaries.sql` | Inserts dictionary values: roles, order statuses, transport statuses, route difficulty levels, cargo types and payment statuses. |
| 3 | `03_sample_data.sql` | Reserved for sample data. Currently empty. |
| 4 | `04_views.sql` | Creates reporting and helper views, for example `v_available_vehicles`, `v_available_cargo`, `v_active_transports`, `v_profit_report`. |
| 5 | `05_functions.sql` | Creates SQL/PLpgSQL functions for counting orders, checking roles, risk scoring and profit calculations. |
| 6 | `06_procedures.sql` | Creates procedures for creating orders, changing statuses, assigning smugglers, vehicles and cargo, and registering payments. |
| 7 | `07_triggers.sql` | Creates trigger functions and triggers for audit, validation, warehouse capacity and profit refresh logic. |
| 8 | `08_roles_permissions.sql` | Creates database roles/permissions used for the project database. |

## Requirements

- PostgreSQL 14 or newer.
- A database user with permission to create tables, indexes, views, functions,
  procedures and triggers.
- The application validates the existing schema with Hibernate:
  `spring.jpa.hibernate.ddl-auto=validate`.

## Recreating the database

Example using `psql`:

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

For an existing database, run the files from `01` to `08` in the same order.
Most objects use `IF NOT EXISTS` or `CREATE OR REPLACE`, but data scripts should
still be executed intentionally on the target database.

## Performance scripts

Additional scripts for database performance checks are stored in
`database/performance`:

- `01_generate_transports.sql` generates larger test datasets for the
  `transports` table.
- `02_explain_transports.sql` runs `EXPLAIN ANALYZE` queries.
- `03_indexes.sql` contains proposed optimization indexes.
