<h1 align="center">📝 SwingFX-Markdown-Preview</h1>

<p align="center">
  <em>Render rich, real-time Markdown previews inside Java Swing apps using JavaFX WebView.</em>
</p>

<p align="center">
  <strong>⚡ Fast. 🖼️ Beautiful. 🎯 Real-Time. 🌓 Theme-Aware.</strong>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.raghul-tech/swingfx-markdown-preview">
    <img src="https://img.shields.io/maven-central/v/io.github.raghul-tech/swingfx-markdown-preview?style=for-the-badge&color=blueviolet" alt="Maven Central" />
  </a>
  <a href="https://github.com/raghul-tech/SwingFX-Markdown-Preview/actions/workflows/maven.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/raghul-tech/SwingFX-Markdown-Preview/maven.yml?label=Build&style=for-the-badge&color=brightgreen" alt="Build Status" />
  </a>
  <a href="https://github.com/raghul-tech/SwingFX-Markdown-Preview/actions/workflows/codeql.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/raghul-tech/SwingFX-Markdown-Preview/codeql.yml?label=CodeQL&style=for-the-badge&color=informational" alt="CodeQL Security" />
  </a>
  <a href="https://javadoc.io/doc/io.github.raghul-tech/swingfx-markdown-preview">
    <img src="https://img.shields.io/badge/Javadoc-1.0.0-blue?style=for-the-badge&logo=java" alt="Javadoc" />
  </a>
  <a href="https://github.com/raghul-tech/Swingfx-Markdown-Preview/releases">
    <img src="https://img.shields.io/github/release/raghul-tech/Swingfx-Markdown-Preview?label=Release&style=for-the-badge&color=success" alt="Latest Release" />
  </a>
  <a href="https://buymeacoffee.com/raghultech">
    <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-Support-orange?style=for-the-badge&logo=buy-me-a-coffee" alt="Buy Me A Coffee" />
  </a>
</p>

---

## ✨ Overview

**SwingFX-Markdown-Preview** is a modern library for rendering live, GitHub-style Markdown previews inside **Java Swing applications**.

It uses **JavaFX WebView embedded inside Swing via `JFXPanel`** to deliver smoother, more advanced rendering than traditional `JEditorPane` or `HTMLEditorKit`.

---

## 💼 What Makes It Special?

- 📦 Works inside any Swing app
- 🌐 Uses JavaFX WebView via `JFXPanel` (not JavaFX stage)
- 🧠 Real-time preview updates as the file changes
- 🎨 Theme support (light/dark toggle)
- 🧩 Modular architecture (choose minimal or fat jar)
- 🧰 Includes Flexmark (optionally) for Markdown parsing
- ✅ Compatible with JavaFX 11+ and Java 8+

---

## 📦 Available Modules

| Artifact Name                       | Includes Flexmark | Includes JavaFX | Use Case                          |
|------------------------------------|-------------------|-----------------|-----------------------------------|
| `swingfx-markdown-preview`         | ❌ No             | ❌ No           | For Maven users with own setup    |
| `swingfx-markdown-preview-flexmark`| ✅ Yes            | ❌ No           | Add your own JavaFX separately    |
| `swingfx-markdown-preview-all`     | ✅ Yes            | ✅ Yes          | Just works, no manual setup       |

---

## 🚀 Installation

### 🛠️ Option 1: All-in-One (Flexmark + JavaFX)

```xml
<dependency>
  <groupId>io.github.raghul-tech</groupId>
  <artifactId>swingfx-markdown-preview-all</artifactId>
  <version>1.0.0</version>
</dependency>
```

### - 📶 Option 2: Code + Flexmark (included flexmark)

```xml
<dependency>
  <groupId>io.github.raghul-tech</groupId>
  <artifactId>swingfx-markdown-preview-flexmark</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>org.openjfx</groupId>
  <artifactId>javafx-controls</artifactId>
  <version>21.0.1</version>
</dependency>

<!-- Also include: javafx-web, javafx-swing, javafx-fxml, javafx-graphics, javafx-base -->

```

### 🪶 Option 3: Minimal (bring your own Flexmark + JavaFX)

