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
    String ip = "ws://ame.treegl.com";   //IP des Backend Servers
    int port = 5555;                    //Port des Backend Servers

    private String myName;              //Eigener Name
    private String myid;                //Eigene ID

    private JSONObject data = new JSONObject();    //JSON Object um User Name und ID zu speichern
    private final String fileName = "data.json";    //Lokale Speicher Datei
    private final String filePath = System.getProperty("user.dir");    //Aktueller Dateipfad zur Datei
    public boolean connected = false;            //True wenn mit dem Server verbunden, sonst false


    /**
     * Verbindet sich mit dem Server
     *
     * @throws URISyntaxException   error
     * @throws InterruptedException error
     */
    public Communication() throws URISyntaxException, InterruptedException {
        System.out.println("Communication has been created");

        client = new WebSocketClient(new URI(ip + ":" + port), new Draft_6455()) { //Der WebsocketClient kommuniziert mit dem Server
            public void onOpen(ServerHandshake serverhandshake) {
            }

            /**
             * Es wurde eine neue Nachricht vom Server entfangen
             * @param message Nachricht
             */
            public void onMessage(String message) {
                //System.out.println(message);
                try {
                    Map<String, String> map = stringToMap(message); //Die Nachricht wird in eine Map konvertiert, damit man dies auslesen kann

                    switch (map.get("type")) {                      //Es wird der Typ der Nachricht geprüft (message = Eine Chat-Nachricht, connect_id = Vom Server Generierte User ID , usw)
                        case "connect_id":      //Der Server hat die Verbindung angenommen und schick die User ID zurück (diese kann sich der Client merken, damit sich der Server den User merken kann)
                            connected = true;   //Es besteht nun eine Verbinung zum server
                            myid = map.get("content");  //Die ID steht im Key "content" der Nachrichten-Map
                            try {
                                writeData("id", myid);  //Die ID wird in einer Datei Lokal gespeichert
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            break;
                        case "message":     //Die nachricht ist eine Chat-Nachticht mit Text, Autor Name und Uhrzeit
                            String timeStamp = new SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(map.get("time"))));        //Die Uhrzeit wird von ms in ein Zeitformat konvertiert
                            ui.displayMessage(map.get("content"), map.get("name"), timeStamp);          //Die daten der Nachricht (text, name, uhrzeit) werden an die UI geleitet
                            break;
                        case "message_history": //Der Server schick die zuvor angefragten alten Nachricht zurück; diese werden nun angezeit
                            List<Map<String, String>> message_history = stringToList(map.get("content"));   //Die Nachrichten sind eine als String konvertierte List; Diese werden wieder in eine Normale Liste konvertier

                            for (int i = 0; i < message_history.size(); i++) {                //Die empfangenen Nachrichten werden nun angezeigt
                                Map<String, String> m = message_history.get(i);
                                String timeStamp2 = new SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(m.get("time"))));
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
            data = getDataFromFile();        //Verusuche Daten (Name, ID) aus einer lokalen Datei zulesen
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            myid = data.getString("id");        //Ist in der Datei eine ID zufinden, wird diese als "myid" gespeichert

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            myName = data.getString("name");    //Ist in der Datei ein Name zufinden, wird dieser als "myName" gespeichert
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Schickt eine Nachricht mit Informationen über sich an den Server (Name und ggf ID)
     *
     * @param name User Name
     * @throws InterruptedException error
     */
    public void connectToServer(String name) throws InterruptedException {
        Map<String, String> map = new HashMap<>();
        try {
            writeData("name", name);                    //Der Name wird in der lokalen Datei gespeichert
        } catch (IOException | JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (myid != null) {                             //Wurde eine ID aus der lokalen Datei gelesen...
            map.put("type", "connect_with_id");         //...wird diese auch dem Server geschickt (dieser erkennt den User dann wieder)
            map.put("id", myid);
        } else {                                        //Ansonsten wird keine ID mit geschickt
            map.put("type", "connect");                 //(Der Server schickt anschließend die generierte User ID zurück)
        }
        map.put("content", name);                       //Der Name wird auch an den Server geschickt

        new Thread() {
            @Override
            public void run() {                              //Die Nachricht wird an den Server geschickt
                try {                                   //Neuer Thread, da sonst der main-UI thread blockiert wird
                    client.connectBlocking();
                    client.send(mapToString(map));      //Beim schicken der Nachricht wird diese zunächst in einen String konvertiert
                    getMessageHistory();                //Es sollten auch die vergangenen Nachrichten geladen werden
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Die Benutzeroberfläche wird bekannt gegeben und falls ein Name existiert, wird dieser als standart in das Namensfeld eingetragen
     *
     * @param ui Benutzeroberfläche
     */
    public void setUI(UI ui) {
        this.ui = ui;

        if (myName != null) {
            ui.setDisplayName(myName);  //Geb der UI bescheid, dass es einen Standartnamen gibt
        }
    }

    /**
     * Eine Textnachricht mit dem Inhalt message soll an den Server geschickt werden
     *
     * @param message Nachricht-text
     */
    public void sendMessage(String message) {

        Map<String, String> map = new HashMap<>();

        map.put("type", "message");     //Typ der Nachricht an den Server soll "message" für Chat-Nachricht sein
        map.put("content", message);    //Inhalt ist der geg. Inhalt

        if (client != null) {           //Nur wenn eine Verbindung zu dem Server besteht, soll die Nachricht verschickt werden
            client.send(mapToString(map));
        }

    }

    /**
     * Konvertiert eine HashMapzu einem String
     *
     * @param o Map
     * @return Map as String
     * @throws IOException Error
     */
    private String mapToString(Map o) {
        //MAGIC
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

    /**
     * Konvertiert einen Map-String zu eine HashMap
     *
     * @param s Map-String
     * @return HashMap from String
     * @throws IOException            Error
     * @throws ClassNotFoundException Error
     */
    private HashMap<String, String> stringToMap(String s) throws IOException,
            ClassNotFoundException {
        //MAGIC 2
        s = s.trim();

        byte[] data = Base64.getDecoder().decode(s);

        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));

        Object o = ois.readObject();

        ois.close();

        return (HashMap<String, String>) o;
    }

    /**
     * Konvertiert einen Listen-String zu einer Liste
     *
     * @param s Listen-String
     * @return List from String
     * @throws IOException            Error
     * @throws ClassNotFoundException Error
     */
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

    /**
     * Frage die Nachrichten der letzen 6 Monate vom Server an
     */
    public void getMessageHistory() {

        long from = System.currentTimeMillis();                 // jetzt in ms
        long to = from - (6L * 30L * 24L * 60L * 60L * 1000L);  // = jetzt - 6 Monate in ms

        Map<String, String> map = new HashMap<>();

        map.put("type", "request_message_history");             //Eine Nachricht vom Typ "request_message_history" soll an den Server geschickt werden,
        map.put("content", "");                                 //
        map.put("from", from + "");                             //...mit anfangs Datum...
        map.put("to", to + "");                                 //...und end Datum

        client.send(mapToString(map));                          //Die Nachricht wird zu einem String konvertiert und an den Server geschickt
    }

    /**
     * Die lokale JSON-Datei wird gelesen
     * @return  JSON Object (Map-like)
     * @throws IOException error
     * @throws JSONException error
     */
    private JSONObject getDataFromFile() throws IOException, JSONException {
        //MAGIC 3
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

    /**
     * Schreibe daten "value" in "key" von lokaler JSON-Datei
     * @param key   key
     * @param value Wert
     * @throws IOException error
     * @throws JSONException error
     */
    public void writeData(String key, String value) throws IOException, JSONException {
        // System.out.println("Trying to write: " + id);
        //MAGIC 4
        File jsonFile = new File(filePath, fileName);

        jsonFile.createNewFile();

        //data.put("name", name);
        data.put(key, value);

        String userString = data.toString();

        FileWriter fileWriter = new FileWriter(jsonFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();

    }


}
