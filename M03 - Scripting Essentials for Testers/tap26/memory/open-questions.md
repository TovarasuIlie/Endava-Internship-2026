# Open questions

Format: `- [ ] Q-### question (asked by, date)`; answered entries become `- [x]` with an indented answer.

- [ ] Q-001 Should the Cypress suite be retired once the Playwright suite covers everything, or kept as a comparison for students? (asked by tap26-setup, 2026-10-16)
- [x] Q-002 Bug: Bob's balance remains stale after page refresh following Alice's completed  payment — fix or accept? (tap26/reports/bugs/BUG-002-stale-balance-after-payment-refresh.md) (asked by manual-tester, 2026-10-22)
  - Answer: Fix KI-34 by refreshing the cached authorized profile on reload; the focused Alice/Bob E2E passed twice. (tap-orchestrator, 2026-10-22)
- [ ] Q-003 Bug: Rejecting a money request changes either user's balance — KI-05, fix or accept? (asked by api-automation, 2026-10-22)
