/**
 * ExampleRenderer demonstrates rendering Markdown files
 * into styled HTML using the MarkdownRenderer utility.
 *
 * <p>
 * This example:
 * <ul>
 *   <li>Loads a Markdown file</li>
 *   <li>Converts it to HTML</li>
 *   <li>Applies styling for dark theme</li>
 *   <li>Prints the styled HTML output</li>
 * </ul>
 */
// Import File class for working with file references
import java.io.File;
// Import Scanner if you want to read files manually (not used here)
import java.util.Scanner;

// Import your MarkdownRenderer utility
import io.github.raghultech.markdown.swingfx.integration.MarkdownRenderer;
// Import your utility class for reading files into strings
import io.github.raghultech.markdown.utils.openloom.OpenLoom;

/**
 * ExampleRenderer demonstrates how to convert a Markdown file
 * to styled HTML using the MarkdownRenderer.
 */
public class ExampleRenderer {

    /**
     * The main method is the entry point of this example program.
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {

        // Create a File object representing your Markdown document
        File mdFile = new File("README.md");

        // Create a MarkdownRenderer instance
        // You can also do: MarkdownRenderer renderer = new MarkdownRenderer();
        MarkdownRenderer renderer = MarkdownRenderer.getInstance();

        // Tip: You can also read the file into a String manually if you prefer
        // Here we use OpenLoom to load the content safely
        String content = OpenLoom.getContent(new File("README.md")).toString();

        // Convert the Markdown content into basic HTML
        String html = renderer.renderMarkdown(content);

       // If you read content from a String (e.g., user input or API response),
// you can pass null for the file argument because there is no file context:
//String styledHtml = renderer.getStyledHtml(
//    html, 
  //  null, // No file reference; it's pure string content
 //   true  // Dark theme
//);

// If you read content from a file on disk, pass the File object:
String styledHtml = renderer.getStyledHtml(
    html, 
    mdFile, // This helps resolve relative paths in the Markdown
    true    // Dark theme
);


        // Print the styled HTML to the console
        System.out.println(styledHtml);
    }
}
