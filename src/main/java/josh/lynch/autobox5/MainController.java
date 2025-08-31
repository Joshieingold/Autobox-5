package josh.lynch.autobox5;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController {

    public HBox navBar;
    @FXML private VBox contentBox;
    @FXML private ScrollPane scrollPane; // new ScrollPane reference
    private int rowCount = 0;
    // For settings later

    @FXML
    private void initialize() {
        addRow(); // first row
        navBar.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                setupHoverAnimation(button);
            }
        });
        DatabaseManager.initialize(new Stage());
    }
    private void setupHoverAnimation(Button button) {
        DropShadow glow = new DropShadow();
        glow.setColor(javafx.scene.paint.Color.RED);
        glow.setRadius(10);

        ScaleTransition grow = new ScaleTransition(Duration.millis(150), button);
        grow.setToX(1.15);
        grow.setToY(1.15);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(150), button);
        shrink.setToX(1.0);
        shrink.setToY(1.0);

        button.setOnMouseEntered(e -> {
            button.setEffect(glow);
            grow.playFromStart();
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            shrink.playFromStart();
        });
    }
    private void addRow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/josh/lynch/autobox5/Row.fxml"));
            HBox row = loader.load();

            RowController controller = loader.getController();
            controller.setIndex(++rowCount);

            // Save controller in userData for focus navigation
            row.setUserData(controller);

            // Listen for Enter or Tab key
            controller.getInputField().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                    int currentIndex = contentBox.getChildren().indexOf(row);

                    if (currentIndex < contentBox.getChildren().size() - 1) {
                        // Focus next row
                        HBox nextRow = (HBox) contentBox.getChildren().get(currentIndex + 1);
                        RowController nextController = (RowController) nextRow.getUserData();
                        nextController.getInputField().requestFocus();
                    } else {
                        // Add new row if last
                        addRow();
                        HBox newRow = (HBox) contentBox.getChildren().get(contentBox.getChildren().size() - 1);
                        RowController newController = (RowController) newRow.getUserData();
                        newController.getInputField().requestFocus();
                    }

                    // Scroll to bottom
                    scrollPane.layout(); // ensure layout is updated
                    scrollPane.setVvalue(1.0); // scroll to bottom
                    event.consume();
                }
            });

            contentBox.getChildren().add(row);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
