package hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Hooks {
//    setup môi trường để chạy test: tạo driver, tearDown
    private WebDriver driver;
    private WebDriverWait wait; // FIX: thêm wait, trước đây bị comment bỏ nên LoginPage nhận null

//    allure-cucumber7-jvm tự lấy "Feature" làm nhãn report
    private static final String EPIC_TAG_PREFIX = "@epic_";

//    đánh dấu đã setup epic cho scenario hiện tại, tránh set lặp lại ở mỗi step
    private boolean epicResolved = false;

//    trước khi chạy test case feature => setup Allure report trước

    @BeforeStep // chạy trước step Given/When/Then
    public void resolveEpic(Scenario scenario) {
        System.out.println("BeforeStep");
        if (epicResolved) {
            return; // đã set epic ở step đầu tiên
        }
        System.out.println("epicResolved: " + epicResolved);
        epicResolved = true;
        scenario.getSourceTagNames().stream()
                .filter(tag -> tag.startsWith(EPIC_TAG_PREFIX))
                .findFirst() // mỗi scenario chỉ nên có 1 epic
                .ifPresent(tag-> Allure.epic(tag.substring(EPIC_TAG_PREFIX.length())));
    }

    @Before
    public void setUp() {
        System.out.println("Before");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // FIX: khởi tạo wait cùng lúc với driver
    }

    @After
    public void tearDown(Scenario scenario) {
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
