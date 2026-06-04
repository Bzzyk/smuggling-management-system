# REST API

REST API działa w ramach aplikacji Spring Boot i zwraca dane w formacie JSON. Dostęp do endpointów wymaga zalogowania, a szczegółowe uprawnienia są kontrolowane przez `@PreAuthorize`.

Ta dokumentacja obejmuje tylko endpointy z zakresu: użytkownicy, zlecenia, audyt i logi uwierzytelniania.

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
