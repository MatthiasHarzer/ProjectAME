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

public class UI implements KeyListener{		//KeyListener wird implementiert
 
	private int profmode = 0;		//Standard-Modus des Schimpfwort-Filters
	
	private String state = "entername", name = "Name";		//Startzustand des Programmes (Namenseingabe) und Standard-Name
	
	private String[] badwords_ignorecase = {"(?i)fuck", "(?i)shit", "(?i)nigger", "(?i)nigga", "(?i)slut", "(?i)cunt", "(?i)arsch", "(?i)hure", "(?i)zipperhead", "(?i)neger", "(?i)faggot", "(?i)schlampe", "(?i)schwuchtel", "(?i)trany", "(?i)transe", "(?i)hurensohn", "(?i)ass", "(?i)fotze", "(?i)wichser", "(?i)pimmel", "(?i)arse", "(?i)bitch", };		//Schimpfwoerter die herausgefiltert/ersetzt werden (Filter-Modus: on) (Gross-/Kleinschreibung wird ignoriert) 
	private String[] badwords = {"fuck", "shit", "Arsch", "Hurensohn", "Schlampe", "(?i)Pimmel"};		//Schimpfwoerter die eingesetzt werden (Filter-Modus: reverse) (enthaellt nur "weniger schlimme" Schimpfwoerter)
	private String[] goodwords_ignorecase = {"(?i)love", "(?i)nice", "(?i)gay", "(?i)great", "(?i)amazing", "(?i)cuddle", "(?i)family", "(?i)pet", "(?i)liebe", "(?i)toll", "(?i)super", "(?i)grandios", "(?i)kuscheln", "(?i)familie", "(?i)haustiere", "(?i)herz", "(?i)heart", "(?i)pizza"};		//Nette Woerter die herausgefiltert/ersetzt werden (Filter-Modus: reverse) (Gross-/Kleinschreibung wird ignoriert) 
	private String[] goodwords = {"love", "nice", "gay", "great", "amazing", "cuddle", "family", "pet", "Liebe", "toll", "super", "grandios", "kuscheln", "Familie", "Haustiere", "Herz", "heart", "Pizza"}; //Nette Woerter die eingesetzt werden (Filter-Modus: on)
	
	private List<Message> messagelist = new ArrayList<>();		//Nachrichtenliste wird angelegt
	
	Communication com;		//Lokales Objekt der Communication Klasse
		
	JFrame frame = new JFrame("Project AME");		//Projektfenster wird angelegt
	
	JLabel background = new JLabel(new ImageIcon("background.png"));		//Hintegrund Label
	JLabel layer3 = new JLabel(new ImageIcon("layer3_namepls.png"));		//Obere und Untere Raender + Logo (fortan "Layer3" genannt)
	
	TextField textfield = new TextField("");		//Textfeld zur Eingabe von Nachrichten 
	TextField namefield = new TextField(name);		//Textfeld zur eingabe von Namen
	
	JButton send = new JButton(new ImageIcon("sendtexture.png"));		//Knopf zum abschicken von Nachrichten
	JButton submit = new JButton(new ImageIcon("submittexture.png"));		//Knopf zum bestaetigen des Namens
	JButton settings = new JButton(new ImageIcon("settingstexture.png"));		//Knopf zum oeffnen der Einstellungen
	JButton profanity = new JButton(new ImageIcon("profanitytexture_on.png"));		//Knopf zur aenderung des Filter-Modus
	
	ActionListener1 al1 = new ActionListener1();		//ActionListener zu erfassung von Knopf-Inputs
	
	
	public UI() {		//Konstruktormethode
		
		System.out.println("UI has been created");		//Kreierungs-Bestaetigung in der Konsole
		
		frame.setLayout(null);	//kein Layout Manager wird verwendet
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );		//X-Knopf des Fensters schliesst dieses
		frame.setSize(1000,750);		//Groesse des Fensters
		frame.setResizable(false);		//Fenster darf nicht in seiner Groesse veraendert werden
		frame.setLocationRelativeTo(null);		//Fenster startet in der Mitte des Bildschirms
		frame.setVisible(true);		//Fenster ist sichtbar
		
		/*
		Start der Element-Konfigurationsphase: 
		
		Alle Knoepfe / Textfelder / Labels werden dem Fenster hinzugefuegt.
		
		Allen Elementen wird Groesse, Position, Schicht auf der Z-Achse und Sichtbarkeit zugewiesen. 
		Diese basieren auf dem Startzustand des Programmes (Namenseingabe). 
		
		Den Knoepfen wird der ActionListener zugeteilt. 
		
		Den beiden Textfeldern werden Farben fuer Hintergrund und Schrift zugewiesen.
		Ihnen wird ausserdem der KeyListener zugeteilt, um Nachrichten mit der Enter-Taste zu senden.
		*/
		
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
		
		profanity.setBounds(1000, 250, 100, 50);		//Knopf zur Aenderung des Filter-Modus erscheint ausserhalb des sichtbaren Bereichs, anstatt unsichtbar zu sein, um Bug vorzubaeugen -> siehe Dokumentation
		frame.add(profanity);
		frame.setComponentZOrder(profanity, 3);
		profanity.addActionListener(al1);
		
