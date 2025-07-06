package io.github.raghultech.markdown.swingfx.preview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import io.github.raghultech.markdown.swingfx.config.JavaFXUtils;
import io.github.raghultech.markdown.swingfx.config.JavaLinkHandler;
import io.github.raghultech.markdown.swingfx.config.PreviewTab;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewContentException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewFileException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewInitializationException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewRenderException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewResourceException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewRuntimeException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewSystemException;
import io.github.raghultech.markdown.swingfx.exception.MarkdownPreviewWindowException;
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
 * A Swing JTabbedPane for Markdown preview in tabs.
 * Provides live reloading, theme switching, emoji rendering, and HTML export.
 */


public class MarkdownTabbedview {

private final JTabbedPane tabbedPane;
private volatile boolean javaFXInitialized = JavaFXUtils.isJavaFXAvailable(); // Make volatile for thread safety
private final Map<Object, PreviewTab> previewTabs = new HashMap<>();
private Timer updateTimer;
private ExecutorService executor;
private File currentFile ;
private String OriginalContent;
private boolean isStringMode = false;
private Icon icon;
private FileWatcher fileWatcher;
  private transient WeakReference<Thread> fileWatcherThread;
  private String tabName =null;
  private JButton tabBtn;
  private volatile boolean disposed = false;
  private int iconSize = 16;
  private MarkdownRenderer render = MarkdownRenderer.getInstance();
 private boolean isDark = false;


public MarkdownTabbedview(JTabbedPane tabbedPane,File file) {
	 if (file == null || !file.exists() || !file.isFile()) {
	        throw new MarkdownPreviewFileException("Invalid file provided: " + file);
	    }
	 if(tabbedPane == null) {
		throw new MarkdownPreviewInitializationException("TabbedPane should not be null");
	}
	this.currentFile = file;
	 this.tabbedPane = tabbedPane;
}

public MarkdownTabbedview(JTabbedPane tabbedPane,String content) {
	if (content == null ||content.isEmpty() ) {
        throw new MarkdownPreviewContentException("The String Content should not be null or empty " );
    }
	 if(tabbedPane == null) {
		throw new MarkdownPreviewInitializationException("TabbedPane should not be null");
	}
	this.OriginalContent = content;
	  this.isStringMode = true;
	  this.tabbedPane = tabbedPane;
}

public void launchPreviewTab() {
	if(!javaFXInitialized) throw new MarkdownPreviewSystemException("JavaFX is not available in this environment");
	 try {
		// initializeJavaFX(); // Initialize JavaFX subsystem
		 createExecutor();
		 setupUpdateTimer();
		 setupTabChangeListener();
		 createAndShowPeviewTab();
	 } catch (Exception e) {
         dispose(); // Clean up if initialization fails
         throw new MarkdownPreviewInitializationException("Failed to initialize Markdown preview window", e);
     }
}

//--- Tab Opening Logic ---
public void relaunchPreviewTab() {

	if(!javaFXInitialized) throw new MarkdownPreviewSystemException("JavaFX is not available in this environment");

 if (!disposed) {
     dispose();
 }

 if (currentFile == null && !isStringMode) {
     throw new MarkdownPreviewResourceException("Cannot reopen: no file or string content available.");
 }

 disposed = false;
 createExecutor();
 setupUpdateTimer();
 createAndShowPeviewTab();
}

public boolean isPreviewShowing() {
	return !disposed;
}

private void setupTabChangeListener() {

    tabbedPane.addChangeListener(e -> {
        int index = tabbedPane.getSelectedIndex();
        if (index != -1) {
            Component comp = tabbedPane.getComponentAt(index);
            if (comp instanceof JFXPanel) {
                JFXPanel fxPanel = (JFXPanel)comp;
                // Restore tab components if needed
                JPanel tabHeader = (JPanel)fxPanel.getClientProperty("tabHeader");
                if (tabHeader != null && tabbedPane.getTabComponentAt(index) != tabHeader) {
                    tabbedPane.setTabComponentAt(index, tabHeader);
                }
            }
        }
    });
}

private void setupUpdateTimer() {
	  if (isStringMode) {
		return;
	}
	  updateTimer = new Timer(700, e -> {
	        Object key = getPreviewKey();
	        PreviewTab previewTab = previewTabs.get(key);
	        if (previewTab != null && tabbedPane.indexOfComponent(previewTab.fxPanel) != -1) {
	            updatePreviewContent(key);
	        }
	    });
  updateTimer.setRepeats(false);
}


private synchronized void createExecutor() {
    if (executor == null || executor.isShutdown()) {
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "JavaFXMarkdownPreviewTabbedPane - " + hashCode());
            t.setDaemon(true); // Allow JVM to exit if this is the only thread left
            return t;
        });
    }
}



