# Wymagania Java

Projekt spełnia podstawowe wymagania części Java. Aplikacja jest napisana jako
projekt Spring Boot i zawiera warstwę webową, bezpieczeństwo, ORM, REST API oraz
widoki HTML.

## Spring Boot

Backend znajduje się w katalogu `backend`. Główna klasa startowa to
`BackendApplication`. Projekt jest budowany przez Maven Wrapper.

## MVC i Thymeleaf

Aplikacja ma klasyczne kontrolery MVC oraz szablony Thymeleaf w katalogu
`backend/src/main/resources/templates`. Widoki obsługują m.in.:

- stronę główną,
- logowanie,
- użytkowników i role,
- zlecenia,
- transporty,
- trasy i pojazdy,
- ładunki i magazyny,
- płatności,
- raporty,
- audyt i logi logowania.

## Formularze i walidacja

Dane z formularzy są przenoszone przez klasy DTO, np. `OrderFormDto`,
`TransportFormDto`, `VehicleFormDto`, `CargoFormDto`, `PaymentFormDto`.
W projekcie używane są adnotacje walidacyjne z `jakarta.validation`, np.
`@Valid` w kontrolerach.

## Spring Security i role

Bezpieczeństwo jest skonfigurowane w `SecurityConfig`. Użytkownicy są ładowani z
bazy przez `CustomUserDetailsService`. Dostęp do widoków i endpointów jest
ograniczany przez `@PreAuthorize`.

Główne role:

- `ADMIN`,
- `BOSS`,
- `SMUGGLER`,
- `ACCOUNTANT`.

Hasła są kodowane przez `BCryptPasswordEncoder`.

## JPA/Hibernate ORM

Aplikacja komunikuje się z bazą przez Spring Data JPA i Hibernate. Encje znajdują
się w pakietach `model`, a repozytoria w pakietach `repository`. Repozytoria
dziedziczą po `JpaRepository`.

Przykładowe encje:

- `User`,
- `Role`,
- `SmugglingOrder`,
- `Transport`,
- `Route`,
- `Vehicle`,
- `Cargo`,
- `Warehouse`,
- `Payment`.

## REST API

Projekt udostępnia REST API dla faktycznie zaimplementowanych kontrolerów:

- użytkownicy,
- zlecenia,
- transporty,
- trasy,
- pojazdy,
- logi audytu,
- logi uwierzytelniania.

Dokładny opis endpointów jest w `docs/08-api-rest.md`.

## Swagger/OpenAPI

Dokumentacja REST API jest generowana przez Springdoc OpenAPI.

Po uruchomieniu aplikacji:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Podział na warstwy

Kod jest podzielony na typowe warstwy:

- `controller` - obsługa MVC i REST,
- `service` - logika aplikacyjna,
- `repository` - dostęp do danych przez Spring Data JPA,
- `model` - encje JPA,
- `dto` - obiekty formularzy i odpowiedzi API,
- `config` - konfiguracja Spring Security, MVC i OpenAPI.

## Obsługa błędów i autoryzacji

Dostęp do chronionych zasobów jest kontrolowany przez Spring Security. Dla braku
dostępu przygotowano prosty widok `templates/error/403.html`. Kontrolery REST
zwracają typowe statusy HTTP, np. `200`, `201`, `204`, `400`, `404`.

## Uruchomienie i testowanie

Na Windows:

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Aplikacja lokalna:

```text
http://localhost:8080
```

Przykładowe requesty HTTP są w pliku `backend/requests.http`.
