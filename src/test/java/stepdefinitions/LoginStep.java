package stepdefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.LoginPage;
import utils.ConfigReader;
import utils.DataReader;
import utils.TestContext;

import java.util.List;
import java.util.Map;

// ===== FIX: nhận Hooks qua constructor để Cucumber (cucumber-picocontainer) tự inject =====
// Trước đây constructor nhận thẳng (WebDriver driver, WebDriverWait wait) nhưng không
// có class nào gọi với giá trị thật -> driver luôn null -> NullPointerException.
// Hooks và LoginStep dùng chung 1 instance Hooks trong 1 scenario nên driver được set
// ở @Before của Hooks sẽ luôn thấy được ở đây.
public class LoginStep {
    private final Hooks hooks;

    public LoginStep(Hooks hooks) {
        this.hooks = hooks;
    }

//    FIX: tạo LoginPage bằng method (gọi lại mỗi lần dùng) thay vì field khởi tạo 1 lần
//    trong constructor -> trước đây field bị khởi tạo với driver=null ngay khi tạo LoginStep
    private LoginPage loginPage() {
        return new LoginPage(hooks.getDriver(), hooks.getWait());
    }
// ===== END FIX =====

//    hàm mở trang login => Given  người dùng đang ở trang đăng nhập OranageHRM

    @Given("người dùng đang ở trang đăng nhập OranageHRM")
    public void nguoi_dung_dang_o_trang_dang_nhap() {
        loginPage().open();
    }

    @When("người dùng đăng nhập với tài khoản hợp lệ")
    public void nguoi_dung_dang_nhap_voi_tai_khoan() {
//        flow: điền username và password vào các ô input tương ứng và click
//        vào nút Login
        ConfigReader configReader = new ConfigReader();
        configReader.loadProperties();

        String username = configReader.get("adminUsername");
        String password = configReader.get("adminPassword");

        LoginPage loginPage = loginPage();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
    }

    @Then("người dùng được chuyển đến trang Dashboard")
    public void nguoi_dung_da_chuyen_den_trang_dashboard() {
        String currentUrl = hooks.getDriver().getCurrentUrl();
        if (!currentUrl.contains("dashboard")) {
            String note = "Mong đợi: đăng nhập thanh cong, chuyen den trang Dashboard"
                    + " | Thực tế: vẫn ở trang " + currentUrl;
            TestContext.setNote(note);
            throw new AssertionError(note); // ném lỗi để cucumber danh dau step/scenario FAILED
        }
//        Assertions.assertTrue(currentUrl.contains("dashboard"), "Phải chuyển đến trang Dashboard");
    }

    @Then("hệ thống báo lỗi và vẫn ở trang đăng nhập")
    public void he_thong_bao_loi_va_vung_o_trang_dang_nhap() {
        String currentUrl = hooks.getDriver().getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("auth/login"), "Phải ở trang Logi");
    }

    @When("người dùng đăng nhập lần lượt với dữ liệu từ file csv")
    public void nguoi_dung_dang_nhap_voi_bo_du_lieu() {
        String csvFilePath = "data/loginData.csv";
        DataReader dataReader = new DataReader();
        List<Map<String, String>> rows = dataReader.readCsv(csvFilePath);
        System.out.println(rows);
        int count = 1;

        for (Map<String, String> row : rows) {
            System.out.println(count);
            System.out.println(row);
            String username = row.get("username");
            String password = row.get("password");
            String expected = row.get("expected");

            LoginPage loginPage = loginPage();
            loginPage.open();
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            loginPage.clickLoginButton();

            String currentUrl = hooks.getDriver().getCurrentUrl();
            String actual = currentUrl.contains("dashboard") ? "success" : "fail";
            Assertions.assertEquals(expected, actual,
                    "Kết quả đăng nhập không khớp dữ liệu file cho username: " + username);

            if (actual.equals("success")) {
                loginPage.logout();
            }
            count++;
        }
    }

    @Then("kết quả đăng nhập phải là {string}")
    public void ket_qua_dang_nhap_phai_la(String expectedResult) {

    }
}
