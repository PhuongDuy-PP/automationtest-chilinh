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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ExcelReportUtil;
import utils.TestContext;

import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Hooks {
//    setup môi trường để chạy test: tạo driver, tearDown
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitThreadLocal = new ThreadLocal<>();

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

    private WebDriver createChromeDriver(boolean isCI) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        if (isCI) {
            options.addArguments("--headless=new"); // chay chrome khong co giao dien
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        } else {
            options.addArguments("--start-maximized");
        }

        return new ChromeDriver(options);
    }

    private WebDriver createFirefoxDriver(boolean isCI) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (isCI) {
            options.addArguments("--headless");
            options.addArguments("--width=1920", "--height=1080");
        }

        FirefoxDriver driver = new FirefoxDriver(options);
        if (!isCI) {
            driver.manage().window().maximize();
        }

        return driver;
    }

//    factory method
    private WebDriver createDriver(String browser, boolean isCI) {
        switch (browser) {
            case "chrome":
                return createChromeDriver(isCI);
            case "firefox":
                return createFirefoxDriver(isCI);
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    @Before
    public void setUp() {
        System.out.println("Before");
        WebDriverManager.chromedriver().setup();

//        GITHUB ACTION tự setup biến môi trường CI=true
//        chạy trên CI => ko có màn hình chrome
        boolean isCI = Boolean.parseBoolean(System.getenv("CI"));

        WebDriver driver = createDriver("chrome", isCI);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // FIX: khởi tạo wait cùng lúc với driver

        driverThreadLocal.set(driver);
        waitThreadLocal.set(wait);
    }

    @After
    public void tearDown(Scenario scenario) {
//        cập nhật kết quả scenario vừa chạy xong vào file TestResults.xlsx
        updateExcelReport(scenario);

        WebDriver driver = driverThreadLocal.get();

        if(driver != null) {
            driver.quit();
        }
        driverThreadLocal.remove();
        waitThreadLocal.remove();
    }

//    trích tên file .feature (bỏ phần path và đuôi .feature) từ URI của scenario
//    Users/Phuong/Desktops/login.feature -> login => map sang tên feature tiếng việt
    private static final Map<String, String> FEATURE_DISPLAY_NAMES = Map.of(
            "login", "Đăng nhập hệ thống OranageHRM",
            "admin", "Quan lý người dùng hệ thống",
            "personal", "Cập nhật ảnh đại diện nhân viên"
    );

    private String extractFeatureName(URI uri) {
        String uriString = uri.toString();
        String fileName = uriString.substring(uriString.lastIndexOf("/") + 1);
        String featureName = fileName.replace(".feature", "");
        return FEATURE_DISPLAY_NAMES.getOrDefault(featureName, featureName);
    }

    private String buildFailureNote(String status) {
        String assertNote = TestContext.getNote();
        if (!assertNote.isEmpty()) {
            return assertNote;
        }

        return "Mong đợi: kịch bản PASSED | thực tế: " + status;
    }

    private void updateExcelReport(Scenario scenario){
        String featureName =  extractFeatureName(scenario.getUri());
        String testCaseName = scenario.getName();
        String status = scenario.getStatus().toString();

        boolean isPassed = "PASSED".equals(status);
        String note = isPassed ? "" : buildFailureNote(status);

        ExcelReportUtil.updateStatus(featureName, testCaseName, status, note);

        TestContext.clear();
    }

    // ===== FIX: thêm getter để LoginStep lấy driver/wait qua DI thay vì tự truyền null =====
    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public WebDriverWait getWait() {
        return waitThreadLocal.get();
    }
    // ===== END FIX =====
}
