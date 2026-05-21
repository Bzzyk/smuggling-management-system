# System zarządzania przemytem papierosów

Projekt edukacyjny realizowany na potrzeby przedmiotów:

- **Systemy baz danych**
- **Projektowanie aplikacji WWW w języku Java**

## Opis projektu

System zarządzania przemytem papierosów to fikcyjna aplikacja webowa służąca do obsługi prześmiewczego scenariusza organizacji przemytu papierosów.

Projekt nie jest przeznaczony do rzeczywistego wykorzystania. Jego celem jest zaprezentowanie umiejętności projektowania relacyjnej bazy danych oraz aplikacji webowej w Javie.

System umożliwia zarządzanie:

- użytkownikami i rolami,
- zleceniami,
- transportami,
- trasami,
- pojazdami,
- przemytnikami przypisanymi do transportów,
- ładunkami,
- magazynami,
- płatnościami,
- raportami,
- historią zmian w systemie.

---

## Technologie

Planowane technologie użyte w projekcie:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- Thymeleaf
- PostgreSQL
- REST API
- Swagger UI
- Bootstrap
- Git / GitHub

---

## Role użytkowników

W systemie przewidziano następujące role:

| Rola | Opis |
|---|---|
| `ADMIN` | Zarządza użytkownikami i rolami |
| `BOSS` | Tworzy zlecenia, zarządza transportami i widzi raporty |
| `SMUGGLER` | Widzi przypisane transporty i może aktualizować ich status |
| `ACCOUNTANT` | Zarządza płatnościami i raportami finansowymi |

---

## Główne moduły systemu

### 1. Użytkownicy i role

Moduł odpowiedzialny za rejestrację, logowanie, zarządzanie użytkownikami oraz przypisywanie ról.

### 2. Zlecenia

Moduł odpowiedzialny za tworzenie i obsługę zleceń przemytu.

### 3. Transporty

Moduł odpowiedzialny za planowanie transportów, zmianę ich statusów oraz przypisywanie przemytników.

### 4. Trasy i pojazdy

Moduł odpowiedzialny za zarządzanie trasami oraz pojazdami wykorzystywanymi w transportach.

### 5. Ładunki

Moduł odpowiedzialny za zarządzanie ładunkami papierosów.

### 6. Magazyny

Moduł odpowiedzialny za obsługę magazynów oraz stanów magazynowych.

### 7. Płatności

Moduł odpowiedzialny za rejestrowanie kosztów, przychodów i rozliczeń.

### 8. Raporty

Moduł odpowiedzialny za generowanie raportów, np. raportu zysków i strat, raportu magazynowego oraz raportu aktywnych transportów.

### 9. Historia zmian

Moduł odpowiedzialny za zapisywanie informacji o wybranych zmianach wykonanych w systemie.

---

## Podział pracy

Projekt realizowany jest przez trzy osoby.

| Osoba | Główny zakres |
|---|---|
| Kamil Osakowicz | Użytkownicy, role, zlecenia, podstawowa konfiguracja bezpieczeństwa, historia zmian |
| Osoba 2 | Transporty, trasy, pojazdy, przypisanie przemytników, bezpieczeństwo modułu transportowego |
| Osoba 3 | Ładunki, magazyny, płatności, raporty, bezpieczeństwo modułu magazynowo-finansowego |

Szczegółowy podział pracy znajduje się w pliku:

```text
docs/04-podzial-pracy.md