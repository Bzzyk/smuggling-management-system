# Backend

Backend aplikacji `smuggling-management-system` jest napisany w Java/Spring Boot.
Zawiera warstwę MVC z Thymeleaf, REST API, Spring Security, Spring Data JPA oraz
konfigurację Swagger/OpenAPI.

## Wymagania

- Java 25,
- Maven Wrapper z repozytorium (`mvnw.cmd`),
- PostgreSQL,
- dostęp do bazy skonfigurowany w `src/main/resources/application.properties`
  albo przez zmienne środowiskowe.

Nie należy wpisywać haseł i sekretów do dokumentacji ani commitować prywatnych
danych dostępowych.

## Uruchomienie na Windows

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Adres aplikacji lokalnej:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Testowanie API

Podstawowe sposoby sprawdzenia endpointów:

- Swagger UI,
- plik `backend/requests.http`.

Endpointy chronione wymagają zalogowania użytkownika z odpowiednią rolą.

## Baza danych

Konfiguracja połączenia z bazą powinna być ustawiona w
`src/main/resources/application.properties` albo przez zmienne środowiskowe.

Skrypty SQL do odtworzenia schematu są w katalogu `../database`. Kolejność
uruchomienia plików opisuje `../database/README.md`.
