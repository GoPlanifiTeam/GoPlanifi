# GoPlanify App Design Documentation

## General Description

This document describes the architecture and design of the GoPlanify travel planning application, including its main entities, attributes, methods, and relationships. GoPlanify is a mobile application that enables users to plan trips, create itineraries, and receive destination recommendations.

## System Architecture

GoPlanify follows a clean architecture approach with the following layers:

1. **Presentation Layer** - Jetpack Compose UI components and ViewModels
2. **Domain Layer** - Business logic and model classes
3. **Data Layer** - Repositories and data sources (Room database and Firebase)

The application uses the following design patterns and frameworks:
- **MVVM (Model-View-ViewModel)** pattern for UI architecture
- **Repository pattern** for data management
- **Dependency Injection** with Hilt
- **Firebase Authentication** for user management
- **Room Database** for local persistence
- **Coroutines and Flow** for asynchronous operations

## Entities and Responsibilities

| Entity                     | Attributes                                                                                                                                         | Methods                                                      |
|----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| **Authentication**         | `userId`, `loginErrors`, `authState`                                                                                                              | `login()`, `logout()`, `signup()`, `sendEmailVerification()`, `sendPasswordResetEmail()`, `deleteAccount()`, `checkEmailVerification()` |
| **User**                   | `userId`, `email`, `password`, `firstName`, `lastName`, `username`, `birthDate`, `address`, `country`, `phoneNumber`, `acceptEmails`, `trips`, `imageURL` | - |
| **Preferences**            | `userId`, `notificationsEnabled`, `preferredLanguage`, `theme`                                                                                     | `savePreferences()`, `getPreferences()` |
| **Trip**                   | `id`, `user`, `destination`, `itineraries`, `startDate`, `endDate`, `images`, `aiRecommendations`, `imageURL`, `map` | `addTrip()`, `updateTrip()`, `deleteTrip()`, `getTripById()`, `getTripsByUser()` |
| **Map**                    | `latitud`, `longitud`, `direction`                                                                                                                 | - |
| **Itinerary Item**         | `id`, `tripId`, `name`, `location`, `startDate`, `endDate`                                                                                         | `addItineraryItem()`, `deleteItineraryItem()`, `getItineraryItemsByTripId()`, `updateItineraryDates()` |
| **Image**                  | `trip`, `id`, `imageURL`                                                                                                                          | - |
| **AI Recommendations**     | `trip`, `recommendations`                                                                                                                          | - |

### 1. Authentication

Manages user authentication, login, registration, password recovery, and email verification using Firebase Authentication.

#### Key Features:
- User signup and account creation
- Email verification
- Login with credential validation
- Password reset functionality
- Authentication state tracking
- Login error tracking

#### Implementation:
- `AuthenticationRepository` interface defines authentication operations
- `AuthenticationRepositoryImpl` implements these operations using Firebase Auth
- `AuthState` sealed class represents different authentication states:
  - `Unauthenticated` - User not logged in
  - `Authenticated` - User logged in
  - `EmailNotVerified` - User registered but email not verified
  - `Loading` - Authentication operation in progress
  - `Error` - Authentication error occurred

### 2. User

Represents application users with their profile information, login credentials, and associated trips.

#### Key Features:
- Comprehensive user profile with personal information
- Support for profile image
- Username uniqueness validation
- Email format validation
- Phone number format validation
- Associated trips management

#### Implementation:
- User data is stored locally using Room Database
- `UserEntity` maps to the database schema
- `User` domain model represents the business entity
- `UserRepository` handles user data operations

### 3. Preferences

Stores and manages user preferences, including language settings and notification preferences.

#### Key Features:
- Multi-language support (English, Spanish, French, Portuguese)
- Theme preferences
- Notification settings

#### Implementation:
- `PreferencesRepository` manages saving and retrieving user preferences
- `SettingsViewModel` exposes preferences to the UI
- Localization is persisted across app restarts

### 4. Trip

Represents a travel plan with destination, dates, and associated itineraries.

#### Key Features:
- Trip creation and management
- Date range validation
- Associated itineraries management
- Support for trip images
- Support for AI-generated recommendations

#### Implementation:
- `TripRepository` handles trip data operations
- Trip data is stored locally using Room Database
- Trips are associated with users
- Validation ensures proper date ranges and itinerary consistency

### 5. Map

Stores geographical information for trips.

#### Key Features:
- Location coordinates (latitude, longitude)
- Direction information

