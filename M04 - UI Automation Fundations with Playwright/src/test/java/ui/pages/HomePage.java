package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.Arrays;

public class HomePage {

    private Page page;

    // Locators
    private Locator logoutButton;
    private Locator transactionList;
    private Locator newTransactionButton;
    private Locator tabList;

    public void init(Page page) {
        this.page = page;
        logoutButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Logout"));
        transactionList = page.locator("//div[@data-test='transaction-list']");
        transactionList.isVisible();
        newTransactionButton = page.locator("a[data-test=\"nav-top-new-transaction\"]");
        tabList = page.locator("div[role=\"tablist\"]");
    }

    public boolean isLogoutButtonVisible() {
        return logoutButton.isVisible();
    }

    public boolean isHomePageVisible() {
        try {
            transactionList.waitFor(
                    new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(5000)
            );

            return true;

        } catch (TimeoutError e) {
            return false;
        }
    }

    public void clickNewTransactionButton() {
        newTransactionButton.click();
    }

    public NewTransactionPage redirectToNewTransactionPage() {
        clickNewTransactionButton();
        NewTransactionPage newTransactionPage = new NewTransactionPage();
        newTransactionPage.init(this.page);

        return newTransactionPage;
    }

    public void clickMineButton() {
        page.getByRole(
                AriaRole.TAB,
                new Page.GetByRoleOptions().setName("Mine")
        ).click();
    }

    public boolean isMinimumOneTransaction() {
        try {
            this.transactionList.locator("li[data-test^=\"transaction-item-\"]").first().waitFor(
                    new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(5000)
            );

            return true;

        } catch (TimeoutError e) {
            return false;
        }
    }

    public boolean verifyTransactionDetails(int index, String... expectedValues) {
        Locator transaction = this.transactionList.locator("li[data-test^=\"transaction-item-\"]").nth(index);

        return Arrays.stream(expectedValues)
                .allMatch(value -> transaction.getByText(value).isVisible());
    }

    public void clickOnFirstTransaction() {
        this.transactionList.locator("li[data-test^=\"transaction-item-\"]").nth(0).click();
    }
}
