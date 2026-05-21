# Analiza biznesowa

## 1. Temat projektu

**System zarządzania przemytem papierosów**

Projekt ma charakter fikcyjny i edukacyjny. Nie jest przeznaczony do rzeczywistego wykorzystania. Celem projektu jest przygotowanie aplikacji webowej oraz relacyjnej bazy danych na potrzeby przedmiotów:

- Projektowanie aplikacji WWW w języku Java,
- Systemy baz danych.

System przedstawia fikcyjną organizację zajmującą się planowaniem i obsługą przemytu papierosów. Aplikacja umożliwia zarządzanie zleceniami, transportami, trasami, pojazdami, ładunkami, magazynami, płatnościami, raportami oraz użytkownikami systemu.

---

## 2. Cel systemu

Celem systemu jest uporządkowanie danych związanych z fikcyjnym procesem przemytu papierosów.

System ma umożliwiać:

- tworzenie i obsługę zleceń przemytu,
- planowanie transportów,
- przypisywanie przemytników do transportów,
- zarządzanie trasami,
- zarządzanie pojazdami,
- zarządzanie ładunkami,
- obsługę magazynów i stanów magazynowych,
- rejestrowanie płatności, kosztów i zysków,
- generowanie raportów,
- zarządzanie użytkownikami i rolami,
- kontrolę dostępu do wybranych modułów,
- zapisywanie historii zmian w systemie.

---

## 3. Zakres systemu

System obejmuje następujące obszary:

### 3.1. Użytkownicy i role

System umożliwia zarządzanie użytkownikami oraz ich rolami. Role określają, do których części aplikacji użytkownik ma dostęp.

Przykładowe role:

- `ADMIN` — zarządza użytkownikami i rolami,
- `BOSS` — tworzy zlecenia, zarządza transportami i widzi raporty,
- `SMUGGLER` — widzi przypisane transporty i może aktualizować ich status,
- `ACCOUNTANT` — zarządza płatnościami i raportami finansowymi.

### 3.2. Zlecenia

Zlecenie jest głównym elementem systemu. Może dotyczyć organizacji konkretnego transportu papierosów.

Zlecenie może zawierać:

- tytuł,
- opis,
- datę utworzenia,
- planowaną datę realizacji,
- status,
- osobę odpowiedzialną,
- powiązane transporty,
- powiązane płatności.

Przykładowe statusy zlecenia:

- `NOWE`,
- `W_TRAKCIE`,
- `ZREALIZOWANE`,
- `ANULOWANE`.

### 3.3. Transporty

Transport oznacza konkretną próbę przewiezienia ładunku z jednego miejsca do drugiego.

Transport może zawierać:

- datę transportu,
- miejsce startowe,
- miejsce docelowe,
- status transportu,
- przypisany pojazd,
- przypisanego przemytnika,
- powiązaną trasę,
- powiązany ładunek.

Przykładowe statusy transportu:

- `ZAPLANOWANY`,
- `W_DRODZE`,
- `DOSTARCZONY`,
- `NIEUDANY`,
- `ANULOWANY`.

### 3.4. Trasy

Trasa opisuje drogę, którą ma zostać wykonany transport.

Trasa może zawierać:

- nazwę trasy,
- punkt startowy,
- punkt końcowy,
- długość trasy,
- poziom trudności,
- poziom ryzyka,
- opis.

Przykładowe poziomy ryzyka:

- `NISKIE`,
- `SREDNIE`,
- `WYSOKIE`,
- `KRYTYCZNE`.

### 3.5. Pojazdy

Pojazdy są wykorzystywane do realizacji transportów.

Pojazd może zawierać:

- numer rejestracyjny,
- markę,
- model,
- typ pojazdu,
- pojemność ładunkową,
- status dostępności.

Przykładowe typy pojazdów:

- `SAMOCHOD_OSOBOWY`,
- `BUS`,
- `CIEZAROWKA`,
- `VAN`.

### 3.6. Ładunki

Ładunek opisuje przewożone papierosy.

Ładunek może zawierać:

- nazwę,
- typ ładunku,
- liczbę paczek,
- szacowaną wartość,
- powiązane zlecenie,
- powiązany transport,
- aktualną lokalizację.

Przykładowe typy ładunku:

- `PAPIEROSY`,
- `TYTON`,
- `MIESZANY`.

### 3.7. Magazyny

Magazyny służą do przechowywania ładunków.

Magazyn może zawierać:

- nazwę,
- lokalizację,
- maksymalną pojemność,
- aktualny stan,
- status aktywności.

System powinien pilnować, aby stan magazynu nie przekraczał maksymalnej pojemności.

### 3.8. Płatności

Płatności opisują koszty, przychody i rozliczenia związane ze zleceniami.

Płatność może zawierać:

- kwotę,
- typ płatności,
- status,
- datę płatności,
- powiązane zlecenie.

