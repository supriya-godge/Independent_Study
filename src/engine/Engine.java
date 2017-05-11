/*
This is a Engine class which is responsible for the controlling the game.
Auther: Supriya Godge
        Sean Srout
        James Helliotis

In detail:
The engine opens a server socket and creates a seperate thread for new client player
When a client player connects to the engine usnig TCP, it recevies back JSON message
{"JSONCommand":"Confirm","Status":"Successful","GameId":<Game ID>}
Server receives the initilization message with both player.
All the server player have id > 900
All other players have id <700
After the initialization, the game starts and server asks for the move to the player
until the game is draw or sombody won the game or there is no player left in the game.
 */

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
    public ProxyGameServer aProxyGameServer;
    private Model aModel;
    private ArrayList<PlayerInfo> player = new ArrayList<>();
    private ServerPlayerProxy serverplayer;
    private Logger aLogger;
    private PlayerMove aLastMove;
    private Socket client;
    private String gameStatus;



    public Engine(){
        gameId+=1;
        aModel = new Model();
        aLogger = new Logger(gameId);
        aProxyGameServer = new ProxyGameServer(this);
        //Add both the players in the player list
        player.add(new PlayerInfo(TicTacToe.CROSS));
        player.add(new PlayerInfo(TicTacToe.ROUND));
        player.get(0).setIsLocal(false); // First player is always guaranteed to be a remote player.
    }

    /* This method returns the mark/piece of the repective player   */
    public String getMark(int id){
        for(int iter=0;iter<player.size();iter++){
            if(player.get(iter).getId()== id){
                return player.get(iter).getMark();
            }
        }
        return null;
    }

    /* This is the main method, it starts the engine */
    public static void main(String[] str){

        Engine aEngine = new Engine();
        aEngine.startProcess();
    }


    /* In the Start process main-thread waits at the accept() statment for player proxy to connect
       after accepting the request it creates new thread and starts the game*/
    public void startProcess(){
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

    /* This method creates a JSON object for request move*/
    public void requestMove(PlayerInfo player){
        JSONObject request = aProxyGameServer.StringtoJSON(JSONCommand.REQUEST,player,aLastMove,gameId);
        send(request,player);

    }

    /*This method writes the string parameter in the log file*/
    public void log(String data){
        aLogger.write(data);
    }

    /*This method writes the sent JSON messages into the log file */
    public void send(JSONObject json, PlayerInfo player){
       log("Sent "+json.toJSONString());
       aProxyGameServer.send(json,player);
    }

    @Override
    /*This method handles the each game, by requesting the move from the client proxy
    * until one player wins or game is draw or there are no more players in the game (All players
    * got invalidated)*/
    public void run() {
        aProxyGameServer.init(client); //Initialize the input and output stream for network communication
        JSONObject json = aProxyGameServer.StringtoJSON(JSONCommand.CONFIRM,player.get(0),aLastMove,gameId);
        send(json,player.get(0)); //Send the confirmation for the connection was successful
        JSONObject jsonInit = aProxyGameServer.receive(); //Initilization message from the client proxy
        log("Received "+jsonInit.toJSONString());
        aProxyGameServer.JSONprocess(jsonInit);
        boolean gameOver = false;
        int index=0;
        do {
            PlayerInfo currentPlayer = player.get(index%player.size()); //Toggle between the players to request the move
            System.out.println("Current Player::" + currentPlayer);
            requestMove(currentPlayer);
            if (!currentPlayer.getIsLocal()) {
                json = aProxyGameServer.receive();
                log("Received "+json.toJSONString());
                aProxyGameServer.JSONprocess(json);
            }
            if (aModel.isDraw() || aModel.isWin(aLastMove)) {
                System.out.println("Game over");
                gameOver = true;
            }
            index+=1;
        }while(!gameOver && player.size()>0);
        System.out.println("Game over !!");
    }

    /* This method processes the Initialize request of the client proxy
        It initializes the player based on the remote and local status.
        If the Id of the player is <700 then the plyer is remote
        If the Id of the player is >900 then the player is local
        eg. {"JSONCommand":"Initialize","Player2":999,"Player1":283}
     */
    public void processInitialize(int player1, int player2){
        if(player2<500){
            player.get(0).setId(player1);
            player.get(1).setId(player2);
            player.get(0).setIsLocal(false);
            player.get(1).setIsLocal(false);
            System.out.println("We got player" + player.get(0) + " and player" + player.get(1));
        }
        else{
            player.get(0).setId(player1);
            player.get(0).setIsLocal(false);
            serverplayer = new ServerPlayerProxy(this,player.get(0).getId());
            aProxyGameServer.init(serverplayer);
            player.get(1).setId(player2);
            player.get(1).setIsLocal(true);
        }
    }

    /* When the move is rceived from the client proxy, first it is checked if the move is valid
     * if it is not valid then the player who made this move is invalidated
     * if it is valid     then the move is updated into the board
     * Eg.{"JSONCommand":"Move","Column":2,"Row":0,"Id":283}*/
    public void processMove(int id, PlayerMove aPlayerMove){
        aLastMove = aPlayerMove;
        if (aModel.checkIfMoveValid(aPlayerMove)) {
            aModel.updateBoard(aPlayerMove);
            for(PlayerInfo iter:player) {
                JSONObject json=aProxyGameServer.StringtoJSON("LastMove", iter,aLastMove,gameId);
                send(json,iter);
            }
        }
        else{
            System.out.println("Player Invalidated "+aLastMove.getId());
            for(PlayerInfo iter:player) {
                JSONObject json=aProxyGameServer.StringtoJSON("Invalid",iter,aLastMove,gameId);
                send(json,iter);

            }
        }
        System.out.println(aModel);
    }




    /*This method removes the invalidated players from the player pool*/
    public void removePlayer(int id){
        for(int iter=0;iter<player.size();iter++){
            if(player.get(iter).getId() ==id) {
                player.remove(iter);
                return;
            }
        }
    }




}


