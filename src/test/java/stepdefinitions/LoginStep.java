package stepdefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.LoginPage;

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

    @When("người dùng đăng nhập với tài khoản {string} và mật khẩu {string}")
    public void nguoi_dung_dang_nhap_voi_tai_khoan(String username, String password) {
//        flow: điền username và password vào các ô input tương ứng và click
//        vào nút Login
        LoginPage loginPage = loginPage();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
    }

    @Then("người dùng được chuyển đến trang Dashboard")
    public void nguoi_dung_da_chuyen_den_trang_dashboard() {
        String currentUrl = hooks.getDriver().getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("dashboard"), "Phải chuyển đến trang Dashboard");
    }

    @Then("hệ thống báo lỗi và vẫn ở trang đăng nhập")
    public void he_thong_bao_loi_va_vung_o_trang_dang_nhap() {
        String currentUrl = hooks.getDriver().getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("auth/login"), "Phải ở trang Logi");
    }

    @Then("kết quả đăng nhập phải là {string}")
    public void ket_qua_dang_nhap_phai_la(String expectedResult) {

    }
}
