package io.github.raghultech.markdown.swingfx.exception;



@SuppressWarnings("serial")
public class MarkdownPreviewInitializationException extends RuntimeException {
    public MarkdownPreviewInitializationException(String message) {
        super(message);
    }

    public MarkdownPreviewInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