		send.setBounds(850, 650, 100, 50);
		frame.add(send);
		frame.setComponentZOrder(send, 4);
		send.addActionListener(al1);
		
		frame.add(textfield);
		frame.setComponentZOrder(textfield, 5);
		textfield.setForeground(Color.white);
		textfield.setBackground(new Color(48,49,54));
		textfield.setBounds(50, 650, 800, 50);
		textfield.addKeyListener(this);
		
		frame.add(layer3);
		frame.setComponentZOrder(layer3, 6);
		layer3.setBounds(0, 0, 1000, 750);
		
		frame.add(background);
		frame.setComponentZOrder(background, 7);
		background.setBounds(0, 0, 1000, 750);
		
		//Ende der Element-Konfigurationsphase
		
	}
	
	
	public void setCommunication(Communication pcom) {
		
		this.com = pcom;		//Lokales Objekt der Communication Klasse = von Main kreiertes Objekt der Communication Klasse
		
	}
	
	
	public void displayMessage(String content, String name, String time) {		//Methode zum Anzeigen von Nachrichten
		
		if(content==null || content.length()<=0) {		//ueberpruefen, ob Nachricht leer ist
			return;
		}
		
		for(int i=0; i<messagelist.size(); i++) {		//Alle Nachrichten eins nach oben Verschieben
			messagelist.get(i).move();
		}
		
		messagelist.add(new Message(content, name, time));		//Neue Nachricht hinzufuegen
		
	}
	
	
	private void b_ActionPerformed(ActionEvent e) {		//Methode, die vom ActionListener aufgerufen wird
		
		if(e.getSource().equals(send)) {		//Wenn der Sendeknopf geklickt
			String content = textfield.getText();		//In Textfeld eingegebene Nachricht wird zu String umgewandelt
			com.sendMessage(content);		//Communication auftragen die Nachricht abzuschicken
			textfield.setText("");		//Eingabefeld fuer Nachrichten leeren
		}
		
		if(e.getSource().equals(submit)) {		//Wenn der Namens-Bestaetigungs-Knopf geklickt
			namefield.setVisible(false);		//Textfeld zur Eingabe des Namens verstecken
			layer3.setIcon(new ImageIcon("layer3.png"));		//Textur von Layer3 veraendern (Aufforderung zur Eingabe des Namens entfernen)
			submit.setVisible(false);		//Namens-Bestaetigungs-Knopf verstecken
			settings.setVisible(true);		//Einstellungsknopf sichtbar machen
			this.name = namefield.getText();		//Name aus Textfeld in Variable uebertragen
			try {
				com.connectToServer(name);		//Unter eingegebenem Namen mit Server verbinden
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.state = "default";		//Standardzustand
		}
		
		if(e.getSource().equals(settings)) {		//Wenn Einstellungsknopf geklickt
			if(state.equals("settings")) {		//Wenn sich das Programm bereits im Einstellungszustand befindet
				layer3.setIcon(new ImageIcon("layer3.png"));		//Textur von layer 3 anpassen (Einstellungspanel schliessen)
				state = "default";		//Standardzustand
				profanity.setLocation(1000, 250);		//Knopf zur Aenderung des Filter-Modus verstecken (ausserhalb des sichtbaren Bereichs schieben, nicht unsichtbar machen, um Bug vorzubaeugen -> siehe Dokumentation)
			} else {		//Wenn im Standardzustand
				layer3.setIcon(new ImageIcon("layer3_settings.png"));		//Textur von layer 3 anpassen (Einstellungspanel oeffnen)
				state = "settings";		//Einstellungszustand
				profanity.setLocation(420, 250);		//Knopf zur Aenderung des Filter-Modus sichtbar machen (
			}
		}
		
		if(e.getSource().equals(profanity)) {		//Wenn Filterknopf geklickt
			profmode++;		//Filtermodus aendern
			if (profmode > 2) {		//Nach Filtermodus 2 wieder bei 0 anfangen
				profmode = 0;
			}
			switch (profmode) {		//Je nach Filtermodus die Textur des Filterknopfes aendern
				case 0:
					profanity.setIcon(new ImageIcon("profanitytexture_on.png"));
					break;
				case 1:
					profanity.setIcon(new ImageIcon("profanitytexture_off.png"));
					break;	
				case 2:
					profanity.setIcon(new ImageIcon("profanitytexture_reverse.png"));
				break;	
			}
			
		} 
		
	}
	
	
	public void setDisplayName(String name) {		//Methode um von ausserhalb der UI-Klasse den Namen zu aendern
		
		namefield.setText(name);
		
	}


	@Override
	public void keyPressed(KeyEvent e1) {		//Methode wird automatisch aufgerufen, wenn eine Taste auf der Tastatur betaetigt wird
		// TODO Auto-generated method stub
		int key = e1.getKeyCode();		//Lokale Variable "key" wird mit dem Key Code der gedrueckten Taste beschrieben
		if(key == KeyEvent.VK_ENTER) {		//Wenn Enter-Taste gedrueckt wurde
			
			if(this.state.equals("default")) {		//Wenn Standardzustand
				String content = textfield.getText();		//In Textfeld eingegebene Nachricht wird zu String umgewandelt
				com.sendMessage(content);		//Communication auftragen die Nachricht abzuschicken
				textfield.setText("");		//Eingabefeld fuer Nachrichten leeren
			}
			
			if(this.state.equals("entername")){		//Wenn Startzustand (Namenseingabe)
				namefield.setVisible(false);		//Textfeld zur Eingabe des Namens verstecken
				layer3.setIcon(new ImageIcon("layer3.png"));		//Textur von Layer3 veraendern (Aufforderung zur Eingabe des Namens entfernen)
				submit.setVisible(false);		//Namens-Bestaetigungs-Knopf verstecken
				this.name = namefield.getText();		//Name aus Textfeld in Variable uebertragen
				try {
					com.connectToServer(name);		//Unter eingegebenem Namen mit Server verbinden
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				this.state = "default";		//Standardzustand
			}
			
				
			}
		
	}


	@Override
	public void keyReleased(KeyEvent arg0) {		//Durch implementierung des Key-Listeners muss diese Methode vorhanden sein (unbenutzt)
		// TODO Auto-generated method stub
	}


	@Override
	public void keyTyped(KeyEvent arg1) {		//Durch implementierung des Key-Listeners muss diese Methode vorhanden sein (unbenutzt)
		// TODO Auto-generated method stub
	}

	
	
	private class ActionListener1 implements ActionListener{		//ActionListener Klasse
		
		public void actionPerformed(ActionEvent e) {		//Methode wird nach Betaetigung einer der Knoepfe automatisch aufgerufen
				
			b_ActionPerformed(e);		//die eigentlichen Reaktionen auf verschiedene Knopf-Inputs sind in einer Methode der UI Klasse geregelt, die hier aufgerufen wird
				
		}
			
	}
		 
	
	
	private class Message{		//Nachrichtenklasse
			
		JLabel mes;		//Nachrichten-Label
		JLabel emoji1 = new JLabel(new ImageIcon("haha.png"));		//Emoji-Label
			
		private boolean isemoji = false;		//wahr/falsch Variable zur angabe, ob es sich bei einer nachricht um einen Emoji handelt. (Ein Emoji kann nicht inerhalb einer Nachricht stehen, die auch normalen Text beinhaltet, er muss fuer sich selbst stehen)
			
			
		public Message(String content, String name, String time) {	//Konstruktormethode
				
			if(content == null) {	//komplett leere Nachrichten mit einem Leerzeichen ersetzen 
					content = " ";
			}
				
			if(content.length()>0) {		//wen die nachricht nicht leer ist 
				switch (profmode) {
					case 0:		//Wenn Filtermodus = on
						for(int i = 0; i < badwords_ignorecase.length; i++) {
							content = content.replaceAll(badwords_ignorecase[i], goodwords[(int) Math.round(Math.random()*(goodwords.length-1))] );		//Alle Schimpfwoerter mit netten Woertern ersetzen
						}
					break;	
					case 2:		//Wenn Filtermodus = reverse
						for(int i = 0; i < goodwords_ignorecase.length; i++) {
							content = content.replaceAll(goodwords_ignorecase[i], badwords[(int) Math.round(Math.random()*(badwords.length-1))]);		//Alle Netten Woerter mit Schimpfwoertern ersetzen
						}
					break;	
				}
					
				if (content.equals("haha")) {		//Der Ausdruck "haha" wird zum Emoji
					content = " ";		//Schrift entfernen
					frame.add(emoji1);		//Emoji-Label hinzufuegen
					frame.setComponentZOrder(emoji1, 7);		//Emoji Schicht auf der Z-Achse 
					emoji1.setBounds(250, 600, 50, 50);		//Emoji Groesse und Startposition
					isemoji = true;		//Nachricht wird als Emoji markiert
					this.mes = new JLabel("it's " + time + " and " +name + " says:  " + content);		//Inhalt (Emoji), Absaender und Zeit zum Nachrichten-Label hinzufuegen
				} else {
					this.mes = new JLabel("it's " + time + " and " +name + " says:  " + content);		//Inhalt (Text), Absaender und Zeit zum Nachrichten-Label hinzufuegen
				}
					
			}
			
			frame.add(mes);		//Nachrichten-Label zum Fenster hinzufuegen
			
			mes.setForeground(Color.white);		//Farbe des Textes
			
			frame.setComponentZOrder(mes, 7);		//Schicht der Nachticht auf der Z-Achse
			
			this.mes.setBounds(50,600,700,50);		//Groesse und Startposition des Nachrichten-Labels
			
		}
		
		
		public void move(){		//Methode zum verschieben von Nachrichten
			
			mes.setLocation(50, mes.getY()-50);		//Nachricht -50 Pixel nach oben verschieben
			
			if (isemoji == true) {		//Wenn die Nachricht ein Emoji ist...
				emoji1.setLocation(250, emoji1.getY()-50);		//...soll dieser auch verschoben werden
			}
			
		}
		
			
	}

		
	
}



