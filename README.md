# 🏃‍♂️ Fitness App

A modern fitness tracking application developed as a group project using **Kotlin** and **Jetpack Compose**. The app enables users to track workouts, monitor physical activity, and visualize performance data in a clean and intuitive interface.

---

# 📱 Project Description

Fitness App provides core health-tracking features such as a **step counter**, **distance tracking**, and **calorie estimation** based on user-specific data (e.g., height and weight).

The app includes a **workout mode**, allowing users to start activities like:

* Walking 🚶
* Running 🏃
* Cycling 🚴

During a workout, the app tracks:

* ⏱ Duration
* 📏 Distance
* ⚡ Pace (min/km)
* 🔥 Calories burned

Users can view their **route in real time** and after the workout using a map powered by **OpenStreetMap** via `osmdroid`.

After completing a workout, the app provides a **detailed summary**, including all recorded metrics and the full route taken.

---

# 🚀 Features

## 👤 User & Authentication

* User account system (Login & Signup)
* Secure password storage using BCrypt
* Persistent login with DataStore
* Multi-user support

## 🧠 Architecture & Design

* MVVM architecture
* Multiple ViewModels (separation of concerns)
* Manual dependency injection (AppContainer)
* Modular component-based structure
* Customized application theme

## 🧭 Navigation & Screens

* Navigation implemented using Navigation Compose
* Clear routing structure with dedicated screens for each feature

* Screens:

  * 🏠 Home Screen - Lists all previously done workouts (history)
  * 📋 Workouts Screen — Lists the three workout types: Walking, Running, and Cycling
  * 🏃 Workout Screen — Displays the real‑time map (osmdroid), steps, distance, pace, and duration during an active workout
  * 📊 Workout Data Screen — Detailed statistics, graphs, and the full recorded route after a workout
  * 👤 Profile Screen — User information, and BMI calculator
  * 🔐 Login Screen — Secure authentication using BCrypt
  * 📝 Signup Screen — Create a new user account

## 🏃 Workout & Tracking

* Workout detection:

  * Walking
  * Running
  * Cycling
* Step counter (SensorManager)
* Timer (workout duration)
* Distance calculation (GPS-based)
* Speed & pace calculation
* Calorie calculation (based on user data)

## 📍 Location & Map

* GPS tracking (LocationManager)
* Route recording during workouts
* Map visualization (OpenStreetMap via osmdroid)
* Post-workout route display

## 📊 Data & Analytics

* Workout history (Room database)
* Graphs for workout statistics (Vico charts)
* Real-time and post-workout data visualization

---

# 🧱 Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture:

```
UI (Jetpack Compose)
        ↓ 
     ViewModels 
        ↓ 
  Managers Repositories 
    ↓            ↓ 
    └──────────→ Room Database + DataStore
```

### Key Components

* **ViewModel** → UI state & business logic
* **Repository** → Single source of truth
* **Room Database** → Stores workouts & user data
* **DataStore** → Stores user session
* **AppContainer** → Manual dependency injection

---

# 🛠️ Technologies and Their Purpose

| Technology                      | Purpose                               |
| ------------------------------- | ------------------------------------- |
| Kotlin                          | Main programming language             |
| Jetpack Compose + Material 3    | Modern UI development                 |
| Navigation Compose              | Screen navigation                     |
| Sensore Manager                 | Step counter                          |
| Android LocationManager         | GPS location (GPS + network fallback) |
| osmdroid (OpenStreetMap)        | Map & route visualization             |
| Accompanist Permissions         | Runtime permissions                   |
| Room + KSP                      | Local database                        |
| DataStore (Preferences)         | Persistent storage                    |
| Kotlinx Serialization           | JSON serialization                    |
| jBCrypt                         | Secure password hashing               |
| Vico Charts                     | Workout statistics visualization      |

---

# 📁 Project Structure

```
com.example.fitnessapp
│
├── data
│   ├── local
│   │   ├── converters
│   │   ├── dao
│   │   ├── entity
│   │   └── AppDatabase
│   ├── model
│   └── repository
│
├── di
│   └── AppContainer
│
├── managers
│   ├── LocationManager
│   ├── StepCounterManager
│   └── UserPreferencesManager
│
├── network
│   └── Network (optional/future)
│
├── ui
│   ├── screens
│   ├── navigation
│   └── theme
│
├── viewmodel
│   ├── AuthViewModel
│   ├── ProfileViewModel
│   ├── ThemeViewModel
│   ├── WorkoutDataViewModel
│   └── WorkoutViewModel
│
├── FitnessApplication
└── MainActivity
```

---

# 🔐 Authentication

* Passwords are hashed using **BCrypt**
* No plain-text passwords stored
* Secure login verification
* Session persisted with DataStore

---

# ⚙️ Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run on emulator or device

---

# 🧠 Key Concepts

* MVVM architecture
* Repository pattern
* Reactive UI (StateFlow)
* Manual dependency injection
* GPS-based tracking
* Secure authentication
* Local-first data storage

---

# 🔮 Future Improvements

* ☁️ Cloud sync (e.g., Firebase)
* ❤️ Health sensor integration
* 🎯 Goal tracking
* 📈 Advanced analytics
* 🗺️ Enhanced maps

---

# 👥 Team

Group 16 - Mobile Developement Project.

---

# 📄 License

This project is for educational purposes.
