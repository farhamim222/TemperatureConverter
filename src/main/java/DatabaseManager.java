import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL =
            "jdbc:mysql://localhost:3307/temperature_db";

    private static final String USER = "root";

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                URL, USER, PASSWORD
        );
    }

    public static void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS conversions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    celsius DOUBLE NOT NULL,
                    fahrenheit DOUBLE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public static void saveConversion(
            double celsius, double fahrenheit
    ) throws SQLException {

        String sql = """
                INSERT INTO conversions (celsius, fahrenheit)
                VALUES (?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setDouble(1, celsius);
            stmt.setDouble(2, fahrenheit);
            stmt.executeUpdate();
        }
    }
}