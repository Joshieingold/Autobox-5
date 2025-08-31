package josh.lynch.autobox5;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:app_data.db"; // file-based DB

    // Get connection
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static String selectAll(String tableName) {
        return "SELECT * FROM " + tableName;
    }
    public static String findSerialLocation(String dbName, String serialNumber) {
        String query = "SELECT location FROM " + dbName + " WHERE serial_number = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, serialNumber); // safely set the serial number
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("location");
            } else {
                return "Not Found"; // not found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Insert a row
    public static void insertRow(String tableName, String serialNumber, String location) {
        String insertSQL = String.format(
                "INSERT INTO %s(serial_number, location) VALUES('%s', '%s')",
                tableName, serialNumber, location
        );

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(insertSQL);
            System.out.println("Inserted row into " + tableName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Print table contents nicely
    private static void printTable(Statement stmt, String tableName) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(selectAll(tableName))) {
            System.out.println("===== " + tableName.toUpperCase() + " TABLE =====");

            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("id=%d | serial=%s | location=%s | created_at=%s%n",
                        rs.getInt("id"),
                        rs.getString("serial_number"),
                        rs.getString("location"),
                        rs.getString("created_at"));
            }

            if (!hasRows) {
                System.out.println("[No rows in this table]");
            }

            System.out.println(); // blank line for readability
        }
    }

    // Initialize database schema
    public static void initialize() {
        String dropWMSTableSQL = "DROP TABLE IF EXISTS wms";
        String dropERPTableSQL = "DROP TABLE IF EXISTS erp";

        String createWMSTableSQL = """
        CREATE TABLE wms(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            serial_number TEXT NOT NULL,
            location TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    """;

        String createERPTableSQL = """
        CREATE TABLE erp(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            serial_number TEXT NOT NULL,
            location TEXT,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Drop tables if they exist
            stmt.execute(dropWMSTableSQL);
            stmt.execute(dropERPTableSQL);

            // Create tables
            stmt.execute(createWMSTableSQL);
            stmt.execute(createERPTableSQL);

            // Insert example rows
            insertRow("erp", "336342452346", "Triage");
            insertRow("wms", "336342452346", "Retail");

            // Print tables
            printTable(stmt, "wms");
            printTable(stmt, "erp");

            System.out.println(findSerialLocation("wms","336342452345"));

            System.out.println("Databases recreated and initialized.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
