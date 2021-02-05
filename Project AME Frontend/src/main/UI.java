package main;

import java.util.concurrent.TimeUnit;

public class UI {
 
	Communication com;
	
	private int i = 0;
	
	public UI() {
		System.out.println("UI has been created");
		
	}
	
	public void setCommunication(Communication pcom) {
		this.com = pcom;
	}
	
	
	
	public void displayMessage(String content, String name, String time) {
		
		//insert ui stuff here
		
		//provisorische lösung
 	 System.out.println("it's " + time + " and " +name + " says:  " + content);
		
	}
	
}
