package stepdefinitions;

import hooks.Hooks;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.PeronalPage;

public class PersonalStep {
    private final Hooks hooks;
//  TODO:  PersonalPage
    private PeronalPage peronalPage;

    public PersonalStep(Hooks hooks) {
        this.hooks = hooks;
        peronalPage = new PeronalPage(hooks.getDriver(), hooks.getWait());
    }

    @And("người dùng mở trang cá nhân của nhân viên")
    public void nguoi_dung_mo_trang_ca_nhan() throws InterruptedException {
        peronalPage.open();
    }

    @When("người dùng upload ảnh đại diện {string}")
    public void nguoi_dung_upload_anh_dai_dien(String fileName) throws  InterruptedException {
        peronalPage.uploadAvatar(fileName);
    }

    @Then("hệ thống hiển thị thông báo upload ảnh thành công")
    public void he_thong_hien_thi_thanh_cong() {
        Assertions.assertTrue(peronalPage.isAvatarUploadSuccessfully(), "Upload that bai");
    }
}
