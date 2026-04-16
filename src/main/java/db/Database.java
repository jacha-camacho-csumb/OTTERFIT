package db;

import config.AppConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * CSUMB CS ONLINE PROGRAM -  * Creates otterfit_app.db, defines schema (users, exercises,
 * workouts), and inserts example data safely without duplicating it on repeated runs.
 *
 * @author : Jose Acha-Camacho
 * @version :  0.3.0
 * @mailto : jacha-camacho@csumb.edu
 * @created : 4/10/26, Friday
 */


public class Database {

  /**
   * JDBC connection string for the SQLite database file
   */
  private static final String DB_URL = AppConfig.DB_URL;

  /**
   * Single connection used by this class
   */
  private final Connection connection;

  public Database() throws SQLException {
    this(DB_URL);
  }

  public Database(String dbUrl) throws SQLException {
    try {
      connection = DriverManager.getConnection(dbUrl);
      System.out.println("Database connected.");
    } catch (SQLException e) {
      System.err.println("Connection failed: " + e.getMessage());
      throw e;
    }
  }

  public Database(Connection connection) {
    this.connection = connection;
  }

  /**
   * Entry point of the program. Initializes the database and prints status messages.
   */
  public static void main(String[] args) {
    try {
      Database db = new Database("jdbc:sqlite:test.db");
      db.initialize();
      System.out.println("db.Database setup complete: otterfit_app.db");
    } catch (SQLException e) {
      System.err.println("db.Database setup failed. :-(");
      e.printStackTrace();
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
   * Orchestrates database setup. Uses a transaction to ensure atomic setup.
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
   */
  private void enableForeignKeys() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON;");
    }
  }

  /**
   * Creates all required tables if they do not already exist.
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
            exercise_id INTEGER NOT NULL,
            workout_date TEXT NOT NULL,
            notes TEXT,
            duration_minutes REAL,
            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
            FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id) ON DELETE CASCADE,
            UNIQUE(user_id, exercise_id, workout_date, notes, duration_minutes)
        );
        """;

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(createUsers);
      stmt.execute(createExercises);
      stmt.execute(createWorkouts);
    }
  }

  /***********************************************************
   *                   USER                                  *
   ***********************************************************/

  /**
   * Creates indexes to improve query performance on foreign keys.
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

    String idxWorkoutsExercise = """
        CREATE INDEX IF NOT EXISTS idx_workouts_exercise_id
        ON workouts(exercise_id);
        """;

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(idxExercisesUser);
      stmt.execute(idxWorkoutsUser);
      stmt.execute(idxWorkoutsExercise);
    }
  }

  /**
   * Inserts example data into the database using upsert-style helpers.
   */
  private void seedExampleData() throws SQLException {
    int userId = upsertUser("otter", "otter@csumb.edu", "otter");

    int pushupId = upsertExercise(userId, "Push-Up", "Strength",
        "A bodyweight chest, shoulder, and triceps exercise.");

    int squatId = upsertExercise(userId, "Bodyweight Squat", "Strength",
        "A lower-body exercise focusing on quads, glutes, and hamstrings.");

    int runId = upsertExercise(userId, "Treadmill Run", "Cardio",
        "A steady-paced indoor running workout.");

    upsertWorkout(userId, pushupId, "2026-04-15",
        "Chest work", 33.0);

    upsertWorkout(userId, squatId, "2026-04-10",
        "Leg work", 45.0);

    upsertWorkout(userId, runId, "2026-04-12",
        "Cardio session", 20.0);
  }

  /**
   * Inserts a user if not already present.
   *
   * @return user_id of existing or newly created user
   */
  private int upsertUser(String username, String email, String passwordPlaintext)
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

  /***********************************************************
   *                   EXERCISE                              *
   ***********************************************************/

