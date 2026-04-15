package config;

/**
 * AppConfig
 * <p>
 * Description:
 * Central location for application-wide configuration values such as
 * database connection strings and other shared settings. Provides a
 * single source of truth to avoid duplication across the application.
 *
 * @author rcwav
 * @since 4/13/2026
 */
public class AppConfig {
    public static final String DB_URL = "jdbc:sqlite:otterfit_app.db";
}