Przykładowe typy płatności:

- `KOSZT`,
- `PRZYCHOD`,
- `PROWIZJA`.

Przykładowe statusy płatności:

- `OCZEKUJACA`,
- `ZAPLACONA`,
- `ANULOWANA`.

### 3.9. Raporty

System umożliwia generowanie raportów, np.:

- raport zysków i strat,
- raport aktywnych transportów,
- raport obciążenia przemytników,
- raport stanu magazynowego,
- raport ryzyka tras.

---

## 4. Główne operacje biznesowe

System powinien obsługiwać następujące operacje:

### 4.1. Operacje na użytkownikach

- rejestracja użytkownika,
- logowanie użytkownika,
- wylogowanie użytkownika,
- wyświetlenie listy użytkowników,
- edycja danych użytkownika,
- przypisanie roli użytkownikowi,
- odebranie roli użytkownikowi.

### 4.2. Operacje na zleceniach

- dodanie nowego zlecenia,
- edycja zlecenia,
- usunięcie zlecenia,
- wyświetlenie listy zleceń,
- wyświetlenie szczegółów zlecenia,
- zmiana statusu zlecenia,
- filtrowanie zleceń po statusie,
- sortowanie zleceń po dacie lub statusie.

### 4.3. Operacje na transportach

- dodanie transportu,
- edycja transportu,
- usunięcie transportu,
- wyświetlenie listy transportów,
- wyświetlenie szczegółów transportu,
- przypisanie przemytnika do transportu,
- przypisanie pojazdu do transportu,
- zmiana statusu transportu,
- filtrowanie transportów po dacie i statusie,
- sortowanie transportów.

### 4.4. Operacje na trasach

- dodanie trasy,
- edycja trasy,
- usunięcie trasy,
- wyświetlenie listy tras,
- obliczenie lub przypisanie poziomu ryzyka trasy.

### 4.5. Operacje na pojazdach

- dodanie pojazdu,
- edycja pojazdu,
- usunięcie pojazdu,
- wyświetlenie listy pojazdów,
- zmiana statusu dostępności pojazdu.

### 4.6. Operacje na ładunkach

- dodanie ładunku,
- edycja ładunku,
- usunięcie ładunku,
- wyświetlenie listy ładunków,
- przypisanie ładunku do transportu,
- przypisanie ładunku do magazynu,
- obliczenie wartości ładunku.

### 4.7. Operacje na magazynach

- dodanie magazynu,
- edycja magazynu,
- usunięcie magazynu,
- wyświetlenie listy magazynów,
- wyświetlenie aktualnego stanu magazynowego,
- dodanie ładunku do magazynu,
- usunięcie ładunku z magazynu,
- kontrola pojemności magazynu.

### 4.8. Operacje na płatnościach

- dodanie płatności,
- edycja płatności,
- usunięcie płatności,
- wyświetlenie listy płatności,
- przypisanie płatności do zlecenia,
- zmiana statusu płatności,
- obliczenie zysku ze zlecenia.

### 4.9. Operacje raportowe

- wygenerowanie raportu zysków i strat,
- wygenerowanie raportu stanu magazynowego,
- wygenerowanie raportu aktywnych transportów,
- wygenerowanie raportu obciążenia przemytników,
- wygenerowanie raportu ryzyka tras.

---

## 5. Użytkownicy systemu

System przewiduje kilka typów użytkowników.

### 5.1. Administrator

Administrator zarządza użytkownikami i rolami.

Może:

- wyświetlać listę użytkowników,
- edytować użytkowników,
- nadawać role,
- odbierać role,
- przeglądać wszystkie moduły systemu.

### 5.2. Boss

Boss zarządza główną częścią operacyjną systemu.

Może:

- tworzyć zlecenia,
- edytować zlecenia,
- planować transporty,
- przypisywać przemytników,
- zarządzać trasami,
- zarządzać pojazdami,
- przeglądać raporty.

### 5.3. Smuggler / Przemytnik

Przemytnik odpowiada za realizację przypisanych transportów.

Może:

- przeglądać swoje transporty,
- sprawdzać szczegóły trasy,
- aktualizować status przypisanego transportu.

Nie powinien mieć dostępu do:

- zarządzania użytkownikami,
- raportów finansowych,
- płatności,
- danych innych przemytników.

### 5.4. Accountant

Accountant odpowiada za płatności i raporty finansowe.

Może:

- przeglądać płatności,
- dodawać płatności,
- zmieniać status płatności,
- przeglądać raporty finansowe.

Nie musi mieć dostępu do zarządzania użytkownikami ani pełnej edycji transportów.

---

## 6. Dane słownikowe

W systemie występują dane słownikowe, które porządkują wartości używane w formularzach i tabelach.

Przykładowe słowniki:

