package io.github.raghultech.markdown.swingfx.preview;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import io.github.raghultech.markdown.swingfx.config.JavaFXUtils;
import io.github.raghultech.markdown.swingfx.config.JavaLinkHandler;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import netscape.javascript.JSObject;

/**
 * A standalone Swing window to display Markdown content.
 * Includes live updates, theme switching, emoji support, and HTML export.
 */



public class MarkdownWindow {
	private FileWatcher fileWatcher;
    //private Thread fileWatcherThread;
	  private transient WeakReference<Thread> fileWatcherThread;
    private static volatile boolean javaFXInitialized = JavaFXUtils.isJavaFXAvailable();;
    private ExecutorService executor;
    private Stage previewStage;
    private WebEngine engine;
    private Timer updateTimer;
    private volatile boolean disposed = false;
 //   private static final String LOGO_PATH = "MD.png";
    private final CountDownLatch cleanupLatch = new CountDownLatch(1);
    private File currentFile ;
    private String OriginalContent;
    private boolean isStringMode = false;
    private String tabName =null;
    private Icon icon;
    private Image fxImage;
    private int frameHeight = 600;
    private int frameWidth = 800;
    private boolean isDark = false;

    private MarkdownRenderer render = MarkdownRenderer.getInstance();

    public MarkdownWindow(File file) {
    	 if (file == null || !file.exists() || !file.isFile()) {
    	        throw new MarkdownPreviewFileException("Invalid file provided: " + file);
    	    }
    	this.currentFile = file;
    	 //initializeJavaFX();
    	//javaFXInitialized = JavaFXUtils.isJavaFXAvailable();
    }

    public MarkdownWindow(String content) {
    	if (content == null ||content.isEmpty() ) {
            throw new MarkdownPreviewContentException("The String Content should not be null or empty " );
        }
    	this.OriginalContent = content;
    	  this.isStringMode = true;
    	//  javaFXInitialized = JavaFXUtils.isJavaFXAvailable();
    }

    public void launchPreview() {
    	if(!javaFXInitialized) throw new MarkdownPreviewSystemException("JavaFX is not available in this environment");

    	 try {
    		 
    	    //    initializeJavaFX();
    	        createExecutor();
    	        setupUpdateTimer();
    	        createAndShowPreviewWindow();
    	        } catch (Exception e) {
    	            dispose(); // Clean up if initialization fails
    	            throw new MarkdownPreviewInitializationException("Failed to initialize Markdown preview window", e);
    	        }
    }

    public synchronized void reopenWindow() {
    	if(!javaFXInitialized) throw new MarkdownPreviewSystemException("JavaFX is not available in this environment");
        if (!disposed) {
            dispose();
        }

        if (currentFile == null && !isStringMode) {
        	 throw new MarkdownPreviewResourceException("Cannot reopen: no file or string content available.");
            // You could also create and throw a custom InvalidPreviewStateException here
        }

        disposed = false;
        createExecutor();
        setupUpdateTimer();
        createAndShowPreviewWindow();
    }

    public boolean isPreviewShowing() {
        //return previewStage != null && previewStage.isShowing();
    	return !disposed;
    }



