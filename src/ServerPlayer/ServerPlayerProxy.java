package ServerPlayer;

import engine.Engine;
import engine.JSONCommand;
import model.TicTacToe;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;

/**
 * Created by sup33 on 4/18/2017.
 */
public class ServerPlayerProxy implements Callable<String> {
    //NetworkCommunication aNetworkCommunication;
    Socket client;
    PlayerStructure player1;
    PlayerStructure player2;
    int gameId;
    static Random aRandom;
    JSONObject json;
    PlayerStructure current;
    Engine aEngine;

    public ServerPlayerProxy(Engine con){
        this.player1 = new ServerPlayer(999,3,TicTacToe.ROUND);
        aEngine = con;

    }



    public void send(String state, PlayerStructure player){
        JSONObject aJSONObject = StringtoJSON(state,player);
        aEngine.JSONprocess(aJSONObject);
       // aNetworkCommunication.send(client,aJSONObject);

    }

    public String getPlayer(int id){
        if (player1.getID() == id)
            return player1.getMark();
        return TicTacToe.CROSS;
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
                break;
            case JSONCommand.LASTMOVE:

                int id = (int)aJSONObject.get("Id");
                String mark = getPlayer(id);
                PlayerMove aPlayerMove = new PlayerMove((int)aJSONObject.get("Row"),
                        (int) aJSONObject.get("Column"),mark,
                        (int)aJSONObject.get("Id"));
                player1.lastMove(aPlayerMove);
                //System.out.println(player1);
                break;
            case JSONCommand.REQUEST:

                if (player1.getID()==((int) aJSONObject.get("PlayerId"))) {
                    current = player1;
                    send("Move", player1);
                }
                else {
                    current = player2;
                    send("Move", player2);
                }
                break;

            case JSONCommand.CONFIRM:
                String status = (String)aJSONObject.get("Status");
                gameId = (int)(long) aJSONObject.get("GameId");
                break;
        }
        return null;
    }


    /*
    This method convert the data into JSON object
     */
    public JSONObject StringtoJSON(String state, PlayerStructure player) {
        JSONObject json = new JSONObject();
        json.put("JSONCommand",state);
        switch (state){
            case JSONCommand.INITIALIZE:
                json.put("Server","No");
                json.put("Player1",(long)player1.getID());
                json.put("Player2",(long)player2.getID());
                break;
            case JSONCommand.MOVE:
                System.out.println(player.getID());
                PlayerMove aPlayerMove = player.move();
                json.put("Row",(long)aPlayerMove.getRow());
                json.put("Column",(long)aPlayerMove.getColumn());
                json.put("Id",(long)aPlayerMove.getId());
                break;
            case JSONCommand.INVALIDATE:
                System.out.println("Player Invalidated "+player.getID());
                json.put("PlayerId",(long)player.getID());
        }
        return json;
    }


    @Override
    public String call() throws Exception {
        JSONprocess(json);
        return null;
    }
}


