# Model danych

Dokument opisuje relacyjny model danych systemu. Baza danych zostala
podzielona na trzy glowne obszary: uzytkownicy i zlecenia, transporty oraz
ladunki, magazyny i platnosci.

## Lista encji

| Tabela                    | Opis                                                            |
| ------------------------- | --------------------------------------------------------------- |
| `users`                   | Uzytkownicy systemu.                                            |
| `roles`                   | Slownik rol uzytkownikow.                                       |
| `user_roles`              | Powiazanie uzytkownikow z rolami.                               |
| `smuggler_profiles`       | Dodatkowe dane uzytkownikow pelniacych role przemytnika.        |
| `order_statuses`          | Slownik statusow zlecen.                                        |
| `orders`                  | Zlecenia realizowane w systemie.                                |
| `audit_logs`              | Historia zmian wykonanych na danych systemowych.                |
| `transport_statuses`      | Slownik statusow transportow.                                   |
| `route_difficulty_levels` | Slownik poziomow trudnosci tras.                                |
| `routes`                  | Trasy transportow.                                              |
| `vehicles`                | Pojazdy wykorzystywane w transportach.                          |
| `transports`              | Transporty przypisane do zlecen.                                |
| `smuggler_assignments`    | Przypisania przemytnikow do transportow.                        |
| `cargo_types`             | Slownik typow ladunku.                                          |
| `warehouses`              | Magazyny.                                                       |
| `cargo`                   | Ladunki przypisane do zlecen, transportow lub magazynow.        |
| `warehouse_stock`         | Stan magazynowy ladunkow.                                       |
| `payment_statuses`        | Slownik statusow platnosci.                                     |
| `payments`                | Platnosci, koszty, przychody i prowizje zwiazane ze zleceniami. |

## Elementy slownikowe

W systemie zastosowano tabele slownikowe, ktore przechowuja stale zestawy
wartosci uzywane przez encje biznesowe. Ogranicza to ryzyko literowek,
ulatwia raportowanie oraz zapewnia spojne relacje przez klucze obce.

| Tabela                    | Opis                       | Przykladowe wartosci                                              |
| ------------------------- | -------------------------- | ----------------------------------------------------------------- |
| `roles`                   | Role uzytkownikow systemu. | `ADMIN`, `BOSS`, `SMUGGLER`, `ACCOUNTANT`                         |
| `order_statuses`          | Statusy zlecen.            | `NOWE`, `W_TRAKCIE`, `ZREALIZOWANE`, `ANULOWANE`                  |
| `transport_statuses`      | Statusy transportow.       | `ZAPLANOWANY`, `W_DRODZE`, `DOSTARCZONY`, `NIEUDANY`, `ANULOWANY` |
| `route_difficulty_levels` | Poziomy trudnosci tras.    | `LATWA`, `STANDARDOWA`, `TRUDNA`, `BARDZO_TRUDNA`, `EKSTREMALNA`  |
| `cargo_types`             | Typy ladunkow.             | `PAPIEROSY`, `TYTON`, `MIESZANY`                                  |
| `payment_statuses`        | Statusy platnosci.         | `OCZEKUJACA`, `ZAPLACONA`, `ANULOWANA`                            |

## Wartosci slownikowe

### Role uzytkownikow (`roles`)

| Wartosc      | Znaczenie                                                                   |
| ------------ | --------------------------------------------------------------------------- |
| `ADMIN`      | Administrator systemu zarzadzajacy uzytkownikami i rolami.                  |
| `BOSS`       | Uzytkownik tworzacy zlecenia, planujacy transporty i przegladajacy raporty. |
| `SMUGGLER`   | Uzytkownik przypisywany do transportow.                                     |
| `ACCOUNTANT` | Uzytkownik odpowiedzialny za platnosci i raporty finansowe.                 |

### Statusy zlecen (`order_statuses`)

| Wartosc        | Znaczenie                                                               |
| -------------- | ----------------------------------------------------------------------- |
| `NOWE`         | Zlecenie zostalo utworzone, ale nie rozpoczeto jeszcze jego realizacji. |
| `W_TRAKCIE`    | Zlecenie jest aktualnie realizowane.                                    |
| `ZREALIZOWANE` | Zlecenie zostalo zakonczone.                                            |
| `ANULOWANE`    | Zlecenie zostalo anulowane i nie bedzie dalej realizowane.              |

### Statusy transportow (`transport_statuses`)

| Wartosc       | Znaczenie                                                           |
| ------------- | ------------------------------------------------------------------- |
| `ZAPLANOWANY` | Transport zostal zaplanowany, ale nie rozpoczal jeszcze realizacji. |
| `W_DRODZE`    | Transport jest aktualnie realizowany.                               |
| `DOSTARCZONY` | Transport zakonczyl sie dostarczeniem ladunku.                      |
| `NIEUDANY`    | Transport zakonczyl sie niepowodzeniem.                             |
| `ANULOWANY`   | Transport zostal anulowany przed zakonczeniem.                      |

### Poziomy trudnosci tras (`route_difficulty_levels`)

| Wartosc         | Poziom ryzyka | Znaczenie                             |
| --------------- | ------------: | ------------------------------------- |
| `LATWA`         |             1 | Trasa o niskim poziomie ryzyka.       |
| `STANDARDOWA`   |             2 | Trasa o umiarkowanym poziomie ryzyka. |
| `TRUDNA`        |             3 | Trasa wymagajaca doswiadczenia.       |
| `BARDZO_TRUDNA` |             4 | Trasa o wysokim poziomie ryzyka.      |
| `EKSTREMALNA`   |             5 | Trasa o najwyzszym poziomie ryzyka.   |

