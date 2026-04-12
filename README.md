# OtterFit

OtterFit is a JavaFX fitness tracking application that allows users to create accounts, log in, and track their workout activity. Users can add exercises, record completed workouts, and view their workout history through a multi-scene interface.

---

## Features

- User authentication (login system)
- Dashboard with summaries and navigation
- Log and manage workouts
- Exercise management (add and view exercises)
- Workout history with sorting and filtering
- Data persistence
- Unit and UI testing support

---

## Application Structure

The application consists of the following scenes:

- Login / Create Account
- Dashboard
- Log Workout
- Workout History
- Exercise Management

---

## Use Cases

### 1. Login

**Main Flow**
1. User opens the application  
2. Login screen appears  
3. User enters username/email and password  
4. System validates credentials  
5. User is logged in and redirected to the dashboard  

**Alternative Flow**
- Invalid credentials → system displays error message → user retries  

---

### 2. View Dashboard

**Main Flow**
- Displays dashboard components such as charts, summaries, and navigation  

**Alternative Flow**
- Data fails to load → system displays error message  

---

### 3. Log Workout

**Main Flow**
1. User selects "Log Workout"  
2. User enters workout details  
3. User saves workout  
4. System stores the data  

**Alternative Flow**
- Missing or invalid input → system displays error  

---

### 4. Delete Workout

**Main Flow**
- User selects workout → confirms deletion → system deletes workout  

**Alternative Flow**
- No workout selected → system prompts user  

---

### 5. View Exercises

**Main Flow**
- Displays all saved exercises  

**Alternative Flow**
- No exercises found → system displays empty state  

---

### 6. Add Exercise

**Main Flow**
- User enters exercise details → system validates → exercise is saved  

**Alternative Flow**
- Missing fields or duplicate exercise → system displays error  

---

### 7. View Workout History

**Main Flow**
- Displays list of past workouts (date, type, duration, etc.)  

**Alternative Flow**
- No history → system prompts user to log a workout  
- Data retrieval error → system displays error  

---

### 8. Sort / Filter History

**Main Flow**
- User filters or sorts workouts by criteria (date, type, duration)  

**Alternative Flow**
- No matching results → system displays message  
- Invalid filter → system displays error  

---

## System Design

### Entity Relationship Diagram
(Add ERD image here if available)

---

## Testing Plan

### Objectives

- Ensure user accounts are created correctly  
- Ensure usernames/emails are unique  
- Validate login functionality  
- Validate exercise and workout creation  
- Ensure accurate workout history retrieval  
- Verify sorting and filtering functionality  
- Ensure proper data persistence  
- Validate frontend and backend integration  

---

### UI Testing (TestFX)

Each test:
- Simulates user input  
- Triggers UI actions  
- Verifies scene transitions  

**Transitions tested:**
- Login → Dashboard  
- Login → Create Account  
- Create Account → Login  
- Dashboard → Workout History  
- Dashboard → Exercises  
- Dashboard → Logout  

---

### Unit Tests

#### Users
- Create, retrieve, update, and delete users  
- Prevent duplicate usernames/emails  
- Validate input fields  

#### Exercises
- Create, retrieve, update, and delete exercises  
- Validate required fields  
- Prevent duplicates  

#### Workouts
- Create workouts with valid inputs  
- Reject invalid inputs  
- Retrieve workout history  
- Ensure correct user association  

#### History / Filtering
- Retrieve full history  
- Sort by date  
- Filter by type or date  
- Handle invalid or empty results  

#### Data Persistence
- Save and load users and workouts  
- Handle corrupted or missing data  

---

## Mockups

Created using Draw.io

- Login  
- Dashboard  
- Log Workout  
- Delete Workout  
- View Exercises  
- Add Exercise  
- Workout History  
- Sort/Filter History  

![Alt text](https://i.imgur.com/SUH9SED.png)
![Alt text](https://i.imgur.com/PhAcUom.png)


---

## Getting Started

### Prerequisites

- Java (JDK 17 or higher recommended)
- JavaFX

### Run the Project

```bash
git clone https://github.com/jacha-camacho-csumb/OTTERFIT
