package SERWER_S;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class S {
	
	private static int steps =0;
	private KeyGenerator keygenerator;
	public static SecretKey keyAS;
	public static String Kas="";
	public  static String Kbs;
	public static void main(String[] args) throws Exception{
		S serwer = new S();
		serwer.recive();
	}
	
	public SecretKey ASSecretKey()throws Exception{
		String desKey = "0123456789abcdef"; // value from user  
		byte[] keyBytes = DatatypeConverter.parseHexBinary(desKey);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
		SecretKey key = factory.generateSecret(new DESKeySpec(keyBytes));
		
		return key;
	}
	
	
	private void recive()throws Exception{
		ServerSocket srvSocket1 = new ServerSocket(1025);
		Socket sock1 = srvSocket1.accept();
		InputStreamReader IR1 = new InputStreamReader(sock1.getInputStream());
		BufferedReader BR1 = new BufferedReader(IR1);
		String message1 = BR1.readLine();
		PrintStream ps = new PrintStream(sock1.getOutputStream());
		ps.println("Message recived");
		System.out.println(message1);
		
		srvSocket1.close();
		String msgA =message1;
		
		run(msgA);
	}
	
	private void run(String msg)throws Exception{
		
		
		//if(steps==0){
			steps++;
			
			/*
			 * Przygotowujemy przesłany SecretKey myDesABkey = tekst przez A do zaszyfrowania
			 */
			StringTokenizer strTokSA= new StringTokenizer(msg, ",");
			String A =strTokSA.nextToken();
			String B = strTokSA.nextToken();
			String Na= strTokSA.nextToken();
			String myDesABkey = generateKeyAB();
			//Wywołujemy funkcje szyfrującą {Kab,A} kluczem Bs
			String messageKbs = encryptDesBS(myDesABkey+","+A);
			String msgSA =Na+","+myDesABkey+","+B+","+messageKbs;
			System.out.println(msgSA);
			encryptDes(msgSA);
		
			
	//	}
	}
	
	/*
	 * Metoda zwracająca klucz AB w postaci stringa
	 */
	private String generateKeyAB()throws Exception{
		
		KeyGenerator keyAB= KeyGenerator.getInstance("DES");
		SecretKey myDesABkey = keyAB.generateKey();
		String encodedKey = Base64.getEncoder().encodeToString(myDesABkey.getEncoded());
		return encodedKey;
	}
	
	
	
	
	
	
	
	/*
	 * Metoda kodująca całą wiadomość przestłanie z A->S
	 */
	private void encryptDes(String message)throws Exception{
		
		
		
		
		
		//tak było
		//keygenerator = KeyGenerator.getInstance("DES");
        SecretKey myDesKey = ASSecretKey();//keygenerator.generateKey(); //tak było
        Kas = Base64.getEncoder().encodeToString(myDesKey.getEncoded());
        
        
        Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        byte[] text = message.getBytes("UTF8");
        desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
        byte[] textEncrypted;  textEncrypted = desCipher.doFinal(text);
        String s = new String(textEncrypted);
        System.out.println(s);
        
        /*
         * Przesyłamy tędy tablice bajtów
         */
        Socket socket = new Socket("localhost",1027);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        dOut.writeInt(textEncrypted.length); // write length of the message
        dOut.write(textEncrypted);
      //  socket.close();
        
    
        
        
        /*
        desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
        byte[] textDecrypted = desCipher.doFinal(textEncrypted);

        s = new String(textDecrypted);
        System.out.println(s);
        */
	}
	
	
	
	
	
	
	private String encryptDesBS(String message)throws Exception{
		keygenerator = KeyGenerator.getInstance("DES");
        SecretKey myDesKey = keygenerator.generateKey();
        Kbs = Base64.getEncoder().encodeToString(myDesKey.getEncoded());
        Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        byte[] text = message.getBytes("UTF8");
        desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
        byte[] textEncrypted = desCipher.doFinal(text);
        String s = new String(textEncrypted);
        
        return s;
	}

}
