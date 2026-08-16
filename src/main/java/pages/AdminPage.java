package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AdminPage extends BasePage{
    private static final By USER_INPUT = By.xpath("(//input[@class='oxd-input oxd-input--active'])[2]");
    private static final By USER_ROLE_SELECT = By.xpath("(//div[@class='oxd-select-text oxd-select-text--active'])[1]");
    private static final By SEARCH_BTN = By.xpath("//button[@type='submit']");
    private static final By DATA_ROWS = By.xpath("//div[@class='oxd-table-card']");
    private static final By RECORD_COUNT_TEXT = By.xpath("//div[@class='orangehrm-horizontal-padding orangehrm-vertical-padding']//span");

    public AdminPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void open() {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/admin/viewSystemUsers");
        wait.until(ExpectedConditions.visibilityOfElementLocated(USER_INPUT));
    }

//    STEP: enter username -> select role -> click search
//    => function chung: filter user(enter username, select role, click search)
    public void enterUsername(String username) throws InterruptedException {
        WebElement userInput = driver.findElement(USER_INPUT);
        userInput.sendKeys(username);
        Thread.sleep(1000);
    }

    public void selectUserRole(String role) throws InterruptedException {
        WebElement roleSelect = driver.findElement(USER_ROLE_SELECT);
        roleSelect.click();
        Thread.sleep(1000);

//        chọn dropdown tương ứng
        String xpath = String.format("//div[@role='option']//span[text()='%s']", role);
        WebElement roleOption = driver.findElement(By.xpath(xpath));
        roleOption.click();
        Thread.sleep(1000);
    }

    public void clickSearchBtn() throws InterruptedException {
        WebElement searchBtn = driver.findElement(SEARCH_BTN);
        searchBtn.click();
        Thread.sleep(1000);
    }

    public void filterByUser(String username, String role) throws InterruptedException {
        enterUsername(username);
        selectUserRole(role);
        clickSearchBtn();
    }

    public boolean checkNumberOfRecords() {
        WebElement recordCount = wait.until(ExpectedConditions.visibilityOfElementLocated(RECORD_COUNT_TEXT));
        String text = recordCount.getText();
//        apply regular exxpersion
//        (1) Record found
        int countRecord = Integer.parseInt(text.replaceAll("\\D+", ""));

        int countDataRows = driver.findElements(DATA_ROWS).size();
        return countRecord == countDataRows;
    }

}
