package Generic;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.bcel.classfile.Method;
import org.testng.annotations.DataProvider;
import DataProvider.TestDataHandler;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
//import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import org.openqa.selenium.support.ui.Select;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.asserts.SoftAssert;


public class BaseClass {

	TestDataHandler testDataHandler;
	protected SoftAssert sAssert = new SoftAssert();
	UtilityClass C1 = new UtilityClass();
	public static boolean blnResult;
	protected static Logger Log = Logger.getLogger(BaseClass.class);
	public static WebDriver driver;
	Select dropdown;
	private static String strFileName;
	private static String strSheetName;
	public static Hashtable<String, String> testData;
	public UtilityClass properties = new UtilityClass();

	/**
	 * Description : Function to Step up the HTML folder in current project
	 * before suite
	 */
	@BeforeSuite
	public void init1() {
		Reporter.folderSetup();
	}

	/**
	 * Description: Function to generate the Final report after suite
	 */
	@AfterSuite
	public void teardown1() {
		Reporter.generateFinalReport();
	}

	/**
	 * Description :- Data provider to fetch the data from Excel
	 * @param M :- 
	 * @return :- 
	 */
	@DataProvider(name = "TestDataProvider")
	protected Iterator<Object[]> getTestData(Method M) {
		testDataHandler = new TestDataHandler();
		List<Hashtable<String, String>> dataList = TestDataHandler.readFile(strFileName, strSheetName, M.getName());
		List<Object[]> testArray = new ArrayList<Object[]>();
		Iterator<Hashtable<String, String>> dataIterator = dataList.iterator();
		while (dataIterator.hasNext()) {
			Hashtable<String, String> testDataSet = dataIterator.next();
			if (!(testDataSet.get("ExecuteFlag").equalsIgnoreCase("Y"))) {
				dataIterator.remove();
			} else {
				Object[] dataObj = { testDataSet };
				testArray.add(dataObj);
			}
		}
		return testArray.iterator();
	}

	/**
	 * Description :- Function to choose sheet name form Excel(Test data excel)
	 * @param sheetName :- Excel sheet name
	 */
	protected void setSheetName(String sheetName) {
		strSheetName = sheetName;
	}

	/**
	 * Description :- Function to choose Excel File name
	 * @param fileName :- Excel file name 
	 */
	protected void setFileName(String fileName) {
		strFileName = fileName;
	}

	/**
	 * Description :- Function to Launch browser and the Application.
	 * - If the browser type is "ff" then Launch FireFox Browser.
	 * - Else If the browser type is "ch" then Launch Chrome Browser.
	 * - Else If the browser type is "ie" then Launch IE Browser.
	 * - Then Maximize the browser window and delete all the cookies.
	 * @param URL : URL to lunch the application
	 */
	public void setBrowser(String URL) {
		blnResult = true;
		if (driver == null) {
			String btype = C1.samplePropertiesFile("Browsertype").trim();
			try {
				if (btype.equals("ff")) {
					driver = new FirefoxDriver();
					System.out.println("Firefox started");
					Reporter.reportEvent("PASS", "Launch FireFox Browser");
					Log.info("Launch FireFox Browser");
				} else if (btype.equals("ch")) {
					String Chromepath;
					if (System.getProperty("os.name").contains("Window")) {
						Chromepath = ClassLoader.getSystemResource("chromedriver.exe").getFile();
					} else {
						Chromepath = ClassLoader.getSystemResource("chromedriver").getFile();
					}
					System.setProperty("webdriver.chrome.driver", Chromepath);
					driver = new ChromeDriver();
					Reporter.reportEvent("PASS", "Launch Chrome Browser");
					Log.info("Launch Chrome Browser");

				} else if (btype.equals("ie")) {
					String IEpath = ClassLoader.getSystemResource("IEDriverServer.exe").getFile();
					System.setProperty("webdriver.ie.driver", IEpath);
					driver = new InternetExplorerDriver();
					Reporter.reportEvent("PASS", "Launch IE Browser");
					Log.info("Launch IE Browser");
				}
			} catch (WebDriverException wd) {
				Reporter.reportEvent("FAIL", "Error in intailizing the Webdriver " + wd.getMessage());
				Log.info("Error in intailizing the Webdriver " + wd.getStackTrace());
			}
		}
		driver.get(URL);
		Reporter.reportEvent("PASS", "Successfully Lunch the Application with the URL " + URL.trim());
		Log.info("Successfully Lunch the Application with the URL " + URL.trim());
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		Reporter.reportEvent("PASS", "Successfully maximize the window and delete all the cookies");
		Log.info("Successfully maximize the window and delete all the cookies");
	}

	/**
	 * Description: Function to close the browser
	 */
	public static void closeBrowser() {
		if(null!=driver) {
		driver.close();
		WebDriver d = driver;
		driver = null;
		d.quit();
		Log.info("Closed the browser");
		}
	}
}
