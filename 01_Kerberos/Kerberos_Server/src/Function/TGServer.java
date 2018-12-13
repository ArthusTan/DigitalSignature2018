package Function;

import java.util.Date;
import java.util.UUID;

public class TGServer {
	private static UUID sessionKey(){
		return UUID.randomUUID();
	}
	private static long timestamp(){
		return new Date().getTime();
	}
	public static String confirm(String webName, String TGT){
		String ticket = "[NOTALLOW]";
		if(TGT.startsWith("[AUTH]")){
			String userName = TGT.split(",")[1];
			int pLevel = AServer.getProtectLevel(webName);
			int vLevel = AServer.getVisitLevel(userName);
			if(pLevel != -1 && vLevel != -1 && pLevel <= vLevel){
				ticket = "[ALLOW]," + userName + "," + webName + "," + sessionKey() + "," + timestamp();
				System.out.println("TICKET FROM TGS : " + ticket);
			}
		}
		return ticket;
	}
}
