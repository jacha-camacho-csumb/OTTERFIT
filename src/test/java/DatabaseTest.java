import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import db.Database;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CSUMB CS ONLINE PROGRAM - db.Database Tests
 *
 * Provides integration tests for the SQLite database used in the OtterFit app.
 * Tests cover database creation, schema validation, seed data, and CRUD
 * operations for all tables: users, exercises, and workouts.
 *
 * Updated to match the simplified schema where workout_entries has been removed
 * and workouts now directly reference exercises.
 *
 * @author : Jose Acha-Camacho
 * @version : 0.3.0
 * @created : 4/12/26, Sunday
 */
class DatabaseTest {

  /**
   * JDBC connection string for the SQLite database.
   * NOTE: Use test database to avoid stepping on app database.
   */
  private static final String DB_NAME = "test.db";
  private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
  private static final String TEMP_DB = "jdbc:sqlite::memory:";

  private Database db;

  /**
   * Runs before each test. Deletes any existing database file and recreates it
   * to ensure a clean state.
   */
  @BeforeEach
  void setup() {
    File dbFile = new File(DB_NAME);
    if (dbFile.exists()) {
      dbFile.delete();
    }

    try {
      db = new Database(DB_URL);
      db.initialize();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  // ---------- Basic Setup Tests ----------

  /**
   * Verifies that the database file is created successfully.
   */
  @Test
  void testDatabaseCreation() {
    File dbFile = new File(DB_NAME);
    assertTrue(dbFile.exists(), "Database file should be created");
  }

  /**
   * Verifies that all required tables exist in the database.
   */
  @Test
  void testTablesExist() throws SQLException {
    try (Connection conn = getConnection()) {
      assertTrue(tableExists(conn, "users"));
      assertTrue(tableExists(conn, "exercises"));
      assertTrue(tableExists(conn, "workouts"));
    }
  }

  /**
   * Verifies that seed data is inserted correctly into all tables.
   */
  @Test
  void testSeedDataInserted() throws SQLException {
    try (Connection conn = DriverManager.getConnection(TEMP_DB)) {
      Database dbTemp = new Database(conn);
      dbTemp.initialize();

      assertEquals(1, countRows(conn, "users"));
      assertEquals(3, countRows(conn, "exercises"));
      assertEquals(3, countRows(conn, "workouts"));
    }
  }

  /**
   * Ensures that running the database setup multiple times does not create duplicate records.
   */
  @Test
  void testIdempotentSeeding() throws SQLException {
    try (Connection conn = DriverManager.getConnection(TEMP_DB)) {
      Database dbTemp = new Database(conn);
      dbTemp.initialize();
      dbTemp.initialize();

      assertEquals(1, countRows(conn, "users"));
      assertEquals(3, countRows(conn, "exercises"));
      assertEquals(3, countRows(conn, "workouts"));
    }
  }

  // ---------- CRUD Tests: USERS ----------

  /**
   * Tests inserting a new user into the users table.
   */
  @Test
  void testCreateUser() throws SQLException {
    try (Connection conn = getConnection()) {
      String sql = "INSERT INTO users (username, email, password_plaintext) VALUES (?, ?, ?)";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "testuser");
        ps.setString(2, "test@csumb.edu");
        ps.setString(3, "password");
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      assertEquals(2, countRows(conn, "users"));
    }
  }

  /**
   * Tests reading a user from the users table.
   */
  @Test
  void testReadUser() throws SQLException {
    try (Connection conn = getConnection()) {
      String sql = "SELECT * FROM users WHERE username = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "otter");
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next());
        assertEquals("otter@csumb.edu", rs.getString("email"));
      }
    }
  }

  /**
   * Tests updating a user in the users table.
   */
  @Test
  void testUpdateUser() throws SQLException {
    try (Connection conn = getConnection()) {
      String sql = "UPDATE users SET email = ? WHERE username = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "updated@csumb.edu");
        ps.setString(2, "otter");
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      try (PreparedStatement ps = conn.prepareStatement(
          "SELECT email FROM users WHERE username = ?")) {
        ps.setString(1, "otter");
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next());
        assertEquals("updated@csumb.edu", rs.getString("email"));
      }
    }
  }

  /**
   * Tests inserting a new user into the users table.
   */
  @Test
  void testUpsertUser() throws SQLException {
    try (Database db = getTempDatabase()) {
      String username = "fred";
      String email1 = "fflintstone@bedrock.com";
      String password = "bambam";
       /**
       * create a user Fred
       */
      assertTrue(db.createUser(username, email1, password));
      User fred = db.readUser(username);
      assertEquals(username, fred.username);
      assertEquals(email1, fred.email);
      assertTrue(db.validateUser(username, password));
    }
  }
  /**
   * Tests changing a user email address
   */
  @Test
  void testUpdateUserEmail() throws SQLException {
    try (Database db = getTempDatabase()) {
      String username = "fred";
      String email1 = "fflintstone@bedrock.com";
      String password = "bambam";
      String email2 = "fflintstone2@bedrock.com";
      /**
       * create a user Fred
       */
      assertTrue(db.createUser(username, email1, password));
      User fred = db.readUser(username);
      assertEquals(email1, fred.email);

      /**
       * update Fred's email address
       */
      int user_id = fred.userId;
      db.updateUserEmail(user_id, email2);
      fred = db.readUser(username);
      assertEquals(username, fred.username);
      assertEquals(email2, fred.email);
    }
  }

  /**
   * Tests changing a user password
   */
  @Test
  void testUpdateUserPassword() throws SQLException {
    try (Database db = getTempDatabase()) {
      String username = "fred";
      String email1 = "fflintstone@bedrock.com";
      String password1 = "bambam";
      String password2 = "bambam2";
      /**
       * create a user Fred
       */
      assertTrue(db.createUser(username, email1, password1));
      User fred = db.readUser(username);
      assertTrue(db.validateUser(username, password1));

      /**
       * Update Fred's password
       */
      assertTrue(db.updateUserPassword(fred, password1, password2));
      assertTrue(db.validateUser(username, password2));

    }
  }

  /**
   * Tests deleting a user from the users table.
   */
  @Test
  void testDeleteUser() throws SQLException {
    try (Connection conn = getConnection()) {
      int startCount = countRows(conn, "users");

      String sql = "DELETE FROM users WHERE username = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "otter");
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      assertEquals(startCount - 1, countRows(conn, "users"));
    }
  }

  // ---------- CRUD Tests: EXERCISES ----------

  /**
   * Tests inserting a new exercise into the exercises table.
   */
  @Test
  void testCreateExercise() throws SQLException {
    try (Connection conn = getConnection()) {
      int userId = getUserIdByUsername(conn, "otter");

      String sql = "INSERT INTO exercises (user_id, name, category, description) VALUES (?, ?, ?, ?)";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setString(2, "Plank");
        ps.setString(3, "Core");
        ps.setString(4, "Core stability exercise.");
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      assertEquals(4, countRows(conn, "exercises"));
    }
  }

  /**
   * Tests reading an exercise from the exercises table.
   */
  @Test
  void testReadExercise() throws SQLException {
    try (Connection conn = getConnection()) {
      String sql = "SELECT * FROM exercises WHERE name = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "Push-Up");
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next());
        assertEquals("Strength", rs.getString("category"));
      }
    }
  }

  /**
   * Tests updating an exercise in the exercises table.
   */
  @Test
  void testUpdateExercise() throws SQLException {
    try (Connection conn = getConnection()) {
      String sql = "UPDATE exercises SET category = ? WHERE name = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "Upper Body");
        ps.setString(2, "Push-Up");
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      try (PreparedStatement ps = conn.prepareStatement(
          "SELECT category FROM exercises WHERE name = ?")) {
        ps.setString(1, "Push-Up");
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next());
        assertEquals("Upper Body", rs.getString("category"));
      }
    }
  }

  /**
   * Tests deleting an exercise from the exercises table.
   */
  @Test
  void testDeleteExercise() throws SQLException {
    try (Connection conn = getConnection()) {
      String sql = "DELETE FROM exercises WHERE name = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "Push-Up");
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      assertEquals(2, countRows(conn, "exercises"));
    }
  }

  // ---------- CRUD Tests: WORKOUTS ----------

  /**
   * Tests inserting a new workout into the workouts table.
   */
  @Test
  void testCreateWorkout() throws SQLException {
    try (Connection conn = getConnection()) {
      int userId = getUserIdByUsername(conn, "otter");
      int exerciseId = getExerciseIdByName(conn, "Push-Up");

      String sql = """
          INSERT INTO workouts (user_id, exercise_id, workout_date, notes, duration_minutes)
          VALUES (?, ?, ?, ?, ?)
          """;

      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, exerciseId);
        ps.setString(3, "2026-04-13");
        ps.setString(4, "Upper body workout.");
        ps.setNull(5, Types.REAL);
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      assertEquals(4, countRows(conn, "workouts"));
    }
  }

  /**
   * Tests reading a workout from the workouts table.
   */
  @Test
  void testReadWorkout() throws SQLException {
    String exerciseName = "Push-Up";
    String userName = "otter";
    String workoutDate = "2026-04-13";
    String notes = "Upper body workout.";
    Double duration = 5.0;

    db.initialize();

    try (Connection conn = getConnection()) {
      /**
       * And then reestablish the record that will be deleted
       */
      String sql = """
          INSERT INTO workouts (user_id, exercise_id, workout_date, notes, duration_minutes)
          VALUES (?, ?, ?, ?, ?)
          """;
      int userId = getUserIdByUsername(conn, userName);
      int exerciseId = getExerciseIdByName(conn, exerciseName);

      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, exerciseId);
        ps.setString(3, workoutDate);
        ps.setString(4, notes);
        ps.setNull(5, Types.REAL);
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      /**
       * Now we can test reading the row
       */

      sql = """
          SELECT w.*, e.name AS exercise_name
          FROM workouts w
          JOIN exercises e ON w.exercise_id = e.exercise_id
          WHERE w.workout_date = ? AND e.name = ?
          """;

      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, workoutDate);
        ps.setString(2, exerciseName);
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next());
        assertEquals(notes, rs.getString("notes"));
      }
    }
  }

  /**
   * Tests updating a workout in the workouts table.
   */
  @Test
  void testUpdateWorkout() throws SQLException {
    String exerciseName = "Push-Up";
    String userName = "otter";
    String workoutDate = "2026-04-13";
    String notes = "Upper body workout.";
    Double duration = 5.0;

    db.initialize();

    try (Connection conn = getConnection()) {
      int userId = getUserIdByUsername(conn, userName);
      int exerciseId = getExerciseIdByName(conn, exerciseName);

      /**
       * Now we can test updating the row
       */

      String sql = "UPDATE workouts SET notes = ? WHERE workout_date = ? AND exercise_id = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "Updated workout notes.");
        ps.setString(2, workoutDate);
        ps.setInt(3, exerciseId);
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      try (PreparedStatement ps = conn.prepareStatement(
          "SELECT notes FROM workouts WHERE workout_date = ? AND exercise_id = ?")) {
        ps.setString(1, workoutDate);
        ps.setInt(2, exerciseId);
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next());
        assertEquals("Updated workout notes.", rs.getString("notes"));
      }
    }
  }

  /**
   * Tests deleting a workout from the workouts table.
   */
  @Test
  void testDeleteWorkout() throws SQLException {
    /**
     * Need to establish a deterministic state before testing
     * The current state of the DB is unknown and order of tests
     * is unknown. To be immutable each test must establish
     * baseline context. One way to do that is make a new database
     * and initialize() at the start of each test.
     */
    String exerciseName = "Push-Up";
    String userName = "otter";
    String workoutDate = "2026-04-13";
    String notes = "Upper body workout.";
    Double duration = 5.0;

    db.initialize();

    try (Connection conn = getConnection()) {
      /**
       * And then reestablish the record that will be deleted
       */
      String sql = """
          INSERT INTO workouts (user_id, exercise_id, workout_date, notes, duration_minutes)
          VALUES (?, ?, ?, ?, ?)
          """;
      int userId = getUserIdByUsername(conn, userName);
      int exerciseId = getExerciseIdByName(conn, exerciseName);

      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, exerciseId);
        ps.setString(3, workoutDate);
        ps.setString(4, notes);
        ps.setNull(5, Types.REAL);
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      /**
       * Now we can test deleting the row
       */

      int startCount = countRows(conn, "workouts");

      sql = "DELETE FROM workouts WHERE workout_date = ? AND exercise_id = ?";
      try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, workoutDate);
        ps.setInt(2, exerciseId);
        int rows = ps.executeUpdate();
        assertEquals(1, rows);
      }

      assertEquals(startCount - 1, countRows(conn, "workouts"));
    }
  }

  // ---------- Relationship Tests ----------

  /**
   * Verifies that deleting a user cascades and removes all related records due to foreign key
   * constraints.
   */
  @Test
  void testCascadeDeleteUser() throws SQLException {
    try (Connection conn = getConnection()) {
      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM users WHERE username = ?")) {
        ps.setString(1, "otter");
        ps.executeUpdate();
      }

      assertEquals(0, countRows(conn, "users"));
      assertEquals(0, countRows(conn, "exercises"));
      assertEquals(0, countRows(conn, "workouts"));
    }
  }

  /**
   * Verifies that deleting an exercise removes dependent workouts.
   */
  @Test
  void testCascadeDeleteExercise() throws SQLException {
    try (Connection conn = getConnection()) {
      int exerciseId = getExerciseIdByName(conn, "Push-Up");

      try (PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM exercises WHERE exercise_id = ?")) {
        ps.setInt(1, exerciseId);
        ps.executeUpdate();
      }

      assertEquals(2, countRows(conn, "exercises"));
      assertEquals(2, countRows(conn, "workouts"));
    }
  }

  // ---------- Helper Methods ----------

  /**
   * Creates a database connection and enables foreign key enforcement.
   *
   * @return active database connection
   */
  private Connection getConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(DB_URL);
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON;");
    }
    return conn;
  }
  /**
   * Creates an in memory database connection and enables foreign key enforcement.
   *  As a new-new database, also must run initialize to ensure records exist
   *  Use case for this method is any database transaction test to ensure
   *    baseline database as a starting point, keep all updates isolated, ensure
   *    consistent expected results.
   *
   * @return active database connection
   */
  private Database getTempDatabase() throws SQLException {
    Connection conn = DriverManager.getConnection(TEMP_DB);
    try (Statement stmt = conn.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON;");
    }

    Database dbTemp = new Database(conn);
    dbTemp.initialize();

    return dbTemp;
  }

  /**
   * Checks whether a table exists in the database.
   *
   * @param conn active database connection
   * @param tableName name of the table to check
   * @return true if the table exists, false otherwise
   */
  private boolean tableExists(Connection conn, String tableName) throws SQLException {
    String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, tableName);
      ResultSet rs = ps.executeQuery();
      return rs.next();
    }
  }

  /**
   * Counts the number of rows in a given table.
   *
   * @param conn active database connection
   * @param table table name
   * @return number of rows in the table
   */
  private int countRows(Connection conn, String table) throws SQLException {
    try (Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table)) {
      return rs.getInt(1);
    }
  }

  /**
   * Finds a user ID by username.
   *
   * @param conn active database connection
   * @param username username to search for
   * @return matching user_id
   */
  private int getUserIdByUsername(Connection conn, String username) throws SQLException {
    String sql = "SELECT user_id FROM users WHERE username = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      assertTrue(rs.next(), "Expected user not found: " + username);
      return rs.getInt("user_id");
    }
  }

  /**
   * Finds an exercise ID by exercise name.
   *
   * @param conn active database connection
   * @param name exercise name
   * @return matching exercise_id
   */
  private int getExerciseIdByName(Connection conn, String name) throws SQLException {
    String sql = "SELECT exercise_id FROM exercises WHERE name = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, name);
      ResultSet rs = ps.executeQuery();
      assertTrue(rs.next(), "Expected exercise not found: " + name);
      return rs.getInt("exercise_id");
    }
  }
}