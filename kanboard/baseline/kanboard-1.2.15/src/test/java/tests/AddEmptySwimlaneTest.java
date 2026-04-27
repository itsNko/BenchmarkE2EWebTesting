package tests;




import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import po.KanboardHomePage;
import po.LoginPage;
import po.SwimlanesPage;




public class AddEmptySwimlaneTest extends BaseTest {
		  
	@Test()
	public void addNewSwimlane() {
		KanboardHomePage home = new LoginPage(driver)
				.loginToKanboard("admin", password);
		
		// Small wait after login for page to fully load
		new WebDriverWait(driver, Duration.ofSeconds(2)).until(
			ExpectedConditions.visibilityOfElementLocated(By.id("dashboard"))
		);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SwimlanesPage swimlanes = home
				.firstProjectSummary()
				.swimlanes()
				.addSwimlane()
				.save();
		
		assertEquals("The name is required", swimlanes.getErrorMessage());
			  
	}
		  
		  
		  
}