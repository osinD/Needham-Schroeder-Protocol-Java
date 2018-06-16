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
		if(steps ==0){
		String msg =stronaA+","+stronaB+","+Na;
		Socket sock = new Socket("localhost",1025);
		PrintStream  ps = new PrintStream(sock.getOutputStream());
		ps.println(msg);
		InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		BufferedReader BR = new BufferedReader(IR);
		String message = BR.readLine();
		System.out.println(message);
		client.receving();
		}
	}
	
	
	private void receving()throws Exception{
		
		
		/*
		 * Pobieramy ciąg bajtowy zaszyfrowany od S -> {Na,Kab,B,(Kab,A}Kbs}Kas
		 
		ServerSocket srvSocket1 = new ServerSocket(1027);
		Socket socket = srvSocket1.accept();
		
		DataInputStream dIn = new DataInputStream(socket.getInputStream());

		int length = dIn.readInt();                    // read length of incoming message
		if(length>0) {
		    byte[] message = new byte[length];
		   dIn.readFully(message, 0, message.length);
		   // read the message'
		   String s = new String(message);
		   System.out.println(s);
		   System.out.println(decryptAS(message));
		}
		srvSocket1.close();
		*/
		
		/*
		 * Tutaj pobieramy ciąg bajtowy zaszyfrowany w postaci dwuwymiarowejTablicy 
		 * array[0] -> zaszyfrowane Na,Kab,B
		 * array[1] ->
		 */
		 ServerSocket ss = new ServerSocket(1027);
		    Socket s = ss.accept();
		    ObjectInputStream is = new ObjectInputStream(s.getInputStream());
		    byte[][] array = (byte[][])is.readObject();
		    array[0]= decryptAS(array[0]);
		    array[1]= decryptAS(array[1]);
		    
		//System.out.println(decryptAS(array[0]));
		//System.out.println(decryptAS(array[1]));
		    firstSendAB(array[1]);
		
		
		
	}
	
	private byte[] decryptAS(byte[] msg)throws Exception{
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        desCipher.init(Cipher.DECRYPT_MODE, ASSecretKey());
        byte[] textDecrypted = desCipher.doFinal(msg);
        
        
        String s = new String(textDecrypted);
        System.out.println(s);
        /*
        StringTokenizer strTok = new StringTokenizer(s, ",");
        String A = strTok.nextToken();
        String Keyab = strTok.nextToken();
        String B = strTok.nextToken();
        String Kbs = strTok.nextToken();
        
        Socket sock = new Socket("localhost",1029);
		PrintStream  ps = new PrintStream(sock.getOutputStream());
		ps.println(Kbs);
		InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		BufferedReader BR = new BufferedReader(IR);
		String message = BR.readLine();
		System.out.println(message);
		*/
        return textDecrypted;
        
	}
	
	public void firstSendAB(byte[] array)throws Exception{
		
		Socket s = new Socket("localhost", 1029);
        ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
        os.writeObject(array);
	}
	
	/*
	private String decryptmessage(String msg)throws Exception{
		
		StringTokenizer strTok = new StringTokenizer(msg, ",");
		String msg1 =strTok.nextToken();
		String key = strTok.nextToken();
		System.out.println(key);
		S sElement = new S();
		byte[] decodedKey = Base64.getDecoder().decode(key);
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES");
		
		Cipher desCipher;
        desCipher = Cipher.getInstance("DES");
        byte[] text = msg1.getBytes("UTF8");
		desCipher.init(Cipher.DECRYPT_MODE, originalKey);
        byte[] textDecrypted = desCipher.doFinal(text);

        return "xxx";
		//String s =  desCipher.doFinal(Base64.getDecoder().decode(text), "UTF-8");;
      //  System.out.println(s);
       // return s;
	}*/
}
