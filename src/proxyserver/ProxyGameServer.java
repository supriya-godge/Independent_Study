package proxyserver;

import ServerPlayer.PlayerMove;
import ServerPlayer.PlayerStructure;
import ServerPlayer.ServerPlayerProxy;
import engine.Engine;
import engine.JSONCommand;
import engine.PlayerInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * This class is intermediatory. between the server and a serverplayer.
 * It connects to the server and gets the messages then it invokes the player methods based on the
 * JSON commands.
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */
public class ProxyGameServer implements NetworkCommunication {
    static protected ServerSocket serverSock;
    PrintWriter out;
    Socket aSocket;
    BufferedReader din;
    Engine aEngine;
    ServerPlayerProxy serverPlayer;
    static{
        try {
            serverSock = new ServerSocket(9001, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ProxyGameServer(Engine eng){
        this.aEngine = eng;
    }


    public void init(Socket aSocket){
        this.aSocket = aSocket;

        try {
            out= new PrintWriter(aSocket.getOutputStream (), true);
            din = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
        }catch (InterruptedIOException e){
            System.err.println("Timeout");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void init(ServerPlayerProxy player){
        this.serverPlayer = player;
    }

    public ServerSocket getServerSock(){
        return serverSock;
    }

    @Override
    public boolean send(JSONObject data, PlayerInfo player) {
        if(!player.getisLocal())
            out.println(data.toJSONString());
        else
            serverPlayer.JSONprocess(data);
        return false;
    }

    @Override
    public JSONObject receive() {
        JSONObject data = null;
        try{

        String newPort = din.readLine();
           // System.out.println("receive"+newPort);
        JSONParser parse = new JSONParser();
        data = (JSONObject) parse.parse(newPort);
        }catch (SocketTimeoutException e){
            System.out.println("Timeout");
            System.exit(1);
        }
        catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    class JSONCommands{
        public static final String INITIALIZE = "Initialize";
        public static final String MOVE = "Move";
        public static final String INVALIDATE = "Invalid";
        public static final String REQUEST = "Request";
        public static final String LASTMOVE = "LastMove";
        public static final String CONFIRM = "Confirm";
    }

    /* This method creates the json objects to send to the client proxy */
    public JSONObject StringtoJSON(String state, PlayerInfo player, PlayerMove aLastMove, int gameId) {

        JSONObject json = new JSONObject();
        json.put("JSONCommand",state);
        switch (state){
            case JSONCommand.INVALIDATE:
                json.put("PlayerId",aLastMove.getId());
                json.put("SendToId",player.getId());
                aEngine.removePlayer(aLastMove.getId());
                break;
            case JSONCommand.LASTMOVE:
                json.put("Row",aLastMove.getRow());
                json.put("Column",aLastMove.getColumn());
                json.put("Id",aLastMove.getId());
                json.put("SendToId",player.getId());
                break;
            case JSONCommand.REQUEST:
                json.put("PlayerId",player.getId());
                break;
            case JSONCommand.CONFIRM:
                json.put("Status","Successful");
                json.put("GameId",gameId);
                break;
        }
        System.out.println("sent"+json.toJSONString());
        return json;
    }

    /*
   This method processes the json objects
    */
    public String JSONprocess(JSONObject aJSONObject) {
        System.out.println("received"+aJSONObject.toJSONString());
        String state = (String) aJSONObject.get("JSONCommand");
        switch (state){
            case JSONCommand.INITIALIZE:
                int player2 = (int) (long)aJSONObject.get("Player2");
                int player1 = (int)(long) aJSONObject.get("Player1");
                aEngine.processInitialize(player1,player2);
                break;
            case JSONCommand.MOVE:
                int id =(int)(long)aJSONObject.get("Id");
                PlayerMove aPlayerMove = new PlayerMove((int)(long)aJSONObject.get("Row"),
                        (int)(long) aJSONObject.get("Column"),id );
                aEngine.processMove(id,aPlayerMove);
                break;
            case JSONCommand.INVALIDATE:
                id =(int)(long)aJSONObject.get("PlayerId");
                aEngine.removePlayer(id);
                break;

        }
        return null;
    }




}