- statusy zleceń,
- statusy transportów,
- typy pojazdów,
- poziomy trudności tras,
- poziomy ryzyka tras,
- typy ładunków,
- statusy płatności,
- role użytkowników.

Dane słownikowe powinny być przechowywane w osobnych tabelach, aby uniknąć powtarzania tych samych wartości tekstowych w wielu miejscach.

---

## 7. Historia zmian

System powinien przechowywać historię wybranych zmian.

Historia zmian pozwala sprawdzić:

- kto wykonał zmianę,
- kiedy wykonano zmianę,
- w jakiej tabeli wykonano zmianę,
- jakiego rekordu dotyczyła zmiana,
- jaka była poprzednia wartość,
- jaka jest nowa wartość.

Przykładowe zdarzenia zapisywane w historii:

- utworzenie zlecenia,
- zmiana statusu zlecenia,
- zmiana statusu transportu,
- przypisanie przemytnika do transportu,
- dodanie płatności,
- zmiana statusu płatności,
- zmiana danych użytkownika.

Historia zmian może być przechowywana w tabeli `audit_logs`.

Przykładowe pola tabeli historii zmian:

- `id`,
- `table_name`,
- `record_id`,
- `action`,
- `changed_by`,
- `changed_at`,
- `old_value`,
- `new_value`.

---

## 8. Bezpieczeństwo systemu

System powinien ograniczać dostęp do poszczególnych modułów na podstawie roli użytkownika.

Podstawowe założenia bezpieczeństwa:

- użytkownik musi się zalogować, aby korzystać z chronionych części systemu,
- hasła użytkowników nie są przechowywane jawnym tekstem,
- hasła są przechowywane jako hash, np. z użyciem BCrypt,
- dostęp do panelu użytkowników ma tylko administrator,
- dostęp do raportów finansowych mają tylko wybrane role,
- przemytnik widzi tylko przypisane do siebie transporty,
- przyciski w interfejsie są ukrywane zależnie od roli użytkownika,
- endpointy REST są zabezpieczone przed dostępem nieuprawnionych użytkowników.

Przykładowy podział dostępu:

| Moduł | ADMIN | BOSS | SMUGGLER | ACCOUNTANT |
|---|---|---|---|---|
| Użytkownicy i role | Tak | Nie | Nie | Nie |
| Zlecenia | Tak | Tak | Nie | Nie |
| Transporty | Tak | Tak | Tylko przypisane | Nie |
| Trasy | Tak | Tak | Podgląd | Nie |
| Pojazdy | Tak | Tak | Podgląd | Nie |
| Ładunki | Tak | Tak | Podgląd przypisanych | Nie |
| Magazyny | Tak | Tak | Nie | Podgląd |
| Płatności | Tak | Tak | Nie | Tak |
| Raporty | Tak | Tak | Nie | Tak |

---

## 9. Wymagania funkcjonalne

System powinien umożliwiać:

- rejestrację użytkownika,
- logowanie użytkownika,
- wylogowanie użytkownika,
- zarządzanie użytkownikami,
- zarządzanie rolami,
- tworzenie, edycję, usuwanie i wyświetlanie zleceń,
- tworzenie, edycję, usuwanie i wyświetlanie transportów,
- tworzenie, edycję, usuwanie i wyświetlanie tras,
- tworzenie, edycję, usuwanie i wyświetlanie pojazdów,
- tworzenie, edycję, usuwanie i wyświetlanie ładunków,
- tworzenie, edycję, usuwanie i wyświetlanie magazynów,
- tworzenie, edycję, usuwanie i wyświetlanie płatności,
- przypisywanie przemytników do transportów,
- przypisywanie ładunków do transportów i magazynów,
- zmianę statusu zlecenia,
- zmianę statusu transportu,
- zmianę statusu płatności,
- sortowanie danych,
- filtrowanie danych,
- generowanie raportów,
- zapisywanie historii zmian.

---

## 10. Wymagania niefunkcjonalne

System powinien spełniać następujące wymagania niefunkcjonalne:

### 10.1. Bezpieczeństwo

Dostęp do danych powinien zależeć od roli użytkownika. Hasła powinny być przechowywane w postaci hashu.

### 10.2. Spójność danych

Baza danych powinna używać kluczy głównych, kluczy obcych oraz ograniczeń takich jak `NOT NULL`, `UNIQUE`, `CHECK`.

### 10.3. Integralność referencyjna

Nie powinno być możliwości utworzenia transportu bez istniejącego zlecenia, przypisania nieistniejącego przemytnika ani dodania płatności do nieistniejącego zlecenia.

### 10.4. Walidacja danych

Formularze powinny sprawdzać poprawność danych, np. wymagane pola, długość tekstu, wartości dodatnie, poprawność dat.

### 10.5. Czytelność i prostota obsługi

