package josh.lynch.autobox5;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RowController {

    @FXML private Label indexLabel;
    @FXML private TextField inputField;
    @FXML private Circle circle1; // first status circle
    @FXML private Circle circle2; // second status circle

    // Set the row index
    public void setIndex(int index) {
        indexLabel.setText(String.valueOf(index));
    }

    // Access to the input field
    public TextField getInputField() {
        return inputField;
    }

    // Initialize is called after FXML is loaded
    @FXML
    public void initialize() {
        // When Enter is pressed in the TextField
        inputField.setOnAction(event -> checkSerial());
    }

    // Check serial in the database and update circle colors
    private void checkSerial() {
        String serial = inputField.getText().trim();
        if (serial.isEmpty()) return;

        // Query the database
        String wmsLocation = DatabaseManager.findSerialLocation("wms", serial);
        String erpLocation = DatabaseManager.findSerialLocation("erp", serial);

        // Determine colors based on location
        Color wmsColor = getColorFromLocation(wmsLocation);
        Color erpColor = getColorFromLocation(erpLocation);

        // Update circles
        circle1.setFill(wmsColor);
        circle2.setFill(erpColor);

        // Update index label color
        if (wmsColor.equals(erpColor) && !wmsColor.equals(Color.BLACK)) {
            indexLabel.setTextFill(Color.GREEN);
        } else {
            indexLabel.setTextFill(Color.web("#ff5722")); // original color
        }
    }

    // Helper method to map location to color
    private Color getColorFromLocation(String location) {
        if (location == null) return Color.BLACK;
        return switch (location.toLowerCase()) {
            case "triage" -> Color.YELLOW;
            case "retail" -> Color.PURPLE;
            case "quar" -> Color.RED;
            case "sub-wip" -> Color.BLUE;
            default -> Color.GRAY;
        };
    }
}
