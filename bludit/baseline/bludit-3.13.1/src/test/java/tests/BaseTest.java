package tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseTest {
	
	public WebDriver driver;
	protected static final String password = "e2eW3Bt3s71nGB3nchM4rK";
	protected static final String username = "admin";
	protected static final String app_url = "http://bludit:80/admin";
	
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
		String testMethodName = testName.getMethodName();
		System.out.println("========== TEST FINISH: " + testMethodName + " ==========\n");
		driver.quit();
	}
}
