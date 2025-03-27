# EnglishApp - Aplikacja do nauki języka angielskiego

EnglishApp to aplikacja ułatwiająca naukę słownictwa angielskiego z wykorzystaniem technologii AI do generowania i tłumaczenia słówek.

## Funkcjonalności

- **Rejestracja i logowanie** - bezpieczny system autentykacji JWT
- **Profil użytkownika** - zarządzanie preferencjami nauki (ilość słówek, preferowana godzina)
- **Sesje nauki** - interaktywne sesje tłumaczenia słów
- **Generowanie słówek** - automatyczne generowanie zestawów słów o różnym poziomie trudności
- **Eksport do PDF** - możliwość zapisania przetłumaczonych słówek w formacie PDF

## Technologie

### Backend
- Java 17
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- PostgreSQL
- OpenAI API (dla tłumaczeń i generowania słówek)
- iText (generowanie PDF)

### Frontend
- React
- React Router
- Axios
- Tailwind CSS

## Struktura projektu

```
EnglishApp/
├── backend/ (Spring Boot)
│   ├── src/main/java/com/dev/englishapp/
│   │   ├── config/                # Konfiguracja Spring Security i CORS
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/            # Kontrolery REST API
│   │   │   ├── AuthController.java
│   │   │   ├── LearningSessionController.java
│   │   │   └── UserController.java
│   │   ├── entity/                # Encje bazodanowe
│   │   │   ├── Role.java
│   │   │   ├── User.java
│   │   │   └── UserPreferences.java
│   │   ├── exception/             # Obsługa wyjątków
│   │   ├── model/                 # Obiekty DTO
│   │   │   ├── LoginDto.java
│   │   │   ├── RegisterDto.java
│   │   │   └── Translation.java
│   │   ├── repository/            # Repozytoria JPA
│   │   ├── security/              # Implementacja JWT
│   │   │   ├── JwtTokenProvider.java
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── service/               # Logika biznesowa
│   │   │   ├── AuthService.java
│   │   │   ├── LearningSessionService.java
│   │   │   └── UserService.java
│   │   └── openAiClient/          # Integracja z API OpenAI
│   │       ├── OpenAiClient.java
│   │       └── Prompt.java
│   └── resources/
│       └── application.properties # Konfiguracja aplikacji
│
├── frontend/ (React)
│   ├── public/
│   └── src/
│       ├── components/            # Komponenty React
│       │   ├── Auth.js            # Komponent logowania/rejestracji
│       │   ├── LearningSession.js # Komponent sesji nauki
│       │   ├── UserProfile.js     # Komponent profilu użytkownika
│       │   └── ProtectedRoute.js  # Komponent routingu chronionego
│       ├── contexts/              # Konteksty React
│       │   └── AuthContext.js     # Kontekst autentykacji
│       ├── services/              # Usługi komunikacji z API
│       │   ├── authService.js     # Serwis autentykacji
│       │   └── userService.js     # Serwis użytkownika
│       ├── App.js                 # Komponent główny
│       └── index.js               # Punkt wejściowy aplikacji
```

## Endpointy API

### Autentykacja
- `POST /api/auth/register` - rejestracja nowego użytkownika
- `POST /api/auth/login` - logowanie

### Profil użytkownika
- `GET /users/me` - pobieranie danych użytkownika
- `POST /users/preferences` - aktualizacja preferencji
- `POST /users/generateWords` - generowanie słówek

### Sesja nauki
- `POST /users/learning/start` - rozpoczęcie sesji
- `GET /users/learning/status` - sprawdzenie statusu sesji
- `POST /users/learning/translate` - tłumaczenie słowa
- `GET /users/learning/translations` - pobranie wszystkich tłumaczeń
- `GET /users/learning/export/pdf` - eksport sesji do PDF
- `POST /users/learning/end` - zakończenie sesji
