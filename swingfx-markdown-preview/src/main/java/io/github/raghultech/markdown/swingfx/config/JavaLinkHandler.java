package io.github.raghultech.markdown.swingfx.config;

import java.util.function.Consumer;

public class JavaLinkHandler {
	        private final Consumer<String> linkHandler;

	        public JavaLinkHandler(Consumer<String> linkHandler) {
	            this.linkHandler = linkHandler;
	        }

	        public void handleLink(String url) {
	                linkHandler.accept(url);
	        }
	    }
