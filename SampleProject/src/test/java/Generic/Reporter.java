package Generic;

	import java.awt.AWTException;
	import java.awt.Rectangle;
	import java.awt.Robot;
	import java.awt.Toolkit;
	import java.awt.image.BufferedImage;
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.io.OutputStreamWriter;
	import java.io.Writer;
	import java.nio.file.Files;
	import java.nio.file.Paths;
	import java.nio.file.StandardOpenOption;
	import java.text.SimpleDateFormat;
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.List;
	import javax.imageio.ImageIO;
	import org.apache.commons.io.FileUtils;
	import org.openqa.selenium.OutputType;
	import org.openqa.selenium.TakesScreenshot;
	import org.openqa.selenium.UnhandledAlertException;
	import org.testng.Assert;
	
	public class Reporter extends BaseClass {

		private static File statText;
		private static FileOutputStream is;
		private static OutputStreamWriter osw;
		private static Writer w;
		private static String reportPath;
		private static List<ReportEvent> details;
		private static final String resultPlaceholder = "<!--INSERT_RESULT-->";
		private static File rptDir;
		private static File finrptDir;

		/**
		 * Description: Function to Create a HTML report folder in Project
		 */
		public static void folderSetup() {
			String strDate = new SimpleDateFormat("dd_MM_YYYY--hh_mm_ss").format(new Date());
			finrptDir = new File("D:\\workspace\\Channels\\Reports\\Result" + strDate);
			rptDir = new File("D:\\workspace\\Channels\\Reports\\Result" + strDate + "\\TestObjectives");
			if (!rptDir.exists()) {
				if (rptDir.mkdirs()) {
					System.out.println("Reporting folder created at the path: " + rptDir.getAbsolutePath());
				} else {
					System.out.println("Failed to create reporting folder at the path:" + rptDir.getAbsolutePath());
				}
			}
		}

		/**
		 * Description: Function to Initialize the HTML report
		 */
		public static void initialize(String rptName) {
			reportPath = rptDir.getAbsolutePath();
			statText = new File(reportPath + "\\" + rptName + ".html");
			details = null;
			details = new ArrayList<ReportEvent>();
			try {
				is = new FileOutputStream(statText);
				osw = new OutputStreamWriter(is);
				w = new BufferedWriter(osw);
				w.write("<!DOCTYPE html><html><head><h1><font size=6 color=#3399FF>GROUP WEBSITES AUTOMATION TEST RESULTS</h1></head>");
				// w.write("<%=System.DateTime.Today.ToShortDateString()%>");
				w.write("<Body bgcolor=#F0F0F0>  <font size=2.5 color=#505050>");
				w.write("<table BORDER=1 CELLSPACING=1 CELLPADDING=2 bgcolor=#FFFFFF>");
				w.write("<tr ALIGN=center bgcolor=#787878> <font size=3 color=#FFFFFF><th>Step No.</th><th>Status</th><th>Step Description</th></tr>");
				w.write(resultPlaceholder);
				w.write("</table></Body></html>");
				w.close();
			} catch (IOException e) {
				System.err.println("Problem creating the file :" + rptName);
			}
		}

		/**
		 * Description: Function to close the file after update
		 */
		public static void fileClosing() {
			try {
				w.append("</table></Body></html>");
				w.close();
			} catch (IOException e) {
				System.err.println("Problem closing the file TestReport");
			}
		}

		public static String captureScreenShot() throws AWTException, IOException {
			try { //
					// RemoteWebDriver driver = BaseClass.initiateDriver();
				String screenShotFileName = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
				File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				File destination = new File(reportPath + "\\" + screenShotFileName + ".png");
				Thread.sleep(500);
				FileUtils.copyFile(screenshot, destination);
				return (screenShotFileName + ".png");
			} catch (UnhandledAlertException uhe) {
				BufferedImage image = new Robot()
						.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				String screenShotFileName = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
				ImageIO.write(image, "png", new File(reportPath + "\\" + screenShotFileName + ".png"));
				return (screenShotFileName + ".png");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return "-1";
			}
		}

		/**
		 * Description: Function to write the HTML report
		 */
		public static void writeResults() {
			try {
				String reportIn = new String(Files.readAllBytes(Paths.get(statText.getAbsolutePath())));
				for (int i = 0; i < details.size(); i++) {
					if (details.get(i).getResult().equals("PASS")) {
						reportIn = reportIn.replaceFirst(resultPlaceholder,
								"<tr bgcolor=#5AF891><td style=\"text-align:center\">" + Integer.toString(i + 1)
										+ "</td><td style=\"text-align:center\">" + details.get(i).getResult()
										+ "</td><td style=\"text-align:center\">" + details.get(i).getResultText()
										+ "</td><td style=\"text-align:center\"></td></tr>" + resultPlaceholder);
					} else {
						reportIn = reportIn.replaceFirst(resultPlaceholder,
								"<tr bgcolor=#F75D59><td style=\"text-align:center\">" + Integer.toString(i + 1)
										+ "</td><td style=\"text-align:center\">" + details.get(i).getResult()
										+ "</td><td style=\"text-align:center\">" + details.get(i).getResultText()
										+ "</td><td style=\"text-align:center\"></td></tr>" + resultPlaceholder);
					}
				}
				Files.write(Paths.get(statText.getAbsolutePath()), reportIn.getBytes(), StandardOpenOption.WRITE);
				details = null;
			} catch (Exception e) {
				System.out.println("Error when writing report file:\n" + e.toString());
			}
		}

		/**
		 * Description: Function to check the assert or to validate the actual
		 * result with expected
		 */
		public static void reportEvent(String strResult, String ResultText) {
			if (strResult.equals("PASS")) {
				ReportEvent result = new ReportEvent("PASS", ResultText);
				details.add(result);
				Assert.assertEquals(true, true, ResultText);
			} else {
				ReportEvent result = new ReportEvent("FAIL", ResultText);
				details.add(result);
				BaseClass.blnResult = false;
				closeBrowser();
				Assert.assertEquals(true, false, ResultText);
			}
		}

		/**
		 * Description: Function to update the report
		 */
		public static void updateResult() {
			writeResults();
			String strupdatedName;
			String strName = statText.getName();
			if (BaseClass.blnResult) {
				strupdatedName = strName.replace(strName, strName + "$PASS");
			} else {
				strupdatedName = strName.replace(strName, strName + "$FAIL");
			}
			File newFile = new File(rptDir.getAbsolutePath() + "\\" + strupdatedName + ".html");
			statText.renameTo(newFile);
		}

		/**
		 * Description: Function to generate Final Report
		 */
		public static void generateFinalReport() {
			if (!finrptDir.exists()) {
				if (finrptDir.mkdirs()) {
					System.out.println("Reporting folder created at the path: " + rptDir.getAbsolutePath());
				} else {
					System.out.println("Failed to create reporting folder at the path:" + rptDir.getAbsolutePath());
				}
			}
			reportPath = finrptDir.getAbsolutePath();
			statText = new File(reportPath + "\\finalReport.html");
			try {
				is = new FileOutputStream(statText);
				osw = new OutputStreamWriter(is);
				w = new BufferedWriter(osw);
				w.write("<!DOCTYPE html><html><head><h1><font size=6 color=#33E5FF>GROUP WEBSITES TEST RESULTS</h1></head>");
				// w.write("<%=System.DateTime.Today.ToShortDateString()%>");
				w.write("<Body bgcolor=#F0F0F0>  <font size=2.5 color=#505050>");
				w.write("<table BORDER=1 CELLSPACING=1 CELLPADDING=2 bgcolor=#FFFFFF>");
				w.write("<tr ALIGN=center bgcolor=#787878> <font size=3 color=#FFFFFF><th>Test Objective ID</th><th>Status</th><th>Detail</th></tr>");
				File[] directorylisting = new File(rptDir.getAbsolutePath()).listFiles();
				if (directorylisting.length != 0) {
					for (File child : directorylisting) {
						if (child.getName().contains(".html")) {
							if (child.getName().contains("$PASS")) {
								w.write("<tr bgcolor=#5AF891><td style=\"text-align:center\">"
										+ child.getName().split("\\$")[0].substring(0,
												child.getName().split("\\$")[0].length() - 5)
										+ "</td><td style=\"text-align:center\">"
										+ child.getName().split("\\$")[1].split("\\.")[0]
										+ "</td><td style=\"text-align:center\"><a href = 'TestObjectives\\"
										+ child.getName() + "'>Detailed Result</a></td></tr>");
							} else {
								w.write("<tr bgcolor=#F75D59><td style=\"text-align:center\">"
										+ child.getName().split("\\$")[0].substring(0,
												child.getName().split("\\$")[0].length() - 5)
										+ "</td><td style=\"text-align:center\">"
										+ child.getName().split("\\$")[1].split("\\.")[0]
										+ "</td><td style=\"text-align:center\"><a href = 'TestObjectives\\"
										+ child.getName() + "'>Detailed Result</a></td></tr>");
							}
						}
					}
				}
				w.write("</table></Body></html>");
				w.close();
			} catch (IOException e) {
				System.err.println("Problem creating the file : FinalReport");
			}
		}

		public static void main(String[] args) {
			Reporter.folderSetup();
		}
	}
