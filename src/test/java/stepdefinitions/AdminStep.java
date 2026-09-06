package stepdefinitions;

import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.AdminPage;

public class AdminStep {
    private final Hooks hooks;
    private final AdminPage adminPage;

    public AdminStep(Hooks hooks) {
        this.hooks = hooks;
        this.adminPage = new AdminPage(hooks.getDriver(), hooks.getWait());
    }

    @And("người dùng mở trang Admin - System Users")
    public void nguoi_dung_mo_trang_admin() throws InterruptedException {
        adminPage.open();
        Thread.sleep(1000);
    }

    @When("người dùng lọc theo username {string} và role {string}")
    public void nguoi_dung_loc_theo_username_role(String username, String role) throws  InterruptedException {
        adminPage.filterByUser(username, role);
    }

    @Then("số lượng bản ghi hiển thị phải khớp với số dòng dữ liệu")
    public void so_luong_record_khop_so_dong_du_lieu(){
        Assertions.assertTrue(adminPage.checkNumberOfRecords(), "Số records không khớp với số dòng dữ liệu");
    }
}
