# REST API

REST API działa w ramach aplikacji Spring Boot i zwraca dane w formacie JSON. Dostęp do endpointów wymaga zalogowania, a szczegółowe uprawnienia są kontrolowane przez `@PreAuthorize`.

Ta dokumentacja obejmuje endpointy z zakresu: użytkownicy, zlecenia, transporty, trasy, pojazdy, audyt i logi uwierzytelniania.

## API użytkowników

Bazowy adres:

```text
/api/users
```

| Metoda | Endpoint | Rola | Opis |
|---|---|---|---|
| `GET` | `/api/users` | `ADMIN` | Lista użytkowników |
| `GET` | `/api/users/{id}` | `ADMIN` | Szczegóły użytkownika |
| `POST` | `/api/users` | `ADMIN` | Utworzenie użytkownika |
| `PUT` | `/api/users/{id}` | `ADMIN` | Edycja danych i ról użytkownika |
| `POST` | `/api/users/{id}/toggle-ban` | `ADMIN` | Zmiana statusu aktywności konta |
| `POST` | `/api/users/{id}/reset-password` | `ADMIN` | Reset hasła użytkownika |

Używane DTO:

- `UserDto`,
- `UserCreateRequest`,
- `UserUpdateRequest`,
- `PasswordResetRequest`.

## API zleceń

Bazowy adres:

```text
/api/orders
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/orders` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista zleceń |
| `GET` | `/api/orders/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły zlecenia |
| `POST` | `/api/orders` | `ADMIN`, `BOSS` | Utworzenie zlecenia |
| `PUT` | `/api/orders/{id}` | `ADMIN`, `BOSS` | Edycja zlecenia |
| `DELETE` | `/api/orders/{id}` | `ADMIN`, `BOSS` | Usunięcie zlecenia |
| `GET` | `/api/orders/statuses` | `ADMIN`, `BOSS` | Lista statusów zleceń |

Używane DTO:

- `OrderDto`,
- `OrderFormDto`,
- `OrderStatusDto`.

Podstawowe odpowiedzi:

- `200 OK` dla poprawnego odczytu i edycji,
- `201 Created` po utworzeniu zlecenia,
- `204 No Content` po usunięciu zlecenia,
- `400 Bad Request` dla niepoprawnych danych,
- `404 Not Found` jeżeli rekord nie istnieje.

## API tras

Bazowy adres:

```text
/api/routes
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/routes?page=0&size=10&sort=id&dir=asc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista tras |
| `GET` | `/api/routes/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły trasy |
| `POST` | `/api/routes` | `ADMIN`, `BOSS` | Utworzenie trasy |
| `PUT` | `/api/routes/{id}` | `ADMIN`, `BOSS` | Edycja trasy |
| `DELETE` | `/api/routes/{id}` | `ADMIN`, `BOSS` | Dezaktywacja trasy |
| `GET` | `/api/routes/difficulty-levels` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista poziomów trudności |

Używane DTO:

- `RouteDto`,
- `RouteFormDto`,
- `RouteDifficultyLevelDto`.

`DELETE` nie usuwa rekordu fizycznie z bazy, tylko ustawia `active = false`.

## API pojazdów

Bazowy adres:

```text
/api/vehicles
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/vehicles?page=0&size=10&sort=id&dir=asc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista pojazdów |
| `GET` | `/api/vehicles/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły pojazdu |
| `POST` | `/api/vehicles` | `ADMIN`, `BOSS` | Utworzenie pojazdu |
| `PUT` | `/api/vehicles/{id}` | `ADMIN`, `BOSS` | Edycja pojazdu |
| `DELETE` | `/api/vehicles/{id}` | `ADMIN`, `BOSS` | Dezaktywacja pojazdu |
| `GET` | `/api/vehicles/types` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista typów pojazdów |

Używane DTO:

- `VehicleDto`,
- `VehicleFormDto`,
- `AvailableVehicleDto`.

`DELETE` nie usuwa rekordu fizycznie z bazy, tylko ustawia `active = false`.

## API transportów

Bazowy adres:

```text
/api/transports
```

| Metoda | Endpoint | Role | Opis |
|---|---|---|---|
| `GET` | `/api/transports?page=0&size=10&sort=transportDate&dir=asc` | `ADMIN`, `BOSS`, `SMUGGLER` | Stronicowana lista transportów widocznych dla użytkownika |
| `GET` | `/api/transports/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` | Szczegóły transportu |
| `POST` | `/api/transports` | `ADMIN`, `BOSS` | Utworzenie transportu |
| `PUT` | `/api/transports/{id}` | `ADMIN`, `BOSS` | Edycja transportu |
| `DELETE` | `/api/transports/{id}` | `ADMIN`, `BOSS` | Anulowanie transportu |
| `PUT` | `/api/transports/{id}/status` | `ADMIN`, `BOSS` | Zmiana statusu transportu |
| `PUT` | `/api/transports/{id}/vehicle/{vehicleId}` | `ADMIN`, `BOSS` | Przypisanie pojazdu |
| `PUT` | `/api/transports/{id}/cargo/{cargoId}` | `ADMIN`, `BOSS` | Przypisanie ładunku |
| `DELETE` | `/api/transports/{id}/cargo/{cargoId}` | `ADMIN`, `BOSS` | Odpięcie ładunku |
| `POST` | `/api/transports/{id}/smugglers` | `ADMIN`, `BOSS` | Przypisanie przemytnika |
| `DELETE` | `/api/transports/{id}/smugglers/{assignmentId}` | `ADMIN`, `BOSS` | Usunięcie przypisania przemytnika |
| `GET` | `/api/transports/{id}/smugglers` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista przypisanych przemytników |
| `GET` | `/api/transports/{id}/estimate` | `ADMIN`, `BOSS`, `SMUGGLER` | Prognozowany zysk transportu |
| `GET` | `/api/transports/statuses` | `ADMIN`, `BOSS`, `SMUGGLER` | Lista statusów transportów |

Lista transportów obsługuje dodatkowe parametry:

- `status` - nazwa statusu, np. `ZAPLANOWANY`,
- `dateFilter` - `past`, `future` albo puste,
- `page`, `size`, `sort`, `dir` - paginacja i sortowanie.

Używane DTO:

- `TransportDto`,
- `TransportFormDto`,
- `ChangeTransportStatusRequest`,
- `AssignSmugglerRequest`,
- `SmugglerAssignmentDto`,
- `TransportStatusDto`.

`DELETE /api/transports/{id}` nie usuwa transportu fizycznie z bazy, tylko zmienia jego status na `ANULOWANY`.

## API audytu

Bazowy adres:

```text
/api/audit-logs
```

| Metoda | Endpoint | Rola | Opis |
|---|---|---|---|
| `GET` | `/api/audit-logs?page=0&size=50` | `ADMIN` | Stronicowana lista wpisów audytu |

Używane DTO:

- `AuditLogDto`.

Parametr `size` jest ograniczany w kontrolerze do maksymalnie 500 rekordów.

## API logów uwierzytelniania

Bazowy adres:

```text
/api/auth-logs
```

| Metoda | Endpoint | Rola | Opis |
|---|---|---|---|
| `GET` | `/api/auth-logs?page=0&size=50` | `ADMIN` | Stronicowana lista logów logowania |

Używane DTO:

- `AuthLogDto`.

Parametr `size` jest ograniczany w kontrolerze do maksymalnie 500 rekordów.
