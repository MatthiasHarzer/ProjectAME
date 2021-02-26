package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.*;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.drafts.*;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;


public class Communication {
 
	UI ui;
	WebSocketClient client;
	String ip = "ws://85.214.147.14";
	int port = 5555;
	
	private JSONObject data = new JSONObject();
	private String myid;
	private final String fileName = "data.json";
	private final String filePath = System.getProperty("user.dir");
	public boolean connected = false;
	
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
					case "connect_id":
						connected = true;
						myid = map.get("content");
						try {
							writeData(myid);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
				  	case "message" : 
					  	String timeStamp = new SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(map.get("time") ) ) );
					  	ui.displayMessage(map.get("content"), map.get("name"), timeStamp);	
				  		break;
				  	case "message_history" : 
				  		List<Map<String, String>> message_history = stringToList(map.get("content"));
				  		
				  		for (int i = 0; i < message_history.size(); i++) {
					  		Map<String, String> m = message_history.get(i);	
					  		String timeStamp2 = new SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(m.get("time") ) ) );
					  		ui.displayMessage(m.get("content"), m.get("author"), timeStamp2);				  		
				  		}
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
		
		try {
            data = getDataFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            myid = data.getString("id");

            System.out.println("Read: " + myid);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
	}
	
	public void connectToServer(String name) throws InterruptedException {	
		Map<String, String> map = new HashMap<>();		
		if(myid != null) {
			map.put("type", "connect_with_id");
			map.put("id", myid);
		}else {
			map.put("type", "connect");
		}		
		map.put("content", name);		
		
		new Thread() {			
			@Override
			public void run() {
				System.out.println("Hello");
				try {
					client.connectBlocking();
					client.send(mapToString(map));
					getMessageHistory();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
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
	
	private List<Map<String, String>> stringToList(String s) throws IOException,
    ClassNotFoundException {

		s = s.trim();
		
		byte[] data = Base64.getDecoder().decode(s);
		
		ObjectInputStream ois = new ObjectInputStream(
		        new ByteArrayInputStream(data));
		
		Object o = ois.readObject();
		
		ois.close();
		
		return (List<Map<String, String>>) o;
}
	
	public void getMessageHistory() {
		
		long from = System.currentTimeMillis();
		long to = from - (6L*30L*24L*60L*60L*1000L);
		
		Map <String, String> map = new HashMap<>();
		
		map.put("type", "request_message_history");
		map.put("content", "");
		map.put("from", from + "");
		map.put("to", to + "");
		
		client.send(mapToString(map));
	}
	
	private JSONObject getDataFromFile() throws IOException, JSONException {
        File jsonFile = new File(filePath, fileName);

        jsonFile.createNewFile();

        FileReader fileReader = new FileReader(jsonFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        System.out.println(stringBuilder.toString());
        return new JSONObject(stringBuilder.toString());
    }

    public void writeData(String id) throws IOException, JSONException {
        System.out.println("Trying to write: " + id);
        File jsonFile = new File(filePath, fileName);

        jsonFile.createNewFile();

        //data.put("name", name);
        data.put("id", id);

        String userString = data.toString();

        FileWriter fileWriter = new FileWriter(jsonFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();

    }
	
	
}
