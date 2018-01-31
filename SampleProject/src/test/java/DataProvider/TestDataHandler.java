package DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import Generic.BaseClass;

public class TestDataHandler extends BaseClass {

	/**
	 * Description: Setting up variables to store required data
	 * it is used to handle the Excel file
	 */
	private static String strDataFileName;
	private static String strDataSheetName;

	/**
	 * Description: -Member Functions, Function to read the excel and convert to
	 * Hash table
	 */
	public static List<Hashtable<String, String>> readFile(String strFileName, String strSheetName,
			String Primary_Key) {
		String colName, colValue;
		strDataFileName = strFileName;
		strDataSheetName = strSheetName;

		List<Hashtable<String, String>> dataList = new ArrayList<Hashtable<String, String>>();
		XSSFSheet sheet = getSheet(strFileName, strSheetName);
		List<Integer> rownum = getRow(Primary_Key, sheet);	
		Iterator<Integer> rowIterator = rownum.iterator();
		while (rowIterator.hasNext()) {
			int rowToLoad = rowIterator.next();
			Hashtable<String, String> codes = new Hashtable<String, String>();
			for (int i = 0; i <= getColumnCount(sheet); i++) {
				colName = sheet.getRow(0).getCell(i).getStringCellValue().trim();
				if (sheet.getRow(rowToLoad).getCell(i) == null) {
					colValue = " ";
				} else {
					colValue = sheet.getRow(rowToLoad).getCell(i).getStringCellValue().trim();
					if (colValue.split(" ")[0].equalsIgnoreCase("Using")) {
						colValue = sheet.getRow(getRow(colValue.split(" ")[1], sheet).get(0))
								.getCell(getColumn(colValue.split(" ")[2], sheet)).getStringCellValue().trim();
					}
				}
				codes.put(colName, colValue);
			}
			dataList.add(codes);
		}
		return dataList;
	}

	/**
	 * Description: Function to set Data in Excel sheet from hashtable for
	 * specific column
	 */
	public void setDataForReview(Hashtable<String, String> hashData, String columnName) {
		try {
			FileInputStream file = new FileInputStream(new File("src\\test\\resources\\" + strDataFileName + ".xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			Cell cell = null;
			XSSFSheet writeSheet = workbook.getSheet(strDataSheetName);
			List<Integer> rownum = getRow(testData.get("PrimaryKey"), writeSheet);
			int cellNum = getColumn(columnName, writeSheet);
			if (null == writeSheet.getRow(rownum.get(0)).getCell(cellNum)) {
				cell = writeSheet.getRow(rownum.get(0)).createCell(cellNum);
			} else {
				cell = writeSheet.getRow(rownum.get(0)).getCell(cellNum);
			}
			cell.setCellValue(hashData.get(columnName));
			file.close();
			FileOutputStream outFile = new FileOutputStream(
					new File("src\\test\\resources\\" + strDataFileName + ".xlsx"));
			workbook.write(outFile);
			outFile.close();
			// workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Description: Function to fetch number of columns in sheet
	 */
	private static int getColumnCount(XSSFSheet sheet) {
		int col = sheet.getRow(0).getLastCellNum();
		return (col - 1);
	}

	/**
	 * Description: Method to locate the row of primary key
	 */
	private static List<Integer> getRow(String primary_Key, XSSFSheet sheet) {
		int i = 1, k = 0;
		List<Integer> rowNum = new ArrayList<Integer>();
		for (i = 1; i < getRowCount(sheet); i++) {
			if (sheet.getRow(i).getCell(0).getStringCellValue().trim().toLowerCase()
					.contains(primary_Key.toLowerCase())) {

				rowNum.add(i);
				k = 1;
				break;
			}
		}
		if (k == 0) {
			rowNum = null;
		}
		return rowNum;
	}

	/**
	 * Description: Function to fetch number of rows in sheet
	 */
	private static int getRowCount(XSSFSheet sheet) {
		return sheet.getLastRowNum() + 1;
	}

	/**
	 * Description: Function to get required sheet as object
	 */
	private static XSSFSheet getSheet(String strFileName, String strSheetName) {
		XSSFSheet sheet = null;
		try {

			FileInputStream fis = new FileInputStream("src/test/resources/" + strFileName + ".xlsx");
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			sheet = wb.getSheet(strSheetName);

			fis.close();
		} catch (FileNotFoundException FnFe) {
			FnFe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return sheet;
	}

	/**
	 * Description: Function to get column number based on column name-
	 */
	private static int getColumn(String columnName, XSSFSheet writeSheet) {
		int i = -1;
		for (i = 2; i < writeSheet.getRow(0).getLastCellNum(); i++) {
			if (writeSheet.getRow(0).getCell(i).getStringCellValue().trim().equalsIgnoreCase(columnName)) {
				break;
			}
		}
		return i;
	}
}
