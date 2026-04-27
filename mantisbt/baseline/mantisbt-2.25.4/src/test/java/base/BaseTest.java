package base;

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
import po.LoginPage;

public class BaseTest {
	
	public static WebDriver driver;
	public static final String app_url = "http://mantisbt:8989";
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void login() {
		System.out.println("\n========== TEST START: " + testName.getMethodName() + " ==========");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox", "--lang=en", "--disable-gpu", "--screen-info={1920x1080}", "--guest");
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
		new LoginPage(driver).setUsername("administrator").setPassword("e2eW3Bt3s71nGB3nchM4rK").login();
	}

	/*@Before
	public void login() {
		//WebDriverManager.chromedriver().clearDriverCache().setup();
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox", "--headless=new", "--lang=en", "--disable-gpu", "--screen-info={1920x1080}", "--guest");
		driver = new ChromeDriver(chromeOptions);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
		driver.manage().window().maximize();
		driver.get("http://localhost:8989");
		new LoginPage(driver).setUsername("administrator").setPassword("e2eW3Bt3s71nGB3nchM4rK").login();
	}*/

	
	@After
	public void tearDown() {
		driver.quit();
	}
}