private void createAndShowPeviewTab() {
	 Object key = getPreviewKey();
	    PreviewTab existingTab = previewTabs.get(key);
	    if (existingTab != null && tabbedPane.indexOfComponent(existingTab.fxPanel) != -1) {
	        tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(existingTab.fxPanel));
	        return;
	    }

	    String markdown;
	    if (isStringMode) {
	        markdown = OriginalContent;
	    } else {
	   //  markdown =  OpenLoom.getContent(currentFile).toString();
	    	 StringBuilder content = OpenLoom.getContent(currentFile);
	         if (content == null) {
	             throw new MarkdownPreviewFileException("Failed to load content from file: " + currentFile);
	         }
	         markdown = content.toString();
	    }

	    String html = render.renderMarkdown(markdown)
	    	    .replace(":emoji_name:", "<img class='emoji' alt=':emoji_name:' src='https://github.githubassets.com/images/icons/emoji/unicode/1f604.png' height='20' width='20'>");
	    String styledHtml = render.getStyledHtml(html, currentFile,isDark); // Final for lambda use
	   // final String finalBasePath = getBasePathForFile(currentFile); // Final for lambda use

	    // Create Swing container for FX content
	    JFXPanel fxPanel = new JFXPanel();

	    // Initialize FX components on the FX thread
	    Platform.runLater(() -> {
	        try {
	            WebView webView = new WebView();
	            WebEngine engine = webView.getEngine();
	         // After creating your WebView

	            engine.setJavaScriptEnabled(true);

	            // Add this to handle emoji rendering
	            engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
	                if (newDoc != null) {
	                    newDoc.setDocumentURI("about:blank");
	              //      newDoc.putProperty("charset", "UTF-8");
	                }
	            });


	            webView.getEngine().setUserStyleSheetLocation("data:text/css;charset=utf-8," +
	                "::-webkit-scrollbar {" +
	                "    width: 10px;" +
	                "}" +
	                "::-webkit-scrollbar-track {" +
	                "    background: " + (isDark ? "#2b2b2b" : "#f1f1f1") + ";" +
	                "}" +
	                "::-webkit-scrollbar-thumb {" +
	                "    background: " + (isDark ? "#555555" : "#888") + ";" +
	                "}" +
	                "::-webkit-scrollbar-thumb:hover {" +
	                "    background: " + (isDark ? "#666666" : "#555") + ";" +
	                "}");

	            // --- Java-to-JavaScript Bridge Setup ---
	            JavaLinkHandler linkHandler = new JavaLinkHandler(url -> {
	                handleExternalLink(url); // Your existing link handler
	            });

	            engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
	                if (newState == Worker.State.SUCCEEDED && !disposed) {
	                	try {
	                    JSObject window = (JSObject) engine.executeScript("window");
	                    window.setMember("javaBridge", linkHandler);  // <--- ADD THIS

	                    // Add click interceptor script
	                    engine.executeScript(  // <--- ADD FROM HERE
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
	                    engine.load(null); // Cancel WebEngine navigation
	                    handleExternalLink(newVal); // Handle URL externally
	                });
	            });

	            engine.loadContent(styledHtml);    // Load the initial HTML

	            Scene scene = new Scene(webView);
	            fxPanel.setScene(scene); // Set the scene on the JFXPanel

	            PreviewTab previewTab = new PreviewTab(fxPanel, engine);
	            previewTabs.put(key, previewTab);

	            // Add tab and listener on the Swing EDT *after* FX setup
	            SwingUtilities.invokeLater(() -> {
	            	 if (!disposed) {
	          		   try {
	                addTabWithPreview(fxPanel ,key);
	                if(!isStringMode) {
						watchFileForChanges();
					}
	          		 } catch (Exception ex) {
	     	            throw new MarkdownPreviewRuntimeException("Failed to initialize preview content or file watcher.", ex);
	     	        }
	            	 }
	            });

	        } catch (Exception e) {
	        	 if (!disposed) {
	        		 throw new MarkdownPreviewWindowException("Unexpected error during preview window creation.", e);
	             }
	            cleanupPreviewResources(currentFile, fxPanel);
	        }
	    });
}

private Object getPreviewKey() {
    return isStringMode ? this : currentFile;
}


private void triggerPreviewUpdate() {
    updateTimer.setActionCommand(String.valueOf(System.identityHashCode(getPreviewKey())));
    updateTimer.restart();
}