Interfejs powinien być prosty i czytelny. Widoki powinny być wykonane w Thymeleaf z użyciem Bootstrapa.

### 10.6. Dostępność

Interfejs powinien uwzględniać podstawowe zasady WCAG 2.1, np. czytelne etykiety formularzy, odpowiedni kontrast, logiczną strukturę nagłówków.

### 10.7. Rozszerzalność

System powinien być podzielony na moduły, aby w przyszłości można było łatwo dodać kolejne funkcje.

---

## 11. Główne encje systemu

W systemie planuje się następujące główne encje:

- `User`,
- `Role`,
- `Order`,
- `Transport`,
- `Route`,
- `Vehicle`,
- `SmugglerAssignment`,
- `Cargo`,
- `Warehouse`,
- `WarehouseStock`,
- `Payment`,
- `Report`,
- `AuditLog`.

---

## 12. Relacje między encjami

Przykładowe relacje:

- jeden użytkownik może mieć wiele ról,
- jedna rola może należeć do wielu użytkowników,
- jedno zlecenie może mieć wiele transportów,
- jeden transport należy do jednego zlecenia,
- jeden transport może mieć jedną trasę,
- jeden transport może mieć jeden pojazd,
- jeden transport może mieć przypisanego jednego przemytnika,
- jeden magazyn może przechowywać wiele ładunków,
- jeden ładunek może należeć do jednego transportu,
- jedno zlecenie może mieć wiele płatności,
- jedna płatność należy do jednego zlecenia,
- wiele zmian w systemie może być zapisanych w historii zmian.

---

## 13. Szacowany rozmiar bazy danych

Na potrzeby projektu zakłada się niewielką bazę testową.

Przykładowe dane początkowe:

- 10 użytkowników,
- 4 role,
- 20 zleceń,
- 30 transportów,
- 10 tras,
- 10 pojazdów,
- 40 ładunków,
- 5 magazynów,
- 30 płatności,
- 100 wpisów historii zmian.

Szacowany miesięczny przyrost danych w przykładowym scenariuszu:

- 10 nowych zleceń,
- 20 nowych transportów,
- 30 nowych ładunków,
- 20 nowych płatności,
- 50 nowych wpisów historii zmian.

Największy przyrost danych będzie dotyczył tabel:

- `audit_logs`,
- `transports`,
- `cargo`,
- `payments`.

---

## 14. Najczęściej używane operacje

Najczęściej wykonywane operacje w systemie:

- logowanie użytkownika,
- wyświetlanie listy aktywnych zleceń,
- wyświetlanie listy aktywnych transportów,
- filtrowanie transportów po statusie,
- wyszukiwanie transportów po dacie,
- zmiana statusu transportu,
- sprawdzanie stanu magazynowego,
- generowanie raportu zysków i strat.

Na kolumnach często używanych w filtrowaniu, sortowaniu i łączeniu tabel powinny zostać utworzone indeksy.

Przykładowe kolumny do indeksowania:

- `orders.status_id`,
- `orders.created_at`,
- `transports.status_id`,
- `transports.transport_date`,
- `transports.smuggler_id`,
- `cargo.transport_id`,
- `payments.order_id`,
- `audit_logs.changed_at`.

---

## 15. Raporty i widoki

System powinien posiadać widoki SQL ułatwiające generowanie raportów.

Przykładowe widoki:

- `v_active_orders` — aktywne zlecenia,
- `v_active_transports` — aktywne transporty,
- `v_user_permissions` — użytkownicy i ich role,
- `v_smuggler_workload` — liczba aktywnych transportów przypisanych do przemytników,
- `v_warehouse_stock` — aktualny stan magazynów,
- `v_profit_report` — zestawienie zysków i kosztów.

---

## 16. Potencjalne trudności

Podczas realizacji projektu mogą pojawić się następujące trudności:

- poprawne zaprojektowanie relacji między tabelami,
- zachowanie spójności danych przy wielu powiązanych tabelach,
- podział ról i uprawnień użytkowników,
- zabezpieczenie dostępu do poszczególnych modułów,
- obsługa historii zmian,
- przygotowanie raportów,
- połączenie logiki bazodanowej z aplikacją Java,
- zachowanie równego podziału pracy w zespole.

---

## 17. Podsumowanie

System zarządzania przemytem papierosów jest fikcyjnym projektem edukacyjnym, który pozwala zaprezentować wiele elementów wymaganych na przedmiotach Systemy baz danych oraz Projektowanie aplikacji WWW w języku Java.

Projekt obejmuje:

- rozbudowany model relacyjnej bazy danych,
- wiele powiązanych encji,
- role użytkowników,
- logowanie i kontrolę dostępu,
- operacje CRUD,
- REST API,
- widoki Thymeleaf,
- walidację formularzy,
- historię zmian,
- raporty,
- funkcje, procedury, triggery i widoki SQL.
