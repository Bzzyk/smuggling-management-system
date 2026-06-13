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
- `orders.estimated_profit` moze byc dodatni albo ujemny, poniewaz jest
  przewidywanym zyskiem netto wyliczanym automatycznie z transportow,
- daty transportu sa sprawdzane tak, aby planowana data przyjazdu nie byla
  wczesniejsza od daty transportu.
- przypisanie pojazdu, ladunku i przemytnika do transportu jest dodatkowo
  kontrolowane przez procedury oraz triggery bazodanowe,
- rozpoczecie transportu wymaga kompletnego skladu: pojazdu, ladunku i co
  najmniej jednego aktywnego przemytnika,
- baza pilnuje, aby ladunek nie przekroczyl ladownosci przypisanego pojazdu,
- baza pilnuje, aby przemytnik lub pojazd nie byl jednoczesnie uzywany w
  innym aktywnym transporcie,
- po opuszczeniu statusu `ZAPLANOWANY` baza blokuje edycje planu transportu,
  zmiany ladunku oraz reczne zmiany przypisanej ekipy,
- po rozpoczeciu transportu ladunek jest usuwany ze stanu magazynowego i nie
  ma juz przypisanego magazynu w tabeli `cargo`.

## Historia zmian

Do przechowywania historii zmian przewidziano tabele `audit_logs`. Tabela
zawiera nazwe zmienionej tabeli, identyfikator rekordu, typ operacji,
uzytkownika wykonujacego zmiane, date zmiany oraz poprzednia i nowa wartosc.

## Widoki modulu transportowego

W module transportowym zastosowano widoki pomocnicze, ktore ulatwiaja
wyszukiwanie dostepnych zasobow, przeglad aktywnych transportow oraz wybor
trasy. Widoki `v_available_*` sa wykorzystywane w aplikacji w selektorach
pojazdow, ladunkow i przemytnikow. Dzieki temu aplikacja nie pobiera wszystkich
rekordow z tabel, tylko pracuje na danych juz wstepnie odfiltrowanych przez
baze danych.

| Widok                   | Opis                                                                                   |
| ----------------------- | -------------------------------------------------------------------------------------- |
| `v_available_smugglers` | Lista aktywnych przemytnikow, ktorzy nie sa przypisani do aktywnego transportu. Widok wylicza takze `success_rate_percent` na podstawie liczby zakonczonych i nieudanych transportow. |
| `v_available_vehicles`  | Lista pojazdow oznaczonych jako dostepne i nieuzywanych w aktywnych transportach.      |
| `v_available_cargo`     | Lista ladunkow, ktore nie sa jeszcze przypisane do transportu.                         |
| `v_active_transports`   | Lista transportow o statusie `ZAPLANOWANY` lub `W_DRODZE`.                             |
| `v_transport_details`   | Szczegolowy widok transportu z trasa, pojazdem, statusem i przypisanymi przemytnikami. |
| `v_route_summary`       | Podsumowanie tras z dystansem, poziomem trudnosci i kategoria ryzyka.                  |

## Funkcje modulu transportowego

W module transportowym przewidziano funkcje obliczeniowe wykorzystywane przy
planowaniu i ocenie transportu. Aplikacja prezentuje ich wyniki w panelu
transportu, gdzie po zmianie ladunku, pojazdu lub ekipy mozna zobaczyc
zaktualizowane ryzyko oraz koszt operacyjny.

| Funkcja                                               | Opis                                                                                                                          |
| ----------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `calculate_transport_risk_score(p_transport_id)`      | Oblicza punktowy poziom ryzyka transportu w skali 0-100. Uwzglednia trudnosc trasy, dystans, typ pojazdu, liczbe paczek, wartosc ladunku oraz doswiadczenie i skutecznosc przypisanych przemytnikow. |
| `estimate_transport_operational_cost(p_transport_id)` | Oblicza szacunkowy koszt operacyjny transportu. Uwzglednia dystans, typ pojazdu, ryzyko trasy, liczbe paczek, wartosc ladunku oraz doswiadczenie przypisanych przemytnikow. |
| `calculate_transport_estimated_profit(p_transport_id)` | Oblicza przewidywany zysk transportu jako wartosc przypisanego ladunku pomniejszona o szacunkowy koszt operacyjny transportu. |
| `refresh_order_estimated_profit(p_order_id)`          | Przelicza `orders.estimated_profit` jako sume przewidywanych zyskow wszystkich nieanulowanych transportow danego zlecenia. |

