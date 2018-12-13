package Function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class AServer{
	private static String filePath = System.getProperty("user.dir") + "/data.txt";
	private static String getRecord(String userName){
		try{
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			@SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(
		    		new InputStreamReader(fileInputStream));
		    String line = null;
		    while ((line = bufferedReader.readLine()) != null) {
		        String[] array = line.split(",");
		        if(userName.equals(array[0])) return line;
		    }
			bufferedReader.close();
		}catch(Exception e) { e.printStackTrace(); }
		return null;
	}
	public static boolean confirm(String message){ // Login
		String[] apply = message.split(",");
		String userName = apply[1];
		String password = apply[2];
		String record = getRecord(userName);
		if(record != null && password.equals(record.split(",")[3]))
			return true;
		return false;
	}
	public static String visitKey(String webName){
		String record = getRecord(webName);
		return record.split(",")[3];
	}
	public static int getProtectLevel(String webName){
		String record = getRecord(webName);
		if(record == null)return -1;
		return Integer.parseInt(record.split(",")[2]);
	}
	public static int getVisitLevel(String userName){
		String record = getRecord(userName);
		if(record == null)return -1;
		return Integer.parseInt(record.split(",")[1]);
	}
}
