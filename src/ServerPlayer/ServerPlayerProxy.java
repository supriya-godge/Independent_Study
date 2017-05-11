package ServerPlayer;

import engine.Engine;
import engine.JSONCommand;
import model.TicTacToe;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;

/**
 * This is a server player proxy.
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */
public class ServerPlayerProxy implements Callable<String> {
    Socket client;
    PlayerStructure player1;
    int gameId;
    JSONObject json;
    PlayerStructure current;
    Engine aEngine;

    public ServerPlayerProxy(Engine con,int opp){
        this.player1 = new ServerPlayer();
        player1.init(999,3,TicTacToe.ROUND,opp);
        aEngine = con;
    }



    public void send(String state, PlayerStructure player){
        JSONObject aJSONObject = StringtoJSON(state,player);
        aEngine.aProxyGameServer.JSONprocess(aJSONObject);
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
                        (int) aJSONObject.get("Column"),
                        (int)aJSONObject.get("Id"));
                player1.lastMove(aPlayerMove);
                break;
            case JSONCommand.REQUEST:
                current = player1;
                send("Move", player1);
                break;

            case JSONCommand.CONFIRM:
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