Przewidywany zysk widoczny w panelu transportu jest wyliczany przez funkcje
`calculate_transport_estimated_profit`. Aplikacja odczytuje wynik z bazy
danych, dzieki czemu panel transportu i automatycznie aktualizowane pole
`orders.estimated_profit` korzystaja z tej samej definicji zysku.

## Procedury modulu transportowego

Procedury modulu transportowego obsluguja operacje, ktore powinny byc
wykonywane razem z kontrola warunkow biznesowych po stronie bazy danych.

| Procedura                                                         | Opis                                                                                                                      |
| ----------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `assign_smuggler_to_transport(p_transport_id, p_smuggler_id, p_note)` | Przypisuje aktywnego przemytnika do zaplanowanego transportu. Sprawdza, czy transport istnieje, czy przemytnik jest aktywny i czy nie ma juz innego aktywnego transportu. |
| `assign_vehicle_to_transport(p_transport_id, p_vehicle_id)`       | Przypisuje dostepny pojazd do zaplanowanego transportu. Sprawdza dostepnosc pojazdu, brak innego aktywnego transportu oraz ladownosc wzgledem aktualnego ladunku. |
| `assign_cargo_to_transport(p_transport_id, p_cargo_id)`           | Przypisuje wolny ladunek do zaplanowanego transportu. Sprawdza, czy ladunek nie jest w innym transporcie, czy pasuje do zlecenia oraz czy po dodaniu nie przekroczy ladownosci pojazdu. |
| `change_transport_status(p_transport_id, p_status_name)`          | Zmienia status transportu na podstawie wartosci ze slownika `transport_statuses`. Pilnuje dozwolonych przejsc statusow oraz wymaga pojazdu, ladunku i przemytnika przy rozpoczeciu transportu. |

## Triggery modulu transportowego

Triggery modulu transportowego automatyzuja techniczne aktualizacje danych oraz
zabezpieczaja najwazniejsze reguly biznesowe niezaleznie od tego, czy operacja
zostanie wykonana przez procedure, aplikacje czy reczny SQL.

