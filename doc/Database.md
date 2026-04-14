## Database Usage Guide

### Overview

The `db.Database` class is responsible for:

* Creating the SQLite database
* Defining all tables and indexes
* Seeding initial data (idempotent – safe to run multiple times)

It manages a **single database connection per instance** and uses transactions to ensure safe initialization.

---

## 🚀 Quick Start

### Run from `main`

You can run the database setup directly:

```java
public static void main(String[] args) {
    Database db = new Database("jdbc:sqlite:test.db");
    db.initialize();
}
```

This will:

* Create the database file if it does not exist
* Create all tables
* Insert seed data (without duplicates)

---

## 🧱 Creating a Database Instance

### Option 1: Default config

```java
Database db = new Database();
```

Uses `AppConfig.DB_URL`.

---

### Option 2: Custom database file

```java
Database db = new Database("jdbc:sqlite:mydb.db");
```

---

### Option 3: Existing connection (used for testing)

```java
Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
Database db = new Database(conn);
```

⚠️ Important:

* When using in-memory databases (`:memory:`), **you must reuse the same connection**.
* Each new connection creates a separate database.

---

## ⚙️ Initialization

```java
db.initialize();
```

This method:

* Disables auto-commit
* Enables foreign keys
* Creates tables and indexes
* Seeds initial data
* Commits the transaction (or rolls back on failure)

---

## 🧪 Testing Guidelines

### Use an in-memory database

```java
Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
Database db = new Database(conn);
db.initialize();
```

### Key rules

* Each test should use a **fresh database**
* Do NOT rely on test execution order
* Use the **same connection** for:

    * initialization
    * queries/assertions

---

## 📊 Seed Data

On initialization, the following are inserted (if not already present):

* 1 user (`otter`)
* 3 exercises
* 1 workout
* 3 workout entries

This is implemented using "upsert-style" logic to prevent duplicates.

---

## ⚠️ Common Pitfalls

### ❌ Multiple connections with `:memory:`

```java
// WRONG (two separate databases)
Database db = new Database("jdbc:sqlite::memory:");
Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
```

### ✅ Correct

```java
Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
Database db = new Database(conn);
```

---

### ❌ Forgetting to initialize

If you skip `db.initialize()`, tables will not exist.

---

### ❌ Manual commits inside helper methods

All transaction control is handled inside `initialize()`. Do not add commits elsewhere.

---

## 🧠 Design Notes

* One `Database` instance = one connection
* Methods operate on shared connection state
* Initialization is atomic (all-or-nothing)
* Seeding is safe to run multiple times

---

## ✅ Summary

Typical usage:

```java
Database db = new Database();
db.initialize();
```

For tests:

```java
Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
Database db = new Database(conn);
db.initialize();
```

---

If anything breaks, check:

1. Connection string (`jdbc:sqlite:...`)
2. That `initialize()` was called
3. That you're not accidentally using multiple connections