#### Implementation:
- Simple data class for storing coordinates
- Associated with Trip entities

### 6. Itinerary Item

Represents activities, visits, or events within a trip's schedule.

#### Key Features:
- Activity name and description
- Location information
- Date and time scheduling
- Association with specific trips

#### Implementation:
- `ItineraryRepository` manages itinerary operations
- Itinerary items can be added, updated, and removed from trips
- Date validation ensures items fall within trip dates

### 7. Image

Represents photos or images associated with trips.

#### Key Features:
- Storage of image URLs
- Association with specific trips

#### Implementation:
- Simple data structure for image references
- Images are referenced by URL

### 8. AI Recommendations

Stores AI-generated trip recommendations for users.

#### Key Features:
- List of recommendations for a trip
- Association with specific trips

#### Implementation:
- Simple data structure for recommendation storage
- Associated with Trip entities

## Data Layer Implementation

### Room Database

The local database is implemented using Room with the following entities:
- `UserEntity`
- `TripEntity`
- `ItineraryItemEntity`
- `AuthenticationEntity`
- `PreferencesEntity`

#### Database Structure:
- `AppDatabase` defines the database schema and migrations
- Type converters handle complex data types like Date
- DAOs (Data Access Objects) provide interfaces for database operations:
  - `UserDao`
  - `TripDao`
  - `ItineraryDao`
  - `AuthenticationDao`
  - `PreferencesDao`

#### Migration Strategy:
The database includes a migration path from version 1 to 2 to add the following fields to the User entity:
- `username`
- `birthDate`
- `address`
- `country`
- `phoneNumber`
- `acceptEmails`

### Firebase Integration

Firebase services are integrated for:
- Authentication (signup, login, password reset)
- Email verification
- App Check for security validation

### Repositories

Repositories form the data access layer, mediating between domain models and data sources:
- `AuthenticationRepositoryImpl`
- `UserRepositoryImpl`
- `TripRepositoryImpl`
- `ItineraryRepositoryImpl`
- `PreferencesRepositoryImpl`

Each repository:
- Implements validation logic
- Handles error management
- Performs data mapping between entity and domain objects
- Provides a clean API for ViewModels

## Presentation Layer Implementation

### ViewModels

The application uses the MVVM pattern with the following ViewModels:
- `AuthViewModel` - Manages authentication state and user profile
- `TripViewModel` - Manages trip operations and user trips
- `ItineraryViewModel` - Manages itinerary operations within trips
- `SettingsViewModel` - Manages user preferences and settings

### UI Components

The UI is built with Jetpack Compose and includes the following screens:
- `LoginScreen` - User login with email/password
- `SignupScreen` - User registration with detailed profile information
- `MainScreen` - Home screen showing user trips
- `ProfileScreen` - User profile management
- `TripsScreen` - List and management of user trips
- `ItineraryScreen` - Itinerary management within trips
- `SettingsScreen` - User preferences management
- `AboutScreen` - Application information
- `VersionScreen` - Version details
- `TermsAndConditionsScreen` - Legal information

### Navigation

Navigation is handled through Jetpack Compose Navigation with:
- `NavGraph` defining the navigation structure
- Deep links for trip details
- Authentication state-aware navigation

### Multi-language Support

The application supports multiple languages:
- English
- Spanish
- French
- Portuguese

Language settings are:
- Stored in user preferences
- Applied using Android's configuration mechanism
- Persisted across app restarts

## Relationships

- `User` can have multiple `Trip` objects
- `Trip` contains `ItineraryItem`, `Image`, and `AIRecommendations`
- `ItineraryItem` is linked to a `Trip`
- `Preferences` and `Authentication` are related to `User`

## Key Features & Implementations

### User Authentication Flow
1. User registers with email, password, and profile information
2. Verification email is sent
3. User verifies email and can then log in
4. Password recovery option is available for forgotten passwords
5. Authentication state persists across app restarts

### User Profile Management
1. Comprehensive user profile with personal information
2. Username uniqueness validation
3. Email format validation
4. Phone number format validation

### Trip Planning
1. Create and manage trips with destination and date range
2. Add, update, and remove itinerary items within trips
3. Validation ensures proper date ranges and itinerary consistency

### Settings and Preferences
1. Multi-language support
2. Theme preferences
3. Notification settings

### Navigation and UI
1. Bottom navigation for main screens
2. Top bar with settings access
3. Clean and intuitive UI with Material Design 3 components

---