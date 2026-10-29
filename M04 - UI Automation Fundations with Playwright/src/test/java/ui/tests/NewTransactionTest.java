package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.NewTransactionPage;

public class NewTransactionTest extends BaseTest {

    private HomePage homePage;
    private NewTransactionPage newTransactionPage;

    @BeforeEach
    void beforeEach() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        page.navigate(ConfigManager.get("auth.login.url"));

        this.homePage = loginPage.login(
                ConfigManager.get("auth.login.username"),
                ConfigManager.get("auth.login.password")
        );

        Assertions.assertTrue( homePage.isHomePageVisible(),
                "Transaction list should be visible after successful login"
        );

        Assertions.assertTrue( homePage.isLogoutButtonVisible(),
                "Logout button should be visible after successful login"
        );
    }

    @Test
    void canUserAccessNewTransactionPagePressingButton() {
        newTransactionPage = homePage.redirectToNewTransactionPage();

        Assertions.assertTrue(page.url().contains(ConfigManager.get("transaction.new.url")), "Current page URL is incorrect!");
        Assertions.assertTrue(newTransactionPage.isSelectContactContainerVisible(), "New Transaction Container is not visible!");
    }

    @Test
    void canUserAccessNewTransactionPageByURL() {
        page.navigate(ConfigManager.get("transaction.new.url"));

        Assertions.assertTrue(page.url().contains(ConfigManager.get("transaction.new.url")), "Current page URL is incorrect!");
    }

    @Test
    void canUserMakeATransaction() {
        newTransactionPage = homePage.redirectToNewTransactionPage();

        Assertions.assertTrue(newTransactionPage.hasContactsInList(), "Users list should contain at least one user!");

        newTransactionPage.clickFirstContact();
        newTransactionPage.getPaymentForm();

        Assertions.assertTrue(newTransactionPage.isSameContactNameOnAndIsVisiblePayment(), "The names are not the same!");
        Assertions.assertTrue(newTransactionPage.isPaymentFormButtonsDisabled(), "Buttons are active with an empty form!");

        newTransactionPage.fillTransactionForm(
                ConfigManager.get("transaction.valid.amount"),
                ConfigManager.get("transaction.valid.note")
        );

        Assertions.assertTrue(newTransactionPage.isPaymentFormButtonActive(), "Buttons are disabled with a fill form!");

        newTransactionPage.clickPayButton();

        Assertions.assertTrue(newTransactionPage.isSuccessToastShown(), "Toast for success action dont shown!");
        Assertions.assertTrue(newTransactionPage.isCompleteTransactionContainerVisible(), "Confirmation is not visible!");
        Assertions.assertTrue(newTransactionPage.isSameNameOfContactAndIsVisibleOnCompletedPayment());
        Assertions.assertTrue(newTransactionPage.isShownCorrectAmountAndNote(
                ConfigManager.get("transaction.valid.amount"),
                ConfigManager.get("transaction.valid.note")
        ), "Incorrect confirmation message in Complete section!");

        newTransactionPage.clickReturnToTransactionsButton();

        Assertions.assertTrue(homePage.isHomePageVisible(), "Can't access home page!");

        homePage.clickMineButton();

        Assertions.assertTrue(homePage.isMinimumOneTransaction(), "Need minimum one transaction!");
        Assertions.assertTrue(homePage.verifyTransactionDetails(
                0,
                "-$" + ConfigManager.get("transaction.valid.amount"),
                ConfigManager.get("transaction.valid.note")
        ));
    }

    @AfterEach
    void afterEach() {
        homePage = null;
        newTransactionPage = null;
    }
}
