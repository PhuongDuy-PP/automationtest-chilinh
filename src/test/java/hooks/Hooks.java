package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Hooks {
//    setup môi trường để chạy test: tạo driver, tearDown
    private WebDriver driver;
    private WebDriverWait wait; // FIX: thêm wait, trước đây bị comment bỏ nên LoginPage nhận null

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // FIX: khởi tạo wait cùng lúc với driver
    }

    @After
    public void tearDown() {
        if(driver != null) {
            driver.quit();
        }
    }

    // ===== FIX: thêm getter để LoginStep lấy driver/wait qua DI thay vì tự truyền null =====
    public WebDriver getDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }
    // ===== END FIX =====
}
