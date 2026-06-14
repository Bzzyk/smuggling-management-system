# System zarządzania przemytem papierosów

Projekt edukacyjny realizowany na potrzeby przedmiotów:

- **Systemy baz danych**
- **Projektowanie aplikacji WWW w języku Java**

## Opis projektu

System zarządzania przemytem papierosów to fikcyjna aplikacja webowa służąca do
obsługi wyimaginowanego scenariusza organizacji przemytu papierosów.

Projekt nie jest przeznaczony do rzeczywistego wykorzystania. Jego celem jest
zaprezentowanie projektowania relacyjnej bazy danych oraz aplikacji webowej w
Javie.

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

## Technologie

Technologie użyte w projekcie:

- Java,
- Spring Boot,
- Spring Security,
- Spring Data JPA / Hibernate,
- Thymeleaf,
- PostgreSQL,
- REST API,
- Swagger UI / OpenAPI,
- Bootstrap,
- Git / GitHub.

## Uruchomienie lokalne

Backend uruchamia się z katalogu `backend`.

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

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Przykładowe requesty HTTP znajdują się w `backend/requests.http`.

## Baza danych

Skrypty SQL znajdują się w katalogu `database`. Kolejność uruchamiania plików
`01-08.sql` jest opisana w `database/README.md`.

Aplikacja korzysta z JPA/Hibernate do komunikacji z bazą. Schemat bazy zawiera
tabele, relacje, ograniczenia, indeksy oraz logikę po stronie PostgreSQL:

- widoki,
- funkcje,
- procedury,
- triggery.

Skrypty do testów wydajnościowych znajdują się w `database/performance`.

## Role użytkowników

W systemie przewidziano następujące role:

| Rola | Opis |
|---|---|
| `ADMIN` | Zarządza użytkownikami, rolami, audytem i dokumentacją API. |
| `BOSS` | Tworzy zlecenia, zarządza transportami i widzi raporty. |
| `SMUGGLER` | Widzi aktywnie przypisane do siebie transporty oraz wybrane dane operacyjne. |
| `ACCOUNTANT` | Zarządza płatnościami i raportami finansowymi. |

## Główne moduły systemu

### 1. Użytkownicy i role

Moduł odpowiedzialny za logowanie, zarządzanie użytkownikami oraz przypisywanie
ról.

### 2. Zlecenia

Moduł odpowiedzialny za tworzenie i obsługę zleceń.

### 3. Transporty

Moduł odpowiedzialny za planowanie transportów, zmianę ich statusów oraz
przypisywanie przemytników.

### 4. Trasy i pojazdy

Moduł odpowiedzialny za zarządzanie trasami oraz pojazdami wykorzystywanymi w
transportach.

### 5. Ładunki

Moduł odpowiedzialny za zarządzanie ładunkami.

### 6. Magazyny

Moduł odpowiedzialny za obsługę magazynów oraz stanów magazynowych.

### 7. Płatności

Moduł odpowiedzialny za rejestrowanie kosztów, przychodów i rozliczeń.

### 8. Raporty

Moduł odpowiedzialny za generowanie raportu zysków i kosztów, raportu
magazynowego oraz raportu ryzyka transportów.

### 9. Historia zmian

Moduł odpowiedzialny za zapisywanie informacji o wybranych zmianach wykonanych w
systemie.

## Dokumentacja

- opis projektu: `docs/01-opis-projektu.md`,
- wymagania Java: `docs/02-wymagania-java.md`,
- wymagania bazy danych: `docs/03-wymagania-baza-danych.md`,
- podział pracy: `docs/04-podzial-pracy.md`,
- analiza biznesowa: `docs/05-analiza-biznesowa.md`,
- model danych i ERD: `docs/06-model-danych.md`,
- bezpieczeństwo: `docs/07-bezpieczenstwo.md`,
- REST API: `docs/08-api-rest.md`,
- testy wydajnościowe: `docs/09-testy-wydajnosciowe.md`.

Bezpośredni plik diagramu:

```text
docs/diagram.svg
```

## Autorzy

- Kamil Osakowicz
- Filip Kamiński
- Karol Daniło
