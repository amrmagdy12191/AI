---
id: SPEC-001
title: Add a dark mode toggle to the header
status: open
service: my-site
depends_on: []
---

## Goal
Visitors can switch between light and dark themes, and the site remembers their choice.

## Acceptance criteria
- The header contains a button with the text "Dark mode" in light theme and "Light mode" in dark theme
- Clicking it toggles a `data-theme="dark"` attribute on the `<html>` element
- All text keeps a contrast ratio of at least 4.5:1 in both themes
- The choice is saved in `localStorage` and applied before first paint, with no flash of the wrong theme
- If nothing is saved, the theme follows the browser's `prefers-color-scheme`
- `npm run build` succeeds

## Out of scope
- Any change to page content or layout