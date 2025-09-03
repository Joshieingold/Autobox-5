package josh.lynch.autobox5;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.awt.event.InputEvent;

public class ActiveSerials {
    ArrayList<String> serials;
   public void ActiveSerials() {
      this.serials = new ArrayList<String>(); // I want to make this written as a string user list
   }

    // ============================================ //
    // Main Utilities
    // ============================================ //

   public void AddSerial(String newSerial) {
       (serials).add(newSerial);
   }


    // ============================================ //
    // UI Functions / End User Functions
    // ============================================ //

   public void BlitzImport() throws AWTException {
      for (String serial : serials) {
        TypeAndWait(serial);
        TypeAndWait("Tab");
      }
   }
   public void FlexiImport(String colorWeWant, String pixel) throws AWTException {
       for (String serial : serials) {
           while (CheckPixel(colorWeWant, pixel) == false) {
               WaitFunction();
           }
          TypeAndWait(serial);
          TypeAndWait("Tab");
       }
   }

   // ============================================ //
   // Useful Helper Functions
   // ============================================ //
    public void WaitFunction() {
      // Will wait for the amount of time designated in the settings.
    }
    public void TypeAndWait(String serial) throws AWTException {
        Robot robot = new Robot();
        int delay = 100; // ms delay between keystrokes

        if (Objects.equals(serial, "Tab")) {
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
        } else if (Objects.equals(serial, "Enter")) {
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } else {
            for (char letter : serial.toCharArray()) {
                int keyCode = KeyEvent.getExtendedKeyCodeForChar(letter);
                if (KeyEvent.CHAR_UNDEFINED == keyCode) {
                    continue; // skip untypable chars
                }
                robot.keyPress(keyCode);
                robot.keyRelease(keyCode);
                robot.delay(delay);
            }
        }

        // Wait a bit after finishing the serial
        robot.delay(250);
    }

    public int[] ExtractPixels(String pixelString) {
        String[] parts = pixelString.split(", ");

        int[] cords = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            cords[i] = Integer.parseInt(parts[i]);
        }

        return cords;
    }


    public void FindPixel(String pixelX, String pixelY) {

    }
    public Boolean CheckPixel(String colorYouWant, String pixelLocation) throws AWTException {
        int[] cords = ExtractPixels(pixelLocation);  // expect [x, y]
        int x_pos = cords[0];
        int y_pos = cords[1];

        Robot robot = new Robot();
        robot.mouseMove(x_pos, y_pos);

        // If you want to actually check pixel color:
        Color pixelColor = robot.getPixelColor(x_pos, y_pos);
        return pixelColor.toString().equalsIgnoreCase(colorYouWant);
    }

    public void clear() {
        serials = new ArrayList<String>();
    }
}
