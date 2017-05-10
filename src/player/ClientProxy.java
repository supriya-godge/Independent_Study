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
    PlayerStructure[] player;
    int gameId;
    static Random aRandom;
    JSONObject json;
    PlayerStructure current;

    public ClientProxy(PlayerStructure[] player){
        this.player = player;

        aNetworkCommunication = new NetworkCommunication();

    }

    public static void main(String[] str){
        aRandom= new Random();
        PlayerA player1 = new PlayerA( aRandom.nextInt(300),3,TicTacToe.CROSS);
        PlayerA player2 = new PlayerA(999,3,TicTacToe.ROUND);
        PlayerStructure[] player={player1,player2};
        ClientProxy aClientPlayer = new ClientProxy(player);
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
        for (PlayerStructure aplayer:player) {
            if (aplayer.getID() == id)
                return aplayer;
        }
        return null;
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
                        (int)(long) aJSONObject.get("Column"),
                        (int)(long)aJSONObject.get("Id"));
                id = (int)(long)aJSONObject.get("SendToId");
                aPlayerStructure = getPlayer(id);
                aPlayerStructure.lastMove(aPlayerMove);
                System.out.println(aPlayerStructure);
                System.out.println(this);
                break;
            case JSONCommand.REQUEST:
                for(PlayerStructure aplayer:player) {
                    if (aplayer.getID() == ((int) (long) aJSONObject.get("PlayerId"))) {
                        current = aplayer;
                        send("Move", aplayer);
                    }
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
    public JSONObject StringtoJSON(String state, PlayerStructure aplayer) {
        JSONObject json = new JSONObject();
        json.put("JSONCommand",state);
        switch (state){
            case JSONCommand.INITIALIZE:
                //json.put("Server","Yes");
                json.put("Player1",player[0].getID());
                json.put("Player2",player[1].getID());
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


    @Override
    public String call() throws Exception {
        JSONprocess(json);
        return null;
    }

    public String toString(){
        String boardMark[][]=convertBoard();
        String printable = "  "+boardMark[0][0]+"   |  "+ boardMark[0][1]+"    |  "+boardMark[0][2]+"   \n"+
                "______|_______|______\n"+
                "      |       |     \n"+
                "  "+boardMark[1][0]+"   |  "+ boardMark[1][1]+"    |  "+boardMark[1][2]+"   \n"+
                "______|_______|______\n"+
                "      |       |     \n"+
                "  "+boardMark[2][0]+"   |  "+ boardMark[2][1]+"    |  "+boardMark[2][2]+"   \n";

        return printable;
    }

    private String[][] convertBoard() {
        int tableSize = player[0].getTableSize();
        String[][] boardMark = new String[tableSize][tableSize];
        for (int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++){
                boardMark[iter][jiter]=" ";
                for (PlayerStructure aplayer :player) {
                    if (player[0].getaTicTactoe().getBoard(iter,jiter) == aplayer.getID())
                        boardMark[iter][jiter] = aplayer.getMark();
                }
            }
        }
        return boardMark;
    }

}


