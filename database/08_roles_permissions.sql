-- Tworzenie użytkownika dla aplikacji
CREATE USER ApplicationIdentity WITH PASSWORD 'gGaaFEeAZg9nNfQk';

-- Nadanie uprawnień do bazy (zakładając nazwę bazy smuggling_db)
GRANT CONNECT ON DATABASE projekt TO ApplicationIdentity;

-- Uprawnienia w schemacie public
GRANT USAGE, CREATE ON SCHEMA public TO ApplicationIdentity;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ApplicationIdentity;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ApplicationIdentity;

-- Automatyczne nadawanie uprawnień do nowych tabel i sekwencji utworzonych w przyszłości
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO ApplicationIdentity;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO ApplicationIdentity;
