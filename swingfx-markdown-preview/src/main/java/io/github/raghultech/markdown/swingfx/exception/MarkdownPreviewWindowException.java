package io.github.raghultech.markdown.swingfx.exception;

@SuppressWarnings("serial")
public class MarkdownPreviewWindowException extends RuntimeException {
    public MarkdownPreviewWindowException(String message, Throwable cause) {
        super(message, cause);
    }
    public MarkdownPreviewWindowException(String message) {
        super(message);
    }
}
