package CLIENT_B;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Sok_B_Client {
	
	private static int steps =0;
	private static int Nb=0;
	private static SecretKey ABSecretKey;
	private Scanner scn;
	
	
	private int generateNb(){
		Random rnd = new Random();
		Nb = rnd.nextInt(10000);
		return Nb;
	}
	
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
		
		if(steps==0){
		ServerSocket ss = new ServerSocket(1029);
	    Socket s = ss.accept();
	    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
	    byte[] array = (byte[])is.readObject();
	    System.out.println("Przesłana wiadomość zaszyfrowana wiadomość od A -> "+new String(array));
	    array = decryptAB(array);
	    ss.close();
	    run(array);
		}else if(steps==1){
			/*
			 * Tutaj mamy odebrać wiadomość  A->B {Nb-1}Kab
			 */
			steps++;
			ServerSocket ss = new ServerSocket(1029);
		    Socket s = ss.accept();
		    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		    byte[] array2 = (byte[])is.readObject();
		    ss.close();
			String msg = new String(array2);
			System.out.println("Otrzymana zaszyfrowana wiadomość od A czli Nb-1-> "+msg);
			array2=decryptAB2(array2);
			
			
			System.out.println("MOŻEMY ROZPOCZĄĆ BEZPIECZNĄ KOMUNIKACJĘ");
			scn = new Scanner(System.in);
			System.out.println("Co chcesz wysać ?");
			String legalMessage =scn.nextLine();
			encryptBAmessage(legalMessage);
			
		}else{
			ServerSocket ss = new ServerSocket(1029);
		    Socket s = ss.accept();
		    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		    byte[] array2 = (byte[])is.readObject();
		    ss.close();
		    
		    String msg = new String(array2);
		    System.out.println("Otrzymana zaszyfrowana wiadomość od A -> "+msg);
			array2=decryptAB2(array2);
			scn = new Scanner(System.in);
			System.out.println("Co chcesz wysać ?");
			String legalMessage =scn.nextLine();
			encryptBAmessage(legalMessage);
		}
	       
	}
	
	
	public void run(byte[] array)throws Exception{
		if(steps ==0){
			steps++;
			String messageAB = new String(array);
			StringTokenizer strTok = new StringTokenizer(messageAB, ",");
			String KeyAB=strTok.nextToken();
			String A= strTok.nextToken();
			
			/*
			 * Tutaj szyfrujemy wiadomość {Na}Kab
			 */
			byte[] decodedKey = Base64.getDecoder().decode(KeyAB);
			//Tutaj się bawiłem utworzyłęm zmienną statyczną zamiast zmienną lokalną 
			ABSecretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
			int rndNb= generateNb();
			String rndNbmessage=Integer.toString(rndNb);
			System.out.println("Wiadomość , którą będziemy szyfrować dla A Nb ->"+rndNbmessage);
			Cipher desCipher;
	        desCipher = Cipher.getInstance("DES");
	        desCipher.init(Cipher.ENCRYPT_MODE, ABSecretKey);
	        byte[] textDecrypted = desCipher.doFinal(rndNbmessage.getBytes("UTF8"));
	        String s = new String(textDecrypted);
	        System.out.println("Zaszyfrowana wiadomość którą prześlemy do A {Nb}Kab ->"+s);
			firstSendBA(textDecrypted);
	        
			
		}
		
	}
	
	
	private byte[] decryptAB(byte[] msg)throws Exception{
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, BSSecretKey());
        byte[] textDecrypted = desCipher.doFinal(msg);
        
        
        String s = new String(textDecrypted);
        System.out.println("Odszyfrowana wiadomość otrzymana od A {Kab,A}Kbs ->"+s);
        return textDecrypted;
	}
	
	private byte[] decryptAB2(byte[] msg)throws Exception{
		
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, ABSecretKey);
        byte[] textDecrypted = desCipher.doFinal(msg);
        
        
        String s = new String(textDecrypted);
        System.out.println("Odszyfrowana wiadomość otrzymana od A  ->"+s);
        return textDecrypted;
	}
	
	public void encryptBAmessage(String msg)throws Exception{
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.ENCRYPT_MODE, ABSecretKey);
        byte[] textDecrypted = desCipher.doFinal(msg.getBytes("UTF8"));
        String s = new String(textDecrypted);
        System.out.println("Zaszyfrowana wiadomość  którą prześlemy do A -> "+s);
		firstSendBA(textDecrypted);
	}
	
	public void firstSendBA(byte[] array)throws Exception{
		
		Socket s = new Socket("localhost", 1027);
        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
        os.writeObject(array);
        s.close();
        reciving();
	}

}
