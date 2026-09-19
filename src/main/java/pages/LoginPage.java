package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginPage extends BasePage{

    private static final By USERNAME_INPUT = By.xpath("//input[@name='username']");
//    cách 2: By.name("username")
    private static final By PASSWORD_INPUT = By.xpath("//input[@name='password']");
    private static final By LOGIN_BUTTON = By.xpath("//button[@type='submit']");
    private static final By USER_DROPDOWN = By.xpath("//span[@class='oxd-userdropdown-tab']");
    private static final By LOGOUT_LINK = By.xpath("//a[text()='Logout']");
    private static final By LOGIN_ERROR_MESSAGE = By.xpath("//p[@class='oxd-text oxd-text--p oxd-alert-content-text']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void open() {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
    }

    public void enterUsername(String username) {
        WebElement usernameInput = driver.findElement(USERNAME_INPUT);
        highlight(usernameInput);
        usernameInput.sendKeys(username);
        unhighlight(usernameInput);
    }

    public void enterPassword(String password) {
        WebElement passwordInput = driver.findElement(PASSWORD_INPUT);
        highlight(passwordInput);
        passwordInput.sendKeys(password);
        unhighlight(passwordInput);
    }

    public void clickLoginButton() {
        WebElement loginButton = driver.findElement(LOGIN_BUTTON);
        highlight(loginButton);
        loginButton.click();
//        unhighlight(loginButton);

        wait.until(
                ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(USER_DROPDOWN),
                        ExpectedConditions.visibilityOfElementLocated(LOGIN_ERROR_MESSAGE)
                )
        );
    }

    public void logout() {
        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(USER_DROPDOWN));
        userDropdown.click();

        WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_LINK));
        logoutLink.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
    }
}
