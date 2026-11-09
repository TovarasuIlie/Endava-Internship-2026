package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.TransactionPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTest extends BaseTest {

    private HomePage homePage;
    private TransactionPage newTransactionPage;

    @BeforeEach
    void beforeEach() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        page.navigate(ConfigManager.get("auth.login.url"));

        this.homePage = loginPage.login(
                ConfigManager.get("auth.login.username"),
                ConfigManager.get("auth.login.password")
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
    void shouldSuccessfullyMakePayment() {
        String amount = ConfigManager.get("transaction.valid.amount");
        String note = ConfigManager.get("transaction.valid.note");

        newTransactionPage.selectFirstContact();
        newTransactionPage.fillPaymentForm(amount, note);
        newTransactionPage.pay();

        assertThat(newTransactionPage.getConfirmationMessage(amount, note))
                .isVisible();

        assertThat(newTransactionPage.getCompletedContact())
                .isVisible();

        newTransactionPage.returnToTransactions();
        homePage.clickMineButton();

        assertTrue(
                homePage.verifyTransactionDetails(0, "-$" + amount, note),
                "Transaction not found in history!"
        );
    }

    @Test
    void shouldDisplayContactsList() {
        assertThat(newTransactionPage.getContacts().first()).isVisible();
    }

    @Test
    void shouldNavigateToPaymentAfterSelectingContact() {
        newTransactionPage.selectFirstContact();

        assertThat(newTransactionPage.getPaymentContainer()).isVisible();
    }

    @Test
    void shouldDisablePaymentButtonsWhenFormIsEmpty() {
        newTransactionPage.selectFirstContact();

        assertThat(newTransactionPage.getPayButton()).isDisabled();
    }

    @Test
    void shouldEnablePayButtonWithValidData() {
        String amount = ConfigManager.get("transaction.valid.amount");
        String note = ConfigManager.get("transaction.valid.note");

        newTransactionPage.selectFirstContact();

        newTransactionPage.fillPaymentForm(amount, note);

        assertThat(newTransactionPage.getPayButton()).isEnabled();
    }

    @Test
    void shouldNotAllowPaymentWithEmptyAmount() {
        String note = ConfigManager.get("transaction.valid.note");

        newTransactionPage.selectFirstContact();

        newTransactionPage.fillPaymentForm("", note);

        assertThat(newTransactionPage.getPayButton()).isDisabled();
    }

    @Test
    void shouldNotAllowPaymentWithEmptyNote() {
        String amount = ConfigManager.get("transaction.valid.amount");

        newTransactionPage.selectFirstContact();

        newTransactionPage.fillPaymentForm(amount, "");

        assertThat(newTransactionPage.getPayButton()).isDisabled();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-1", "-1.00", "-100"})
    void shouldNotAllowPaymentWithInvalidAmount(String amount) {
        String note = ConfigManager.get("transaction.valid.note");

        newTransactionPage.selectFirstContact();
        newTransactionPage.fillPaymentForm(amount, note);

        assertThat(newTransactionPage.getPayButton()).isDisabled();
    }

    @Test
    void shouldDisplaySuccessToastAfterPayment() {
        String amount = ConfigManager.get("transaction.valid.amount");
        String note = ConfigManager.get("transaction.valid.note");

        newTransactionPage.selectFirstContact();

        newTransactionPage.fillPaymentForm(amount, note);

        newTransactionPage.pay();

        assertThat(newTransactionPage.getSuccessToast()).isVisible();
    }

    @Test
    void shouldDisplayCorrectPaymentConfirmation() {
        String amount = ConfigManager.get("transaction.valid.amount");
        String note = ConfigManager.get("transaction.valid.note");

        newTransactionPage.selectFirstContact();

        newTransactionPage.fillPaymentForm(amount, note);

        newTransactionPage.pay();

        assertThat(newTransactionPage.getConfirmationMessage(amount, note)).isVisible();
    }

    @AfterEach
    void afterEach() {
        homePage = null;
        newTransactionPage = null;
    }
}