private  synchronized void watchFileForChanges() {
	  if (isStringMode) {
		return;
	}
  stopFileWatcher();  // Stop existing watcher before starting a new one

  if (currentFile == null || !currentFile.exists()) {
      return;
  }

  fileWatcher = new FileWatcher(currentFile);
  fileWatcher.setFileChangeListener(changed -> {
      if (changed) {
         // SwingUtilities.invokeLater(this::updatePreviewContent);
          SwingUtilities.invokeLater(this::triggerPreviewUpdate);
      	//triggerPreviewUpdate();
      }
  });//file watcher changign to boolean



  Thread watcherThread = new Thread(fileWatcher);
  watcherThread.setDaemon(true);
  fileWatcherThread = new WeakReference<>(watcherThread);
  watcherThread.start();


}

private synchronized void stopFileWatcher() {
  if (fileWatcher != null) {
      fileWatcher.stopWatching(); // Gracefully stop the watcher
  }

  Thread watcherThread = (fileWatcherThread != null) ? fileWatcherThread.get() : null;

  if (watcherThread != null && watcherThread.isAlive()) {
      watcherThread.interrupt();
      try {
          watcherThread.join(1000); // Wait for the thread to stop
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
      }
  }

  // Clear WeakReference to help with garbage collection
  if (fileWatcherThread != null) {
      fileWatcherThread.clear();
  }
}



