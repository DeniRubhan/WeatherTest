package WeatherReportTest;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class TestWeatherApp {
	Actions action;
	ExtentReports extent;
	ExtentTest logger;
	
	SoftAssert sa;
	WebDriver driver;
	
	@BeforeClass(alwaysRun = true)
	  public void setUp() {
		System.out.println("Entering into the @BeforeClass");
		System.setProperty("webdriver.chrome.driver", ReportConfig.chromeDriverPath); //
		ExtentHtmlReporter htmlReporter;
		htmlReporter = new ExtentHtmlReporter(ReportConfig.htmlReporter);
		
		
		extent = new ExtentReports();
		extent.setSystemInfo("BaseUrl", ReportConfig.URL);
		extent.setSystemInfo("Environment", "Automation Testing");
		htmlReporter.loadXMLConfig(new File(System.getProperty("user.dir") + File.separator + "extent-config.xml"));
		extent.attachReporter(htmlReporter);
		
		
		driver = new ChromeDriver(); //
		
		driver.manage().window().maximize();//
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);//
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

	  }

	
	@Test(priority=0,timeOut = 300000)
	public void testWeatherApp() {
		try {
			logger = extent.createTest("check Page-Load");
		System.out.println("Entering into the @Test");
		driver.manage().window().maximize();
		sa = new SoftAssert();
		
		driver.get(ReportConfig.URL);
		checkForThePageLoad("moreDetails","More details");
		driver.findElement(By.xpath("//input")).click();
	    driver.findElement(By.xpath("//input")).clear();
	    
	    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Help'])[1]/following::button[1]")).click();
	    Thread.sleep(1000);
	    String message1 = driver.findElement(By.id("message1")).getText();
	    
	    if(message1.equalsIgnoreCase("Need to provide the address")) {
	    	Reporter.log("Search is working fine",true);
	    } else {
	    	Reporter.log("Search is not working fine",true);
	    }
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		} finally {
			sa.assertAll();
		}
	}
	
	@Test(priority=1,timeOut = 300000)
	public void testWithAddress() {
		try {
			logger = extent.createTest("Checking the Address");
			System.out.println("Entering into the @Test");
			
			driver.findElement(By.xpath("//input")).click();
		    driver.findElement(By.xpath("//input")).clear();
		    driver.findElement(By.xpath("//input")).sendKeys(ReportConfig.placeToTest);
		    driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Help'])[1]/following::button[1]")).click();
		    Thread.sleep(1000);
		    String message1 = driver.findElement(By.id("message1")).getText();
		    if(message1.contains(ReportConfig.placeToTest)) {
		    	Reporter.log("Weather report is obtained",true);
		    } else {
		    	Reporter.log("Weather report is not obtained",true);
		    }
		    Thread.sleep(10000);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			sa.assertAll();
		}
	}
	
	@Test(priority=2,timeOut = 300000)
	public void testMoreInfo() {
		try {
			logger = extent.createTest("checkPageLoad More info");
			System.out.println("Entering into the More info");
			sa = new SoftAssert();
			Reporter.log("Moving to More info",true);
			
			driver.findElement(By.id("moreDetails")).click();
			Reporter.log("More info Page is loaded",true);
			 Thread.sleep(10000);
			 driver.navigate().back();
			 checkForThePageLoad("moreDetails","More details");
			 
		} catch (Exception e) {
			// TODO: handle exception
			Reporter.log("Error in loading",true);
		} finally {
			sa.assertAll();
		}
	}
	
	@Test(priority=3,timeOut = 300000)
	public void checkAbout() {
		try {
			logger = extent.createTest("checkPageLoad About Page");
			System.out.println("Entering into the About Page");
			sa = new SoftAssert();
			Reporter.log("Entering into the About Page",true);
			
			driver.findElement(By.linkText("About")).click();
			Thread.sleep(10000);
			driver.findElement(By.linkText("Weather")).click();
			checkForThePageLoad("moreDetails","More details");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			sa.assertAll();
		}
	}
	
	public void checkForThePageLoad(String id, String name) {
		System.out.println("Testing the Data loaded");
		if(isElementDisplayed(driver, 10, id, name)) {
			Reporter.log("Page Loaded",true);
		} else {
			Reporter.log("Page Not Loaded",true);
		}
		
	}
	
	public static boolean isElementDisplayed(WebDriver driver, int implicitTime, String id, String elementName) {
		driver.manage().timeouts().implicitlyWait(implicitTime, TimeUnit.SECONDS);
		boolean result = true;
		try {

			for (int second = 0; second <= 120; second++) {
//				if (second >= 10)
//					try {
				if (driver.findElement(By.id(id)).isDisplayed()) {
					
					System.out.println("element displayed");
					break;
				}
				if(second==120){
					System.out.println("element not displayed");
					result = false;
				}
//					} catch (Exception e) {
//					}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			driver.manage().timeouts().implicitlyWait(implicitTime, TimeUnit.SECONDS);
		}
		return result;
	}
	
	@AfterMethod //
	public void getResut(ITestResult result) {
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				
				logger.log(Status.FAIL, result.getName() + " Test Case is failed");
				logger.log(Status.FAIL, "Test Case failed is " + result.getThrowable());
			} else if (result.getStatus() == ITestResult.SKIP) {
				logger.log(Status.SKIP, result.getName() + " Test Case is Skipped");
				logger.log(Status.SKIP, "Test Case skipped is " + result.getThrowable());
			}
		} catch (Exception e) {
			logger.log(Status.FAIL, e.getMessage());
		} finally {
			// extent.removeTest(logger);
		}
	}

	
     
	
	@AfterClass(alwaysRun = true) //
	  public void tearDown() throws Exception {
		//extent.createTest("TestName");
		extent.flush();
		driver.quit();
		
	}
	

}
