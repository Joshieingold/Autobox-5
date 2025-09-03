package josh.lynch.autobox5;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        String sql = "INSERT INTO " + tableName + "(serial_number, location) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, serialNumber);
            pstmt.setString(2, location);
            pstmt.executeUpdate();

            System.out.println("Inserted: " + serialNumber + " | " + location);
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

    private static String FormatLocations(String locationName) {
        if ((locationName.toLowerCase()).contains("triage")) {
            return "Triage";
        }
        else if (locationName.toLowerCase().contains("quar")) {
            return "Quar";

        }
        else if (locationName.toLowerCase().contains("sub")) {
            return "Sub-Wip";
        }
        else if (locationName.toLowerCase().contains("retail")) {
            return "Retail";
        }
        else  {
            return "Not Found";
        }

    }


    private static void populateDatabase(String databaseName, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select " + databaseName + " Excel File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            System.out.println("No file selected.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis);
             Connection conn = connect()) {

            conn.setAutoCommit(false); // start transaction

            Sheet sheet = workbook.getSheetAt(0);

            String insertSQL = "INSERT INTO " + databaseName + "(serial_number, location) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

                for (Row row : sheet) {
                    if (row.getCell(0) != null && row.getCell(1) != null) {
                        String serial = row.getCell(0).getStringCellValue();
                        String location = FormatLocations(row.getCell(1).getStringCellValue());

                        pstmt.setString(1, serial);
                        pstmt.setString(2, location);
                        pstmt.addBatch();
                    }
                }

                pstmt.executeBatch(); // execute all inserts at once
                conn.commit();        // commit transaction
            }

            System.out.println("Finished populating " + databaseName + " database.");

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    // Initialize database schema
    public static void initialize(Stage stage) {
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
            //stmt.execute(dropWMSTableSQL);
            //stmt.execute(dropERPTableSQL);

            // Create tables
            //stmt.execute(createWMSTableSQL);
            //stmt.execute(createERPTableSQL);

            // Prompt twice (once for each DB)
            System.out.println("Select Excel for WMS:");
            //populateDatabase("wms", stage);

            System.out.println("Select Excel for ERP:");
            //populateDatabase("erp", stage);

            // Print tables
            //printTable(stmt, "wms");
            //printTable(stmt, "erp");

            //System.out.println("Databases recreated and initialized.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
