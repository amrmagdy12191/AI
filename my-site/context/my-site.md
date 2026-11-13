# Service context — my-site

## What this service is

A personal static site built with Vite and plain HTML/CSS. Deployed to GitHub Pages.

## Repository layout

```
index.html          Single-page entry point; all markup lives here
src/
  style.css         All styles; CSS custom properties used for theming
vite.config.js      Vite config; base set to './' for GitHub Pages compatibility
specs/              Feature specs (managed by Spec Kit); one .md file per feature
context/            MCP server context files; this file
```

No JavaScript modules yet. The page has no bundled JS — only HTML and CSS.

## Key design decisions

- **CSS custom properties for theming.** `:root` defines `--bg`, `--fg`, `--muted`, `--accent`, `--card`, `--border`. All colours are vars. A `@media (prefers-color-scheme: dark)` block overrides them. Any theme toggle must follow this pattern.
- **Single HTML file.** There is no component system. HTML changes go in `index.html` directly.
- **No test suite.** The only correctness gate is `npm run build` (Vite build must succeed).

## Public API surface

None — this is a rendered HTML page, not a library or service.

## Build and test commands

| Command | What it does |
| --- | --- |
| `npm run dev` | Start Vite dev server at localhost:5173 |
| `npm run build` | Production build to `dist/`; must pass for a PR to merge |
| `npm run preview` | Serve the `dist/` build locally |

## Key dependencies

| Package | Version | Role |
| --- | --- | --- |
| vite | ^8.3.6 | Build tool and dev server |

## Header structure (relevant to dark-mode spec)

```html
<header class="site-header">
  <a class="brand" href="./">My Site</a>
  <nav>
    <a href="#about">About</a>
    <a href="#projects">Projects</a>
    <a href="#contact">Contact</a>
  </nav>
</header>
```

The toggle button (SPEC-001) goes inside `.site-header`, after the `<nav>`.

## CSS token values (light / dark)

| Token | Light | Dark |
| --- | --- | --- |
| `--bg` | `#fafafa` | `#111214` |
| `--fg` | `#1a1a1a` | `#ececec` |
| `--muted` | `#5c5c5c` | `#a0a0a0` |
| `--accent` | `#2563eb` | `#60a5fa` |
| `--card` | `#ffffff` | `#1a1b1e` |
| `--border` | `#e5e5e5` | `#2a2b2f` |

A `data-theme="dark"` attribute on `<html>` (toggled by JS) must override both the light defaults and the `prefers-color-scheme` media query so the user's explicit choice always wins.
