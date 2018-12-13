package Function;

public class TGServer {
	public static String getTicket(String TGTicket){
		String ticket = "[NOTALLOW],[SECERT],[SECERT]";
		if(AServer.visit(TGTicket))
			ticket = TGTicket;
		return ticket;
	}
}