| Trigger                                                  | Tabela                  | Opis                                                                                                 |
| -------------------------------------------------------- | ----------------------- | ---------------------------------------------------------------------------------------------------- |
| `trg_set_transport_updated_at`                           | `transports`            | Ustawia `updated_at` przy kazdej aktualizacji transportu.                                             |
| `trg_prevent_non_planned_transport_edit`                 | `transports`            | Blokuje edycje planu transportu, pojazdu, trasy, dat i opisu po opuszczeniu statusu `ZAPLANOWANY`. |
| `trg_validate_transport_status_change`                   | `transports`            | Pilnuje dozwolonych przejsc statusow transportu oraz wymaga pojazdu, ladunku i przemytnika przy rozpoczeciu transportu. Sprawdza tez, czy ladunek miesci sie w pojezdzie. |
| `trg_validate_smuggler_assignment`                       | `smuggler_assignments`  | Blokuje aktywne przypisanie przemytnika do transportu innego niz `ZAPLANOWANY` oraz blokuje zajetego lub nieaktywnego przemytnika. |
| `trg_prevent_non_planned_assignment_edit`                 | `smuggler_assignments`  | Blokuje dodawanie, usuwanie i reczna edycje ekipy, jezeli transport nie jest juz w statusie `ZAPLANOWANY`. |
| `trg_close_transport_assignments_and_update_stats`       | `transports`            | Po zmianie statusu na `DOSTARCZONY`, `NIEUDANY` albo `ANULOWANY` dezaktywuje przypisania, a dla sukcesu lub porazki aktualizuje statystyki przemytnikow. |
| `trg_remove_transport_cargo_from_warehouse`              | `transports`            | Po rozpoczeciu transportu usuwa przypisany ladunek ze stanu magazynowego i czysci `cargo.warehouse_id`. |
| `trg_prevent_non_planned_cargo_assignment_edit`          | `cargo`                 | Blokuje przypinanie i odpinanie ladunku od transportu, ktory nie jest juz w statusie `ZAPLANOWANY`. |
| `trg_refresh_order_profit_transports`                    | `transports`            | Po dodaniu, edycji lub usunieciu transportu przelicza przewidywany zysk powiazanego zlecenia. |
| `trg_refresh_order_profit_cargo`                         | `cargo`                 | Po zmianie ladunku, jego wartosci, liczby paczek lub przypisania do transportu odswieza przewidywany zysk zlecenia. |
| `trg_refresh_order_profit_smuggler_assignments`          | `smuggler_assignments`  | Po zmianie skladu ekipy transportu odswieza przewidywany zysk zlecenia, poniewaz doswiadczenie przemytnikow wplywa na koszt operacyjny. |
| `trg_refresh_order_profit_routes`                        | `routes`                | Po zmianie dystansu albo poziomu trudnosci trasy odswieza zysk zlecen powiazanych z transportami na tej trasie. |
| `trg_refresh_order_profit_vehicles`                      | `vehicles`              | Po zmianie typu pojazdu odswieza zysk zlecen powiazanych z transportami uzywajacymi tego pojazdu. |
| `trg_refresh_order_profit_route_difficulty_levels`       | `route_difficulty_levels` | Po zmianie poziomu ryzyka odswieza zysk zlecen korzystajacych z tras o tym poziomie trudnosci. |

## Widoki magazynowo-finansowe

Poza modulem transportowym baza zawiera widoki wykorzystywane w obszarze
magazynow i raportow finansowych.

| Widok                | Opis                                                                 |
| -------------------- | -------------------------------------------------------------------- |
| `v_warehouse_stock`  | Pokazuje aktualny stan magazynow razem z nazwa magazynu, lokalizacja, ladunkiem, typem ladunku, iloscia i wartoscia. |
| `v_profit_report`    | Pokazuje przychody, koszty oraz zysk netto dla zlecen na podstawie oplaconych platnosci. |

## Funkcje ladunkow i finansow

| Funkcja                              | Opis                                                                 |
| ------------------------------------ | -------------------------------------------------------------------- |
| `calculate_cargo_value(p_cargo_id)`  | Zwraca szacowana wartosc wskazanego ladunku na podstawie pola `estimated_value`. |
| `calculate_order_profit(p_order_id)` | Oblicza zysk ze zlecenia na podstawie oplaconych platnosci typu `PRZYCHOD`, `KOSZT` i `PROWIZJA`. |

## Procedury magazynowo-finansowe

| Procedura                                                          | Opis                                                                 |
| ------------------------------------------------------------------ | -------------------------------------------------------------------- |
| `add_cargo_to_warehouse(p_warehouse_id, p_cargo_id, p_quantity)`   | Dodaje ladunek do magazynu albo zwieksza istniejacy stan magazynowy. Aktualizuje tez magazyn przypisany do ladunku. |
| `register_payment(p_order_id, p_amount, p_payment_type, p_status_id, p_description)` | Rejestruje platnosc dla zlecenia z podanym typem, statusem i opcjonalnym opisem. |

## Triggery magazynowo-finansowe

| Trigger                        | Tabela            | Opis                                                                 |
| ------------------------------ | ----------------- | -------------------------------------------------------------------- |
| `trg_check_warehouse_capacity` | `warehouse_stock` | Blokuje dodanie lub aktualizacje stanu magazynowego, jezeli suma ilosci przekroczylaby maksymalna pojemnosc magazynu. |
| `trg_audit_payments`           | `payments`        | Zapisuje audyt dodania, usuniecia oraz zmiany statusu platnosci w tabeli `payments_audit`. |
