package cts.mars;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import org.sikuli.script.*;


public class compUtils {

	public static void dbVsJson(String db, String json) {
		int dbColPosition = 5;
		String jsonPath = "$.Addresses[1].ADDRESSLINE1";
		
		String[] result = db.split(",");
		String field = result[5].trim();
		System.out.println(field);
		
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);
	
		String tag = JsonPath.read(document, jsonPath);
		System.out.println(tag);
		
		if (field.equals(tag)) {
			System.out.println("PASS");
		}
	
	
	}
	

	public static void sqlVsSql(String sqlFile1, String sqlFile2 ) {
		String base_dir = "";
		String sqlFl1 = sqlFile1;
        String sqlFl2 = sqlFile2; //
        HashSet <String> sqlHS = new HashSet<String>();
        HashSet <String> mqlHS = new HashSet<String>();
        HashSet <String> sqlHScopy = new HashSet<String>();
        HashSet <String> mqlHScopy = new HashSet<String>();
        HashSet <String> extraInSQL = new HashSet<String>();
        HashSet <String> extraInMQL = new HashSet<String>();
        String str = null;
        String str2 = null;
        String colHdrs = null;
        Set<String> log = new LinkedHashSet<String>();
        
        int sqlCnt = 0;
        int mqlCnt = 0;
        int sqlExtraCnt = 0;
        int mqlExtraCnt = 0;
        int intersectCnt = 0;
              
        
        try { //Read SQL output file into HSet
            BufferedReader in = new BufferedReader(new FileReader(sqlFl1));
            int i= 0;
            while ((str = in.readLine()) != null) {
            	if (i == 0 ) {
            		colHdrs = str;            		
            	}
            	else { //removed the column names from SQL HashSet to facilitate comparison
            	sqlHS.add(str);
            	sqlCnt++;
            	}
            i++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // SQL result set is now in HSet------------------------------------------------------------------------
        
        try { //Read MongoQL output file into Hash Set
            BufferedReader in = new BufferedReader(new FileReader(sqlFl2));
            int i= 0;
            while ((str = in.readLine()) != null) {
            	if (i == 0 ) {
            		//Do nothing            		
            	}
            	else { //removed the column names from SQL HashSet2 to facilitate comparison
            	mqlHS.add(str);
            	mqlCnt++;
            	}
            i++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // MQL result set is now in HSet------------------------------------------------------------------------
        
        // Finding INTERSECT between SQL & MQL
        sqlHScopy.addAll(sqlHS);
        mqlHScopy.addAll(mqlHS);     
        sqlHScopy.retainAll(mqlHScopy); // INTERSECT between SQL & MQL
        
        
        for (String sqlLine : sqlHS) { //take each line of SQL & check for match in MQL HSet
            if (!mqlHS.contains(sqlLine)) { //if MISmatch found
                extraInSQL.add(sqlLine);
                sqlExtraCnt++;
            }
        }
        for (String mqlLine : mqlHS) { //take each line of MQL & check for match in sQL HSet
            if (!sqlHS.contains(mqlLine)) { //if MISmatch found
            	extraInMQL.add(mqlLine);
            	mqlExtraCnt++;
            }
        }
        
        for (String line : sqlHScopy) {
            intersectCnt++;
        }
        
        // Logging ============================================================================================
        // Build Log Set ======================================================================================
        //log.add("Detailed Comparison Report on SQL & MongoQL Results");
        //log.add(colHdrs);
        //log.add("\nExtra in SQL Output:");
        
        
        // Printing Summary Log  
        System.out.println("Comparison SUMMARY Report on SQL & MongoQL Results");
        System.out.println("==================================================");
        System.out.println("Record Count of SQL : " + sqlCnt);
        System.out.println("Record Count of MQL : " + mqlCnt);
        System.out.println("Record Count in SQL resultset which are absent/differnt in MQL : " + sqlExtraCnt);
        System.out.println("Record Count in MQL resultset which are absent/differnt in SQL : " + mqlExtraCnt);
        System.out.println("Record Count of common Data in SQL & MQL : " + intersectCnt + "\n\n");
        
        // Printing Log  
        System.out.println("Comparison DETAIL Report on SQL & MongoQL Results");
        System.out.println("===================================================");
        System.out.println(colHdrs);
        System.out.print("\nExtra in SQL Output:"); //Dont put Newline, so no println
        
        //System.out.print("Extra in SQL Hash Set Size: " + extraInSQL.size());  //logging to debug
        
        if (extraInSQL.isEmpty() || extraInSQL == null || extraInSQL.size() < 1) {
        	System.out.println(" :: 0");
        }
        else {
        	System.out.println("");
	        for (String line : extraInSQL) {
	        	//System.out.println(line.length());
	            System.out.println(line);
	            //log.add(line); // temporarily disabled as method is not returning Set
	        }
        }
        
        //log.add("\nExtra in MQL Output:");
        System.out.print("\nExtra in MQL Output:"); //Dont put Newline, so no println
        
        if (extraInMQL.isEmpty() || extraInMQL == null || extraInMQL.size() <= 1) {
        	System.out.println(" :: 0");
        }
        else {
        	System.out.println("");
	        for (String line : extraInMQL) {
	            System.out.println(line);
	            //log.add(line); // temporarily disabled as method is not returning Set
	        }
        }
	        
        //log.add("\nCommon between SQL & MQL Output:");
        System.out.print("\nCommon between SQL & MQL Output:");
        
        if (sqlHScopy.isEmpty() || sqlHScopy == null || sqlHScopy.size() <= 1) {
        	System.out.println(" :: 0");
        }
        else {
        	System.out.println("");
	        for (String line : sqlHScopy) {
	            System.out.println(line);
	            //log.add(line);
	        }
        }
		
		//return log;
	}

	
	public static void sqlVsMql(String sqlFile, String mqlFile ) {
		String base_dir = "";
		String sqlFl = null;
        String mqlFl = null; //
        HashSet <String> sqlHS = new HashSet<String>();
        HashSet <String> mqlHS = new HashSet<String>();
        HashSet <String> sqlHScopy = new HashSet<String>();
        HashSet <String> mqlHScopy = new HashSet<String>();
        HashSet <String> extraInSQL = new HashSet<String>();
        HashSet <String> extraInMQL = new HashSet<String>();
        String str = null;
        String str2 = null;
        String colHdrs = null;
        Set<String> log = new LinkedHashSet<String>();
        
        int sqlCnt = 0;
        int mqlCnt = 0;
        int sqlExtraCnt = 0;
        int mqlExtraCnt = 0;
        int intersectCnt = 0;
        
        //file type detection-------------------------------------
        if (sqlFile.contains("sql")) {
        	sqlFl = sqlFile;
        }
        else if (mqlFile.contains("sql")) {
        	sqlFl = mqlFile;
        }
        
        if (sqlFile.contains("mql")) {
        	mqlFl = sqlFile;
        }
        else if (mqlFile.contains("mql")) {
        	mqlFl = mqlFile;
        } //file type detection ENDs-----------------------------
        
        
        try { //Read SQL output file into HSet
            BufferedReader in = new BufferedReader(new FileReader(sqlFl));
            int i= 0;
            while ((str = in.readLine()) != null) {
            	if (i == 0 ) {
            		colHdrs = str;            		
            	}
            	else { //removed the column names from SQL HashSet to facilitate comparison
            	sqlHS.add(str);
            	sqlCnt++;
            	}
            i++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // SQL result set is now in HSet------------------------------------------------------------------------
        
        try { //Read MongoQL output file into Hash Set
            BufferedReader in = new BufferedReader(new FileReader(mqlFl));
            while ((str2 = in.readLine()) != null) {
            	mqlHS.add(str2);
            	mqlCnt++;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } // MQL result set is now in HSet------------------------------------------------------------------------
        
        // Finding INTERSECT between SQL & MQL
        sqlHScopy.addAll(sqlHS);
        mqlHScopy.addAll(mqlHS);     
        sqlHScopy.retainAll(mqlHScopy); // INTERSECT between SQL & MQL
        
        
        for (String sqlLine : sqlHS) { //take each line of SQL & check for match in MQL HSet
            if (!mqlHS.contains(sqlLine)) { //if MISmatch found
                extraInSQL.add(sqlLine);
                sqlExtraCnt++;
            }
        }
        for (String mqlLine : mqlHS) { //take each line of MQL & check for match in sQL HSet
            if (!sqlHS.contains(mqlLine)) { //if MISmatch found
            	extraInMQL.add(mqlLine);
            	mqlExtraCnt++;
            }
        }
        
        for (String line : sqlHScopy) {
            intersectCnt++;
        }
        
        // Logging ============================================================================================
        // Build Log Set ======================================================================================
        //log.add("Detailed Comparison Report on SQL & MongoQL Results");
        //log.add(colHdrs);
        //log.add("\nExtra in SQL Output:");
        
        
        // Printing Summary Log  
        System.out.println("Comparison SUMMARY Report on SQL & MongoQL Results");
        System.out.println("==================================================");
        System.out.println("Record Count of SQL : " + sqlCnt);
        System.out.println("Record Count of MQL : " + mqlCnt);
        System.out.println("Record Count in SQL resultset which are absent/differnt in MQL : " + sqlExtraCnt);
        System.out.println("Record Count in MQL resultset which are absent/differnt in SQL : " + mqlExtraCnt);
        System.out.println("Record Count of common Data in SQL & MQL : " + intersectCnt + "\n\n");
        
        // Printing Log  
        System.out.println("Comparison DETAIL Report on SQL & MongoQL Results");
        System.out.println("===================================================");
        System.out.println(colHdrs);
        System.out.print("\nExtra in SQL Output:"); //Dont put Newline, so no println
        
        //System.out.print("Extra in SQL Hash Set Size: " + extraInSQL.size());  //logging to debug
        
        if (extraInSQL.isEmpty() || extraInSQL == null || extraInSQL.size() < 1) {
        	System.out.println(" :: 0");
        }
        else {
        	System.out.println("");
	        for (String line : extraInSQL) {
	        	//System.out.println(line.length());
	            System.out.println(line);
	            //log.add(line); // temporarily disabled as method is not returning Set
	        }
        }
        
        //log.add("\nExtra in MQL Output:");
        System.out.print("\nExtra in MQL Output:"); //Dont put Newline, so no println
        
        if (extraInMQL.isEmpty() || extraInMQL == null || extraInMQL.size() <= 1) {
        	System.out.println(" :: 0");
        }
        else {
        	System.out.println("");
	        for (String line : extraInMQL) {
	            System.out.println(line);
	            //log.add(line); // temporarily disabled as method is not returning Set
	        }
        }
	        
        //log.add("\nCommon between SQL & MQL Output:");
        System.out.print("\nCommon between SQL & MQL Output:");
        
        if (sqlHScopy.isEmpty() || sqlHScopy == null || sqlHScopy.size() <= 1) {
        	System.out.println(" :: 0");
        }
        else {
        	System.out.println("");
	        for (String line : sqlHScopy) {
	            System.out.println(line);
	            //log.add(line);
	        }
        }
		
		//return log;
	}

	static void xls2CsvConv(File inputFile, File outputFile, String sheetNm, String delim) {
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        try 
        {
	        FileInputStream fio = new FileInputStream(inputFile);
        	FileOutputStream fos = new FileOutputStream(outputFile);
	        // Get the workbook object for XLS file
	        XSSFWorkbook workbook = new XSSFWorkbook(fio);
	        // Get first sheet from the workbook
	        XSSFSheet sheet = workbook.getSheet(sheetNm);
	        Row row;
	        Cell cell;

        // Iterate through each rows from first sheet
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) 
        {
                row = rowIterator.next();
                // For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) 
                {
                        cell = cellIterator.next();
                        
                        switch (cell.getCellType()) 
                        {
                        case Cell.CELL_TYPE_BOOLEAN:
                                data.append(cell.getBooleanCellValue() + delim);
                                break;
                                
                        case Cell.CELL_TYPE_NUMERIC:
                        	if (row.getCell(0).getCellType() == Cell.CELL_TYPE_NUMERIC){
                                data.append(cell.getNumericCellValue() + delim);}
                        	 if (DateUtil.isCellDateFormatted(row.getCell(0))) {
                                 System.out.println ("Row No.: " + row.getRowNum ()+ " " + 
                                     row.getCell(0).getDateCellValue());
                                 data.append(row.getCell(0).getDateCellValue() + delim);
                             }
                                break;
                                
                        case Cell.CELL_TYPE_STRING:
                                data.append(cell.getStringCellValue() + delim);
                                break;

                        case Cell.CELL_TYPE_BLANK:
                                data.append("" + delim);
                                break;
                        
                        default:
                                data.append(cell + delim);
                        }
                   }data.append("\r\n");
        }
        fos.write(data.toString().getBytes());
        fos.close();
        }
        catch (FileNotFoundException e) 
        {
                e.printStackTrace();
        }
        catch (IOException e) 
        {
                e.printStackTrace();
        }
        }
	
	

	public static void main(String[] args) {
		
	    if (args[0].equals("sqlVsMql")) {
	    	String sqlFile = args[1];
			String mqlFile = args[2];
			sqlVsMql(sqlFile, mqlFile);
	    }
	    else if (args[0].equals("sqlVsSql")) {
	    	String sqlFile1 = args[1];
			String sqlFile2 = args[2];
			sqlVsSql(sqlFile1, sqlFile2);
	    }
	    else if(args[0].equals("xls2CsvConv")) {  //xls2CsvConv C:\Users\tatai\Dropbox\Career\Skills\Jmeter_Frameworks\bin\MARS_Test_Data_Input.xlsx C:\Users\tatai\Dropbox\Career\Skills\Jmeter_Frameworks\bin\Test_Data_Input.csv SqlVsMql ;
	    	String xls= null;
	    	String csv= null;
	    	String sheetNm = null;
	    	String delim = null;
	        try {	
	        	xls=args[1];
	        	csv=args[2];
	        	sheetNm=args[3];
	        	delim=args[4];
	        	
	        	System.out.println(args[0]);
	        	System.out.println(xls);
	        	System.out.println(csv);
	        	System.out.println(sheetNm);
	        	System.out.println(delim);
	        }
	        catch (ArrayIndexOutOfBoundsException e) {
	        	System.out.println ("ERR :: Usage > JAR METHOD_NAME $XLS $CSV $SHEET_NAME $DELIMITER");
	        	System.exit(1);
	        }
	        
	            File inputFile = new File(xls);
	            File outputFile = new File(csv);
	            xls2CsvConv(inputFile, outputFile, sheetNm, delim);
	            System.out.println("Excel sheet converted to CSV");
	    }
	    else {
	        //Do other main stuff, or print error message
	    }
		
	
		
		Set<String> report = new LinkedHashSet<String>();
		String db = "661M1234, SUMAN, CHAKRABORTY, 333224444, HOME, 123 RESIDENTIAL DR, MASON, ";
		String json = "{     \"PARTY_ID\": \"661M90319\",     \"FIRSTNAME\": \"SUMAN\",     \"LASTNAME\": \"CHAK\",     \"SSN\": \"333224444\",     \"Addresses\": [         {             \"ADDRESSTYPE\": \"HOME\",             \"ADDRESSLINE1\": \"3333 Steeplechcase Dr\", 						\"CITY\": \"Mason\"         },         {             \"ADDRESSTYPE\": \"WORK\",             \"ADDRESSLINE1\": \"123 RESIDENTIAL DR\", 						\"CITY\": \"Cincinnati\"         }     ] }";

		//dbVsJson(db, json);


	}

}