### Typy ladunkow (`cargo_types`)

| Wartosc     | Znaczenie                                                  |
| ----------- | ---------------------------------------------------------- |
| `PAPIEROSY` | Ladunek zawierajacy papierosy.                             |
| `TYTON`     | Ladunek zawierajacy tyton.                                 |
| `MIESZANY`  | Ladunek mieszany, zawierajacy wiecej niz jeden typ towaru. |

### Statusy platnosci (`payment_statuses`)

| Wartosc      | Znaczenie                                                              |
| ------------ | ---------------------------------------------------------------------- |
| `OCZEKUJACA` | Platnosc zostala zarejestrowana, ale nie zostala jeszcze zrealizowana. |
| `ZAPLACONA`  | Platnosc zostala zrealizowana.                                         |
| `ANULOWANA`  | Platnosc zostala anulowana.                                            |

## Najwazniejsze relacje

| Relacja                              | Opis                                                                     |
| ------------------------------------ | ------------------------------------------------------------------------ |
| `users` - `roles`                    | Relacja wiele-do-wielu przez tabele `user_roles`.                        |
| `users` - `smuggler_profiles`        | Relacja jeden-do-jednego dla uzytkownikow bedacych przemytnikami.        |
| `orders` - `order_statuses`          | Kazde zlecenie posiada jeden status.                                     |
| `orders` - `users`                   | Zlecenie ma autora oraz opcjonalnego uzytkownika odpowiedzialnego.       |
| `orders` - `transports`              | Jedno zlecenie moze miec wiele transportow.                              |
| `transports` - `transport_statuses`  | Kazdy transport posiada jeden status.                                    |
| `transports` - `routes`              | Transport moze byc przypisany do trasy.                                  |
| `transports` - `vehicles`            | Transport moze korzystac z pojazdu.                                      |
| `transports` - `users`               | Przemytnicy sa przypisywani do transportow przez `smuggler_assignments`. |
| `routes` - `route_difficulty_levels` | Kazda trasa posiada poziom trudnosci.                                    |
| `cargo` - `cargo_types`              | Kazdy ladunek posiada typ.                                               |
| `cargo` - `orders`                   | Ladunek moze byc powiazany ze zleceniem.                                 |
| `cargo` - `transports`               | Ladunek moze byc powiazany z transportem.                                |
| `warehouses` - `cargo`               | Stan magazynowy jest przechowywany w tabeli `warehouse_stock`.           |
| `payments` - `orders`                | Platnosci sa przypisane do zlecen.                                       |
| `payments` - `payment_statuses`      | Kazda platnosc posiada status.                                           |

## Integralnosc danych

Model wykorzystuje klucze glowne, klucze obce, ograniczenia `UNIQUE`,
ograniczenia `CHECK` oraz indeksy. Przykladowo:

- nazwy uzytkownikow i adresy email sa unikalne,
- statusy i typy slownikowe sa ograniczone przez `CHECK`,
- wartosci liczbowe, takie jak pojemnosc pojazdu, pojemnosc magazynu,
  liczba paczek i kwoty platnosci, musza byc dodatnie,
- daty transportu sa sprawdzane tak, aby planowana data przyjazdu nie byla
  wczesniejsza od daty transportu.

## Historia zmian

Do przechowywania historii zmian przewidziano tabele `audit_logs`. Tabela
zawiera nazwe zmienionej tabeli, identyfikator rekordu, typ operacji,
uzytkownika wykonujacego zmiane, date zmiany oraz poprzednia i nowa wartosc.

## Widoki modulu transportowego

W module transportowym przewidziano widoki pomocnicze, ktore ulatwiaja
wyszukiwanie dostepnych zasobow, przeglad aktywnych transportow oraz wybor
trasy.

| Widok                   | Opis                                                                                   |
| ----------------------- | -------------------------------------------------------------------------------------- |
| `v_available_smugglers` | Lista aktywnych przemytnikow, ktorzy nie sa przypisani do aktywnego transportu.        |
| `v_available_vehicles`  | Lista pojazdow dostepnych i nieuzywanych w aktywnych transportach.                     |
| `v_active_transports`   | Lista transportow o statusie `ZAPLANOWANY` lub `W_DRODZE`.                             |
| `v_transport_details`   | Szczegolowy widok transportu z trasa, pojazdem, statusem i przypisanymi przemytnikami. |
| `v_route_summary`       | Podsumowanie tras z dystansem, poziomem trudnosci i kategoria ryzyka.                  |

## Funkcje modulu transportowego

W module transportowym przewidziano funkcje obliczeniowe wykorzystywane przy
planowaniu i ocenie transportu.

| Funkcja                                               | Opis                                                                                                                          |
| ----------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `calculate_transport_risk_score(p_transport_id)`      | Oblicza punktowy poziom ryzyka transportu w skali 0-100 na podstawie trasy, dystansu, pojazdu oraz przypisanych przemytnikow. |
| `estimate_transport_operational_cost(p_transport_id)` | Oblicza szacunkowy koszt operacyjny transportu na potrzeby planowania. Uwzglednia dystans, typ pojazdu, ryzyko trasy oraz doswiadczenie przypisanych przemytnikow. |