    private synchronized void createExecutor() {
        if (executor == null || executor.isShutdown()) {
            executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "JavaFXMarkdownPreviewWindow-" + hashCode());
                t.setDaemon(true);
                return t;
            });
        }
    }

	private void setupUpdateTimer() {
    	  if (isStringMode) {
			return;
		}
        updateTimer = new javax.swing.Timer(700, e -> {
            if (!disposed && currentFile != null) {
                updatePreviewContent();
            }
        });
        updateTimer.setRepeats(false);
    }

    private void createAndShowPreviewWindow() {
        Platform.runLater(() -> {
            try {
            	  if (disposed) {
					return;
				}

            	  try {
            		  if (previewStage == null) {
            			    previewStage = new Stage();
            			}
            		} catch (Exception e) {
            		    throw new MarkdownPreviewInitializationException("Failed to create JavaFX Stage.", e);
            		}

             //   previewStage.setTitle("Markdown Preview: " + currentFile.getAbsolutePath());

            	  if(tabName != null) {
            		  previewStage.setTitle(tabName);
            	  }
            	  else if (isStringMode) {
            		  tabName = "Markdown Preview";
                    previewStage.setTitle(tabName);
                 } else {
                	 tabName = "Markdown Preview: " + currentFile.getName();
                    previewStage.setTitle(tabName);
                 }

            	  Image fxIcon = createJavaFXIconImage();
            	  if (fxIcon != null && fxIcon.getWidth() > 0) {
            	      previewStage.getIcons().add(fxIcon);
            	  }

                WebView webView = new WebView();
                engine = webView.getEngine();

                // Add window listener for close event
                previewStage.setOnCloseRequest(event -> {
                	if (!disposed) {
                        dispose();
                    }
                });


                JavaLinkHandler linkHandler = new JavaLinkHandler(this::handleExternalLink);
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

                engine.setJavaScriptEnabled(true);

                Scene scene = new Scene(webView,frameWidth,frameHeight);
              //  scene.getStylesheets().add(getClass().getResource(getCss()).toExternalForm());
                previewStage.setScene(scene);

                previewStage.setOnShown(e -> {
                	   if (!disposed) {
                		   try {
                	            updatefullContent();
                	            if(!isStringMode) {
									watchFileForChanges();
								}
                	        } catch (Exception ex) {
                	            throw new MarkdownPreviewRuntimeException("Failed to initialize preview content or file watcher.", ex);
                	        }
                	   }
                });

                previewStage.setOnCloseRequest(this::handleWindowClose);

                previewStage.show();



            } catch (Exception e) {
            	 if (!disposed) {
            		 throw new MarkdownPreviewWindowException("Unexpected error during preview window creation.", e);
                 }
            }
        });
    }

    private Image createJavaFXIconImage() {
        try {
            // If the user set an FX image, prefer it
            if (fxImage != null) {
                return fxImage;
            }

            // Otherwise, if the user set an AWT Icon
            if (icon instanceof ImageIcon) {
                java.awt.Image awtImage = ((ImageIcon) icon).getImage();
                if (awtImage == null) {
                    throw new IllegalStateException("AWT image from ImageIcon is null.");
                }
                BufferedImage bufferedImage = new BufferedImage(
                    awtImage.getWidth(null),
                    awtImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
                );
                Graphics2D g2d = bufferedImage.createGraphics();
                g2d.drawImage(awtImage, 0, 0, null);
                g2d.dispose();
                return SwingFXUtils.toFXImage(bufferedImage, null);
            }

            // Otherwise, fallback to default
            String resourcePath = "/MD.png";
            java.net.URL url = getClass().getResource(resourcePath);
            if (url == null) {
                throw new  MarkdownPreviewResourceException("Default icon not found: " + resourcePath);
            }
            return new Image(url.toExternalForm());

        } catch (Exception e) {
            throw new MarkdownPreviewResourceException("Failed to create JavaFX Image: " + e, e);
        }
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
            if (changed && !disposed) {
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

    private void triggerPreviewUpdate() {
        if (!disposed && updateTimer != null) {
            updateTimer.restart();
        }
    }
    
    
    private void updatePreviewContent() {
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

    
    private void updatefullContent() {
        if (disposed || engine == null) {
			return;
		}

        String markdown = isStringMode ? OriginalContent : OpenLoom.getContent(currentFile).toString();
        if (markdown == null) {
            throw new MarkdownPreviewResourceException("Content or File is null");
        }


        String html = render.renderMarkdown(markdown);
        String styledHtml = render.getStyledHtml(html, currentFile,isDark);

        Platform.runLater(() -> engine.loadContent(styledHtml));
    }


    private void handleWindowClose(WindowEvent event) {
        dispose();
    }

    public synchronized void dispose() {
        if (disposed) {
			return;
		}
        disposed = true;

        // Stop any pending updates
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
        stopFileWatcher();

        // Shutdown executor
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }

        // Clean up JavaFX resources
        if (Platform.isFxApplicationThread()) {
            cleanupFxResources();
        } else {

            Platform.runLater(() -> {
                try {
                    cleanupFxResources();
                } finally {
                    cleanupLatch.countDown();
                }
            });
            try {
                cleanupLatch.await(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void cleanupFxResources() {
        try {
            if (engine != null) {
                engine.load(null);
            }
        } catch (Exception e) {
           // System.err.println("Error cleaning up WebEngine: " + e.getMessage());
        }

        try {
            if (previewStage != null) {
                previewStage.close();
            }
        } catch (Exception e) {
          //  System.err.println("Error closing stage: " + e.getMessage());
        }

        // Clear references
        engine = null;
        previewStage = null;
    }



    public void setIconFX(Image fxImage) {
       this.fxImage = fxImage;
       this.icon = null;
    }

    public void setIcon(Icon icon) {
    	this.icon = icon;
    	this.fxImage = null;
    }

    public Icon getIcon() {
        return icon;
    }

    public Image getIconFX() {
       return fxImage;
    }

    public  void setWindowSize(int width, int height) {
    	this.frameWidth = width;
    	this.frameHeight = height;
    }

    public int getWindowWidth() { return frameWidth; }
    public int getWindowHeight() { return frameHeight; }



    public void setWindowName(String name) { this.tabName = name; }
    public String getWindowName() { return tabName; }


    public void setCurrentFile(File file) {
    	this.isStringMode = false;
    	this.OriginalContent = null;
    	this.currentFile = file;
    	this.disposed = false;
    	updatefullContent();
    }
    public File getCurrentFile () { return currentFile; }

    public void setContent(String content) {
    	this.currentFile = null;
    	this.isStringMode = true;
    	this.OriginalContent = content;
    	this.disposed = false;
    	updatefullContent();
    	}
    public String getContent() { return OriginalContent; }

    public void setDarkMode(boolean dark) {
    	this.isDark = dark;
    	 updatefullContent();
    }
    public boolean getisDarkMode() { return isDark; }

}