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
import po.BaseNavBar;
import po.ProfilePageInfo;

public class BaseTest {
	
	protected static final String rootPassword = "dodicicaratteri";
	protected static final String app_url = "http://joomla/";
	public static WebDriver driver;
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void goToHome() {
		System.out.println("\n========== TEST START: " + testName.getMethodName() + " ==========");
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox", "--disable-gpu", "--screen-info={1920x1080}");
		chromeOptions.setCapability("se:recordVideo", true);
		chromeOptions.setCapability("se:timeZone", "UTC");
		chromeOptions.setCapability("se:screenResolution", "1920x1080");
		try {
			driver = new RemoteWebDriver(new URL("http://browser:4444/wd/hub"), chromeOptions);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
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

		try {
			// Wait for video file to be finalized
			Thread.sleep(2000); 
			java.nio.file.Path videoDir = java.nio.file.Paths.get("videos");
			
			if (!java.nio.file.Files.exists(videoDir)) {
				System.out.println("✗ Videos directory does not exist");
				return;
			}
			
			// Find the most recently modified .mp4 file
			java.nio.file.Path latestVideo = java.nio.file.Files.list(videoDir)
				.filter(p -> p.toString().endsWith(".mp4"))
				.max((p1, p2) -> {
					try {
						long t1 = java.nio.file.Files.getLastModifiedTime(p1).toMillis();
						long t2 = java.nio.file.Files.getLastModifiedTime(p2).toMillis();
						return Long.compare(t1, t2);
					} catch (Exception e) {
						return 0;
					}
				})
				.orElse(null);
			
			if (latestVideo != null) {
				java.nio.file.Path newFile = videoDir.resolve(testMethodName + ".mp4");
				java.nio.file.Files.move(latestVideo, newFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				System.out.println("✓ Renamed video to: " + testMethodName + ".mp4");
			} else {
				System.out.println("✗ No video file found in videos directory");
			}
		} catch (Exception e) {
			System.err.println("✗ Failed to rename video file: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected ProfilePageInfo loginAsAdmin() {
		return new BaseNavBar(driver)
			.authorLogin()
			.setUsername("administrator")
			.setPassword(rootPassword)
			.login();
	}
}
