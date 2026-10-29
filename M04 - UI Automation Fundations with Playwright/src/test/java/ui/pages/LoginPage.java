package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

public class
LoginPage {

    public enum LoginFormField {
        USERNAME,
        PASSWORD
    }

    private Page page;
    private final String homePageTransactionList = "//div[@data-test='transaction-list']";

    // Locators
    private Locator usernameFormControl;
    private Locator passwordFormControl;
    private Locator signButton;  // = login button

    // Initialization
    public void init(Page page) {
        this.page = page;

        usernameFormControl = page.locator("div[data-test=\"signin-username\"]");
        passwordFormControl = page.locator("div[data-test=\"signin-password\"]");
        signButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));
    }

    public void enterUsername(String username) {
        usernameFormControl.locator("input#username").fill(username);
    }

    public boolean isShownErrorEmptyUsername() {
        return usernameFormControl.getByText("Username is required").isVisible();
    }

    public boolean isShownErrorPasswordLength() {
        return passwordFormControl.getByText("Password must contain at least 4 characters").isVisible();
    }

    public boolean isSignInButtonDisabled() {
        return signButton.isDisabled();
    }

    public Locator selectInputField(LoginFormField field) {
        return switch (field) {
            case USERNAME -> usernameFormControl.locator("input#username");
            case PASSWORD -> passwordFormControl.locator("input#password");
        };
    }

    public void enterPassword(String password) {
        passwordFormControl.locator("input#password").fill(password);
    }

    public void clickLogin() {
        signButton.click();
    }

    public boolean isAlertErrorVisible() {
        Locator alert = page.getByRole(AriaRole.ALERT)
                .filter(new Locator.FilterOptions()
                        .setHasText("Username or password is invalid"));

        try {
            alert.waitFor(
                    new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(5000)
            );

            return true;
        } catch (TimeoutError e) {
            return false;
        }
    }

    public HomePage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();

        page.locator(homePageTransactionList).waitFor();
        HomePage homePage = new HomePage();
        homePage.init(page);

        return homePage;
    }
}