```xml
<dependency>
  <groupId>io.github.raghul-tech</groupId>
  <artifactId>swingfx-markdown-preview</artifactId>
  <version>1.0.0</version>
</dependency>

<dependency>
  <groupId>com.vladsch.flexmark</groupId>
  <artifactId>flexmark-all</artifactId>
  <version>0.64.8</version>
</dependency>

<dependency>
  <groupId>org.openjfx</groupId>
  <artifactId>javafx-controls</artifactId>
  <version>21.0.1</version>
</dependency>

<!-- Also include: javafx-web, javafx-swing, javafx-fxml, javafx-graphics, javafx-base -->
```

---

## 💡 Key Features

- ✅ Live File Monitoring: auto-refreshes when Markdown file changes
- ✅ GitHub-Flavored Markdown (matches the style of GitHub's Markdown)
- ✅ Theme Toggle: light or dark mode with `.setDarkMode(true)`
- ✅ HTML Export: easily convert Markdown to clean, styled HTML strings
- ✅ Emoji Support
- ✅ Component Types:
  - MarkdownPanel
  - MarkdownTabbedview
  - MarkdownWindow
  - MarkdownRenderer (no GUI)

- ✅ HTML export and emoji support

- ✅ No JavaFX Stage needed

---

## 🧪 Quick Example

```java
import io.github.raghultech.markdown.swingfx.preview.MarkdownWindow;
import java.io.File;

public class PreviewDemo {
    public static void main(String[] args) {
        File file = new File("README.md");
        MarkdownWindow preview = new MarkdownWindow(file);
        preview.setWindowTitle("Markdown Live Preview");
        preview.setWindowSize(700, 700);
      //  preview.isDarkMode(true);
        preview.launchPreview();
    }
}

```

---

## 🎨 Theme Switching
- Toggle dark mode anytime:
```bash
preview.setDarkMode(true); // dark mode ON
preview.setDarkMode(false); // light mode
```
- Live updates automatically.

---

## 📂 How to Use the JAR

### Compile:

```bash
javac -cp swingfx-markdown-preview-all-1.0.0.jar MyPreviewApp.java
```

### Run:
> Windows:
```bash
java -cp .;swingfx-markdown-preview-all-1.0.0.jar MyPreviewApp
```
> macOS/Linux:
```bash
java -cp .:swingfx-markdown-preview-all-1.0.0.jar MyPreviewApp
```
---

## 🏗️ Example Projects
- You’ll find ready-to-run examples in the examples/ directory:

	- [`ExamplePanel.java`](examples/ExamplePanel.java) – Embed preview as a JPanel

	- [`ExampleTabbedPane.java`](examples/ExampleTabbedPane.java) – Add preview as a new tab in JTabbedPane

	- [`ExampleWindow.java`](examples/ExampleWindow.java) – Show preview in a standalone window
	
	- [`ExampleRenderer.java`](examples/ExampleRenderer.java) –  convert a Markdown file to styled HTML 

✅ To run an example:

1. Download or clone this repository.

2. Navigate to examples/.

3. Compile and run the desired file.

---

## 🔍 Documentation

- 📚 [Javadoc](https://javadoc.io/doc/io.github.raghul-tech/swingfx-markdown-preview)

- 📝 [Changelog](CHANGELOG.md)

- ❓ [Issue Tracker](https://github.com/raghul-tech/Swingfx-Markdown-Preview/issues)

---

## 🆕 Changelog

- see [CHANGELOG.md](CHANGELOG.md) for release history.

---

## 🤝 Contributing
- We welcome all contributions!

	- 🐛 Bug fixes

	- ✨ Features

	- 📝 Documentation improvements

	- 🧪 Example enhancements

👉 [Contributing Guide](CONTRIBUTING.md)

---

## 🐞 Report a Bug
- Found an issue? [Open an Issue](https://github.com/raghul-tech/Swingfx-Markdown-Preview/issues) with clear details.

---

## 📄 License
- This project is licensed under the [MIT License](LICENSE).

---

## ☕ Support
- If you love this project, you can [Buy Me a Coffee](https://buymeacoffee.com/raghultech) ❤








