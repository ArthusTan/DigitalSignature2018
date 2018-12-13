package Function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class AServer{
	private static String filePath = "D:/workspace/MyEclipse/Kerberos_Server/data.txt";
	public static int confirm(String message, int type, int numb){// return the power of user
		String[] apply = message.split(",");
		try{
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		    @SuppressWarnings("resource")
			BufferedReader bufferedReader = new BufferedReader(
		    		new InputStreamReader(fileInputStream));
		    String line = null;
		    while ((line = bufferedReader.readLine()) != null) {
		        String[] array = line.split(",");
		        if(array[0].compareTo(apply[1])==0 && 
		        		(type==2 || array[3].compareTo(apply[2])==0))//UserName & Password
		        	return Integer.parseInt(array[numb]);///
		    }
		    bufferedReader.close();
		}catch(Exception e) { e.printStackTrace(); }
		return -1;
	}
	public static boolean visit(String message){
		String[] array = message.split(",");
		int pow1 = confirm("getPow,"+array[1]+",null", 2, 1);
		int pow2 = confirm("getPow,"+array[2]+",null", 2, 2);
		return pow1 >= pow2;
	}
	public static String visitKey(String message){
		String[] array = message.split(",");
		String userName = array[2];
		String webKey = "";
		try{
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
		    BufferedReader  bufferedReader  = new BufferedReader(
		    		new InputStreamReader(fileInputStream));
		    String line = null;
		    while ((line = bufferedReader.readLine()) != null) {
		        String[] arrayList = line.split(",");
		        System.out.println("[WEB KEY COMPARE] : " + userName + " , " 
		        										  + arrayList[0] + " , " 
		        										  + userName.equals(arrayList[0]));
		        if(userName.equals(arrayList[0])){//if the webServer user
		        	webKey = arrayList[3];
		        	break;
		        }
		    }
		    bufferedReader.close();
		}catch(Exception e) { e.printStackTrace(); }
		return webKey;
	}
}
