# Opis projektu

Projekt jest edukacyjną aplikacją webową napisaną w Javie. System przedstawia fikcyjny scenariusz zarządzania operacjami i służy do pokazania pracy z aplikacją Spring Boot, bazą danych, warstwą bezpieczeństwa oraz prostym REST API.

Ta część dokumentacji opisuje zakres modułów:

- bezpieczeństwo,
- użytkownicy,
- role,
- zlecenia,
- audyt i logi uwierzytelniania.

Pozostałe moduły, takie jak transporty, trasy, pojazdy, ładunki, magazyny, płatności i raporty, istnieją w projekcie, ale są opisane poza tą częścią dokumentacji.

## Technologie

W zakresie opisanych modułów użyto:

- Java 25,
- Spring Boot 4.0.6,
- Spring MVC,
- Spring Security,
- Spring Data JPA / Hibernate,
- Thymeleaf,
- Jakarta Bean Validation,
- Lombok,
- PostgreSQL jako baza uruchomieniowa,
- H2 jako baza testowa,
- Maven Wrapper.

## Moduł użytkowników i ról

Moduł użytkowników pozwala administratorowi zarządzać kontami użytkowników. Administrator może tworzyć użytkowników, edytować ich dane, przypisywać role, blokować konta i resetować hasła.

Moduł ról pozwala przeglądać role systemowe oraz edytować ich opisy. Opis roli jest walidowany po stronie serwera i może mieć maksymalnie 255 znaków.

## Moduł zleceń

Moduł zleceń pozwala przeglądać, tworzyć, edytować i usuwać zlecenia. Dostęp zależy od roli użytkownika:

- `ADMIN` i `BOSS` mogą zarządzać zleceniami,
- `SMUGGLER` może przeglądać zlecenia.

## Bezpieczeństwo i audyt

Aplikacja korzysta z logowania formularzowego Spring Security. Wszystkie główne zasoby aplikacji wymagają zalogowania, a szczegółowe uprawnienia są kontrolowane przez role i adnotacje `@PreAuthorize`.

System zapisuje informacje audytowe dla wybranych operacji oraz udostępnia logi audytu i logi uwierzytelniania dla administratora.
