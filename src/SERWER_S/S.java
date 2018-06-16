package SERWER_S;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class S {
	
	private static int steps =0;
	private KeyGenerator keygenerator;
	
	public static void main(String[] args) throws Exception{
		S serwer = new S();
		serwer.recive();
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
		
		if(steps==0){
			steps++;
			
			/*
			 * Przygotowujemy przesłąny SecretKey myDesABkey = tekst przez A do zaszyfrowania
			 */
			StringTokenizer strTokSA= new StringTokenizer(msg, ",");
			String encryptMessage ="{";
			
			SecretKey myDesABkey = generateKeyAB();
			
			
			
			
			
			
			Socket sock = new Socket("localhost",1026);
			PrintStream ps = new PrintStream(sock.getOutputStream());
			ps.println(msg);
			InputStreamReader IR = new InputStreamReader(sock.getInputStream());
			BufferedReader BR = new BufferedReader(IR);
			String message = BR.readLine();
			System.out.println(message);
			
		}
	}
	
	
	private SecretKey generateKeyAB()throws Exception{
		KeyGenerator keyAB= KeyGenerator.getInstance("DES");
		SecretKey myDesABkey = keyAB.generateKey();
		
		String encodedKey = Base64.getEncoder().encodeToString(myDesABkey.getEncoded());
		System.out.println(encodedKey);
		return myDesABkey;
	}
	
	private void encryptDes()throws Exception{
		keygenerator = KeyGenerator.getInstance("DES");
        SecretKey myDesKey = keygenerator.generateKey();

        Cipher desCipher;
        desCipher = Cipher.getInstance("DES");


        byte[] text = "No body can see me.".getBytes("UTF8");


        desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
        byte[] textEncrypted = desCipher.doFinal(text);

        String s = new String(textEncrypted);
        System.out.println(s);

        desCipher.init(Cipher.DECRYPT_MODE, myDesKey);
        byte[] textDecrypted = desCipher.doFinal(textEncrypted);

        s = new String(textDecrypted);
        System.out.println(s);
	}

}
