package main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.*;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class Communication {
 
	UI ui;
	WebSocketClient client;
	String ip = "ws://85.214.147.14";
	int port = 5555;
	
	public Communication () throws URISyntaxException, InterruptedException {
		
	System.out.println("Communication has been created");
	
	client = new WebSocketClient(new URI(ip + ":" + port), new Draft_6455()) {
	public void onOpen(ServerHandshake serverhandshake) {
		
	}
	
	public void onMessage(String message) {
		System.out.println(message);
	try {
		Map<String, String> map = stringToMap(message);
		
	 
		  switch(map.get("type")) {
		  	case "message" : 
		  	String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date(Long.parseLong(map.get("time") ) ) );
		  	ui.displayMessage(map.get("content"), map.get("name"), timeStamp);	
		  		break;
		  }
	
		 
	 
		
		
		
		
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	
	}
	
	public void onClose(int code, String reason, boolean remote) {
		
		
	}
	
	public void onError(Exception ex) {
	ex.printStackTrace();	
	}
	
	};
	}
	
	public void connectToServer(String name) throws InterruptedException {
	
	Map<String, String> map = new HashMap<>();
	map.put("type", "connect");
	map.put("content", name);
	
	client.connectBlocking();
	client.send(mapToString(map));
	
	}
		
	
	
	
	public void setUI(UI ui) {
		
	this.ui = ui;
		
	}
	
	public void sendMessage(String message) {
		Map<String, String> map = new HashMap<>();
		map.put("type", "message");
		map.put("content", message);
		
		if (client != null)	{
			client.send(mapToString(map));
		}
		
	}
	
	private String mapToString(Map o) {
	    try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos = new ObjectOutputStream(baos);
	        oos.writeObject(o);
	        oos.close();
	        return Base64.getEncoder().encodeToString(baos.toByteArray());
	    } catch (IOException e) {
	        e.printStackTrace();
	        System.exit(320);
	        return "";
	    }
	}
	private HashMap<String, String> stringToMap(String s) throws IOException,
	        ClassNotFoundException {
	    s = s.trim();
	    byte[] data = Base64.getDecoder().decode(s);
	    ObjectInputStream ois = new ObjectInputStream(
	            new ByteArrayInputStream(data));
	    Object o = ois.readObject();
	    ois.close();
	    return (HashMap<String, String>) o;
	}
	
	
}
