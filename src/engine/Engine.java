package engine;


import ServerPlayer.ServerPlayerProxy;
import logger.Logger;
import model.Model;
import model.TicTacToe;
import org.json.simple.JSONObject;
import proxyserver.ProxyGameServer;
import ServerPlayer.PlayerMove;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by sup33 on 3/13/2017.
 */
public class Engine implements Runnable{
    private static int gameId =1000;
    private ProxyGameServer aProxyGameServer;
    private Model aModel;
    private ArrayList<PlayerInfo> player = new ArrayList<>();
    private ServerPlayerProxy serverplayer;
    private Logger aLogger;
    private PlayerMove aLastMove;
    private Socket client;
    private String gameStatus;



    public Engine(){
        aModel = new Model();
        aLogger = new Logger();
        aProxyGameServer = new ProxyGameServer();
        player.add(new PlayerInfo(TicTacToe.CROSS));
        player.get(0).setIsLocal(false);
        player.add(new PlayerInfo(TicTacToe.ROUND));
    }

    public String getMark(int id){
        for(int iter=0;iter<player.size();iter++){
            if(player.get(iter).getId()== id){
                return player.get(iter).getMark();
            }
        }
        return null;
    }
    public static void main(String[] str){

        Engine aEngine = new Engine();
        aEngine.start();
    }
    public void start(){
        while(true) {
            try {
                System.out.println("Waiting to accept the player request");
                client = aProxyGameServer.getServerSock().accept();
                Engine temp = new Engine();
                temp.client = client;
                Thread newGame = new Thread(temp);
                System.out.println("Request accepted:" + temp.client);
                newGame.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestMove(PlayerInfo player){
        JSONObject request = StringtoJSON(JSONCommand.REQUEST,player);
        aProxyGameServer.send(request,player);
    }

    public void log(String data){
        aLogger.write(data);
    }

    public void send(String command, PlayerInfo player){
       JSONObject json = StringtoJSON(command,player);
       aProxyGameServer.send(json,player);
    }

    @Override
    public void run() {
        System.out.println("In run"+client);
        gameId++;
        aProxyGameServer.init(client);
        send(JSONCommand.CONFIRM,player.get(0));
        JSONObject jsonInit = aProxyGameServer.receive();
        JSONprocess(jsonInit);
        boolean gameOver = false;
        int index=0;
        PlayerInfo prevPlayer=player.get(0);
        //PlayerMove aPlayerMove;
        do {
            PlayerInfo currentPlayer = player.get(index%player.size());
            System.out.println("Current Player::" + currentPlayer);
            requestMove(currentPlayer);
            if (!currentPlayer.getIsLocal()) {
                JSONObject json = aProxyGameServer.receive();
                JSONprocess(json);
            }
            if (aModel.isDraw() || aModel.isWin(aLastMove)) {
                System.out.println("Game over");
                gameOver = true;
            }
            index+=1;
        }while(!gameOver && player.size()>0);
        System.out.println("Game over !!");
    }



    public String JSONprocess(JSONObject aJSONObject) {
        System.out.println("received"+aJSONObject.toJSONString());
        String state = (String) aJSONObject.get("JSONCommand");
        switch (state){
            case JSONCommand.INITIALIZE:
                String remote = (String) aJSONObject.get("Server");
                int player2 = (int) (long)aJSONObject.get("Player2");
                //if (remote.toLowerCase().equals("no")) {
                if(player2<500){
                    player.get(0).setId((int)(long) aJSONObject.get("Player1"));
                //    player.get(1).id = (int) (long)aJSONObject.get("Player2");
                    player.get(1).setId(player2);
                    player.get(0).setIsLocal(false);
                    player.get(1).setIsLocal(false);
                    System.out.println("We got player" + player.get(0) + " and player" + player.get(1));
                }
                else{
                    player.get(0).setId((int)(long) aJSONObject.get("Player1"));
                    player.get(0).setIsLocal(false);
                    serverplayer = new ServerPlayerProxy(this);
                    aProxyGameServer.init(serverplayer);
                    player.get(1).setId(player2);
                    player.get(1).setIsLocal(true);
            }
                break;
            case JSONCommand.MOVE:
                int id =(int)(long)aJSONObject.get("Id");
                PlayerMove aPlayerMove = new PlayerMove((int)(long)aJSONObject.get("Row"),
                        (int)(long) aJSONObject.get("Column"),id );
                aLastMove = aPlayerMove;
                if (aModel.checkIfMoveValid(aPlayerMove)) {
                    aModel.updateBoard(aPlayerMove);
                    for(PlayerInfo iter:player) {
                        JSONObject json=StringtoJSON("LastMove", iter);
                        aProxyGameServer.send(json,iter);
                    }
                }
                else{
                    System.out.println("Player Invalidated "+aLastMove.getId());
                    for(PlayerInfo iter:player) {
                        JSONObject json=StringtoJSON("Invalid",iter);
                        aProxyGameServer.send(json,iter);

                    }
                }
                System.out.println(aModel);
                break;
            case JSONCommand.INVALIDATE:
                id =(int)(long)aJSONObject.get("PlayerId");
                removePlayer(id);
                break;

        }
        return null;
    }

    public void removePlayer(int id){
        for(int iter=0;iter<player.size();iter++){
            if(player.get(iter).getId() ==id) {
                player.remove(iter);
                return;
            }
        }
    }

    public JSONObject StringtoJSON(String state, PlayerInfo player) {

        JSONObject json = new JSONObject();
        json.put("JSONCommand",state);
        switch (state){
            case JSONCommand.INVALIDATE:
                    json.put("PlayerId",aLastMove.getId());
                    removePlayer(aLastMove.getId());
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




}


