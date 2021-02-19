package main;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import java.util.List;


public class UI {
 
	public int mesposition = 600;
	public String state = "entername", name = "Name";
	public List<Message> messagelist = new ArrayList<>();
	
	Communication com;
		
	JFrame frame = new JFrame("Project AME");
	JLabel background = new JLabel(new ImageIcon("background.png"));
	JLabel layer3 = new JLabel(new ImageIcon("layer3.png"));
	TextField textfield = new TextField("Message Content");
	TextField namefield = new TextField(name);
	JLabel namepls = new JLabel(new ImageIcon("namepls.png"));
	
	JButton send = new JButton("Send");
	JButton submit = new JButton("Submit");
	
	ActionListener1 al1 = new ActionListener1();
	
	public UI() {
		System.out.println("UI has been created");
		
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		frame.setSize(1000,750);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		submit.setBounds(450, 400, 100, 50);
		frame.add(submit);
		frame.setComponentZOrder(submit, 0);
		submit.addActionListener(al1);
		
		frame.add(namefield);
		frame.setComponentZOrder(namefield, 1);
		namefield.setBounds(375, 350, 250, 50);
		
		frame.add(namepls);
		frame.setComponentZOrder(namepls, 2);
		namepls.setBounds(350, 250, 300, 225);
		
		send.setBounds(850, 650, 100, 50);
		frame.add(send);
		frame.setComponentZOrder(send, 3);
		send.addActionListener(al1);
		
		frame.add(textfield);
		frame.setComponentZOrder(textfield, 4);
		textfield.setBounds(50, 650, 800, 50);
		
		frame.add(layer3);
		frame.setComponentZOrder(layer3, 5);
		layer3.setBounds(0, 0, 1000, 750);
		
		frame.add(background);
		frame.setComponentZOrder(background, 6);
		background.setBounds(0, 0, 1000, 750);
		
		
	}
	
	
	public void setCommunication(Communication pcom) {
		this.com = pcom;
	}
	
	
	
	public void displayMessage(String content, String name, String time) {
		
		for(int i=0; i<messagelist.size(); i++) {
			messagelist.get(i).move();
		}
		messagelist.add(new Message(content, name, time));
 	 //System.out.println("it's " + time + " and " +name + " says:  " + content);
		
	}
	
	public void b_ActionPerformed(ActionEvent e) {
		if(e.getSource().equals(send)) {
			String content = textfield.getText();
			//this.displayMessage(content, this.name, "6:90 am");
			com.sendMessage(content);
		}
		if(e.getSource().equals(submit)) {
			namefield.setVisible(false);
			namepls.setVisible(false);
			submit.setVisible(false);
			this.name = namefield.getText();
			try {
				com.connectToServer(name);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class ActionListener1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			b_ActionPerformed(e);
		}
	}
	 
	
	public class Message{
		
		JLabel mes;
		
		public Message(String content, String name, String time) {
		
			this.mes = new JLabel("it's " + time + " and " +name + " says:  " + content);
			
			frame.add(mes);
			frame.setComponentZOrder(mes, 6);
			this.mes.setBounds(50,600,700,50);
			
		}
		
		public void move(){
			mes.setLocation(50, mes.getY()-50);
		}
		
		
	}
}



