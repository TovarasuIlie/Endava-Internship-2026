# Change ledger

Application edits, appended automatically by the `change-ledger` hook and by `memory.mjs log-change`.
Format: `- YYYY-MM-DD HH:MM | file | tool (area) | agent`. Consumed by `/tap-sync-tests` and `impact.mjs`.

- 2026-10-22 13:47 | backend/contact-routes.ts | Return 404 when deleting a contact with a valid unknown ID | be-dev
- 2026-10-22 13:47 | backend/database.ts | Guard contact removal against missing IDs to prevent deleting all contacts | be-dev
- 2026-10-22 14:00 | playwright/tests/api/social.api.spec.ts | Add KI-01 contact deletion regression coverage | api-automation
- 2026-10-22 17:26 | src/machines/authMachine.ts | Fix stale profile balance after auth-state restoration | fe-dev
