/**
 * Example demonstrating how to use SwingFXMarkdownPreviewPanel
 * to render and preview Markdown content in a Swing application.
 *
 * <p>
 * Features demonstrated:
 * <ul>
 *   <li>Create a preview panel using a File</li>
 *   <li>Add the panel to a JFrame</li>
 *   <li>Optionally switch between light and dark themes</li>
 *   <li>Dispose the preview resources when done</li>
 *   <li>Check if the preview is currently visible</li>
 * </ul>
 *
 * <p>
 * The panel can be constructed using either:
 * <ul>
 *   <li>A <code>File</code> pointing to a Markdown file</li>
 *   <li>A <code>String</code> containing Markdown content</li>
 * </ul>
 *
 * <p>
 * Example usage of constructors:
 * <pre>{@code
 * // From a File
 * SwingFXMarkdownPreviewPanel panel = new SwingFXMarkdownPreviewPanel(new File("README.md"));
 *
 * // From a String
 * SwingFXMarkdownPreviewPanel panel = new SwingFXMarkdownPreviewPanel("# Hello Markdown!");
 * }</pre>
 *
 * <p>
 * Example of changing theme:
 * <pre>{@code
 * panel.isDarkMode(true); // Enable dark mode
 * panel.isDarkMode(false); // Switch back to light mode
 * }</pre>
 *
 * <p>
 * Example of checking if the preview is showing:
 * <pre>{@code
 * boolean showing = panel.isPreviewShowing();
 * }</pre>
 *
 * <p>
 * Example of disposing resources:
 * <pre>{@code
 * panel.dispose();
 * }</pre>
 */

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import io.github.raghultech.markdown.swingfx.preview.MarkdownPanel;

public class ExamplePanel {

    public static void main(String[] args) {
        // Always launch Swing code on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create the main application window
            JFrame frame = new JFrame("SwingFXMarkdownPreviewPanel Example");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(900, 700);

            // Create a File pointing to a Markdown file you want to preview
            File mdFile = new File("README.md");

            // Create the preview panel using the File
            // You can also pass a String instead of a File:
            // MarkdownPanel panel = new MarkdownPanel("# Hello World!");
            MarkdownPanel panel = new MarkdownPanel(mdFile);

            // OPTIONAL: Switch to dark mode theme
            // panel.setDarkMode(true);

            // Add the preview panel to the JFrame's content pane
            frame.getContentPane().add(panel);

            // Center the frame on screen
            frame.setLocationRelativeTo(null);

            // Display the frame
            frame.setVisible(true);

            // Example: Checking if the preview is currently showing
            boolean isShowing = panel.isPreviewShowing();
            System.out.println("Preview showing? " + isShowing);

            // You can later dispose resources if needed:
            // panel.dispose();
        });
    }
}
