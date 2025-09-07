# FetchJSON - Android Application

A modern Android application built with Jetpack Compose that demonstrates fetching and displaying JSON data from remote APIs with user authentication features.

## Features

- **User Authentication**: Login and registration functionality
- **Post Feed**: Fetch and display posts from JSONPlaceholder API
- **Favorites**: Save and manage favorite posts
- **Account Management**: User profile and account settings
- **Offline Support**: Local data storage using Room database
- **Modern UI**: Built with Jetpack Compose and Material Design 3

## Setup & Build Instructions

### Prerequisites

- **Android Studio**: Arctic Fox or later
- **JDK**: Java 11 or higher
- **Android SDK**: API level 24 (Android 7.0) or higher
- **Gradle**: 8.10.1 (handled by wrapper)

### Clone and Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/AlveeMorshed/FetchJSON.git
   cd FetchJSON
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

### Build Variants

The project includes two build flavors:

- **Dev**: Development environment with debug features
  - Base URL: `https://jsonplaceholder.typicode.com`
  - Application ID suffix: `.dev`

- **Production**: Production-ready build
  - Base URL: `https://jsonplaceholder.typicode.com`
  - Application ID suffix: `.production`

### Building the App

#### From Android Studio:
1. Select the desired build variant (devDebug, productionRelease, etc.)
2. Click **Run** or use `Ctrl+R`

#### From Command Line:
```bash
# Debug build
./gradlew assembleDevDebug

# Release build
./gradlew assembleProductionRelease

# Install on device
./gradlew installDevDebug
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

## App Architecture

The application follows **Clean Architecture** principles with **MVVM (Model-View-ViewModel)** pattern:

### Architecture Layers

```
📱 Presentation Layer (UI)
├── Screens (Compose UI)
├── ViewModels
├── Navigation
└── Components

🔄 Domain Layer (Business Logic)
├── Use Cases
├── Repository Interfaces
└── Domain Models

💾 Data Layer
├── Repositories (Implementation)
├── Data Sources (Remote & Local)
├── Database (Room)
└── Network (Retrofit)
```

### Project Structure

```
com.alvee.fetchjson/
├── data/
│   ├── database/          # Room database entities and DAOs
│   ├── datasource/        # Local and remote data sources
│   ├── model/            # Data transfer objects
│   ├── remote/           # API service interfaces
│   └── repository/       # Repository implementations
├── domain/
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Business logic use cases
├── presentation/
│   ├── components/       # Reusable UI components
│   ├── navigation/       # Navigation setup
│   └── screens/          # App screens and ViewModels
├── di/                   # Dependency injection modules
├── ui/                   # Theme and UI utilities
└── utils/                # Utility classes
```

## Libraries and Technologies Used

### Core Technologies
- **Kotlin**: Primary programming language
- **Jetpack Compose**: Modern UI toolkit
- **Material Design 3**: UI design system

### Architecture & DI
- **Hilt**: Dependency injection framework
- **ViewModel**: UI-related data management
- **Navigation Compose**: Type-safe navigation

### Networking
- **Retrofit**: HTTP client for API calls
- **Gson**: JSON parsing
- **OkHttp Logging Interceptor**: Network request logging

### Local Storage
- **Room**: Local database for offline support
- **DataStore**: Preferences and settings storage


### Other Dependencies
- **Accompanist Permissions**: Runtime permission handling
- **Kotlin Coroutines**: Asynchronous programming
- **StateFlow & LiveData**: Reactive data streams

## Key Features Implementation

### Authentication
- User registration and login
- Password visibility toggle
- Email validation
- Secure local storage of user credentials
- Multiple user support

### Data Management
- Fetch posts from JSONPlaceholder API
- Local caching with Room database
- Offline-first approach
- Favorites management

### UI/UX
- Modern Material Design 3 interface
- Responsive layouts
- Loading states and error handling
- Smooth navigation between screens

## Assumptions and Limitations

### Assumptions
- **API Availability**: The app assumes JSONPlaceholder API is always available
- **Network Connectivity**: Internet connection required for initial data fetch
- **User Data**: Registration data is stored locally (not sent to a real backend)
- **Authentication**: Uses local/offline authentication (no real server validation)

### Current Limitations
- **No Real Backend**: Uses JSONPlaceholder for demo purposes only
- **Local Authentication**: User credentials are stored locally without server validation
- **Limited Offline Features**: Some features require network connectivity
- **No Data Synchronization**: No sync mechanism for offline changes
- **No Push Notifications**: Real-time updates not implemented
- **No Real-time User Switching**: No real-time or dynamic account switching

### Known Issues
- **Memory Management**: Large datasets might impact performance
- **Error Recovery**: Limited retry mechanisms for failed network requests
- **Data Validation**: Basic validation implemented (could be enhanced)

## API Integration

The app integrates with [JSONPlaceholder](https://jsonplaceholder.typicode.com/) API:

- **Base URL**: `https://jsonplaceholder.typicode.com`
- **Endpoints Used**:
  - `/posts` - Fetch all posts
  - `/posts/{id}` - Fetch specific post
  - `/users` - User information (if implemented)

## Development Notes

### Build Configuration
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35 (Android 15)
- **Compile SDK**: API 35
- **Java Version**: 11

### Code Quality
- Follows Kotlin coding conventions
- MVVM architecture pattern
- Clean Architecture principles
- Dependency injection with Hilt
- Reactive programming with Coroutines


---

**Package Name**: `com.alvee.fetchjson`  
**Version**: 1.0  
**Minimum Android Version**: 7.0 (API level 24)  
**Target Android Version**: 15 (API level 35)

