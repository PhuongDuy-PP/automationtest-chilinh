package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

public class PeronalPage extends BasePage{
    private static final By AVATAR_IMG = By.xpath("//div[@class='orangehrm-edit-employee-image']");
    private static final By UPLOAD_BTN = By.xpath("//button[contains(@class, 'employee-image-action')]");
    private static final By FILE_INPUT = By.xpath("//input[@type='file']");
    private static final By SAVE_BTN = By.xpath("//button[@type='submit']");
    private static final By SUCCESS_TOAST = By.xpath("//div[contains(@class, 'oxd-toast')]");

    public PeronalPage(WebDriver driver, WebDriverWait wait) {
        super(driver, wait);
    }

    public void open() throws  InterruptedException {
        driver.get("https://opensource-demo.orangehrmlive.com/web/index.php/pim/viewPhotograph/empNumber/7");
        Thread.sleep(1000);
    }

    public void uploadAvatar(String fileName) throws InterruptedException {
//        B1: click vao avatar
        WebElement avataImg = wait.until(ExpectedConditions.elementToBeClickable(AVATAR_IMG));
        avataImg.click();
        Thread.sleep(1000);

//        B2: click nút + để upload avatar
        WebElement uploadBtn = wait.until(ExpectedConditions.elementToBeClickable(UPLOAD_BTN));
        uploadBtn.click();
        Thread.sleep(1000);

//        B3: đợi hiển thị file input -> truyền path image vào file input (absolute path)
        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(FILE_INPUT));
//        <path system>/<path source code>
//        getAbsolutePath()/src/...
        String absolutePath = new File("src/test/resources/images/" + fileName).getAbsolutePath();
        fileInput.sendKeys(absolutePath);
        Thread.sleep(1000);

//        B4: click nuts save
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(SAVE_BTN));
        saveBtn.click();
        Thread.sleep(1000);
    }

    public boolean isAvatarUploadSuccessfully() {
        WebElement successToast = wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_TOAST));
        return successToast.isDisplayed();
    }
}
