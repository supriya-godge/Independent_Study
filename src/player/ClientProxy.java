package player;


/**
 * This is the network communication class which is responsible for lower level
 * network communication. It uses the sockets and TCP connection for the communication
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class ClientProxy {
        private static Socket clientSock;
        private BufferedReader din;
        private PrintWriter out;
        ClientManager aClientProxy;



        public ClientProxy(ClientManager cpobj)  {
            this.aClientProxy = cpobj;
            try {
                System.out.println("Connecting to server");
                clientSock = new Socket("localhost", 9001);
                //setup of input and output stream
                out = new PrintWriter(clientSock.getOutputStream(), true);
                din = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        This method implements the necessary code to send the
        data over the network using the socket.
         */
        public boolean send(Socket aSocket, JSONObject data) {
            out.println(data.toJSONString());
            System.out.println("\nsent"+data.toJSONString());
            return true;
        }

        /*
        This method implements the necessary code to receive the
        data over the network using the socket.s
         */
        public JSONObject receive(Socket aSocket) throws IOException {
            JSONObject data = null;
            try {
                String newPort = din.readLine();
                JSONParser parse = new JSONParser();
                data = (JSONObject) parse.parse(newPort);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("\nreceived "+data.toJSONString());
            return data;
        }


    /*
    This method reads the JSON objects and take necessary action
     */
    public String JSONprocess(JSONObject aJSONObject) {
        String state = (String) aJSONObject.get("JSONCommand");
        switch (state){
            case JSONCommand.MOVE:
                break;
            case JSONCommand.INVALIDATE:
                aClientProxy.processInvalidate((int)(long)aJSONObject.get("PlayerId"),(int)(long)aJSONObject.get("SendToId"));
                break;
            case JSONCommand.LASTMOVE:
                PlayerMove aPlayerMove = new PlayerMove((int)(long)aJSONObject.get("Row"),
                        (int)(long) aJSONObject.get("Column"),
                        (int)(long)aJSONObject.get("Id"));
                aClientProxy.processLastMove(aPlayerMove,(int)(long)aJSONObject.get("SendToId"));
                break;
            case JSONCommand.REQUEST:
                aClientProxy.processRequest(((int) (long) aJSONObject.get("PlayerId")));
                break;

            case JSONCommand.CONFIRM:
                String status = (String)aJSONObject.get("Status");
                aClientProxy.processConfirm((int)(long) aJSONObject.get("GameId"));
                break;
        }
        return null;
    }


    /*
    This method convert the data into JSON object
     */
    public JSONObject StringtoJSON(String state, PlayerStructure aplayer, ArrayList<PlayerStructure> player,
                                   ArrayList<Integer> serverPlayer) {
        JSONObject json = new JSONObject();
        json.put("JSONCommand",state);
        switch (state){
            case JSONCommand.INITIALIZE:
                //json.put("Server","Yes");
                int iter=0;
                for(iter=0;iter<player.size();iter++) {
                    json.put("Player"+iter, player.get(iter).getID());
                }
                for(int jiter=iter;jiter<serverPlayer.size()+iter;jiter++)
                    json.put("Player"+jiter,serverPlayer.get(jiter-iter));
                break;
            case JSONCommand.MOVE:
                PlayerMove aPlayerMove = aplayer.move();
                json.put("Row",aPlayerMove.getRow());
                json.put("Column",aPlayerMove.getColumn());
                json.put("Id",aPlayerMove.getId());
                break;
            case JSONCommand.INVALIDATE:
                System.out.println("Player Invalidated "+aplayer.getID());
                json.put("PlayerId",aplayer.getID());
        }
        return json;
    }

    public void sendHelper(Socket client, String state, PlayerStructure aplayer, ArrayList<PlayerStructure> player,
                           ArrayList<Integer> serverPlayer) {
        JSONObject aJSONObject = StringtoJSON(state, aplayer, player,serverPlayer);
        send(client, aJSONObject);
    }



}




