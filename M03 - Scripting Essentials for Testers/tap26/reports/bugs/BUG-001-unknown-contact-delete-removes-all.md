# BUG-001 — DELETE /contacts/{id} removes all contacts for an unknown id

| Field | Value |
|---|---|
| Status | fixed |
| Severity | critical — a single valid-looking but unknown contact id deletes the entire contact table |
| Priority | P1 — destructive data loss affects every user and has no in-app recovery path |
| Area | `DELETE /contacts/:contactId` |
| Reported by / date | student / 2026-10-22 |
| Related | KI-01 in `tap26/knowledge/known-issues.md` |
| Reproduction rate | 2/2 |

## Environment
- Commit: `eeadef7` · Branch: `m03_feature/task_2`
- App: UI http://localhost:3000 · API http://localhost:3001 · DB reseeded before repro: yes, before each run
- Browser / viewport: API only · OS: Windows 10.0.26200.0 · Node: v22.20.0
- User: `Heath93` (password `s3cret`)

## Preconditions
- The app and API are running locally.
- The seeded database has 15 contacts across five users; authenticate as seeded user `Heath93`.

## Steps to reproduce
1. Reset the local database with `POST http://localhost:3001/testData/seed`.
2. Log in with `POST http://localhost:3001/login`, JSON body `{"username":"Heath93","password":"s3cret"}`, and retain the session cookie.
3. Confirm the seed has contacts: `GET http://localhost:3001/contacts/Heath93` returns three contacts.
4. Generate a valid short ID that is not one of those contact IDs (observed examples: `gqnZPgisI` and `2pPBu2GKv`).
5. Send authenticated `DELETE http://localhost:3001/contacts/<unknown-short-id>`.
6. Query `GET /contacts/<username>` for each of the five seeded users.

## Expected result
The unknown contact ID should not delete any contacts and should return `404 Not Found` (status selected by the student and now documented in OpenAPI).

## Actual result
Both attempts returned `200` with `{}`. After each attempt, all five users had zero contacts (15 seeded contacts removed).

## Evidence
```http
DELETE /contacts/gqnZPgisI

→ 200
{}

Afterward: Heath93 0, Arvilla_Hegmann 0, Dina20 0, Reyes.Osinski 0,
Judah_Dietrich50 0 contacts (15 total removed).

Second clean-seed attempt:
DELETE /contacts/2pPBu2GKv

→ 200
{}

Afterward: all five users had 0 contacts (15 total removed).
```
- Screenshot / trace: not applicable; reproduced directly through the local API.
- `data-test` elements involved: none.

## Impact
Any authenticated caller can submit a syntactically valid but unknown short ID and remove every user's contacts. This is destructive cross-user data loss; no in-app workaround or recovery path was identified. The database can be restored from the seed, but that overwrites current local data.

## Root cause (confirmed by debugger)
- **Status: confirmed (KI-01).** `backend/contact-routes.ts:35-46` validates the request and then passes `contactId` to `removeContactById` (`:39-43`), but does not check whether a contact was found. `backend/validators.ts:18-20` checks only whether the value has valid short-ID syntax; a well-formed unknown ID therefore passes validation. In `backend/database.ts:227-231`, `getContactBy("id", contactId)` returns `undefined` when there is no match; the shared lookup at `backend/database.ts:121-129` returns the `.find(...).value()` result unchanged. That `undefined` is passed to Lowdb's `.remove(contact)` call (`database.ts:230`), which removes all contacts when given no predicate/match value. The route then reports success (`200`) without checking whether anything was removed. This explains the reproduced deletion of all 15 contacts.
- **Fix implemented:** `removeContactById` now returns `false` for a missing match without calling `.remove()`; the route returns 404 in that case. Existing-contact deletion remains 200 with `{}`.

## Regression test
- `playwright/tests/api/social.api.spec.ts` › "DELETE /contacts/:contactId returns 404 for a deleted contact without deleting other contacts" (passed twice; asserts exact 404 and unchanged contact lists for all five seeded users).

## Notes
- Reproduction used a clean seed before each attempt. The seed reset was approved by the student.
- The existing known-issue catalogue identifies this behavior as KI-01.
