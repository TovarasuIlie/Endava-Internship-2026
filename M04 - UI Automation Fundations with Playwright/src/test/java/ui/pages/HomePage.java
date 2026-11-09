package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class HomePage {

    private Page page;

    // Locators
    private Locator logoutButton;
    private Locator transactionList;
    private Locator myAccountLink;
    private Locator newTransactionButton;
    private Locator tabList;
    private Locator notificationLink;

    public void init(Page page) {
        this.page = page;
        logoutButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Logout"));
        transactionList = page.locator("//div[@data-test='transaction-list']");
        myAccountLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("My Account"));
        transactionList.isVisible();
        newTransactionButton = page.locator("a[data-test=\"nav-top-new-transaction\"]");
        tabList = page.locator("div[role=\"tablist\"]");
        notificationLink = page.locator("//a[@data-test='sidenav-notifications']");
    }

    public boolean isLogoutButtonVisible() {
        return logoutButton.isVisible();
    }

    public boolean isHomePageVisible() {
        return transactionList.isVisible();
    }

    public void clickNewTransactionButton() {
        newTransactionButton.click();
    }

    public TransactionPage redirectToNewTransactionPage() {
        clickNewTransactionButton();
        TransactionPage newTransactionPage = new TransactionPage();
        newTransactionPage.init(this.page);

        return newTransactionPage;
    }

    public void clickMineButton() {
        page.getByRole(
                AriaRole.TAB,
                new Page.GetByRoleOptions().setName("Mine")
        ).click();
    }

    public boolean verifyTransactionDetails(int index, String... expectedValues) {
        try {
            Locator transaction = this.transactionList
                    .locator("li[data-test^='transaction-item-']")
                    .nth(index);

            transaction.waitFor(
                    new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(5000)
            );

            return Arrays.stream(expectedValues)
                    .allMatch(value -> transaction.getByText(value).isVisible());

        } catch (TimeoutError e) {
            return false;
        }
    }

    public LoginPage logout() {
        logoutButton.click();
        LoginPage loginPage = new LoginPage();
        loginPage.init(this.page);

        return loginPage;
    }
}




