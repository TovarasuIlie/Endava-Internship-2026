package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomeTest extends BaseTest {

    @Test
    void userCanNavigateToMyAccount() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        page.navigate(ConfigManager.get("auth.login.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );
    }
}
