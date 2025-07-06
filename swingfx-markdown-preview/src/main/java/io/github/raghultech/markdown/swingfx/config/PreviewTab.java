package io.github.raghultech.markdown.swingfx.config;

import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;

public  class PreviewTab {
    public final JFXPanel fxPanel;
    public  final WebEngine engine;
   // final Object key;

   public  PreviewTab(JFXPanel fxPanel, WebEngine engine) {
        this.fxPanel = fxPanel;
        this.engine = engine;
      //  this.key = fxPanel.getClientProperty("previewKey");
    }
}