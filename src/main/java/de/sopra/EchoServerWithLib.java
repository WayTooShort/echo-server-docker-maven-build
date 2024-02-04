package de.sopra;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import org.json.JSONObject;

public class EchoServerWithLib extends WebSocketServer {

    public EchoServerWithLib(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Neue Verbindung geöffnet von " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Verbindung geschlossen zu " + conn.getRemoteSocketAddress() + "; Code: " + code + "; Grund: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            // Parsen der Nachricht als JSON
            JSONObject jsonMessage = new JSONObject(message);

            // Überprüfen, ob das "message" Attribut vorhanden ist
            if (jsonMessage.has("message")) {
                // Antwort mit demselben Inhalt im "message" Attribut
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("code", 200);
                jsonResponse.put("data", jsonMessage.getString("message"));
                conn.send(jsonResponse.toString());
            } else {
                // Wenn kein "message" Attribut vorhanden ist, Fehler senden
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("code", 400);
                jsonResponse.put("data", "Bad Request");
                conn.send(jsonResponse.toString());
            }
        } catch (Exception e) {
            // Bei einem Parsing-Fehler der eingehenden Nachricht Fehler senden
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("code", 400);
            jsonResponse.put("data", "Bad Request");
            conn.send(jsonResponse.toString());
        }

        //evtl. falls noch ein 2. Tag drin ist auch Fehler?
    }



    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Ein Fehler trat auf bei " + (conn != null ? conn.getRemoteSocketAddress() : "unknown address") + ": " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("Server gestartet und lauscht auf Port " + getPort());
    }

    public static void main(String[] args) {
        int port = 3000;
        WebSocketServer server = new EchoServerWithLib(new InetSocketAddress(port));
        server.start();
    }
}
