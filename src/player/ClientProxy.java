package player;

import org.json.simple.JSONObject;
import sun.net.ConnectionResetException;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;
import java.util.Random;

/**
 * Created by sup33 on 3/21/2017.
 */
public class ClientProxy implements Callable<String> {
    NetworkCommunication aNetworkCommunication;
    Socket client;
    PlayerStructure player1;
    PlayerStructure player2;
    int gameId;
    static Random aRandom;
    JSONObject json;
    PlayerStructure current;

    public ClientProxy(PlayerStructure player1, PlayerStructure player2){
        this.player1 = player1;
        this.player2 = player2;

             aNetworkCommunication = new NetworkCommunication();

    }

    public static void main(String[] str){
        aRandom= new Random();
        PlayerA player1 = new PlayerA( aRandom.nextInt(300),3,TicTacToe.CROSS);
        PlayerA player2 = new PlayerA(999,3,TicTacToe.ROUND);
        ClientProxy aClientPlayer = new ClientProxy(player1,player2);
        FutureTask<String> futureTask1 = new FutureTask<>(aClientPlayer);
        ExecutorService executor = Executors.newFixedThreadPool(1);

        try {
            aClientPlayer.json=aClientPlayer.aNetworkCommunication.receive(aClientPlayer.client);
            executor.execute(futureTask1);
            futureTask1.get(200L, TimeUnit.MILLISECONDS);

            aClientPlayer.send(JSONCommand.INITIALIZE,null);

                do{
                    try {
                        aClientPlayer.json = aClientPlayer.aNetworkCommunication.receive(aClientPlayer.client);
                        //executor.execute(futureTask1);
                        futureTask1 = new FutureTask<>(aClientPlayer);
                        executor = Executors.newFixedThreadPool(1);
                        executor.execute(futureTask1);
                        futureTask1.get(20000L, TimeUnit.MILLISECONDS);
                    }catch (TimeoutException e) {
                        System.out.println("Timeout Exception");
                        JSONObject json=aClientPlayer.StringtoJSON(JSONCommand.INVALIDATE,aClientPlayer.current);
                        aClientPlayer.aNetworkCommunication.send(aClientPlayer.client,json);
                    }
                    //System.exit(0);
                //aClientPlayer.JSONprocess(json);
                }while(true);

            }catch (ConnectionResetException e){
                System.exit(0);
            } catch (SocketTimeoutException e){
                System.exit(0);
            }catch (IOException e) {
                System.exit(0);
            } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
               // System.exit(0);
            }


    }


    public void send(String state, PlayerStructure player){
        JSONObject aJSONObject = StringtoJSON(state,player);
        aNetworkCommunication.send(client,aJSONObject);

    }

    public PlayerStructure getPlayer(int id){
        if (player1.getID() == id)
            return player1;
        return player2;
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
                int id = (int)(long)aJSONObject.get("Id");
                PlayerStructure aPlayerStructure = getPlayer(id);
                current = aPlayerStructure;
                PlayerMove aPlayerMove = new PlayerMove((int)(long)aJSONObject.get("Row"),
                        (int)(long) aJSONObject.get("Column"),aPlayerStructure.getMark (),
                        (int)(long)aJSONObject.get("Id"));
                id = (int)(long)aJSONObject.get("SendToId");
                aPlayerStructure = getPlayer(id);
                aPlayerStructure.lastMove(aPlayerMove);
                System.out.println(aPlayerStructure);
                break;
            case JSONCommand.REQUEST:

                if (player1.getID()==((int)(long) aJSONObject.get("PlayerId"))) {
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
        System.out.println("Inside the string to json"+state);
        JSONObject json = new JSONObject();
        json.put("JSONCommand",state);
        switch (state){
            case JSONCommand.INITIALIZE:
                //json.put("Server","Yes");
                json.put("Player1",player1.getID());
                json.put("Player2",player2.getID());
                break;
            case JSONCommand.MOVE:
                System.out.println(player.getID());
                PlayerMove aPlayerMove = player.move();
                json.put("Row",aPlayerMove.getRow());
                json.put("Column",aPlayerMove.getColumn());
                json.put("Id",aPlayerMove.getId());
                break;
            case JSONCommand.INVALIDATE:
                System.out.println("Player Invalidated "+player.getID());
                json.put("PlayerId",player.getID());
        }
        return json;
    }


    @Override
    public String call() throws Exception {
        JSONprocess(json);
        return null;
    }
}


