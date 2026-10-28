package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class NewTransactionPage {

    private Page page;

    private Locator selectContactContainer;
    private Locator paymentContainer;
    private Locator contactList;
    private Locator amountField;
    private Locator noteField;
    private Locator paymentFormButtons;
    private Locator completeTransactionContainer;

    private String selectedContactName;


    public void init(Page page) {
        this.page = page;

        this.selectContactContainer = page.locator("div[class=\"MuiPaper-root MuiPaper-elevation MuiPaper-rounded MuiPaper-elevation0 TransactionCreateStepOne-paper css-bg379a-MuiPaper-root\"]");

        this.contactList = this.selectContactContainer.locator("ul[data-test=\"users-list\"]");
    }

    public boolean isSelectContactContainerVisible() {
        return selectContactContainer.isVisible();
    }

    public boolean hasContactsInList() {
        return contactList.locator("li").count() > 0;
    }

    public void clickFirstContact() {
        this.selectedContactName = this.contactList.locator("li").first().locator("span").first().innerText();
        this.contactList.locator("li").first().click();

        this.paymentContainer = this.page.locator("div[class=\"MuiPaper-root MuiPaper-elevation MuiPaper-rounded MuiPaper-elevation0 TransactionCreateStepTwo-paper css-1q6oae9-MuiPaper-root\"]");
    }

    public void getPaymentForm() {
        this.amountField = this.paymentContainer.locator("input#amount");
        this.noteField = this.paymentContainer.locator("input#transaction-create-description-input");

        this.paymentFormButtons = this.paymentContainer.getByRole(AriaRole.BUTTON);
    }

    public boolean isPaymentFormButtonsDisabled() {
        return paymentFormButtons.all()
                .stream()
                .allMatch(Locator::isDisabled);
    }

    public boolean isSameContactNameOnAndIsVisiblePayment() {
        return this.paymentContainer.getByText(this.selectedContactName).isVisible();
    }

    public boolean isPaymentFormButtonActive() {
        return paymentFormButtons.all()
                .stream()
                .allMatch(Locator::isEnabled);
    }

    public void fillTransactionForm(String amount, String note) {
        this.amountField.fill(amount);
        this.noteField.fill(note);
    }

    public void clickPayButton() {
        this.paymentFormButtons.nth(1).click();

        this.completeTransactionContainer = this.page.locator("div[class=\"MuiPaper-root MuiPaper-elevation MuiPaper-rounded MuiPaper-elevation0 TransactionCreateStepThree-paper css-fqmquq-MuiPaper-root\"]");
    }

    public boolean isCompleteTransactionContainerVisible() {
        return completeTransactionContainer.isVisible();
    }

    public boolean isSameNameOfContactAndIsVisibleOnCompletedPayment() {
        return this.completeTransactionContainer.getByText(this.selectedContactName).isVisible();
    }

    public boolean isShownCorrectAmountAndNote(String amount, String note) {
        return this.completeTransactionContainer.getByText("Paid $" + amount + " for " + note).isVisible();
    }

    public boolean isSuccessToastShown() {
        return this.page.getByRole(AriaRole.PRESENTATION).isVisible();
    }

    public void clickReturnToTransactionsButton() {
        this.completeTransactionContainer.getByRole(AriaRole.LINK).nth(0).click();
    }
}
