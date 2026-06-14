-- Additional indexes proposed for transports performance tests.

CREATE INDEX IF NOT EXISTS idx_transports_status_date
ON transports(status_id, transport_date);

CREATE INDEX IF NOT EXISTS idx_transports_order_status
ON transports(order_id, status_id);

CREATE INDEX IF NOT EXISTS idx_transports_date_id
ON transports(transport_date, id);

CREATE INDEX IF NOT EXISTS idx_smuggler_assignments_smuggler_active
ON smuggler_assignments(smuggler_id, active);
