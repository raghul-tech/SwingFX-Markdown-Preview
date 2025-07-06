package io.github.raghultech.markdown.swingfx.exception;

@SuppressWarnings("serial")
public class MarkdownPreviewRenderException extends RuntimeException {
    public MarkdownPreviewRenderException(String message) {
        super(message);
    }

    public MarkdownPreviewRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}

