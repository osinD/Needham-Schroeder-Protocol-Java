package CLIENT_B;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Sok_B_Client {
	
	public static void main(String[] args) throws Exception
	{
		Sok_B_Client client = new Sok_B_Client();
		client.reciving();
		
	}
	
	
	public void reciving()throws Exception{
		
		
		ServerSocket srvSocket = new ServerSocket(1029);
		Socket sock = srvSocket.accept();
		
		InputStreamReader IR = new InputStreamReader(sock.getInputStream());
		BufferedReader BR = new BufferedReader(IR);
		String message = BR.readLine();  //pobranie wiadomosci z klient 1
		
		System.out.println(message);
		byte[] text = message.getBytes("UTF8");
		for(byte b : text){
        	System.out.print(b+" ");
        }
        System.out.println("");
	}

}
