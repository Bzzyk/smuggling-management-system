# Bezpieczeństwo

Bezpieczeństwo aplikacji jest oparte o Spring Security. Konfiguracja znajduje się w `SecurityConfig` i włącza zarówno zabezpieczenie żądań HTTP, jak i autoryzację metod przez `@PreAuthorize`.

## Logowanie i wylogowanie

Aplikacja używa logowania formularzowego:

- strona logowania: `/login`,
- domyślne przekierowanie po zalogowaniu: `/`,
- wylogowanie: `/logout`,
- przekierowanie po wylogowaniu: `/login?logout`.

Dostęp publiczny mają tylko zasoby statyczne, strona błędu oraz sama obsługa logowania. Pozostałe adresy wymagają zalogowanego użytkownika.

## Hasła

Hasła użytkowników są obsługiwane z użyciem `BCryptPasswordEncoder`. Użytkownik może zmienić własne hasło przez `/change-password`, podając obecne hasło oraz nowe hasło.

Administrator może zresetować hasło użytkownika z poziomu modułu użytkowników.

## Role

W opisywanym zakresie używane są role:

| Rola | Znaczenie w tym zakresie |
|---|---|
| `ADMIN` | Zarządza użytkownikami, rolami, audytem i ma pełny dostęp do zleceń |
| `BOSS` | Może zarządzać zleceniami |
| `SMUGGLER` | Może przeglądać zlecenia |

## Reguły dostępu

Najważniejsze reguły dostępu:

| Obszar | Dostęp |
|---|---|
| `/users/**` | `ADMIN` |
| `/roles/**` | `ADMIN` |
| `/orders` i `/orders/{id}` | `ADMIN`, `BOSS`, `SMUGGLER` |
| tworzenie i edycja zleceń w MVC | `ADMIN`, `BOSS` |
| `/audit-logs` | `ADMIN` |
| REST API użytkowników | `ADMIN` |
| REST API zleceń - odczyt | `ADMIN`, `BOSS`, `SMUGGLER` |
| REST API zleceń - zapis/usuwanie/statusy | `ADMIN`, `BOSS` |
| REST API audytu i logów logowania | `ADMIN` |

## Walidacja opisu roli

Edycja opisu roli jest dostępna tylko dla `ADMIN`. Opis jest walidowany po stronie serwera:

- opis jest opcjonalny,
- przed zapisem jest przycinany metodą `trim()`,
- maksymalna długość po przesłaniu to 255 znaków.

Jeżeli opis jest za długi, formularz edycji roli jest wyświetlany ponownie z komunikatem Bootstrap `alert-danger`.

## Audyt i logi uwierzytelniania

Moduł audytu udostępnia historię wybranych zmian w systemie. Dostęp do audytu ma tylko administrator.

Logi uwierzytelniania pozwalają analizować próby logowania. REST API dla audytu i logów uwierzytelniania również jest ograniczone do roli `ADMIN`.
