# Release notes

User-visible changes to the Real World App made through TAP26 (newest first). Generated and maintained by
`doc-writer` via `/tap-docs release-notes` from `tap26/memory/changes.md`, `git log` and
`tap26/openapi/CHANGELOG.md`, using `tap26/templates/release-notes.md`.

## KI-01 — 2026-10-22
- Fixed `DELETE /contacts/{contactId}`: a well-formed unknown ID now returns `404` without deleting contacts.
- Added API regression coverage for repeated deletion of a contact ID and preservation of all users' contacts.

## TAP26-Junior 1.0.0 baseline — 2026-10-16
- No application changes yet. Baseline: Cypress Real World App `develop` + Playwright framework (`playwright/`)
  + TAP26-Junior framework. Known API defects are catalogued in `tap26/knowledge/known-issues.md`.

## Test tooling — 2026-10-22
- Added the Playwright `ui-tablet` project at 500×800, with the `test:ui:tablet` script, to exercise
  responsive UI behavior at an intermediate viewport.
