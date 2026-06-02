-- Tworzenie użytkownika dla aplikacji
CREATE USER app_user WITH PASSWORD 'app_password';

-- Nadanie uprawnień do bazy (zakładając nazwę bazy smuggling_db)
GRANT CONNECT ON DATABASE smuggling_db TO app_user;

-- Uprawnienia w schemacie public
GRANT USAGE, CREATE ON SCHEMA public TO app_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO app_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO app_user;

-- Automatyczne nadawanie uprawnień do nowych tabel i sekwencji utworzonych w przyszłości
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO app_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO app_user;
