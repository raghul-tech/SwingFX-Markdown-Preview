package io.github.raghultech.markdown.swingfx.integration;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewLinkException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewSystemException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewUnsupportedLinkException;
import javafx.application.HostServices;

public class SwingFXhandleExternalLinkMailFile {

    private static SwingFXhandleExternalLinkMailFile instance;

    private HostServices hostServices; // Optional, for JavaFX WebView

    public static synchronized SwingFXhandleExternalLinkMailFile getInstance() {
        if (instance == null) {
            instance = new SwingFXhandleExternalLinkMailFile();
        }
        return instance;
    }

    /**
     * Optionally provide HostServices (JavaFX) to support non-Desktop platforms.
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public synchronized void handleExternalLink(String url) {
        try {
        	// if (hostServices != null) {
              //   hostServices.showDocument(url);
                // return;
           //  }
            URI uri = new URI(url);
            String scheme = uri.getScheme().toLowerCase();

            boolean desktopSupported = Desktop.isDesktopSupported();
            Desktop desktop = desktopSupported ? Desktop.getDesktop() : null;

            switch (scheme) {
                case "mailto":
                    if (desktopSupported && desktop.isSupported(Desktop.Action.MAIL)) {
                        handleMailToLink(uri);
                    } else {
                        throw new MarkdownPreviewSystemException("Mailto links are not supported on this system.");
                    }
                    break;
                case "http":
                case "https":
                    if (desktopSupported && desktop.isSupported(Desktop.Action.BROWSE)) {
                        desktop.browse(uri);
                    } else if (hostServices != null) {
                        hostServices.showDocument(url);
                    } else {
                        throw new MarkdownPreviewSystemException("No way to open HTTP link on this system.");
                    }
                    break;
                case "file":
                    if (desktopSupported && desktop.isSupported(Desktop.Action.OPEN)) {
                        handleFileLink(uri);
                    } else if (hostServices != null) {
                        hostServices.showDocument(url);
                    } else {
                        throw new MarkdownPreviewSystemException("No way to open file link on this system.");
                    }
                    break;
                default:
                    throw new MarkdownPreviewUnsupportedLinkException("Unsupported link scheme: " + scheme);
            }
        } catch (URISyntaxException e) {
            throw new MarkdownPreviewLinkException("Invalid URI syntax: " + url, e);
        } catch (IOException e) {
            throw new MarkdownPreviewLinkException("I/O error while opening link: " + url, e);
        } catch (Exception e) {
            throw new MarkdownPreviewLinkException("Unexpected error while handling link: " + url, e);
        }
    }

    private void handleMailToLink(URI uri) throws Exception {
    	try {
        if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
            String mailTo = uri.toString()
                .replace(" ", "%20")
                .replace("+", "%2B");
            Desktop.getDesktop().mail(new URI(mailTo));
        }  else {
        	   fallbackMail(uri.toString());
           // throw new MarkdownPreviewSystemException("Mailto links are not supported on this system.");
        }
    } catch (Exception e) {
        throw new MarkdownPreviewLinkException("Failed to handle mailto link: " + uri, e);
    }
    }

    private void fallbackMail(String mailto) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            new ProcessBuilder("open", mailto).start();
        } else if (os.contains("nix") || os.contains("nux")) {
            new ProcessBuilder("xdg-email", mailto).start();
        } else {
        	  throw new MarkdownPreviewSystemException("Mailto links are not supported on this system.");
            //throw new IOException("No fallback mail handler.");
        }
    }


    private void handleFileLink(URI uri) {
        try {
            File file = Paths.get(uri).toFile();
            if (file.exists()) {
                if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(file);
                } else if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(file.toURI());
                } else if (hostServices != null) {
                    hostServices.showDocument(file.toURI().toString());
                } else {
                	  fallbackOpen(file);
                  //  throw new MarkdownPreviewSystemException("No way to open the file.");
                }
            } else {
                throw new MarkdownPreviewLinkException("File not found: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new MarkdownPreviewLinkException("Failed to handle file link: " + uri, e);
        }
    }

    private void fallbackOpen(File file) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            new ProcessBuilder("explorer", file.getAbsolutePath()).start();
        } else if (os.contains("mac")) {
            new ProcessBuilder("open", file.getAbsolutePath()).start();
        } else if (os.contains("nix") || os.contains("nux")) {
            new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
        } else {
        	 throw new MarkdownPreviewSystemException("No way to open the file.");
          //  throw new IOException("Cannot determine OS to open file.");
        }
    }


}
