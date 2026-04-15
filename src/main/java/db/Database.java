package db;

import config.AppConfig;

import java.sql.*;

/**
 * CSUMB CS ONLINE PROGRAM - Initializes and seeds a SQLite database for OtterFit fitness app.
 *
 * @author : Jose Acha-Camacho
 * @version :  0.1.0
 * @mailto : jacha-camacho@csumb.edu
 * @created : 4/10/26, Friday
 */

/**
 * db.Database.java Creates otterfit_app.db, defines schema (users, exercises, workouts,
 * workout_entries), and inserts example data safely without duplicating it on repeated runs.
 * Requires SQLite JDBC driver on the classpath.
 */

public class Database {

  /**
   * JDBC connection string for the SQLite database file
   */
  private static final String DB_URL = AppConfig.DB_URL;

  // Use a single connection for the entire class
  private final Connection connection;

  /**
   * Entry point of the program. Initializes the database and prints status messages.
   */
  public static void main(String[] args) {
    try {
//      Database db = new Database(DB_URL);
      Database db = new Database("jdbc:sqlite:test.db");
      db.initialize();
//      createDatabaseAndSeedData(); // renamed to initialize
      System.out.println("db.Database setup complete: otterfit_app.db");
    } catch (SQLException e) {
      System.err.println("db.Database setup failed. :-(");
      e.printStackTrace();
    }
  }

  public Database() throws SQLException {
    this(DB_URL);
  }

  public Database(String dbUrl) throws SQLException {
    try {
      // Opens (or creates) app.db in the project root
      connection = DriverManager.getConnection(dbUrl);
      System.out.println("Database connected.");
    } catch (SQLException e) {
      System.err.println("Connection failed: " +
              e.getMessage());
      throw e;
    }
  }

  public Database(Connection connection) {
    this.connection = connection;
  }

  /**
   * Orchestrates database setup. Uses a transaction to ensure atomic
   * setup (rollback on failure).
   *
   */
  public void initialize() throws SQLException {
    connection.setAutoCommit(false);

    try {
        enableForeignKeys();
        createTables();
        createIndexes();
        seedExampleData();
        connection.commit();
      } catch (SQLException e) {
        connection.rollback();
        throw e;
      }
    }

