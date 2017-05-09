package proxyserver;

import ServerPlayer.PlayerStructure;
import ServerPlayer.ServerPlayerProxy;
import engine.PlayerInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by sup33 on 3/13/2017.
 */
public class ProxyGameServer implements NetworkCommunication {
    static protected ServerSocket serverSock;
    PrintWriter out;
    Socket aSocket;
    BufferedReader din;
    ServerPlayerProxy serverPlayer;
    static{
        try {
            serverSock = new ServerSocket(9001, 100);
            //serverSock.setSoTimeout(500000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void init(Socket aSocket){
        this.aSocket = aSocket;

        try {
            //this.aSocket.setSoTimeout(5000);
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

}
