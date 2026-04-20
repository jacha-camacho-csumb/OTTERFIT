# OtterFit

OtterFit is a JavaFX fitness tracking application that allows users to create accounts, log in, and track their workout activity. Users can add exercises, record completed workouts, and view their workout history through a multi-scene interface.

---

## Table of Contents

- [Build and Run](#build-and-run)
- [Test Accounts](#test-accounts)
- [Features](#features)
- [Application Structure](#application-structure)
- [ERD](#erd)
- [Mockups](#mockups)


---
## Build and Run

Requires Java (JDK 17+ recommended). If using Gradle, the wrapper handles dependencies.

```bash
./gradlew run      # launch the app
./gradlew build    # compile and run tests
./gradlew test     # run unit tests only
```
---
## Test Accounts

Accounts with seed data have been created for debugging and demo purposes.

| username | password |
|----------|----------|
| otter    | otter    |
| john     | john     |
| jane     | jane     |

---

## Features

- User authentication (login system)
- Dashboard with A.I. weather updates (API) and navigation
- Log and manage workouts
- Exercise management (add and view exercises)
- Sorted workout history
- Data persistence
- Unit and UI testing support

---

## Application Structure

The application consists of the following scenes:

- Login / Create Account
- Dashboard / Main View
- Log Workout
- View Exercies
- Add Exercise
- View Workout History
- Sign Out

---
## ERD

![Alt text](https://i.imgur.com/Q0xyNgE.png)

---

## Mockups

![Alt text](https://i.imgur.com/4CUZliM.png) 
![Alt text](https://i.imgur.com/bqdqDHR.png)
![Alt text](https://i.imgur.com/wGQfX5O.png) 
![Alt text](https://i.imgur.com/vMMiJkS.png)

---