  /**
   * Enables enforcement of foreign key constraints in SQLite.
   *
   * @throws SQLException if execution fails
   */
  private void enableForeignKeys() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON;");
    }
  }

  /**
   * Creates all required tables if they do not already exist.
   *
   * @throws SQLException if table creation fails
   */
  private void createTables() throws SQLException {
    String createUsers = """
        CREATE TABLE IF NOT EXISTS users (
            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            email TEXT NOT NULL UNIQUE,
            password_plaintext TEXT NOT NULL
        );
        """;

    String createExercises = """
        CREATE TABLE IF NOT EXISTS exercises (
            exercise_id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            name TEXT NOT NULL,
            category TEXT,
            description TEXT,
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
            UNIQUE(user_id, name)
        );
        """;

    String createWorkouts = """
        CREATE TABLE IF NOT EXISTS workouts (
            workout_id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            workout_date TEXT NOT NULL,
            notes TEXT,
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
            UNIQUE(user_id, workout_date, notes)
        );
        """;

    String createWorkoutEntries = """
        CREATE TABLE IF NOT EXISTS workout_entries (
            entry_id INTEGER PRIMARY KEY AUTOINCREMENT,
            workout_id INTEGER NOT NULL,
            exercise_id INTEGER NOT NULL,
            sets INTEGER,
            reps INTEGER,
            weight REAL,
            duration_minutes REAL,
            distance REAL,
            FOREIGN KEY (workout_id) REFERENCES workouts(workout_id) ON DELETE CASCADE,
            FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id) ON DELETE CASCADE,
            UNIQUE (
                workout_id,
                exercise_id,
                sets,
                reps,
                weight,
                duration_minutes,
                distance
            )
        );
        """;

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(createUsers);
      stmt.execute(createExercises);
      stmt.execute(createWorkouts);
      stmt.execute(createWorkoutEntries);
    }
  }

  /**
   * Creates indexes to improve query performance on foreign keys.
   *
   * @throws SQLException if index creation fails
   */
  private void createIndexes() throws SQLException {
    String idxExercisesUser = """
        CREATE INDEX IF NOT EXISTS idx_exercises_user_id
        ON exercises(user_id);
        """;

    String idxWorkoutsUser = """
        CREATE INDEX IF NOT EXISTS idx_workouts_user_id
        ON workouts(user_id);
        """;

    String idxEntriesWorkout = """
        CREATE INDEX IF NOT EXISTS idx_workout_entries_workout_id
        ON workout_entries(workout_id);
        """;

    String idxEntriesExercise = """
        CREATE INDEX IF NOT EXISTS idx_workout_entries_exercise_id
        ON workout_entries(exercise_id);
        """;

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(idxExercisesUser);
      stmt.execute(idxWorkoutsUser);
      stmt.execute(idxEntriesWorkout);
      stmt.execute(idxEntriesExercise);
    }
  }

  /**
   * Inserts example data into the database. Uses "upsert"-style helpers to avoid duplicate
   * records.
   *
   * @throws SQLException if insertion fails
   */
  private void seedExampleData() throws SQLException {
    int userId = upsertUser("otter", "otter@csumb.edu", "otter");

    int pushupId = upsertExercise(userId, "Push-Up", "Strength",
        "A bodyweight chest, shoulder, and triceps exercise.");

    int squatId = upsertExercise(userId, "Bodyweight Squat", "Strength",
        "A lower-body exercise focusing on quads, glutes, and hamstrings.");

    int runId = upsertExercise(userId, "Treadmill Run", "Cardio",
        "A steady-paced indoor running workout.");

    int workoutId = upsertWorkout(userId, "2026-04-12",
        "Full body starter workout.");

    upsertWorkoutEntry(workoutId, pushupId, 3, 12, 0.0, null, null);
    upsertWorkoutEntry(workoutId, squatId, 3, 15, 0.0, null, null);
    upsertWorkoutEntry(workoutId, runId, null, null, null, 20.0, 2.5);
  }
  /***********************************************************
   *                   USER                                  *
   ***********************************************************/
  /**
   * Inserts a user if not already present.
   *
   * @return user_id of existing or newly created user
   */
  private int upsertUser(String username, String email,
                                String passwordPlaintext)
          throws SQLException {

    Integer existingId = findUserIdByUsername(username);
    if (existingId != null) {
      return existingId;
    }

    String sql = "INSERT INTO users (username, email, password_plaintext) VALUES (?, ?, ?);";

    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, username);
      ps.setString(2, email);
      ps.setString(3, passwordPlaintext);
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    }

    throw new SQLException("Failed to insert or retrieve user.");
  }

  /**
   * Looks up a user's ID by username.
   *
   * @return user_id or null if not found
   */
  private Integer findUserIdByUsername(String username)
          throws SQLException {
    String sql = "SELECT user_id FROM users WHERE username = ?;";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, username);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("user_id");
        }
      }
    }
    return null;
  }

  /**
   * Validates the username and password.
   *
   * @return boolean
   */
  public boolean validateUser(String username, String password) throws SQLException {
    String sql = "SELECT 1 FROM users WHERE username = ? AND password_plaintext = ?";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, username);
      ps.setString(2, password);

      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }
  /***********************************************************
   *                   EXERCISE                              *
   ***********************************************************/
  /**
   * Inserts an exercise if it does not already exist for the user.
   *
   * @return exercise_id
   */
  private int upsertExercise(int userId, String name, String category,
                                    String description)
          throws SQLException {

    Integer existingId = findExerciseId(userId, name);
    if (existingId != null) {
      return existingId;
    }

    String sql = "INSERT INTO exercises (user_id, name, category, description) VALUES (?, ?, ?, ?);";

    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, userId);
      ps.setString(2, name);
      ps.setString(3, category);
      ps.setString(4, description);
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    }

    throw new SQLException("Failed to insert or retrieve exercise: " + name);
  }

  /**
   * Finds an exercise ID for a user by name.
   */
  private Integer findExerciseId(int userId, String name)
          throws SQLException {
    String sql = "SELECT exercise_id FROM exercises WHERE user_id = ? AND name = ?;";

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setString(2, name);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("exercise_id");
        }
      }
    }
    return null;
  }
  /***********************************************************
   *                   WORKOUT                               *
   ***********************************************************/
  /**
   * Inserts a workout if it does not already exist.
   *
   * @return workout_id
   */
  private int upsertWorkout(int userId, String workoutDate, String notes)
          throws SQLException {

    Integer existingId = findWorkoutId(userId, workoutDate, notes);
    if (existingId != null) {
      return existingId;
    }

    String sql = "INSERT INTO workouts (user_id, workout_date, notes) VALUES (?, ?, ?);";

    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, userId);
      ps.setString(2, workoutDate);
      ps.setString(3, notes);
      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    }

    throw new SQLException("Failed to insert or retrieve workout.");
  }

  /**
   * Finds a workout by user, date, and notes.
   */
  private Integer findWorkoutId(int userId, String workoutDate,
                                       String notes)
          throws SQLException {

    String sql = """
        SELECT workout_id FROM workouts
        WHERE user_id = ?
          AND workout_date = ?
          AND ((notes = ?) OR (notes IS NULL AND ? IS NULL));
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setString(2, workoutDate);
      ps.setString(3, notes);
      ps.setString(4, notes);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("workout_id");
        }
      }
    }
    return null;
  }

  /**
   * Inserts a workout entry if it does not already exist.
   *
   * @return entry_id
   */
  private int upsertWorkoutEntry(
          int workoutId,
          int exerciseId,
          Integer sets,
          Integer reps,
          Double weight,
          Double durationMinutes,
          Double distance
  ) throws SQLException {

    Integer existingId = findWorkoutEntryId(
            workoutId, exerciseId, sets, reps, weight, durationMinutes, distance
    );

    if (existingId != null) {
      return existingId;
    }

    String sql = """
        INSERT INTO workout_entries (
            workout_id, exercise_id, sets, reps, weight, duration_minutes, distance
        ) VALUES (?, ?, ?, ?, ?, ?, ?);
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, workoutId);
      ps.setInt(2, exerciseId);
      setIntegerOrNull(ps, 3, sets);
      setIntegerOrNull(ps, 4, reps);
      setDoubleOrNull(ps, 5, weight);
      setDoubleOrNull(ps, 6, durationMinutes);
      setDoubleOrNull(ps, 7, distance);

      ps.executeUpdate();

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      }
    }

    throw new SQLException("Failed to insert or retrieve workout entry.");
  }

  /**
   * Finds an existing workout entry with matching values.
   */
  private Integer findWorkoutEntryId(
          int workoutId,
          int exerciseId,
          Integer sets,
          Integer reps,
          Double weight,
          Double durationMinutes,
          Double distance
  ) throws SQLException {

    String sql = """
        SELECT entry_id FROM workout_entries
        WHERE workout_id = ?
          AND exercise_id = ?
          AND ((sets = ?) OR (sets IS NULL AND ? IS NULL))
          AND ((reps = ?) OR (reps IS NULL AND ? IS NULL))
          AND ((weight = ?) OR (weight IS NULL AND ? IS NULL))
          AND ((duration_minutes = ?) OR (duration_minutes IS NULL AND ? IS NULL))
          AND ((distance = ?) OR (distance IS NULL AND ? IS NULL));
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setInt(1, workoutId);
      ps.setInt(2, exerciseId);

      setIntegerOrNull(ps, 3, sets);
      setIntegerOrNull(ps, 4, sets);
      setIntegerOrNull(ps, 5, reps);
      setIntegerOrNull(ps, 6, reps);
      setDoubleOrNull(ps, 7, weight);
      setDoubleOrNull(ps, 8, weight);
      setDoubleOrNull(ps, 9, durationMinutes);
      setDoubleOrNull(ps, 10, durationMinutes);
      setDoubleOrNull(ps, 11, distance);
      setDoubleOrNull(ps, 12, distance);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("entry_id");
        }
      }
    }
    return null;
  }
  /***********************************************************
   *                   utilities                             *
   ***********************************************************/
  /**
   * Utility method to safely set Integer values.
   */
  private static void setIntegerOrNull(PreparedStatement ps, int index, Integer value)
          throws SQLException {
    if (value == null) {
      ps.setNull(index, java.sql.Types.INTEGER);
    } else {
      ps.setInt(index, value);
    }
  }

  /**
   * Utility method to safely set Double values.
   */
  private static void setDoubleOrNull(PreparedStatement ps, int index, Double value)
          throws SQLException {
    if (value == null) {
      ps.setNull(index, java.sql.Types.REAL);
    } else {
      ps.setDouble(index, value);
    }
  }
  /**
   * Utility method to dump all tables to a string for debug display
   */
  public String dumpAllTables() {
    StringBuilder sb = new StringBuilder();

    String[] tables = {"users", "exercises", "workouts", "workout_entries"};

    for (String table : tables) {
      sb.append("=== ").append(table).append(" ===\n");

      try (Statement stmt = connection.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        // Column headers
        for (int i = 1; i <= colCount; i++) {
          sb.append(meta.getColumnName(i)).append("\t");
        }
        sb.append("\n");

        // Rows
        while (rs.next()) {
          for (int i = 1; i <= colCount; i++) {
            sb.append(rs.getString(i)).append("\t");
          }
          sb.append("\n");
        }

      } catch (SQLException e) {
        sb.append("Error reading table\n");
      }

      sb.append("\n");
    }

    return sb.toString();
  }
}