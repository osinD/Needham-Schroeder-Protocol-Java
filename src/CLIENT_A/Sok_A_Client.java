package CLIENT_A;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import SERWER_S.S;

public class Sok_A_Client {
	
	public final String stronaA ="A" ,stronaB="B";
	public static int Na =1234567;
	public static int steps =0;
	public static String  ABSecretKey= "";
	public Scanner scn;
	

	
	public static void main(String[] args) throws Exception
	{
		Sok_A_Client client = new Sok_A_Client();
		client.run();
	}
	
	public SecretKey ASSecretKey()throws Exception{
		String desKey = "0123456789abcdef"; // value from user  
		byte[] keyBytes = DatatypeConverter.parseHexBinary(desKey);

		SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
		SecretKey key = factory.generateSecret(new DESKeySpec(keyBytes));
		
		return key;
	}
	
	
	private void run() throws Exception {
		
		Random rnd = new Random();
		Na= rnd.nextInt(10000);
		Sok_A_Client client = new Sok_A_Client();
		//if(steps ==0){
		String msg =stronaA+","+stronaB+","+Na;
		Socket sock = new Socket("localhost",1025);
		PrintStream  ps = new PrintStream(sock.getOutputStream());
		ps.println(msg);
		InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		BufferedReader BR = new BufferedReader(IR);
		String message = BR.readLine();
		System.out.println(message);
		client.receving();
		//}
	}
	
	
	private void receving()throws Exception{
		
		if(steps==0){
		
		/*
		 * Tutaj pobieramy ciąg bajtowy zaszyfrowany w postaci dwuwymiarowejTablicy 
		 * array[0] -> zaszyfrowane Na,Kab,B
		 * array[1] -> {{Kab,A}Kbs}Kas
		 */
		 ServerSocket ss = new ServerSocket(1027);
		    Socket s = ss.accept();
		    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		    byte[][] array = (byte[][])is.readObject();
		    array[0]= decryptAS(array[0]);
		    array[1]= decryptAS(array[1]);
		    /*
		     * Rozdzielamy tutaj zaszyfrowane wiadomości i przeszukujemy w celu odebrania i zapisania do zmiennych statycznych 
		     */
		    String message= new String(array[0]);
		    StringTokenizer strTok = new StringTokenizer(message, ",");
		    String Na=strTok.nextToken();
		    ABSecretKey= strTok.nextToken();
		    System.out.println("Klucz AB ->"+ASSecretKey());
		    
		    
		    
		    
		    
		    ss.close();
		    steps++;
		    firstSendAB(array[1]);
		}else if(steps==1){
			/*
			 * W tym miejscu pobieramy wiadomości od B przerabiamy i wysyłamy{Nb-1}Kab
			 *
			 */
			steps++;
			ServerSocket ss = new ServerSocket(1027);
		    Socket s = ss.accept();
		    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		    byte[] array1 = (byte[])is.readObject();
		    ss.close();
		    System.out.println("zaszyfrowana wiadomość od B {Nb}Kab -> "+new String(array1));
		    array1= decryptAB(array1);
		    /*
		     * Pobieramy wiadomość od B deszyfrujemy a pod spodem
		     * Przerabiamy na Int następnie na Inta odejmujemy 1 przerabiamy znowy na string i wysyłamy
		     */
		    String BA= new String(array1);
		    int Nbminus1= Integer.parseInt(BA)-1;
		    System.out.println("Wiadomość do przesłsania do A->b ->"+Nbminus1);
		    String messageAbToSend = Integer.toString(Nbminus1);
		   
		    byte[] decodedKey = Base64.getDecoder().decode(ABSecretKey);
			SecretKey ABSecretKey1 = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
		    
		    Cipher desCipher;
	        desCipher = Cipher.getInstance("DES");
		    desCipher.init(Cipher.ENCRYPT_MODE, ABSecretKey1);
		    byte[] textEncrypted1 = desCipher.doFinal(messageAbToSend.getBytes("UTF8"));
		    System.out.println("Wiadomość A->B {NB-1} zaszyfrowana ->"+new String (textEncrypted1));
		    firstSendAB(textEncrypted1);
		}else{
			ServerSocket ss = new ServerSocket(1027);
		    Socket s = ss.accept();
		    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		    byte[] array1 = (byte[])is.readObject();
		    ss.close();
		    System.out.println("ROZPOCZYNAMY BEZPIECZNĄ KOMUNIKACJĘ");
		    System.out.println("zaszyfrowana wiadomość komunikacji  od B  -> "+new String(array1));
		    array1 = decryptAB(array1);
		    System.out.println("Podaj wiadomość ");
		    scn = new Scanner(System.in);
		    String legalMessage = scn.nextLine();
		    firstSendAB(encryptAB(legalMessage));
		    
		}
		
		
		
	}
	/*
	 * Metoda odszyfrowująca pierwszą komunikację S -> A
	 */
	
	private byte[] decryptAS(byte[] msg)throws Exception{
		
		System.out.println("Zaszyfrowana wiadomość otrzymana od strony S - >"+ new String(msg));
		
		
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, ASSecretKey());
        byte[] textDecrypted = desCipher.doFinal(msg);
        String s = new String(textDecrypted);
        System.out.println("Odszyfrowana wiadomość S ->A -> "+s);

        return textDecrypted;
        
	}
	
	/*
	 * Metoda rozszyrowująca pierwszą komunikacją B - > A
	 */
	private byte[] decryptAB(byte[] msg)throws Exception{
		
		
		byte[] decodedKey = Base64.getDecoder().decode(ABSecretKey);
		SecretKey ABSecretKey1 = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, ABSecretKey1);
        byte[] textDecrypted = desCipher.doFinal(msg);
        String s = new String(textDecrypted);
        System.out.println("Wiadomość B->A po zdeszyfrowaniu - > "+s);

        return textDecrypted;
        
	}
	private byte[] encryptAB(String msg)throws Exception {
		
		byte[] decodedKey = Base64.getDecoder().decode(ABSecretKey);
		SecretKey ABSecretKey1 = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
	    
	    Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
	    desCipher.init(Cipher.ENCRYPT_MODE, ABSecretKey1);
	    byte[] textEncrypted1 = desCipher.doFinal(msg.getBytes("UTF8"));
	    System.out.println("Wiadomość A->B {NB-1} zaszyfrowana ->"+new String (textEncrypted1));
	    return textEncrypted1;
	    
	    
	}
	
	public void firstSendAB(byte[] array)throws Exception{
		
		Socket s = new Socket("localhost", 1029);
        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
        os.writeObject(array);
        s.close();
        receving();
	}
	
	
	
}
