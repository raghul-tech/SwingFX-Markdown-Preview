package io.github.raghultech.markdown.swingfx.config;

public class SwingFXTheme {


    private static final String FONT_CSS = """
        @font-face {
            font-family: 'Twemoji Mozilla';
            src: local('Apple Color Emoji'),
                 local('Segoe UI Emoji'),
                 local('Segoe UI Symbol'),
                 local('Noto Color Emoji');
            unicode-range: U+1F300-1F5FF, U+1F600-1F64F, U+1F680-1F6FF,
                           U+2600-26FF, U+2700-27BF, U+FE00-FE0F, U+1F900-1F9FF;
        }
        body, body * {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
                          Helvetica, Arial, sans-serif, "Twemoji Mozilla";
        }
        """;

    public static final String LIGHT_CSS = FONT_CSS + """
        body {
            background: #ffffff;
            color: #333333;
            line-height: 1.6;
            padding: 20px;
            max-width: 800px;
            margin: 0 auto;
        }
        h1, h2, h3, h4, h5, h6 {
            margin: 1.2em 0 0.6em;
            font-weight: 600;
            line-height: 1.25;
        }
        h1 { font-size: 2em; border-bottom: 1px solid #cccccc; }
        h2 { font-size: 1.5em; }
        h3 { font-size: 1.25em; }
        p { margin: 1em 0; }
        a {
            color: #0366d6;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        ul, ol {
            padding-left: 2em;
        }
        li { margin: 0.4em 0; }
        blockquote {
            margin: 1em 0;
            padding: 0 1em;
            border-left: 4px solid #0366d6;
            background-color: rgba(0,0,0,0.05);
        }
        :not(pre) > code {
            background: #f0f0f0;
            padding: 0.2em 0.4em;
            border-radius: 3px;
            font-size: 0.9em;
        }
        pre {
            border: 1px solid #dddddd;
            border-radius: 6px;
            margin: 1em 0;
            overflow: auto;
        }
        pre code {
            display: block;
            padding: 16px;
        }
        table {
            border-collapse: collapse;
            margin: 1.5em 0;
            width: 100%;
        }
        th, td {
            border: 1px solid #cccccc;
            padding: 8px 12px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: rgba(0,0,0,0.02);
        }
        hr {
            border: 0;
            height: 1px;
            background-color: #eeeeee;
            margin: 2em 0;
        }
        img {
            max-width: 100%;
        }
        .emoji-container {
            display: inline-block;
            vertical-align: middle;
            line-height: 1;
        }
        .emoji {
            height: 1.2em;
            width: 1.2em;
            vertical-align: middle;
            margin: 0 0.05em;
        }
         .emoji-fallback {
	                font-family: "Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", sans-serif;
	            }
        pre code, code {
            font-family:
                "SFMono-Regular",
                Consolas,
                "Liberation Mono",
                Menlo,
                monospace,
                "Apple Color Emoji",
                "Segoe UI Emoji",
                "Noto Color Emoji";
        }
        .code-block {
            position: relative;
        }
        .copy-button {
            position: absolute;
            top: 6px;
            right: 6px;
            background: #f6f8fa;
            border: 1px solid #d0d7de;
            border-radius: 4px;
            padding: 4px 8px;
            font-size: 0.75em;
            cursor: pointer;
            opacity: 0.6;
            z-index: 10;
            transition: opacity 0.2s, background 0.2s, border-color 0.2s;
        }
        .copy-button:hover {
            opacity: 1;
        }
        .copy-button.copied {
            opacity: 1;
            background: #d1f0d1;
            border-color: #6cc070;
        }
        .code-block.copied pre {
            box-shadow: 0 0 0 2px #6cc070;
            border-color: #6cc070;
        }
        """;

    public static final String DARK_CSS = FONT_CSS + """
        body {
            background: #1e1e1e;
            color: #cccccc;
            line-height: 1.6;
            padding: 20px;
            max-width: 800px;
            margin: 0 auto;
        }
        h1, h2, h3, h4, h5, h6 {
            margin: 1.2em 0 0.6em;
            font-weight: 600;
            line-height: 1.25;
        }
        h1 { font-size: 2em; border-bottom: 1px solid #444444; }
        h2 { font-size: 1.5em; }
        h3 { font-size: 1.25em; }
        p { margin: 1em 0; }
        a {
            color: #58a6ff;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        ul, ol {
            padding-left: 2em;
        }
        li { margin: 0.4em 0; }
        blockquote {
            margin: 1em 0;
            padding: 0 1em;
            border-left: 4px solid #4a9cff;
            background-color: rgba(255,255,255,0.05);
        }
        :not(pre) > code {
          font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
            background: #373737;
            padding: 0.2em 0.4em;
            border-radius: 3px;
            font-size: 0.9em;
        }
        pre {
            border: 1px solid #444444;
            border-radius: 6px;
            margin: 1em 0;
            overflow: auto;
        }
        pre code {
            display: block;
            padding: 16px;
        }
        table {
            border-collapse: collapse;
            margin: 1.5em 0;
            width: 100%;
        }
        th, td {
            border: 1px solid #444444;
            padding: 8px 12px;
            text-align: left;
        }
        th {
            background-color: #2a2a2a;
            font-weight: 600;
        }
        tr:nth-child(even) {
            background-color: rgba(255,255,255,0.02);
        }
        hr {
            border: 0;
            height: 1px;
            background-color: #333333;
            margin: 2em 0;
        }
        img {
            max-width: 100%;
        }
        .emoji-container {
            display: inline-block;
            vertical-align: middle;
            line-height: 1;
        }
        .emoji {
            height: 1.2em;
            width: 1.2em;
            vertical-align: middle;
            margin: 0 0.05em;
        }
         .emoji-fallback {
	                font-family: "Apple Color Emoji", "Segoe UI Emoji", "Noto Color Emoji", sans-serif;
	            }
        pre code, code {
            font-family:
                "SFMono-Regular",
                Consolas,
                "Liberation Mono",
                Menlo,
                monospace,
                "Apple Color Emoji",
                "Segoe UI Emoji",
                "Noto Color Emoji";
        }
        .code-block {
            position: relative;
        }
        .copy-button {
            position: absolute;
            top: 6px;
            right: 6px;
            background: #2d2d2d;
            border: 1px solid #444444;
            border-radius: 4px;
            padding: 4px 8px;
            font-size: 0.75em;
            cursor: pointer;
            opacity: 0.6;
            z-index: 10;
            transition: opacity 0.2s, background 0.2s, border-color 0.2s;
        }
        .copy-button:hover {
            opacity: 1;
        }
        .copy-button.copied {
            opacity: 1;
            background: #3a523a;
            border-color: #6cc070;
        }
        .code-block.copied pre {
            box-shadow: 0 0 0 2px #6cc070;
            border-color: #6cc070;
        }
        """;
}
