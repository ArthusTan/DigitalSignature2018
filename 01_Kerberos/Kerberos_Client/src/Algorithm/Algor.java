package Algorithm;

public class Algor {
    public static byte[] encrypt(String content,String key){
        byte[] contentBytes = content.getBytes();
        byte[] keyBytes = key.getBytes();
        byte dkey = 0;
        for(byte b : keyBytes)
            dkey ^= b;
        byte salt = 0;  //随机盐值
        byte[] result = new byte[contentBytes.length];
        for(int i = 0 ; i < contentBytes.length; i++){
            salt = (byte)(contentBytes[i] ^ dkey ^ salt);
            result[i] = salt;
        }
        String tmp = new String(result);
        tmp=tmp.replaceAll("\n", "ISAN");
        tmp=tmp.replaceAll("\r", "ISAR");
        tmp=tmp.replaceAll("\t", "ISAT");
        //return result;
        return tmp.getBytes();
    }
 
    public static byte[] decipher(String content,String key){

    	content=content.replaceAll("ISAN", "\n");
    	content=content.replaceAll("ISAR", "\r");
    	content=content.replaceAll("ISAT", "\t");
        
        byte[] contentBytes = content.getBytes();
        byte[] keyBytes = key.getBytes();
        byte dkey = 0;
        for(byte b : keyBytes)
            dkey ^= b;
        byte salt = 0;  //随机盐值
        byte[] result = new byte[contentBytes.length];
        for(int i = contentBytes.length - 1 ; i >= 0 ; i--){
            if(i == 0)
            	salt = 0;
            else
                salt = contentBytes[i - 1];
            result[i] = (byte)(contentBytes[i] ^ dkey ^ salt);
        }
        return result;
    }
}
