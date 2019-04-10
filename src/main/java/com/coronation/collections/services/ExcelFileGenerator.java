package com.coronation.collections.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelFileGenerator {
	private String reportGeneratedFilePath;

	private String fileName;

	private List<String> headerList = new ArrayList<String>();

	private String pageHeader;

	private List<String[]> data = new ArrayList<String[]>();

	private String sheetTitle;
	
	public String generateExcel() {
		String fileName = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMYYYY");
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			fileName = this.getReportGeneratedFilePath().concat("/")
					.concat(this.getFileName().concat(dateFormat.format(new Date())).concat(".xlsx"));

			//System.out.println("excel b");

			XSSFSheet sheet = workbook.createSheet(this.getSheetTitle());
			CellStyle style = workbook.createCellStyle();
			style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
			style.setFillPattern(CellStyle.SOLID_FOREGROUND);
			Font font = workbook.createFont();
			font.setColor(IndexedColors.WHITE.getIndex());
			style.setFont(font);

			//System.out.println("excel c");
			int rowNum = 0;
			// System.out.println("Creating excel");
			Row rowh = sheet.createRow(rowNum++);
			if (rowNum == 1) {
				for (int i = 0; i < this.getHeaderList().size(); i++) {
					Cell cellh = rowh.createCell(i);
					if (i == this.getHeaderList().size() / 2) {
						// System.out.println("Creating excel");
						cellh.setCellValue(pageHeader);
					} else {
						cellh.setCellValue("");
					}
				}
			}
			//System.out.println("excel 1");

			Row row = sheet.createRow(rowNum++);
			if (rowNum == 2) {

				for (int i = 0; i < this.getHeaderList().size(); i++) {
					// System.out.println(i);
					Cell cell = row.createCell(i);
					cell.setCellStyle(style);

					cell.setCellValue(this.getHeaderList().get(i));

				}
			}
			//System.out.println("excel 2");
			for (String[] data : this.getData()) {
				row = sheet.createRow(rowNum++);

				for (int i = 0; i < data.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(data[i]);

				}
			}

			// add 3 empty rows to separate inflow and outflow
			/**
			 * rowNum = rowNum + 3; System.out.println("excel 3");
			 * 
			 * Row rowb = sheet.createRow(rowNum++); // if (rowNum == 1) { for
			 * (int i = 0; i < 8; i++) { Cell cellb = rowb.createCell(i); if (i
			 * == 3) { // System.out.println("Creating excel");
			 * cellb.setCellValue("DAILY REPORT ON ALL BORROWING CUSTOMERS
			 * (OUTFLOWS)"); } else { cellb.setCellValue(""); } } // }
			 * 
			 * System.out.println("excel 4"); Row row2 =
			 * sheet.createRow(rowNum++); if (rowNum > 0) {
			 * 
			 * for (int i = 0; i < 8; i++) { // System.out.println(i); Cell
			 * cell1 = row2.createCell(i); cell1.setCellStyle(style); if (i ==
			 * 0) { cell1.setCellValue("CUSTOMER ACCOUNT NUMBER"); } if (i == 1)
			 * { cell1.setCellValue("CUSTOMER ACCOUNT NAME"); } if (i == 2) {
			 * cell1.setCellValue("OUTSTANDING BALANCE"); } if (i == 3) {
			 * cell1.setCellValue("BENEFICIARY NAME"); } if (i == 4) {
			 * cell1.setCellValue("BENEFICIARY ACCT_NUM"); } if (i == 5) {
			 * cell1.setCellValue("AMOUNT"); } if (i == 6) {
			 * cell1.setCellValue("RECEIVING BANK"); } if (i == 7) {
			 * cell1.setCellValue("DESCRIPTION"); }
			 * 
			 * } }
			 * 
			 * System.out.println("excel 5"); for (InwardData data : list2) {
			 * row2 = sheet.createRow(rowNum++);
			 * 
			 * for (int i = 0; i < 8; i++) { Cell cell1 = row2.createCell(i); if
			 * (i == 0) {
			 * cell1.setCellValue(data.getBeneficiaryAccountNumber()); } if (i
			 * == 1) { cell1.setCellValue(data.getBeneficiaryAccountName()); }
			 * if (i == 2) {
			 * cell1.setCellValue(Double.valueOf(data.getOutstandingBalance().toString()));
			 * } if (i == 3) {
			 * cell1.setCellValue(data.getOriginatorAccountName()); } if (i ==
			 * 4) { cell1.setCellValue(data.getOriginatorAccountNumber()); } if
			 * (i == 5) {
			 * cell1.setCellValue(Double.valueOf(data.getAmount().toString()));
			 * } if (i == 6) { cell1.setCellValue(data.getSourceInstitution());
			 * } if (i == 7) { cell1.setCellValue(data.getDescription()); }
			 * 
			 * } }
			 * 
			 * // add 3 empty rows to separate inflow and outflow rowNum =
			 * rowNum + 3; System.out.println("excel 6");
			 * 
			 * Row rowDraft = sheet.createRow(rowNum++); // if (rowNum == 1) {
			 * for (int i = 0; i < 8; i++) { Cell cellDraft =
			 * rowDraft.createCell(i); if (i == 3) { //
			 * System.out.println("Creating excel");
			 * cellDraft.setCellValue("DAILY REPORT ON ALL BORROWING CUSTOMERS
			 * (OVERDRAFT)"); } else { cellDraft.setCellValue(""); } }
			 * 
			 * System.out.println("excel 4"); Row row2Draft =
			 * sheet.createRow(rowNum++); if (rowNum > 0) {
			 * 
			 * for (int i = 0; i < 8; i++) { // System.out.println(i); Cell
			 * cell1Draft = row2Draft.createCell(i);
			 * cell1Draft.setCellStyle(style); if (i == 0) {
			 * cell1Draft.setCellValue("CUSTOMER ACCOUNT NUMBER"); } if (i == 1)
			 * { cell1Draft.setCellValue("CUSTOMER ACCOUNT NAME"); } if (i == 2)
			 * { cell1Draft.setCellValue("OUTSTANDING BALANCE"); } if (i == 3) {
			 * cell1Draft.setCellValue("BENEFICIARY NAME"); } if (i == 4) {
			 * cell1Draft.setCellValue("BENEFICIARY ACCT_NUM"); } if (i == 5) {
			 * cell1Draft.setCellValue("AMOUNT"); } if (i == 6) {
			 * cell1Draft.setCellValue("RECEIVING BANK"); } if (i == 7) {
			 * cell1Draft.setCellValue("DESCRIPTION"); }
			 * 
			 * } }
			 * 
			 * System.out.println("excel 7"); for (InwardData data :
			 * overDraftData) { row2Draft = sheet.createRow(rowNum++);
			 * 
			 * for (int i = 0; i < 8; i++) { Cell cell1Draft =
			 * row2Draft.createCell(i); if (i == 0) {
			 * cell1Draft.setCellValue(data.getBeneficiaryAccountNumber()); } if
			 * (i == 1) {
			 * cell1Draft.setCellValue(data.getBeneficiaryAccountName()); } if
			 * (i == 2) {
			 * cell1Draft.setCellValue(Double.valueOf(data.getOutstandingBalance().toString()));
			 * } if (i == 3) {
			 * cell1Draft.setCellValue(data.getOriginatorAccountName()); } if (i
			 * == 4) {
			 * cell1Draft.setCellValue(data.getOriginatorAccountNumber()); } if
			 * (i == 5) {
			 * cell1Draft.setCellValue(Double.valueOf(data.getAmount().toString()));
			 * } if (i == 6) {
			 * cell1Draft.setCellValue(data.getSourceInstitution()); } if (i ==
			 * 7) { cell1Draft.setCellValue(data.getDescription()); }
			 * 
			 * } }
			 */
			//System.out.println("excel 8");
			for (int i = 0; i < this.getHeaderList().size(); i++) {
				sheet.autoSizeColumn(i);				
			}
			FileOutputStream outputStream = new FileOutputStream(fileName);
			workbook.write(outputStream);
			//workbook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//System.out.println("Done");
		// return fileName1.concat(dateFormat.format(new
		// Date())).concat(".xlsx");
		return fileName;
	}

	//@SuppressWarnings("deprecation")
	public List<List<String>> readExcel() {
		List<List<String>> returnList = new ArrayList<List<String>>();
		try (FileInputStream excelFile = new FileInputStream(new File(this.getFileName()));
			 Workbook workbook = new XSSFWorkbook(excelFile);) {


			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();

			int i = 0;
			List<String> rowData = null;
			while (iterator.hasNext()) {
				//System.out.println("i === " + i);
				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				if (i != 0) {
					rowData = new ArrayList<String>();

					while (cellIterator.hasNext()) {

						Cell currentCell = cellIterator.next();
						if (currentCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							rowData.add(currentCell.getStringCellValue());
							//System.out.println("i === " + i+",currentCell.getStringCellValue()==="+currentCell.getStringCellValue());
						} else if (currentCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							rowData.add(String.valueOf(currentCell.getNumericCellValue()));
							//System.out.println("i === " + i+"value==="+String.valueOf(currentCell.getNumericCellValue()));
						}

					}
					returnList.add(rowData);

				}
				i++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnList;
	}

	/**
	 * @return the reportGeneratedFilePath
	 */
	public String getReportGeneratedFilePath() {
		return reportGeneratedFilePath;
	}

	/**
	 * @param reportGeneratedFilePath
	 *            the reportGeneratedFilePath to set
	 */
	public void setReportGeneratedFilePath(String reportGeneratedFilePath) {
		this.reportGeneratedFilePath = reportGeneratedFilePath;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the headerList
	 */
	public List<String> getHeaderList() {
		return headerList;
	}

	/**
	 * @param headerList
	 *            the headerList to set
	 */
	public void setHeaderList(List<String> headerList) {
		this.headerList = headerList;
	}

	/**
	 * @return the pageHeader
	 */
	public String getPageHeader() {
		return pageHeader;
	}

	/**
	 * @param pageHeader
	 *            the pageHeader to set
	 */
	public void setPageHeader(String pageHeader) {
		this.pageHeader = pageHeader;
	}

	/**
	 * @return the data
	 */
	public List<String[]> getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List<String[]> data) {
		this.data = data;
	}

	/**
	 * @return the sheetTitle
	 */
	public String getSheetTitle() {
		return sheetTitle;
	}

	/**
	 * @param sheetTitle
	 *            the sheetTitle to set
	 */
	public void setSheetTitle(String sheetTitle) {
		this.sheetTitle = sheetTitle;
	}


}
