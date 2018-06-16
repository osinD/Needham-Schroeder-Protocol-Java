package CLIENT_B;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Sok_B_Client {
	
	public static void main(String[] args) throws Exception
	{
		Sok_B_Client client = new Sok_B_Client();
		client.reciving();
		
	}
	public SecretKey BSSecretKey()throws Exception{
		String desKey = "0123456789abcdef"; // value from user  
		byte[] keyBytes = DatatypeConverter.parseHexBinary(desKey);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
		SecretKey key = factory.generateSecret(new DESKeySpec(keyBytes));
		
		return key;
	}
	
	public void reciving()throws Exception{
		
		
		ServerSocket ss = new ServerSocket(1029);
	    Socket s = ss.accept();
	    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
	    byte[] array = (byte[])is.readObject();
	    array = decryptAB(array);
	    
	}
	
	private byte[] decryptAB(byte[] msg)throws Exception{
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, BSSecretKey());
        byte[] textDecrypted = desCipher.doFinal(msg);
        
        
        String s = new String(textDecrypted);
        System.out.println(s);
        return textDecrypted;
	}

}
