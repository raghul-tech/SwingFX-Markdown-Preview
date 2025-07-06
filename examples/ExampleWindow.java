/**
 * Example demonstrating how to use SwingFXMarkdownWindowPreview
 * to create a standalone Markdown preview window with live updates.
 *
 * <p>
 * Features demonstrated:
 * <ul>
 *   <li>Load Markdown from a File</li>
 *   <li>Launch the preview in its own window</li>
 *   <li>Detect when the preview window is closed</li>
 *   <li>Reopen the preview window on demand</li>
 *   <li>Optionally set dark mode and a custom icon</li>
 * </ul>
 *
 * <p>
 * Example usage of constructors:
 * <pre>{@code
 * SwingFXMarkdownWindowPreview preview = new SwingFXMarkdownWindowPreview(new File("README.md"));
 * }</pre>
 *
 * <p>
 * Example of enabling dark theme:
 * <pre>{@code
 * preview.setDarkMode(true);
 * }</pre>
 *
 * <p>
 * Example of reopening window:
 * <pre>{@code
 * preview.reopenWindow();
 * }</pre>
 *
 * <p>
 * Example of checking if the preview is showing:
 * <pre>{@code
 * boolean showing = preview.isPreviewShowing();
 * }</pre>
 *
 * <p>
 * Example of setting a custom icon:
 * <pre>{@code
 * preview.setIcon("/MD.png");
 * }</pre>
 */
import java.io.File;
import java.util.Scanner;

import io.github.raghultech.markdown.swingfx.preview.MarkdownWindow;

public class ExampleWindow {

    public static void main(String[] args) {
        // Create a File representing your Markdown document
        File mdFile = new File("README.md");

        // Create the SwingFXMarkdownWindowPreview
        // This automatically sets up the JavaFX WebView inside a JFrame
        MarkdownWindow preview = new MarkdownWindow(mdFile);

        // Launch the preview window so it becomes visible
        preview.launchPreview();

        // Create a Scanner to read user input from the console
        Scanner scanner = new Scanner(System.in);

        // Keep looping to monitor the window and offer reopening
        while (true) {
            // While the preview window is open, wait and check every second
            while (preview.isPreviewShowing()) {
                try {
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException ignored) {
                }
            }

            // If the preview window was closed, prompt the user to reopen
            System.out.print("Preview closed. Reopen? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes")) {
                // Optionally set a custom icon for the window
                // (Requires the icon file to be in your classpath/resources)
                // preview.setIcon("/MD.png");

			// Optionally enable dark mode
                // preview.setDarkMode(true);
                
                // Reopen the preview window
                preview.reopenWindow();

                
            } else {
                // If the user said "no," exit the loop
                break;
            }
        }

        // Clean up the scanner
        scanner.close();

        // Exit the program cleanly
        System.exit(0);
    }
}
