package io.github.raghultech.markdown.swingfx.preview;

import java.awt.BorderLayout;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import io.github.raghultech.markdown.swingfx.config.JavaLinkHandler;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewContentException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewFileException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewRenderException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewSystemException;
import io.github.raghultech.markdown.swingfx.integration.MarkdownRenderer;
import io.github.raghultech.markdown.swingfx.integration.SwingFXhandleExternalLinkMailFile;
import io.github.raghultech.markdown.utils.filesentry.FileWatcher;
import io.github.raghultech.markdown.utils.openloom.OpenLoom;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * A Swing JPanel for live Markdown preview.
 * Supports theme switching, file change detection, emoji rendering, and HTML export.
 */


@SuppressWarnings("serial")
public class MarkdownPanel extends JPanel {

    private final JFXPanel fxPanel;
    private WebEngine engine;
    private File currentFile;
    private String originalContent;
    private boolean isStringMode = false;
    private Timer updateTimer;
    private ExecutorService executor;
    private FileWatcher fileWatcher;
    private transient WeakReference<Thread> fileWatcherThread;
    private volatile boolean disposed = false;
    private MarkdownRenderer render = MarkdownRenderer.getInstance();
    private boolean isDark = false;

    // --- Constructors ---
    public MarkdownPanel(File file) {
        if (file == null || !file.exists()) {
            throw new MarkdownPreviewFileException("Invalid file provided: " + file);
        }
        this.currentFile = file;
        this.fxPanel = new JFXPanel();
        init();
    }

    public MarkdownPanel(String content) {
        if (content == null || content.isEmpty()) {
            throw new MarkdownPreviewContentException("Content cannot be null or empty.");
        }
        this.originalContent = content;
        this.isStringMode = true;
        this.fxPanel = new JFXPanel();
        init();
    }

    // --- Initialization ---
    private void init() {
        setLayout(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);
        createExecutor();
        setupUpdateTimer();
        initializeWebView();
    }

