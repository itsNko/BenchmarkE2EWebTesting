package tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseTest {

	public static final String password = "admin";
	public static final String app_url = "http://kanboard:80";
	public WebDriver driver;

	@Rule
	public TestName testName = new TestName();

	@Before
	public void setUp() {
		System.out.println("\n========== TEST START: " + testName.getMethodName() + " ==========");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox", "--disable-gpu", "--screen-info={1920x1080}");
		chromeOptions.setCapability("se:recordVideo", true);
		chromeOptions.setCapability("se:timeZone", "UTC");
		chromeOptions.setCapability("se:screenResolution", "1920x1080");
		chromeOptions.setCapability("se:name", testName.getMethodName());
		chromeOptions.setCapability("se:videoUploadOnPass", true);
		try {
			driver = new RemoteWebDriver(new URL("http://browser:4444/wd/hub"), chromeOptions);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		driver.manage().window().maximize();
		driver.get(app_url);
	}

	@After
	public void tearDown() {
		System.out.println("========== TEST FINISH: " + testName.getMethodName() + " ==========\n");
		if (driver != null) {
			driver.quit();
		}
	}

}
