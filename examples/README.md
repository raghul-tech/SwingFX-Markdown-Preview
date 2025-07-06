# 📝 SwingFX Markdown Preview 

- A modern Java library that uses **JavaFX WebView embedded in Swing** to render live Markdown previews.

- This JAR includes: 

	- ✅ Real-time rendering   
	- ✅ Theme switching (light/dark)   
	- ✅ String and File support  
	- ✅ HTML export   
	- ✅ Fully embeddable Swing components

--- 


## 📦 Key Classes

Below is a quick guide to each class and what they do:

---

## 🎯 `MarkdownPanel` (Embeddable Preview)

> ✅ **Use this when you want a `JPanel` you can put anywhere.**

### ✨ How to Use

```java

// Create from File
MarkdownPanel preview = new MarkdownPanel(new File("README.md"));

// Or create from String
MarkdownPanel preview = new MarkdownPanel("# Hello Markdown");

// Add to any container
frame.getContentPane().add(preview);

```

---

## 🎛 Features 
- setCurrentFile(File file): Load a different file at runtime.

- setContent(String text): Load Markdown from a String.

- setDarkMode(boolean): Toggle dark/light theme.

- dispose(): Clean up resources.

- isPreviewShowing(): Check if the preview is visible.

---
 
## 🎯 `MarkdownTabbedview` (Tabbed Interface) 
> ✅ Use this when you want to add a preview as a tab in your JTabbedPane.

### ✨ How to Use

```java

JTabbedPane tabs = new JTabbedPane();
MarkdownTabbedview preview = new MarkdownTabbedview(tabs, new File("README.md"));
preview.launchPreviewTab();

```

### 🎛 Features

- launchPreviewTab() : launch the tab 

- setCurrentFile(File): Change content.

- setContent(String): Load Markdown text.

- setTabbedPaneIcon(Icon, int size): Set an image in the tab.

- setDarkMode(boolean): Toggle theme.

- dispose(): Clean up.

- isPreviewShowing(): Check if preview is open.

- relaunchPreviewTab(): Recreate the tab.

> Don’t call launchPreviewTab() again on the same object without first disposing.

---

## 🎯 `MarkdownWindow` (Standalone Viewer)
> ✅ Use this when you want a standalone preview window.

### ✨ How to Use
 
```java
MarkdownWindow preview = new MarkdownWindow(new File("README.md"));
preview.launchPreview();
```

### 🎛 Features
- setCurrentFile(File)

- setContent(String)

- setIcon(Icon icon): Set window icon.

- setDarkMode(boolean)

- reopenWindow(): Reopen after closing.

- dispose()

- isPreviewShowing()

---

## 🧪 `MarkdownRenderer` (Convert Markdown to HTML)
> ✅ Use this utility if you only want to get the rendered HTML as a String (without displaying it).

### ✨ How to Use

```java 
MarkdownRenderer renderer = new MarkdownRenderer();
//or 
MarkdownRenderer renderer = MarkdownRenderer.getInstance();

// Convert Markdown to HTML
String html = renderer.renderMarkdown("# Hello");

// Optionally get styled HTML
// If no file, pass null. But the content must NOT be null.
String styledHtml = renderer.getStyledHtml(
    "# Hello",
    new File("README.md"),
    true // true = dark theme
);

// Tip: You can also read a file into a string yourself, e.g.:
// String content = OpenLoom.getContent(new File("README.md")).toString();



```

---

## 🛠️ Common Operations
### All preview classes support:

| Method                    | Purpose                           |
| ------------------------- | --------------------------------- |
| `setCurrentFile(File)`    | Load Markdown from a file         |
| `setContent(String)`      | Load Markdown from a string       |
| `setDarkMode(boolean)`    | Enable dark mode                  |
| `dispose()`               | Release resources                 |
| `isPreviewShowing()`      | Check visibility                  |
| ` reopenWindow` / ` relaunchPreviewTab()` | Re-show the preview tab or window |


---

## 🏗️ Example Use Cases

- Embed a Markdown preview in your Swing form

- Add a preview tab to an editor

- Show a standalone Markdown window

- Get HTML strings for custom rendering

---

## ❤️ License
* This project is licensed under the [MIT License](https://github.com/raghul-tech/SwingFX-Markdown-Preview/blob/main/LICENSE)