    private void createExecutor() {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "SwingFXMarkdownPreviewPanel");
                t.setDaemon(true);
                return t;
            });
        }
    }


	private void setupUpdateTimer() {
        if (isStringMode) {
			return;
		}
        updateTimer = new Timer(500, e -> updateContent());
        updateTimer.setRepeats(false);
    }


	private void initializeWebView() {
        Platform.runLater(() -> {
            WebView webView = new WebView();
            engine = webView.getEngine();
            engine.setJavaScriptEnabled(true);

            JavaLinkHandler linkHandler = new JavaLinkHandler(this::handleExternalLink);

            // JS click interception
            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED && !disposed) {
                    try {
                        JSObject window = (JSObject) engine.executeScript("window");
                        window.setMember("javaBridge", linkHandler);

                        engine.executeScript(
                            "document.addEventListener('click', function(e) {" +
                            "   var target = e.target.closest('a');" +
                            "   if (target) {" +
                            "       e.preventDefault();" +
                            "       window.javaBridge.handleLink(target.href);" +
                            "   }" +
                            "});"
                        );
                    } catch (Exception e) {
                        throw new MarkdownPreviewRenderException("Failed to set up JavaScript bridge.", e);
                    }
                }
            });

            engine.locationProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null || newVal.isEmpty() || newVal.startsWith("data:text/html")) {
					return;
				}
                Platform.runLater(() -> {
                	if(!disposed && engine != null) {
                    engine.load(null);
                    handleExternalLink(newVal);
                	}
                });
            });
            updatefullContent(); // initial load

            Scene scene = new Scene(webView);
            fxPanel.setScene(scene);

            if (!isStringMode) {
                watchFileForChanges();
            }
        });
    }

    private void handleExternalLink(String url) {
        if (disposed) {
			return;
		}

        try {
            if (executor == null || executor.isShutdown()) {
                createExecutor();
            }

            executor.execute(() -> {
                if (disposed) {
					return;
				}
                SwingFXhandleExternalLinkMailFile.getInstance().handleExternalLink(url);
            });
        } catch (Exception e) {
            if (!disposed) {
                throw new MarkdownPreviewSystemException("Failed to submit external link handling task.", e);
            }
        }
    }
    private void updateContent() {
     
        if (engine == null) {
    		return;
    	}

        // Get current content
        String markdown = isStringMode ? originalContent : OpenLoom.getContent(currentFile).toString();
        if (markdown == null) {
            throw new MarkdownPreviewContentException("Content or File is null");
        }

        // Render markdown to HTML
        String html = render.renderMarkdown(markdown)
                .replace(":emoji_name:", "<img class='emoji' alt=':emoji_name:' src='https://github.githubassets.com/images/icons/emoji/unicode/1f604.png' height='20' width='20'>");
        String styledHtml = render.getStyledHtml(html, currentFile, isDark);

        Platform.runLater(() -> {
            try {
                // Always use full HTML from getStyledHtml() for complete document structure
                // Save scroll position before refresh
                Object scrollY = engine.executeScript("window.scrollY || window.pageYOffset");

                // Load the complete styled HTML (already contains all required scripts/styles)
                engine.loadContent(styledHtml);

                // Restore scroll position after DOM is ready
                engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED && scrollY instanceof Number) {
                        engine.executeScript(String.format(
                            "window.scrollTo({ top: %d, behavior: 'instant' })",
                            ((Number)scrollY).intValue()
                        ));

                        // Re-attach event listeners if needed
                        engine.executeScript(
                            "if (typeof hljs !== 'undefined') hljs.highlightAll();"
                        );
                    }
                });
            } catch (Exception e) {
                // Fallback to simple refresh if anything fails
                engine.loadContent(styledHtml);
            }
        });
    }



    private void updatefullContent() {
        if (disposed || engine == null) {
			return;
		}

        String markdown = isStringMode ? originalContent : OpenLoom.getContent(currentFile).toString();
        if (markdown == null) {
            throw new MarkdownPreviewContentException("Content or File is null");
        }


        String html = render.renderMarkdown(markdown);
        String styledHtml = render.getStyledHtml(html, currentFile,isDark);

        Platform.runLater(() -> engine.loadContent(styledHtml));
    }

    private synchronized void watchFileForChanges() {
        stopFileWatcher();
        fileWatcher = new FileWatcher(currentFile);
        fileWatcher.setFileChangeListener(changed -> {
            if (changed) {
                SwingUtilities.invokeLater(this::triggerUpdate);
            }
        });
        Thread watcherThread = new Thread(fileWatcher);
        watcherThread.setDaemon(true);
        fileWatcherThread = new WeakReference<>(watcherThread);
        watcherThread.start();
    }

    private void triggerUpdate() {
        if (updateTimer != null) {
            updateTimer.restart();
        }
    }

    private synchronized void stopFileWatcher() {
        if (fileWatcher != null) {
            fileWatcher.stopWatching();
        }
        Thread watcherThread = (fileWatcherThread != null) ? fileWatcherThread.get() : null;
        if (watcherThread != null && watcherThread.isAlive()) {
            watcherThread.interrupt();
            try {
                watcherThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (fileWatcherThread != null) {
            fileWatcherThread.clear();
        }
    }

    public synchronized void dispose() {
        if (disposed) {
			return;
		}
        disposed = true;
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
        stopFileWatcher();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        Platform.runLater(() -> {
            if (engine != null) {
                engine.load("about:blank");
            }
            fxPanel.setScene(null);
        });
    }

    // --- Additional setters ---
    public void setContent(String content) {
        this.originalContent = content;
        this.isStringMode = true;
        this.currentFile = null;
        updatefullContent();
    }

    public void setCurrentFile(File file) {
        this.currentFile = file;
        this.isStringMode = false;
        this.originalContent = null;
        updatefullContent();
        watchFileForChanges();
    }
    public void setDarkMode(boolean dark) {
    	this.isDark = dark;
    	 updatefullContent();
    }
    public boolean getisDarkMode() { return isDark; }

    public boolean isPreviewShowing() {
    	return !disposed;
    }
}
