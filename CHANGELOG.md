# 📄 Changelog

All notable changes to **SwingFX Markdown Preview** are documented in this file.

This project follows [Semantic Versioning](https://semver.org/).

---

## [1.0.0] - 2025-07-04

🎉 **Initial Release**

### 🪶 SwingFX Markdown Preview (Core)

- First release of the **core library**.
- Provides:
  - Swing components (panel, window, tabbed preview) for rendering Markdown via JavaFX WebView.
  - Theme switching (light/dark).
  - Live preview updates.
  - File change detection and reload prompts.
  - HTML export support.
- **Does NOT include Flexmark or JavaFX**—developers must add them separately.

---

### ✨ SwingFX Markdown Preview (Flexmark)

- First release of the **Flexmark preconfigured module**.
- Includes:
  - All features of the Core module.
  - Pre-bundled **Flexmark (`flexmark-all 0.64.8`)** for Markdown parsing.
  - JavaFX libraries still **required separately** on the classpath or module path.
- Recommended if you want Flexmark included but manage JavaFX yourself.

---

### 💼 SwingFX Markdown Preview (All-In-One)

- First release of the **fat jar** distribution.
- Bundles:
  - **Flexmark** for Markdown rendering.
  - **All JavaFX libraries** for out-of-the-box runtime.
- Designed for:
  - Easiest setup with no external dependencies.
  - Immediate integration in Swing applications.

---

## 📦 Artifacts

| Module                                  | Maven Coordinates                                                          |
|-----------------------------------------|----------------------------------------------------------------------------|
| Core (no Flexmark, no JavaFX)           | `io.github.raghul-tech:swingfx-markdown-preview:1.0.0`                    |
| Flexmark (includes Flexmark only)       | `io.github.raghul-tech:swingfx-markdown-preview-flexmark:1.0.0`           |
| All-In-One (Flexmark + JavaFX)          | `io.github.raghul-tech:swingfx-markdown-preview-all:1.0.0`                |

---

## 🚀 How to Upgrade

This is the initial release—no prior versions exist.

---

## 🔗 Links

- [Repository](https://github.com/raghul-tech/SwingFX-Markdown-Preview)
- [Issue Tracker](https://github.com/raghul-tech/SwingFX-Markdown-Preview/issues)

---
