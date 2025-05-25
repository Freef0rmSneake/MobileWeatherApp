# Aplikacja Pogodowa

Aplikacja pogodowa napisana w Kotlinie dla systemu Android, wyświetlająca informacje o pogodzie dla największych miast Polski.

## Struktura Projektu

Projekt jest podzielony na trzy główne komponenty:

### 1. Frontend (`app/src/main/java/com/example/mobileweatherapp/frontend/`)
Zawiera wszystkie komponenty interfejsu użytkownika:
- `MainActivity.kt`: Główna aktywność i komponenty Composable UI
- Pliki motywu UI (Color, Theme, Type)
- Odpowiada za interakcję z użytkownikiem i wyświetlanie danych pogodowych

### 2. Backend (`app/src/main/java/com/example/mobileweatherapp/backend/`)
Obsługuje logikę biznesową i operacje na danych:
- `WeatherViewModel.kt`: Zarządza stanem UI i operacjami na danych
- `WeatherRepository.kt`: Obsługuje pobieranie danych z API i lokalnej bazy danych
- Implementuje wzorzec architektury MVVM

### 3. Baza Danych (`app/src/main/java/com/example/mobileweatherapp/database/`)
Zarządza lokalnym przechowywaniem danych:
- `Weather.kt`: Encja danych
- `WeatherDao.kt`: Interfejs dostępu do bazy danych
- `WeatherDatabase.kt`: Konfiguracja bazy danych Room
- Zapewnia wsparcie trybu offline z 24-godzinnym cache

## Pomysły do zrobienia w UI/UX/FRONTEND

### Komponenty do Implementacji
1. Ekran Główny
   - Karta pogody z animowanymi przejściami
   - Wskaźnik aktualnej temperatury w stylu Material3
   - Animowana ikona stanu pogody
   - Wskaźnik odświeżania danych

2. Dialog Wyboru Miasta
   - Lista 10 największych miast z miniaturami
   - Wyszukiwarka z autouzupełnianiem
   - Animacje przejść między miastami

3. Stany Aplikacji
   - Ekran ładowania z animacją
   - Ekran błędu z przyjaznym komunikatem
   - Stan braku połączenia z informacją o trybie offline

## Funkcje
- Wyświetlanie pogody dla 10 największych miast Polski
- Wybór miasta przez okno dialogowe
- Wyświetlanie temperatury i opisu pogody
- Wsparcie trybu offline z lokalnym cache
- Obsługa błędów i stanów ładowania
- Implementacja Clean Architecture

## Użyte Technologie
- Kotlin
- Android Jetpack Compose
- Architektura MVVM
- Baza danych Room
- Klient Ktor
- Coroutines
- LiveData
- Material3 Design

## Konfiguracja
### Klucz API
Aplikacja wymaga klucza API OpenWeatherMap. Klucz jest konfigurowany poprzez zmienną środowiskową `OW_KEY` w pliku `local.properties`:
```properties
OW_KEY=twój_klucz_api
```