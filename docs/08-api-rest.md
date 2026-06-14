# REST API

REST API działa w ramach aplikacji Spring Boot i zwraca dane w formacie JSON.
Dostęp do endpointów wymaga zalogowania, a szczegółowe uprawnienia są
kontrolowane przez `@PreAuthorize`.

Dokumentacja OpenAPI jest dostępna po uruchomieniu aplikacji:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Przykładowe requesty do ręcznego testowania są w `backend/requests.http`.

## Uwaga o zakresie REST

W aktualnym kodzie REST API obejmuje:

- użytkowników,
- zlecenia,
- transporty,
- trasy,
- pojazdy,
- logi audytu,
- logi uwierzytelniania.

Moduły `cargo`, `warehouses`, `payments` i `reports` są w tej wersji obsługiwane
przez kontrolery MVC i widoki Thymeleaf. Nie mają osobnych kontrolerów REST.

## Paginacja

Endpointy listujące zwracające `Page` mają rekordy w polu `content`. Oprócz tego
odpowiedź zawiera metadane strony, np. numer strony, rozmiar, liczbę elementów i
liczbę stron.

## API użytkowników

Klasa: `UserRestController`

Bazowy adres:

```text
/api/users
```

| Metoda | Endpoint | Rola | Opis |
|---|---|---|---|
| `GET` | `/api/users?page=0&size=20&sort=id&dir=asc` | `ADMIN` | Stronicowana lista użytkowników. Dane są w polu `content`. |
| `GET` | `/api/users/{id}` | `ADMIN` | Szczegóły użytkownika. |
| `POST` | `/api/users` | `ADMIN` | Utworzenie użytkownika. |
| `PUT` | `/api/users/{id}` | `ADMIN` | Edycja danych i ról użytkownika. |
| `POST` | `/api/users/{id}/toggle-ban` | `ADMIN` | Zmiana statusu aktywności konta. |
| `POST` | `/api/users/{id}/reset-password` | `ADMIN` | Reset hasła użytkownika. |

Używane DTO:

- `UserDto`,
- `UserCreateRequest`,
- `UserUpdateRequest`,
- `PasswordResetRequest`.

## API zleceń

Klasa: `OrderRestController`

Bazowy adres:

```text
/api/orders
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/orders?page=0&size=20&sort=id&dir=asc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista zleceń. Dane są w polu `content`. |
| `GET` | `/api/orders/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły zlecenia. |
| `POST` | `/api/orders` | `ADMIN`, `BOSS` | Utworzenie zlecenia. |
| `PUT` | `/api/orders/{id}` | `ADMIN`, `BOSS` | Edycja zlecenia. |
| `DELETE` | `/api/orders/{id}` | `ADMIN`, `BOSS` | Usunięcie zlecenia. |
| `GET` | `/api/orders/statuses` | `ADMIN`, `BOSS` | Lista statusów zleceń. |

Używane DTO:

- `OrderDto`,
- `OrderFormDto`,
- `OrderStatusDto`.

## API tras

Klasa: `RouteRestController`

Bazowy adres:

```text
/api/routes
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/routes?page=0&size=10&sort=name&dir=asc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista aktywnych tras. Dane są w polu `content`. |
| `GET` | `/api/routes/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły trasy. |
| `POST` | `/api/routes` | `ADMIN`, `BOSS` | Utworzenie trasy. |
| `PUT` | `/api/routes/{id}` | `ADMIN`, `BOSS` | Edycja trasy. |
| `DELETE` | `/api/routes/{id}` | `ADMIN`, `BOSS` | Dezaktywacja trasy. |
| `GET` | `/api/routes/difficulty-levels` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista poziomów trudności tras. |

`DELETE` nie usuwa rekordu fizycznie z bazy, tylko ustawia trasę jako nieaktywną.

Używane DTO:

- `RouteDto`,
- `RouteFormDto`,
- `RouteDifficultyLevelDto`.

## API pojazdów

Klasa: `VehicleRestController`

Bazowy adres:

```text
/api/vehicles
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/vehicles?page=0&size=10&sort=registrationNumber&dir=asc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista aktywnych pojazdów. Dane są w polu `content`. |
| `GET` | `/api/vehicles/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły pojazdu. |
| `POST` | `/api/vehicles` | `ADMIN`, `BOSS` | Utworzenie pojazdu. |
| `PUT` | `/api/vehicles/{id}` | `ADMIN`, `BOSS` | Edycja pojazdu. |
| `DELETE` | `/api/vehicles/{id}` | `ADMIN`, `BOSS` | Dezaktywacja pojazdu. |
| `GET` | `/api/vehicles/types` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista typów pojazdów. |

