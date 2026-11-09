package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import lombok.Getter;

@Getter
public class TransactionPage {

    private Page page;

    private Locator contactList;
    private Locator contacts;

    private Locator paymentContainer;
    private Locator amountField;
    private Locator noteField;
    private Locator payButton;

    private Locator completeContainer;
    private Locator returnButton;
    private Locator successToast;

    private String selectedContactName;


    public void init(Page page) {
        this.page = page;

        contactList = page.locator("[data-test='users-list']");
        contacts = contactList.locator("li");

        paymentContainer = page.locator(
                "[class*='TransactionCreateStepTwo-paper']"
        );

        amountField = paymentContainer.locator("#amount");

        noteField = paymentContainer.locator(
                "#transaction-create-description-input"
        );

        payButton = paymentContainer.getByRole(
                AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Pay")
        );

        completeContainer = page.locator(
                "[class*='TransactionCreateStepThree-paper']"
        );

        successToast = page.getByRole(AriaRole.PRESENTATION);

        returnButton = completeContainer.getByRole(
                AriaRole.LINK
        ).first();
    }

    public void selectFirstContact() {
        Locator contact = contacts.first();

        selectedContactName = contact.locator("span")
                .first()
                .innerText();

        contact.click();
    }

    public Locator getSelectedContact() {
        return paymentContainer.getByText(selectedContactName);
    }

    public void fillPaymentForm(String amount, String note) {
        amountField.fill(amount);
        noteField.fill(note);
    }

    public void pay() {
        payButton.click();
    }

    public Locator getCompletedContact() {
        return completeContainer.getByText(selectedContactName);
    }

    public Locator getConfirmationMessage(String amount, String note) {
        return completeContainer.getByText(
                "Paid $" + amount + " for " + note
        );
    }

    public void returnToTransactions() {
        returnButton.click();
    }
}
