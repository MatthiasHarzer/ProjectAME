package main;

public class Main {

	private int i = 0;
	
	public static void main(String[] args) {
		UI ui = new UI();
		Communication com = new Communication();
		
		ui.setCommunication(com);
	}

}
