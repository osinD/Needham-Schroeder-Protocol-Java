package SERWER_S;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
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
	
	/*
	 * Metoda ustalająca klucz prywatny AS
	 * na razie ustawiam na sztywno potem  dodam argument String do czytania z pliku
	 */
	public SecretKey ASSecretKey()throws Exception{
		String desKey = "0123456789abcdef"; // value from user  
		byte[] keyBytes = DatatypeConverter.parseHexBinary(desKey);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
		SecretKey key = factory.generateSecret(new DESKeySpec(keyBytes));
		
		return key;
	}
	
	/*
	 * Metoda ustalająca klucz prywatny BS
	 */
	public SecretKey BSSecretKey()throws Exception{
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
		
			
			/*
			 * Przygotowujemy przesłany SecretKey myDesABkey = tekst przez A do zaszyfrowania
			 */
			StringTokenizer strTokSA= new StringTokenizer(msg, ",");
			String A =strTokSA.nextToken();
			String B = strTokSA.nextToken();
			String Na= strTokSA.nextToken();
			String myDesABkey = generateKeyAB();
			/*
			 * Wywołujemy funkcje szyfrującą {Kab,A} kluczem Bs
			 */
			byte[] messageKbs = encryptDesBS(myDesABkey+","+A);
			String msgSA =Na+","+myDesABkey+","+B+",";
			System.out.println(msgSA);
			encryptDes(msgSA,messageKbs);
		
			
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
	private void encryptDes(String message , byte[] array)throws Exception{
		
		
		
		
		
		//tak było
		//keygenerator = KeyGenerator.getInstance("DES");
        SecretKey myDesKey = ASSecretKey();//keygenerator.generateKey(); //tak było
        Kas = Base64.getEncoder().encodeToString(myDesKey.getEncoded());
        
        System.out.println(message);
        Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        byte[][] text = {message.getBytes("UTF8"),array};
       // text[0] = message.getBytes("UTF8");
       // text[1] = array;
        desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
        byte[] textEncrypted1;  
        textEncrypted1 = desCipher.doFinal(message.getBytes("UTF8"));
        byte[] textEncrypted2;  
        textEncrypted2 = desCipher.doFinal(array);
        
        byte[][] encryptedText = {textEncrypted1,textEncrypted2} ;
       // encryptedText[0] =textEncrypted1;
       // encryptedText[1] = textEncrypted2;
        String s1 = new String(textEncrypted1);
        String s2 = new String(textEncrypted2);
        System.out.println(s1 + s2);
        
        /*
         * Przesyłamy tędy tablice bajtów
         */
        /*
        Socket socket = new Socket("localhost",1027);
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
        dOut.writeInt(textEncrypted.length); // write length of the message
        dOut.write(textEncrypted);
        
        /*
         * Próbujemy przeslac dwuwymiarową tablicę bajtów
         * 
         */
        Socket s = new Socket("localhost", 1027);
        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
        os.writeObject(encryptedText);
        
      
	}
	
	
	
	
	
	
	private byte[] encryptDesBS(String message)throws Exception{
		
        SecretKey myDesKey = BSSecretKey();
        Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        byte[] text = message.getBytes("UTF8");
        desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
        byte[] textEncrypted = desCipher.doFinal(text);
        for(byte b : textEncrypted){
        	System.out.print(b+" ");
        }
        System.out.println("");
        String s = new String(textEncrypted);
        System.out.println(s);
        return textEncrypted;
	}

}
