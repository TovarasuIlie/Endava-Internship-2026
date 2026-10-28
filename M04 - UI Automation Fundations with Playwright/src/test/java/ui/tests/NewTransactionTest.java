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

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewTransactionTest extends BaseTest {

    private HomePage homePage;
    private NewTransactionPage newTransactionPage;

    @BeforeEach
    void beforeEach() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        loginPage.openLoginPage(ConfigManager.get("base.url"));

        this.homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        assertTrue( homePage.isHomePageVisible(),
                "Transaction list should be visible after successful login"
        );

        assertTrue( homePage.isLogoutButtonVisible(),
                "Logout button should be visible after successful login"
        );

        newTransactionPage = homePage.redirectToNewTransactionPage();
    }

    @Test
    void canUserAccessNewTransactionPage() {
        assertTrue(newTransactionPage.isSelectContactContainerVisible(), "New Transaction Container is not visible!");
    }

    @Test
    void canUserMakeATransaction() {
        assertTrue(newTransactionPage.hasContactsInList(), "Users list should contain at least one user!");

        newTransactionPage.clickFirstContact();
        newTransactionPage.getPaymentForm();

        Assertions.assertTrue(newTransactionPage.isSameContactNameOnAndIsVisiblePayment(), "The names are not the same!");
        Assertions.assertTrue(newTransactionPage.isPaymentFormButtonsDisabled(), "Buttons are active with an empty form!");

        newTransactionPage.fillTransactionForm(
                ConfigManager.get("newtransactionpage.amount"),
                ConfigManager.get("newtransactionpage.note")
        );

        Assertions.assertTrue(newTransactionPage.isPaymentFormButtonActive(), "Buttons are disabled with a fill form!");

        newTransactionPage.clickPayButton();

        Assertions.assertTrue(newTransactionPage.isSuccessToastShown(), "Toast for success action dont shown!");
        Assertions.assertTrue(newTransactionPage.isCompleteTransactionContainerVisible(), "Confirmation is not visible!");
        Assertions.assertTrue(newTransactionPage.isSameNameOfContactAndIsVisibleOnCompletedPayment());
        Assertions.assertTrue(newTransactionPage.isShownCorrectAmountAndNote(
                ConfigManager.get("newtransactionpage.amount"),
                ConfigManager.get("newtransactionpage.note")
        ), "Incorrect confirmation message in Complete section!");

        newTransactionPage.clickReturnToTransactionsButton();

        Assertions.assertTrue(homePage.isHomePageVisible(), "Can't access home page!");

        homePage.clickMineButton();

        Assertions.assertTrue(homePage.isMinimumOneTransaction(), "Need minimum one transaction!");
        Assertions.assertTrue(homePage.verifyTransactionDetails(
                0,
                "-$" + ConfigManager.get("newtransactionpage.amount"),
                ConfigManager.get("newtransactionpage.note")
        ));
    }

    @AfterEach
    void afterEach() {
        homePage = null;
        newTransactionPage = null;
    }
}
