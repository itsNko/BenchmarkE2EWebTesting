package tests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class Installer {
	
	protected WebDriver driver;
	protected static final String install_url = "http://some-prestashop:80/install/";
	@Test
	public void install() throws InterruptedException {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--no-sandbox", "--headless=new", "--lang=it", "--disable-gpu", "--screen-info={1920x1080}");
		try {
			driver = new RemoteWebDriver(new URL("http://browser:4444/wd/hub"), chromeOptions);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		driver.manage().window().maximize();
		driver.get(install_url);
		new Select(driver.findElement(By.id("langList"))).selectByVisibleText("English (English)");
		driver.findElement(By.id("btNext")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("set_license")).click();
		driver.findElement(By.id("btNext")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("infosShop")).sendKeys("E2E Web Testing store");
		driver.findElement(By.id("infosActivity_chosen")).click();
		driver.findElement(By.xpath("//*[@id=\"infosActivity_chosen\"]/div/div/input")).sendKeys("Computer");
		driver.findElement(By.xpath("//*[@id=\"infosActivity_chosen\"]/div/div/input")).sendKeys(Keys.ENTER);
		driver.findElement(By.id("infosFirstname")).sendKeys("Dario");
		driver.findElement(By.id("infosName")).sendKeys("Olianas");
		driver.findElement(By.id("infosEmail")).sendKeys("admin@prestashop.com");
		driver.findElement(By.id("infosPassword")).sendKeys("e2eW3Bt3s71nGB3nchM4rK");
		driver.findElement(By.id("infosPasswordRepeat")).sendKeys("e2eW3Bt3s71nGB3nchM4rK");
		driver.findElement(By.id("btNext")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("dbServer")).clear();
		driver.findElement(By.id("dbServer")).sendKeys("some-mysql");
		driver.findElement(By.id("dbPassword")).sendKeys("admin");
		driver.findElement(By.id("btTestDB")).click();
		Thread.sleep(5000);
		driver.findElement(By.id("btCreateDB")).click();
		Thread.sleep(5000);
		driver.findElement(By.id("btNext")).click();
		boolean success = new WebDriverWait(driver, Duration.ofSeconds(180)).until(ExpectedConditions.textToBe(By.xpath("//*[@id=\"install_process_success\"]/div[1]/h2"), "Your installation is finished!"));
		if(success) {
			System.out.println("Setup complete. Now: \n1) access the container using docker exec -it some-prestashop bash\n 2) remove directory \"install\"\n 3) rename \"admin\" directory to \"administrator\" ");
		}
		else {
			System.out.println("!! POSSIBLY an error occurred during the installation. Check you application");
		}
		driver.quit();
	}

}
