package runners;

// define chay toàn bộ test case

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

// ===== FIX: đổi tên class CucumberTestRunner -> RunCucumberTest =====
// Lý do: Surefire chỉ nhận diện class test có tên khớp **/*Test.java,
// nên tên cũ khiến 0 test nào được chạy.
@Suite
@IncludeEngines( "cucumber")
@SelectPackages( "features") // FIX: đổi từ @SelectClasspathResource("features") vì "features" là package chứa nhiều file .feature
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value="hooks,stepdefinitions")
public class RunCucumberTest {
}
// ===== END FIX =====
