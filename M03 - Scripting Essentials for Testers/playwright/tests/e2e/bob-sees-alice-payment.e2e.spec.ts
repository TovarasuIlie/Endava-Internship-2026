import { test, expect } from "@fixtures/ui";
import {
  alertBar,
  bySel,
  bySelLike,
  fillTransactionForm,
  loginViaForm,
  searchUsersInput,
  selectContact,
  startNewTransaction,
  waitForApi,
} from "@ui";
import { env } from "@config";
import { dollarsToCents, formatCents } from "@utils/money";
import type { BrowserContext } from "@playwright/test";

/**
 * Journey: Alice pays Bob through Alice's UI; Bob independently reloads and sees the
 * increased balance. The actors are the first two users supplied by the seeded-user
 * fixture, rather than names or database IDs embedded in the test.
 */
test("Bob sees Alice's $67 payment after refreshing", { tag: ["@e2e", "@regression"] }, async ({
  browser,
  db,
  users: [alice, bob],
}) => {
  const contexts: BrowserContext[] = [];
  let transactionId = "";

  try {
    let alicePage!: Awaited<ReturnType<BrowserContext["newPage"]>>;
    let bobPage!: Awaited<ReturnType<BrowserContext["newPage"]>>;

    await test.step("Alice and Bob authenticate in separate browser contexts", async () => {
      const aliceContext = await browser.newContext({ baseURL: env.baseURL });
      contexts.push(aliceContext);
      const bobContext = await browser.newContext({ baseURL: env.baseURL });
      contexts.push(bobContext);

      alicePage = await aliceContext.newPage();
      bobPage = await bobContext.newPage();

      const [aliceLogin, bobLogin] = await Promise.all([
        loginViaForm(alicePage, alice.username, env.defaultPassword),
        loginViaForm(bobPage, bob.username, env.defaultPassword),
      ]);

      expect(alicePage.context()).not.toBe(bobPage.context());
      expect(aliceLogin.status()).toBe(200);
      expect(bobLogin.status()).toBe(200);
      await expect(alicePage).not.toHaveURL(/\/signin$/);
      await expect(bobPage).not.toHaveURL(/\/signin$/);
    });

    let initialBobBalanceCents = 0;
    await test.step("Record Bob's starting balance", async () => {
      const balance = bySel(bobPage, "sidenav-user-balance");
      await expect(balance).toBeVisible();
      const displayedBalance = await balance.innerText();
      initialBobBalanceCents = Math.round(
        Number(displayedBalance.replace(/[$,]/g, "")) * 100,
      );
      expect(initialBobBalanceCents).toBe(bob.balance);
    });

    await test.step("Alice sends Bob $67 through the payment form", async () => {
      await startNewTransaction(alicePage);
      const searched = waitForApi(alicePage, { method: "GET", path: "/users/search" });
      await searchUsersInput(alicePage).fill(bob.username);
      await searched;
      await selectContact(alicePage, bob.username);
      await fillTransactionForm(alicePage, "67", "E2E payment from Alice");

      const created = waitForApi(alicePage, { method: "POST", path: "/transactions" });
      await bySelLike(alicePage, "submit-payment").click();
      const response = await created;
      expect(response.status()).toBe(200);
      transactionId = ((await response.json()) as { transaction: { id: string } }).transaction.id;
      expect(transactionId).not.toBe("");
      await expect(alertBar(alicePage)).toHaveText("Transaction Submitted!");
    });

    await test.step("Bob refreshes and sees exactly $67 added to his balance", async () => {
      const refreshedProfile = waitForApi(bobPage, { method: "GET", path: "/checkAuth" });
      await bobPage.reload();
      expect((await refreshedProfile).status()).toBe(200);
      await expect(bySel(bobPage, "sidenav-user-balance")).toHaveText(
        formatCents(initialBobBalanceCents + dollarsToCents(67)),
      );
    });

    await test.step("Verify the payment completed for Alice and Bob", async () => {
      const payment = await db.find("transactions", { id: transactionId });
      expect(payment.senderId).toBe(alice.id);
      expect(payment.receiverId).toBe(bob.id);
      expect(payment.amount).toBe(dollarsToCents(67));
      expect(payment.description).toBe("E2E payment from Alice");
      expect(payment.status).toBe("complete");
    });
  } finally {
    await Promise.all(contexts.map((context) => context.close()));
  }
});
