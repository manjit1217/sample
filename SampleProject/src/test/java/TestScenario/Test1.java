
package TestScenario;

import java.lang.reflect.Method;
import java.util.Hashtable;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import Generic.BaseClass;
import Generic.Reporter;
import Generic.UtilityClass;

public class Test1 extends BaseClass {
	
	
UtilityClass properties = new UtilityClass();

	/**
	 * Description: Get Test data from Excel Sheet
	 */
	@BeforeClass
	private void setDataPath() {
		setFileName("AIB");
		setSheetName("BranchLocator");
	}

	/**
	 * Description:Launch Internet Login Reset Registration Application
	 */
	@BeforeMethod
	private void init(Method m) {
		Reporter.initialize(m.getName().trim());
		setBrowser(properties.samplePropertiesFile("URL").trim());
	}

	@Test(dataProvider = "TestDataProvider", priority = 1)
	private void BranchLocator1001(Hashtable<String, String> dataTable) {
		testData = dataTable;
	}
}
