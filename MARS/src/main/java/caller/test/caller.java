package caller.test;

import java.util.LinkedHashSet;
import java.util.Set;

import cts.mars.compUtils;


public class caller {


	public static void main(String[] args) {
		compUtils cu = new compUtils();
		Set<String> report = new LinkedHashSet<String>();
		
		cu.sqlVsMql("C:\\Users\\tatai\\Dropbox\\Career\\Skills\\MongoDB\\MARS_Mongo\\logs\\sql.log", "C:\\Users\\tatai\\Dropbox\\Career\\Skills\\MongoDB\\MARS_Mongo\\logs\\mql.log");
		
		

	}

}
