package CLIENT_A;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Sok_A_Client {
	
	public final String stronaA ="A" ,stronaB="B";
	public static int Na =1234567;
	public static int steps =0;

	
	public static void main(String[] args) throws Exception
	{
		Sok_A_Client client = new Sok_A_Client();
		client.run();
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
		
		if(steps ==0){
		steps ++;
		ServerSocket srvSocket = new ServerSocket(1026);
		Socket sock = srvSocket.accept();
		InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		BufferedReader BR = new BufferedReader(IR);
		String message = BR.readLine();  //pobranie wiadomosci z klient 1
		System.out.println(message);
		
		
		
		Socket sockB = new Socket("localhost",1027);
		PrintStream ps = new PrintStream(sockB.getOutputStream());
		ps.println(message);
		InputStreamReader IRB = new InputStreamReader(sockB.getInputStream());
		BufferedReader BRB = new BufferedReader(IRB);
		String messageB = BRB.readLine();
		}
		
	}
}
