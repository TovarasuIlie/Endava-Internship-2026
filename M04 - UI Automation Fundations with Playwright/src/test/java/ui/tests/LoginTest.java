package ui.tests;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import framework.config.ConfigManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.TransactionPage;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {

    private HomePage homePage;

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
    }

    @Test
    void shouldRedirectToLoginWhenSessionIsInvalid() {
        page.context().clearCookies(
                new BrowserContext.ClearCookiesOptions()
                        .setName("connect.sid")
        );

        page.reload();

        page.waitForURL("http://localhost:3000/signin");

        assertTrue(
                page.url().contains("/signin"),
                "User should be redirected to login"
        );
    }
}