  /**
   * Looks up a user's ID by username.
   *
   * @return user_id or null if not found
   */
  private Integer findUserIdByUsername(String username) throws SQLException {
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
   * @return true if credentials match, false otherwise
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
   *                   WORKOUT                               *
   ***********************************************************/

  /**
   * Inserts an exercise if it does not already exist for the user.
   *
   * @return exercise_id
   */
  private int upsertExercise(int userId, String name, String category, String description)
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
  private Integer findExerciseId(int userId, String name) throws SQLException {
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

  /**
   * Inserts a workout log if it does not already exist.
   *
   * @return workout_id
   */
  private int upsertWorkout(int userId, int exerciseId, String workoutDate, String notes,
      Double durationMinutes) throws SQLException {

    Integer existingId = findWorkoutId(userId, exerciseId, workoutDate, notes, durationMinutes);
    if (existingId != null) {
      return existingId;
    }

    String sql = """
        INSERT INTO workouts (user_id, exercise_id, workout_date, notes, duration_minutes)
        VALUES (?, ?, ?, ?, ?);
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, userId);
      ps.setInt(2, exerciseId);
      ps.setString(3, workoutDate);
      ps.setString(4, notes);
      setDoubleOrNull(ps, 5, durationMinutes);
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
   * Finds a workout by user, exercise, date, notes, and duration.
   */
  private Integer findWorkoutId(int userId, int exerciseId, String workoutDate, String notes,
      Double durationMinutes) throws SQLException {

    String sql = """
        SELECT workout_id FROM workouts
        WHERE user_id = ?
          AND exercise_id = ?
          AND workout_date = ?
          AND ((notes = ?) OR (notes IS NULL AND ? IS NULL))
          AND ((duration_minutes = ?) OR (duration_minutes IS NULL AND ? IS NULL));
        """;

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setInt(2, exerciseId);
      ps.setString(3, workoutDate);
      ps.setString(4, notes);
      ps.setString(5, notes);
      setDoubleOrNull(ps, 6, durationMinutes);
      setDoubleOrNull(ps, 7, durationMinutes);

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return rs.getInt("workout_id");
        }
      }
    }
    return null;
  }

  /***********************************************************
   *                   utilities                             *
   ***********************************************************/

  /**
   * Utility method to dump all tables to a string for debug display.
   */
  public String dumpAllTables() {
    StringBuilder sb = new StringBuilder();

    String[] tables = {"users", "exercises", "workouts"};

    for (String table : tables) {
      sb.append("=== ").append(table).append(" ===\n");

      try (Statement stmt = connection.createStatement();
          ResultSet rs = stmt.executeQuery("SELECT * FROM " + table)) {

        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        for (int i = 1; i <= colCount; i++) {
          sb.append(meta.getColumnName(i)).append("\t");
        }
        sb.append("\n");

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

  /**
   * Returns recent workouts logged by a signed-in user.
   */
  public java.util.List<String> getWorkoutHistory(String username) throws SQLException {
    java.util.List<String> historyList = new java.util.ArrayList<>();

    String sql = """
        SELECT w.workout_date,
               w.notes,
               w.duration_minutes,
               e.name AS exercise_name
        FROM users u
        JOIN workouts w ON u.user_id = w.user_id
        JOIN exercises e ON w.exercise_id = e.exercise_id
        WHERE u.username = ?
        ORDER BY w.workout_date DESC, e.name
        """;

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, username);

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          String workoutDate = resultSet.getString("workout_date");
          String workoutNotes = resultSet.getString("notes");
          String exerciseName = resultSet.getString("exercise_name");

          Double durationMinutes = resultSet.getObject("duration_minutes") != null
              ? resultSet.getDouble("duration_minutes") : null;

          StringBuilder entryBuilder = new StringBuilder();
          entryBuilder.append(workoutDate)
              .append(" - ")
              .append(exerciseName);

          if (durationMinutes != null) {
            entryBuilder.append(" | ").append(durationMinutes).append(" min");
          }

          if (workoutNotes != null && !workoutNotes.isBlank()) {
            entryBuilder.append(" | ").append(workoutNotes);
          }

          historyList.add(entryBuilder.toString());
        }
      }
    }

    if (historyList.isEmpty()) {
      historyList.add("No workout history found for " + username);
    }

    return historyList;
  }
}