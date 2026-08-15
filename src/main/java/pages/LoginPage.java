package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends BasePage{

    private static final By USERNAME_INPUT = By.xpath("//input[@name='username']");
//    cách 2: By.name("username")
    private static final By PASSWORD_INPUT = By.xpath("//input[@name='password']");
    private static final By LOGIN_BUTTON = By.xpath("//button[@type='submit']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void open() {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
    }

    public void enterUsername(String username) {
        WebElement usernameInput = driver.findElement(USERNAME_INPUT);
        usernameInput.sendKeys(username);
    }

    public void enterPassword(String password) {
        WebElement passwordInput = driver.findElement(PASSWORD_INPUT);
        passwordInput.sendKeys(password);
    }

    public void clickLoginButton() {
        WebElement loginButton = driver.findElement(LOGIN_BUTTON);
        loginButton.click();
    }
}
