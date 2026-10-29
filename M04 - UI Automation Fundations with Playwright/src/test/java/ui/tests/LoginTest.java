package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {

    private LoginPage loginPage;

    @BeforeEach
    void beforeEach() {
        loginPage = new LoginPage();
        loginPage.init(page);

        page.navigate(ConfigManager.get("auth.login.url"));
    }

    @Test
    void userCanLoginSuccessfully() {

        HomePage homePage = loginPage.login(
                ConfigManager.get("auth.login.username"),
                ConfigManager.get("auth.login.password")
        );

        assertTrue( homePage.isHomePageVisible(),
                "Transaction list should be visible after successful login"
        );

        assertTrue( homePage.isLogoutButtonVisible(),
                "Logout button should be visible after successful login"
        );
    }

    @Test
    void showErrorMessageHelperForUsername() {
        loginPage.selectInputField(LoginPage.LoginFormField.USERNAME).focus();
        loginPage.selectInputField(LoginPage.LoginFormField.USERNAME).blur();

        assertTrue(loginPage.isShownErrorEmptyUsername(), "Username Error helper don't shown!");
    }

    @Test
    void showErrorMessageHelperForPassword() {
        loginPage.enterPassword("a");
        loginPage.selectInputField(LoginPage.LoginFormField.PASSWORD).blur();

        assertTrue(loginPage.isShownErrorPasswordLength(), "Password Error helper don't shown!");
    }

    @Test
    void signInButtonIsDisabledIfLoginFieldsIsEmpty() {
        loginPage.selectInputField(LoginPage.LoginFormField.USERNAME).focus();
        loginPage.selectInputField(LoginPage.LoginFormField.USERNAME).blur();

        loginPage.selectInputField(LoginPage.LoginFormField.PASSWORD).focus();
        loginPage.selectInputField(LoginPage.LoginFormField.PASSWORD).blur();

        assertTrue(loginPage.isSignInButtonDisabled(), "Sign In button is not disabled!");
    }

    @Test
    void loginWithIncorrectCredentials() {
        loginPage.enterUsername(ConfigManager.get("auth.login.invalid.username"));
        loginPage.enterPassword(ConfigManager.get("auth.login.invalid.password"));

        loginPage.clickLogin();

        assertTrue(loginPage.isAlertErrorVisible(), "Alert error is not visible!");
    }

    @AfterEach
    void afterEach() {
        loginPage = null;
    }
}