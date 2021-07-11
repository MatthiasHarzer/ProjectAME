package main;

import java.net.URISyntaxException;
import org.java_websocket.drafts.*;

public class Main {
	
	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		
		UI ui = new UI();
		
		Communication com = new Communication();
		
		ui.setCommunication(com);
		
		com.setUI(ui);
	
	}

}