private void updatePreviewContent(Object key) {
    PreviewTab previewTab = previewTabs.get(key);
    if (previewTab == null || tabbedPane.indexOfComponent(previewTab.fxPanel) == -1) {
        return;
    }

    WebEngine engine = previewTab.engine;
    if (engine == null) {
		return;
	}

    // Get current content
    String markdown = isStringMode ? OriginalContent : OpenLoom.getContent(currentFile).toString();
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


private void forceFullRefresh(Object key) {
    PreviewTab previewTab = previewTabs.get(key);
    if (previewTab == null) {
		return;
	}

    // Rebuild COMPLETE HTML using getStyledHtml()
    String markdown = isStringMode ? OriginalContent : OpenLoom.getContent(currentFile).toString();
    if (markdown == null) {
        throw new MarkdownPreviewContentException("Content or File is null");
    }

    String fullHtml = render.getStyledHtml(
        render.renderMarkdown(markdown),
        currentFile,
        isDark
    );

    Platform.runLater(() -> {
        previewTab.engine.loadContent(fullHtml); // Full document reload
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



private void addTabWithPreview(JFXPanel fxPanel,Object key) {
    SwingUtilities.invokeLater(() -> {
        if (tabbedPane.indexOfComponent(fxPanel) != -1) {
            tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(fxPanel));
            return;
        }

        Icon tabIcon = icon != null ? icon : null;
         String title = tabName != null ? tabName : isStringMode ?  "Markdown Preview" : "Preview: " + currentFile.getName();


        // Create tab components
        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);
        JLabel titleLabel = new JLabel(title, tabIcon, SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton closeButton = tabBtn != null ? tabBtn : createCloseButton(fxPanel);
        tabHeader.add(titleLabel);
        tabHeader.add(closeButton);

        // Store components in the JFXPanel (which is a JComponent)
        fxPanel.putClientProperty("tabHeader", tabHeader);
        fxPanel.putClientProperty("titleLabel", titleLabel);
        fxPanel.putClientProperty("closeButton", closeButton);
        // Find the source tab position
        int sourceIndex = tabbedPane.indexOfComponent(previewTabs.get(key).fxPanel);
        int insertIndex;

        if (sourceIndex == -1) {
            // Source tab not found, add at end
            insertIndex = tabbedPane.getTabCount();
        } else {
            // Insert after source tab
            insertIndex = sourceIndex + 1;
        }

        // Handle edge cases
        if (insertIndex > tabbedPane.getTabCount()) {
            insertIndex = tabbedPane.getTabCount();
        }

        // Insert or add based on position
        if (tabbedPane.getTabCount() == 0) {
            tabbedPane.addTab(title, icon, fxPanel);
        } else {
            tabbedPane.insertTab(title, icon, fxPanel, null, insertIndex);
        }

        tabbedPane.setTabComponentAt(insertIndex, tabHeader);
        tabbedPane.setSelectedIndex(insertIndex);
    });
}

private JButton createCloseButton(JFXPanel fxPanel) {
    JButton closeButton = new JButton("×");
    closeButton.setFont(new Font("Arial", Font.BOLD, 14));
    closeButton.setMargin(new Insets(0, 2, 0, 2));
    closeButton.setBorder(BorderFactory.createEmptyBorder());
    closeButton.setContentAreaFilled(false);
    closeButton.setFocusPainted(false);
    closeButton.setForeground(UIManager.getColor("Label.foreground"));

    closeButton.addActionListener(e -> {
        int index = tabbedPane.indexOfComponent(fxPanel);
        if (index != -1) {
        	   Object key = fxPanel.getClientProperty("previewKey");
               cleanupPreviewResources(key, fxPanel);
               previewTabs.remove(key);
                tabbedPane.remove(index);
        }
    });

    closeButton.addMouseListener(new MouseAdapter() {
        @Override public void mouseEntered(MouseEvent e) {
            closeButton.setForeground(Color.RED);
            closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        @Override public void mouseExited(MouseEvent e) {
            closeButton.setForeground(UIManager.getColor("Label.foreground"));
            closeButton.setCursor(Cursor.getDefaultCursor());
        }
    });

    return closeButton;
}


public void dispose() {
    if (disposed) {
		return;
	}
    disposed = true;

    if (updateTimer != null && updateTimer.isRunning()) {
        updateTimer.stop();
    }

    if (executor != null && !executor.isShutdown()) {
        executor.shutdownNow();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                // Optional: log or handle
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    List<Object> keys = new ArrayList<>(previewTabs.keySet());

    // First, clean up resources but DO NOT remove previewTabs entries yet
    for (Object key : keys) {
        PreviewTab previewTab = previewTabs.get(key);
        if (previewTab != null) {
            cleanupPreviewResources(key, previewTab.fxPanel);
        }
    }

    // Now remove from UI and clear the map
    SwingUtilities.invokeLater(() -> {
        for (Object key : keys) {
            PreviewTab tab = previewTabs.get(key);
            if (tab != null) {
                JFXPanel panel = tab.fxPanel;
                int index = tabbedPane.indexOfComponent(panel);
                if (index != -1) {
                    tabbedPane.remove(index);
                }
            }
        }
        previewTabs.clear();
    });
}
private void cleanupPreviewResources(Object key, JFXPanel panel) {
    if (panel == null) {
		return;
	}

    if (key instanceof File) {
        stopFileWatcher();
    }

    PreviewTab previewTab = previewTabs.get(key);
    if (previewTab == null) {
		return;
	}

    WebEngine engine = previewTab.engine;
    if (engine != null) {
        Platform.runLater(() -> {
            try {
                if (engine.getLoadWorker().isRunning()) {
                    engine.getLoadWorker().cancel();
                }
                engine.load("about:blank");
            } catch (Exception e) {
                // Log or ignore
            }
        });
    }

    Platform.runLater(() -> {
        try {
            if (panel.getScene() != null) {
                panel.setScene(null);
            }
        } catch (Exception e) {
            // Log or ignore
        }
    });
}




public Icon getTabbedPaneIcon() { return icon; }

public void setTabbedPaneIcon( Icon icon ,int size) {
	this.icon = icon;
    this.iconSize = size;
    // Optionally rescale the icon if it already exists
    if (this.icon instanceof ImageIcon) {
        java.awt.Image original = ((ImageIcon) icon).getImage();
        java.awt.Image resized = original.getScaledInstance(iconSize, iconSize, java.awt.Image.SCALE_SMOOTH);
        this.icon = new ImageIcon(resized);
    }
}
public void setTabbedPaneIcon( Icon icon) { this.icon = icon; }

public int getTabbedPaneIconSize() {
    return iconSize;
}


public void setTabbedPaneButton(JButton btn) { this.tabBtn = btn; }
public JButton getTabbedPaneButton() { return tabBtn; }

public void setTabbedPaneName(String name) { this.tabName = name; }
public String getTabbedPaneName() { return tabName; }

public void setCurrentFile(File file) {
	 if (file == null || !file.exists() || !file.isFile()) {
	        throw new MarkdownPreviewFileException("Invalid file provided: " + file);
	    }
	this.isStringMode = false;
	this.OriginalContent = null;
	this.currentFile = file;
	this.disposed = false;
	 Object key = getPreviewKey();
     PreviewTab previewTab = previewTabs.get(key);
     if (previewTab != null && tabbedPane.indexOfComponent(previewTab.fxPanel) != -1) {
        forceFullRefresh(key);
     }
}
public File getCurrentFile () { return currentFile; }

public void setContent(String content) {
	if (content == null ||content.isEmpty() ) {
        throw new MarkdownPreviewContentException("The String Content should not be null or empty " );
    }
	this.currentFile = null;
	this.isStringMode = true;
	this.OriginalContent = content;
	this.disposed = false;
	 Object key = getPreviewKey();
     PreviewTab previewTab = previewTabs.get(key);
     if (previewTab != null && tabbedPane.indexOfComponent(previewTab.fxPanel) != -1) {
    	 forceFullRefresh(key);
     }
	}
public String getContent() { return OriginalContent; }

public void setDarkMode(boolean dark) {
	this.isDark = dark;
	 Object key = getPreviewKey();
     PreviewTab previewTab = previewTabs.get(key);
     if (previewTab != null && tabbedPane.indexOfComponent(previewTab.fxPanel) != -1) {
    	 forceFullRefresh(key);
     }
}
public boolean getisDarkMode() { return isDark; }

}