`DELETE` nie usuwa rekordu fizycznie z bazy, tylko ustawia pojazd jako
nieaktywny.

Używane DTO:

- `VehicleDto`,
- `VehicleFormDto`.

## API transportów

Klasa: `TransportRestController`

Bazowy adres:

```text
/api/transports
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/transports?page=0&size=10&sort=transportDate&dir=desc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista transportów widocznych dla użytkownika. Dane są w polu `content`. |
| `GET` | `/api/transports/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły transportu widocznego dla użytkownika. |
| `POST` | `/api/transports` | `ADMIN`, `BOSS` | Utworzenie transportu. |
| `PUT` | `/api/transports/{id}` | `ADMIN`, `BOSS` | Edycja transportu. |
| `DELETE` | `/api/transports/{id}` | `ADMIN`, `BOSS` | Anulowanie transportu. |
| `PUT` | `/api/transports/{id}/status` | `ADMIN`, `BOSS` | Zmiana statusu transportu. |
| `PUT` | `/api/transports/{id}/vehicle/{vehicleId}` | `ADMIN`, `BOSS` | Przypisanie pojazdu. |
| `PUT` | `/api/transports/{id}/cargo/{cargoId}` | `ADMIN`, `BOSS` | Przypisanie ładunku. |
| `DELETE` | `/api/transports/{id}/cargo/{cargoId}` | `ADMIN`, `BOSS` | Odpięcie ładunku. |
| `POST` | `/api/transports/{id}/smugglers` | `ADMIN`, `BOSS` | Przypisanie przemytnika. |
| `DELETE` | `/api/transports/{id}/smugglers/{assignmentId}` | `ADMIN`, `BOSS` | Usunięcie przypisania przemytnika. |
| `GET` | `/api/transports/{id}/smugglers` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista przypisanych przemytników. |
| `GET` | `/api/transports/{id}/estimate` | `ADMIN`, `BOSS`, `SMUGGLER` | Prognozowany koszt, ryzyko i zysk transportu. |
| `GET` | `/api/transports/statuses` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista statusów transportów. |

Lista transportów obsługuje dodatkowe parametry:

- `status` - nazwa statusu, np. `ZAPLANOWANY`,
- `dateFilter` - `ALL`, `TODAY`, `FUTURE`, `NEXT_7` albo `NEXT_30`,
- `page`, `size`, `sort`, `dir` - paginacja i sortowanie.

`DELETE /api/transports/{id}` nie usuwa transportu fizycznie z bazy, tylko
zmienia jego status na `ANULOWANY`.

Używane DTO:

- `TransportDto`,
- `TransportFormDto`,
- `ChangeTransportStatusRequest`,
- `AssignSmugglerRequest`,
- `SmugglerAssignmentDto`,
- `TransportStatusDto`.

## API audytu

Klasa: `AuditLogRestController`

Bazowy adres:

```text
/api/audit-logs
```

| Metoda | Endpoint | Rola | Opis |
|---|---|---|---|
| `GET` | `/api/audit-logs?page=0&size=50` | `ADMIN` | Stronicowana lista wpisów audytu. Dane są w polu `content`. |

Parametr `size` jest ograniczany w kontrolerze do maksymalnie 500 rekordów.

Używane DTO:

- `AuditLogDto`.

## API logów uwierzytelniania

Klasa: `AuthLogRestController`

Bazowy adres:

```text
/api/auth-logs
```

| Metoda | Endpoint | Rola | Opis |
|---|---|---|---|
| `GET` | `/api/auth-logs?page=0&size=50` | `ADMIN` | Stronicowana lista logów logowania. Dane są w polu `content`. |

Parametr `size` jest ograniczany w kontrolerze do maksymalnie 500 rekordów.

Używane DTO:

- `AuthLogDto`.

## Typowe odpowiedzi

- `200 OK` - poprawny odczyt lub edycja,
- `201 Created` - utworzenie zasobu,
- `204 No Content` - usunięcie albo dezaktywacja,
- `400 Bad Request` - niepoprawne dane,
- `404 Not Found` - rekord nie istnieje albo nie jest widoczny dla użytkownika.
