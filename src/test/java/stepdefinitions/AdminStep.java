package stepdefinitions;

import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.AdminPage;

public class AdminStep {
    private final Hooks hooks;
    private AdminPage adminPage;

    public AdminStep(Hooks hooks) {
        this.hooks = hooks;
        this.adminPage = new AdminPage(hooks.getDriver(), hooks.getWait());
    }

    @And("người dùng mở trang Admin - System Users")
    public void nguoi_dung_mo_trang_admin() {

    }

    @When("người dùng lọc theo username {string} và role {string}")
    public void nguoi_dung_loc_theo_username_role(String username, String role) {

    }

    @Then("số lượng bản ghi hiển thị phải khớp với số dòng dữ liệu")
    public void so_luong_record_khop_so_dong_du_lieu(){

    }
}
