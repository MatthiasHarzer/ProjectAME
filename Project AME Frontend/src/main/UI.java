package main;

import java.awt.Color;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.*;

import java.util.List;

import java.util.Random;

public class UI implements KeyListener{
 
	
	
	public int mesposition = 600, profmode = 1;
	
	public String state = "entername", name = "Name";
	
	public String[] badwords = {"fuck", "shit", "nigger", "nigga", "slut", "cunt", "Ersch", "Hure", "zipperhead", "Neger", "slut", "faggot", "Schlampe"};
	
	public String[] goodwords = {"love", "nice", "gay", "great", "amazing", "cuddle", "family", "pet", "Liebe", "toll", "super", "grandios", "kuscheln", "Famlie", "Haustiere"};
	
	public List<Message> messagelist = new ArrayList<>();
	
	Communication com;
		
	JFrame frame = new JFrame("Project AME");
	JLabel background = new JLabel(new ImageIcon("background.png"));
	JLabel layer3 = new JLabel(new ImageIcon("layer3_namepls.png"));
	
	TextField textfield = new TextField("");
	TextField namefield = new TextField(name);
	
	JButton send = new JButton(new ImageIcon("sendtexture.png"));
	JButton submit = new JButton(new ImageIcon("submittexture.png"));
	JButton settings = new JButton(new ImageIcon("settingstexture.png"));
	JButton profanity = new JButton("on");
	
	JList<JLabel> messagePanel = new JList<JLabel>();
	
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
		
		settings.setBounds(830, 50, 100, 50);
		frame.add(settings);
		frame.setComponentZOrder(settings, 1);
		settings.setVisible(false);
		settings.addActionListener(al1);
		
		frame.add(namefield);
		frame.setComponentZOrder(namefield, 2);
		namefield.setForeground(Color.white);
		namefield.setBackground(new Color(48,49,54));
		namefield.setBounds(375, 350, 250, 50);
		namefield.addKeyListener(this);
		
		profanity.setBounds(420, 250, 100, 50);
		frame.add(profanity);
		frame.setComponentZOrder(profanity, 3);
		profanity.setVisible(false);
		profanity.addActionListener(al1);
		
		send.setBounds(850, 650, 100, 50);
		frame.add(send);
		frame.setComponentZOrder(send, 3);
		send.addActionListener(al1);
		
		frame.add(textfield);
		frame.setComponentZOrder(textfield, 4);
		textfield.setForeground(Color.white);
		textfield.setBackground(new Color(48,49,54));
		textfield.setBounds(50, 650, 800, 50);
		textfield.addKeyListener(this);
		
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
		
		if(content==null || content.length()<=0) {
		return;
		}
		for(int i=0; i<messagelist.size(); i++) {
			messagelist.get(i).move();
		}
		messagelist.add(new Message(content, name, time));
		
	}
	
	public void b_ActionPerformed(ActionEvent e) {
		if(e.getSource().equals(send)) {
			String content = textfield.getText();
			com.sendMessage(content);
		}
		if(e.getSource().equals(submit)) {
			namefield.setVisible(false);
			//namepls.setVisible(false);
			layer3.setIcon(new ImageIcon("layer3.png"));
			submit.setVisible(false);
			settings.setVisible(true);
			this.name = namefield.getText();
			try {
				com.connectToServer(name);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.state = "default";
		}
		
		if(e.getSource().equals(settings)) {
			
			
			if(state.equals("settings")) {
				
				layer3.setIcon(new ImageIcon("layer3.png"));
				state = "default";
					
				profanity.setVisible(false);

				
			} else {
				
				layer3.setIcon(new ImageIcon("layer3_settings.png"));
				state = "settings";
				
				profanity.setVisible(true);
				
			}
			
			
		}
		
		
		if(e.getSource().equals(profanity)) {
			
			profmode++;
			
			if (profmode > 2) {
				profmode = 0;
			}
			
			switch (profmode) {
			
			case 0:
				profanity.setText("off");
				break;
			case 1:
				profanity.setText("on");
				break;	
			case 2:
				profanity.setText("reverse");
				break;	
			}
			
		}
		
	}
	
	class ActionListener1 implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			b_ActionPerformed(e);
		}
	}
	 
	
	
	public class Message{
		
		int savedx = 0, savedy = 0;
		
		JLabel mes;
		JLabel emoji1 = new JLabel(new ImageIcon("haha.png"));
		
		private boolean isemoji = false;
		
		public Message(String content, String name, String time) {
			
			if(content == null) {
				
				content = " ";
				
			}
			
			if(content.length()>0) {		//Keine Leere Nachricht? 
				
				switch (profmode) {
				
				case 1:
					
					for(int i = 0; i < badwords.length; i++) {
						
						content = content.replaceAll(badwords[i], goodwords[(int) Math.round(Math.random()*(goodwords.length-1))] );
						
					}
					
				break;	
					
				case 2:
					
					for(int i = 0; i < goodwords.length; i++) {
						
						content = content.replaceAll(goodwords[i], badwords[(int) Math.round(Math.random()*(badwords.length-1))]);
						
					}
					
				break;	
				}
				
				
				if (content.equals("haha")) {
					content = " ";
					frame.add(emoji1);
					frame.setComponentZOrder(emoji1, 6);
					emoji1.setBounds(250, 600, 50, 50);
					isemoji = true;
					this.mes = new JLabel("it's " + time + " and " +name + " says:  " + content);
				} else {
					this.mes = new JLabel("it's " + time + " and " +name + " says:  " + content);
				}
				
			}
			frame.add(mes);
			mes.setForeground(Color.white);
			
			frame.setComponentZOrder(mes, 6);
			this.mes.setBounds(50,600,700,50);
			
		}
		
		public void move(){
			mes.setLocation(50, mes.getY()-50);
			if (isemoji == true) {
				emoji1.setLocation(250, emoji1.getY()-50);
			}
		}
		
		
		
	}

	public void setDisplayName(String name) {
		
		namefield.setText(name);
		
	}
	
	
	public class Settings{
		
		
		
	}
	
	
	@Override
	public void keyPressed(KeyEvent e1) {
		// TODO Auto-generated method stub
		int key = e1.getKeyCode();
		if(key == KeyEvent.VK_ENTER) {
			
			if(this.state.equals("default")) {
				String content = textfield.getText();
				com.sendMessage(content);
			}
			
			if(this.state.equals("entername")){
				namefield.setVisible(false);
				layer3.setIcon(new ImageIcon("layer3.png"));
				submit.setVisible(false);
				this.name = namefield.getText();
				try {
					com.connectToServer(name);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				this.state = "default";
			}
			
				
			}
		
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyTyped(KeyEvent arg1) {
		
	}
}



