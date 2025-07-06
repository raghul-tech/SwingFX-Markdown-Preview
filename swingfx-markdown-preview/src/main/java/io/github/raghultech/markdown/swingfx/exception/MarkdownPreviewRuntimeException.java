package io.github.raghultech.markdown.swingfx.exception;

@SuppressWarnings("serial")
public class MarkdownPreviewRuntimeException extends RuntimeException{

	 public MarkdownPreviewRuntimeException(String message) {
	        super(message);
	    }

	    public MarkdownPreviewRuntimeException(String message, Throwable cause) {
	        super(message, cause);
	    }

}
