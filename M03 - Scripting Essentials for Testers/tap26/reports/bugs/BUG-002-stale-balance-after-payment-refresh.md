# BUG-002 — Bob's profile balance stays stale after reloading following Alice's payment

| Field | Value |
|---|---|
| Status | confirmed |
| Severity | major — Bob sees an incorrect financial balance after a completed payment |
| Priority | P2 — balance display needs to refresh to reflect completed transactions |
| Area | Authenticated profile balance / `authMachine.ts` |
| Reported by / date | manual-tester / 2026-10-22 |
| Related | Planned KI-34; KI-30 is related but distinct (see Notes) |
| Reproduction rate | 2/2 |

## Environment
- Commit: `eeadef7` · Branch: `m03_feature/task_7`
- App: UI http://localhost:3000 · API http://localhost:3001 · DB reseeded before each repro: yes
- Browser / viewport: Playwright Chromium / 1280×1000 · OS: Windows · Node: v22.20.0
- Users: Alice `Heath93` (ID `uBmeaz5pX`) and Bob `Arvilla_Hegmann` (ID `GjWovtg2hr`); password `s3cret`

## Preconditions
- Reseed the DB. Use two browser contexts so Bob stays authenticated while Alice submits the payment.

## Steps to reproduce
1. In the Bob context, sign in as `Arvilla_Hegmann` and note the profile balance ($930.26).
2. In the separate Alice context, sign in as `Heath93`, create a payment to `Arvilla_Hegmann` for **$67**, and submit it. Confirm the UI shows `Transaction Submitted!`.
3. In Bob's still-authenticated context, perform a full page reload without signing out.
4. Check `sidenav-user-balance`. This is the Bob-sees-Alice-payment-after-reload journey referenced by `playwright/tests/e2e/bob-sees-alice-payment.e2e.spec.ts`.

## Expected result
Bob's profile balance updates from $930.26 to $997.26 after reload, reflecting the completed $67 payment — source: the stated scenario expectation.

## Actual result
Bob's profile continues to show the pre-payment balance after reload. In both attempts, Alice's `POST /transactions` returned 200; the payment was persisted as complete for $67 with Alice's and Bob's IDs, and Bob's DB balance delta was exactly +6,700 cents. Bob's UI nevertheless remained at the old balance.

## Evidence
- Two initial `--repeat-each=2` attempts reproduced the stale balance; the initial test wait timed out because it waited for `GET /checkAuth`, which the app does not call during localStorage auth-state restoration. After removing that invalid wait and retaining the correct balance assertion, the focused expected-failure test reproduced KI-34 twice (Playwright: 2 passed as expected failures). The latest Playwright JSON report records expected `$997.26`, received `$930.26` at `data-test="sidenav-user-balance"` after reload.
- Debugger trace review confirmed the restoration path; the absence of a `/checkAuth` call is based on that review, not solely on a network assertion.
- Screenshot / trace: no saved artifact path recorded. Scenario reference: `playwright/tests/e2e/bob-sees-alice-payment.e2e.spec.ts`.

## Impact
Bob is shown an inaccurate balance after a completed incoming payment, potentially causing confusion or incorrect financial decisions. No workaround was verified during this repro; signing out and back in may force a profile refresh.

## Root cause (confirmed by debugger)
- Location: `src/machines/authMachine.ts`, approximately lines 265–277; profile refresh at approximately line 182.
- Explanation: Auth restoration uses cached localStorage state and does not refresh the user profile. `getUserProfile` / `checkAuth` runs on the refresh path, which this restoration does not take.
- Proposed fix: refresh the authenticated user profile during cached-state restoration (or otherwise ensure a full page reload refreshes the balance).

## Regression test
- Expected-failure regression: `playwright/tests/e2e/bob-sees-alice-payment.e2e.spec.ts` › “Bob sees Alice's $67 payment after refreshing”, marked `test.fail(true, "KI-34: Bob's cached profile balance is stale after a page reload")` until fixed. Focused E2E run: 2/2 expected failures; typecheck passes.

## Notes
KI-30 concerns the UI appearing logged in after the server session is lost (for example, after an API restart); this report concerns stale profile data after a valid completed payment and a full page reload. The two issues share cached auth-state restoration but are distinct behaviours.
