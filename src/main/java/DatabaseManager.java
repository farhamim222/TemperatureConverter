import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseManager {

    private static final String URL =
            "jdbc:mariadb://" + System.getenv().getOrDefault("DB_HOST", "127.0.0.1") + ":3308/temperature_db";

    private static final String USER = "tempapp";

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "DB_PASSWORD is missing from IntelliJ environment variables"
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
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
            double celsius,
            double fahrenheit
    ) throws SQLException {

        String sql = """
                INSERT INTO conversions (celsius, fahrenheit)
                VALUES (?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, celsius);
            stmt.setDouble(2, fahrenheit);
            stmt.executeUpdate();
        }
    }

    public static List<String> getLastFiveConversions()
            throws SQLException {

        List<String> history = new ArrayList<>();

        String sql = """
                SELECT celsius, fahrenheit, created_at
                FROM conversions
                ORDER BY created_at DESC, id DESC
                LIMIT 5
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                double celsius = rs.getDouble("celsius");
                double fahrenheit = rs.getDouble("fahrenheit");

                String entry = String.format(
                        Locale.US,
                        "%.2f °C → %.2f °F",
                        celsius,
                        fahrenheit
                );

                history.add(entry);
            }
        }

        return history;
    }

    public static String getUnitName(
            String unitKey,
            String language
    ) throws SQLException {

        return getLocalizedValue(unitKey, language, "name");
    }

    public static String getUnitDescription(
            String unitKey,
            String language
    ) throws SQLException {

        return getLocalizedValue(unitKey, language, "description");
    }

    private static String getLocalizedValue(
            String unitKey,
            String language,
            String column
    ) throws SQLException {

        String sql = """
                SELECT ut.name, ut.description
                FROM unit_translations ut
                JOIN units u ON ut.unit_id = u.id
                WHERE u.unit_key = ?
                  AND ut.lang = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, unitKey);
            stmt.setString(2, language);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(column);
                }
            }
        }

        if (!language.equals("en")) {
            return getLocalizedValue(unitKey, "en", column);
        }

        return unitKey;
    }
}

