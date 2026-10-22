# TAP26-Junior — evolution metrics

Evidence for (and against) framework changes. `/tap-retro` and `/tap-evolve apply` add rows; the
knowledge-curator reads this table during `/tap-evolve review`. Keep one row per measurement; never
rewrite past rows — add a new one.

## How to measure
| Metric | How to get it |
|---|---|
| Gates passed / failed | Count of BLUEPRINT §7 gates per orchestrated task (hand-off reports, reviews) |
| Review verdicts | `tap26/reports/reviews/*` — approve vs request-changes |
| Test health | `playwright/test-results/.last-run.json`; passed/failed/flaky counts per run |
| Flake rate | Tests that failed then passed on rerun ÷ tests run |
| Rework loops | Times the orchestrator sent work back to an agent |
| Memory quality | New learnings per retro (target ≤ 3), duplicates found, open questions answered |
| Student friction | Questions logged (`open-questions.md`), lab checkpoints failed (`/tap-lab NN check`) |

## Session / retro metrics
| Date | Scope | Tasks | Gates ✅/❌ | Reviews ✅/✋ | Tests added | Flake rate | Rework loops | Learnings | Notes |
|---|---|---|---|---|---|---|---|---|---|
| 2026-10-16 | 1.0.0 baseline | — | — | — | 55 API + 59 UI (per viewport) | 0 % | — | L-001…L-006 | Initial release |
| 2026-10-22 | today | 8 workstreams: setup/tests, KI-05, KI-01 fix, accessibility audits, bank-account empty state, Alice/Bob E2E, tablet project, KI-34/KI-30 auth fixes | Passed: API 73/73; relevant typechecks; OpenAPI/spec, lint, drift; repeated KI-05/KI-31 expected-fail checks; bank-account desktop/mobile 2/2 each; KI-34 E2E 2/2 after fix; KI-30 UI auth regression 2/2; tablet 2/2; project-list check. Initial doctor/app-readiness and invalid-wait failures were resolved or correctly classified; no final test failure | 4 approved (KI-01, E2E, tablet, auth); 2 request-changes resolved (provider-auth scope, KI-30 side effect) | 5 added (KI-05 API, KI-01 API, KI-31 UI, Alice/Bob E2E, KI-30 auth UI); 1 existing bankaccounts test updated; 0 a11y tests | 0 flaky observed after correct protocol | 6: corrected E2E wait; reviewer-suggested docs/assertion fixes; tablet debugger-doc omission; two blocked a11y delegations replaced by parent scans; provider-auth guard; KI-30 test/catalogue follow-up | L-012 (reload network-wait behavior) | Approved test runs reseeded shared DB; latest test resets were authorized. `usage.json` absent, so top skills/agents n/a. |

## Evolution outcomes
<!-- One row per applied proposal; revisit after ~3 sessions to fill "Observed effect". -->
| EP | Version | Applied | Success metric (from proposal) | Baseline | Observed effect | Keep / revert |
|---|---|---|---|---|---|---|
| — | 1.0.0 | 2026-10-16 | — | — | — | — |
