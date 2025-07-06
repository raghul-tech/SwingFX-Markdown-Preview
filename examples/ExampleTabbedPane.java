/**
 * Example demonstrating how to use SwingFXMarkdownTabbedPreview
 * to add a live-updating Markdown preview as a tab in a JTabbedPane.
 *
 * <p>
 * Features demonstrated:
 * <ul>
 *   <li>Create the preview tab using a File</li>
 *   <li>Add the tab to a custom JTabbedPane</li>
 *   <li>Launch the preview</li>
 *   <li>Optionally set an icon for the tab</li>
 *   <li>Toggle between dark/light themes</li>
 *   <li>Change the previewed file or Markdown string dynamically</li>
 *   <li>Dispose resources when done</li>
 *   <li>Check if the preview is currently visible</li>
 * </ul>
 *
 * <p>
 * Example usage of constructors:
 * <pre>{@code
 * // From a File
 * SwingFXMarkdownTabbedPreview preview = new SwingFXMarkdownTabbedPreview(tabbedPane, new File("README.md"));
 *
 * // From a String
 * SwingFXMarkdownTabbedPreview preview = new SwingFXMarkdownTabbedPreview(tabbedPane, "# Hello Markdown!");
 * }</pre>
 *
 * <p>
 * Example of setting an icon:
 * <pre>{@code
 * Icon icon = new ImageIcon(getClass().getResource("/MD.png"));
 * preview.setTabbedPaneIcon(icon, 16);
 * }</pre>
 *
 * <p>
 * Example of switching theme:
 * <pre>{@code
 * preview.setDarkMode(true); // Enable dark theme
 * }</pre>
 *
 * <p>
 * Example of replacing the content later:
 * <pre>{@code
 * preview.setCurrentFile(new File("CHANGELOG.md"));
 * preview.setContent("# New Content");
 * }</pre>
 *
 * <p>
 * Example of checking if preview is showing:
 * <pre>{@code
 * boolean showing = preview.isPreviewShowing();
 * }</pre>
 *
 * <p>
 * Example of disposing resources:
 * <pre>{@code
 * preview.dispose();
 * }</pre>
 */

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import io.github.raghultech.markdown.swingfx.preview.MarkdownTabbedview;

public class ExampleTabbedPane {

    public static void main(String[] args) {
        // Create the main JFrame for your application
        JFrame frame = new JFrame("Markdown Preview Tabs");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Create a JTabbedPane to hold multiple tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        // Allow scrolling if there are too many tabs
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Add some example tabs before adding the preview
        tabbedPane.addTab("Editor 1", new JScrollPane(new JTextArea()));
        tabbedPane.addTab("Editor 2", new JScrollPane(new JTextArea()));

        // Add the tabbed pane to the JFrame
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Create a File pointing to the Markdown file you want to preview
        File markdownFile = new File("README.md");

        // Create the preview instance
        // You can also pass a String:
        // MarkdownTabbedview preview = new MarkdownTabbedview(tabbedPane, "# My Markdown");
        MarkdownTabbedview preview = new MarkdownTabbedview(tabbedPane, markdownFile);

        // Show the window
        frame.setVisible(true);

        // OPTIONAL: Set an icon for the preview tab
        // Icon icon = new ImageIcon(ExampleTabbedPane.class.getResource("/MD.png"));
        // preview.setTabbedPaneIcon(icon, 16); // 16 is the icon size
	//preview.setTabbedPaneIcon(icon);

        // OPTIONAL: Enable dark mode
      //  preview.setDarkMode(true);
        
        // Launch the preview tab (adds it to the JTabbedPane)
        preview.launchPreviewTab();


        // OPTIONAL: Replace the previewed content dynamically:
        // preview.setCurrentFile(new File("CHANGELOG.md"));
        // preview.setContent("# Replaced Markdown Content");

        // OPTIONAL: Check if the preview is showing
        boolean showing = preview.isPreviewShowing();
        System.out.println("Is preview showing? " + showing);

        // OPTIONAL: Dispose resources when you no longer need the preview
     /*    Timer disposeTimer = new Timer(7000, e -> {
            System.out.println("Disposing preview...");
            preview.dispose();

            // Schedule relaunch after 5 seconds
            Timer relaunchTimer = new Timer(5000, e2 -> {
                System.out.println("Relaunching preview...");
                preview.relaunchPreviewTab();
            });
            relaunchTimer.setRepeats(false); // Run once
            relaunchTimer.start();
        });
        disposeTimer.setRepeats(false); // Run once
        disposeTimer.start();*/
    }
}
