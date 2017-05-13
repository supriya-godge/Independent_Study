package player;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sun.net.ConnectionResetException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.Random;


/**
 * This class is intermediatory between the server and a player.
 * It connects to the server and gets the messages then it invokes the player methods based on the
 * JSON commands. It uses the network communication class for the low level socket communication.
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Heliotis 
 */
public class ClientManager implements Callable<String> {
    ClientProxy aNetworkCommunication;
    TicTacToe aTicTactoe;
    Socket client;
    ArrayList<PlayerStructure> player; //List of all players
    int gameId;
    static Random aRandom;
    JSONObject json;
    PlayerStructure current;
    ArrayList<Integer> serverPlayer;

    public ClientManager(){
        aTicTactoe = new TicTacToe();
    }

    public ClientManager(ArrayList<PlayerStructure> player){
        this.player = player;
        aNetworkCommunication = new ClientProxy(this);
        aTicTactoe=new TicTacToe();

    }

    public static void main(String[] str){
        ClientManager temp = new ClientManager();
        ArrayList<String[]> players = new ArrayList<>();
        String tutple[] =temp.readFile(str[0],players);
        for(String[] item :players)
            System.out.println(item[0]);
        temp.start(players);


    }

    private String[] readFile(String fileName,ArrayList<String[]> player) {
        String line = null,dim =null;
        try (FileReader fr = new FileReader(fileName)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                while ((line = br.readLine()) != null) {
                    if (line.length() >1) {
                        if (line.substring(0, 1).equals("#"))
                            continue;
                        String[] words = line.split(" ");
                        if (words[0].equals("PLAYER")) {
                            String[] innerList = {words[1],words[2],words[3]};
                            player.add(innerList);
                        }
                        if (words[0].equals("DIM"))
                            dim = words[1];
                    }

                }

            }
        } catch (FileNotFoundException e) {
            System.err.print("ERROR:FILE NOT FOUND"+fileName);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] tuple = {dim};
        return tuple;
    }


    public  void start(ArrayList<String[]> playerList)  {
        aRandom = new Random();
        createObject(playerList);
        ClientManager aClientPlayer = new ClientManager(player);
        FutureTask<String> futureTask1 = new FutureTask<>(aClientPlayer);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        try {
            aClientPlayer.json=aClientPlayer.aNetworkCommunication.receive(aClientPlayer.client);
            executor.execute(futureTask1);
            futureTask1.get(200L, TimeUnit.MILLISECONDS);
            aClientPlayer.aNetworkCommunication.sendHelper(aClientPlayer.client,JSONCommand.INITIALIZE,null,
                    player,serverPlayer);
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
                    JSONObject json=aClientPlayer.aNetworkCommunication.StringtoJSON(JSONCommand.INVALIDATE,
                            aClientPlayer.current,null,serverPlayer);
                    aClientPlayer.aNetworkCommunication.send(aClientPlayer.client,json);
                }
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
        }

    }

    private void createObject(ArrayList<String[]> playerList) {
        PlayerStructure player1=null;
        serverPlayer = new ArrayList<>();
        int index=0;
        player = new ArrayList<>();
        ArrayList<Integer> opp = new ArrayList<>();
        for(String[] item : playerList) {
            opp.add(Integer.parseInt(item[1]));
        }
        for(String[] item : playerList) {
            try {
                if (!item[0].toLowerCase().contains("server")) {
                    Object object1 = Class.forName(item[0]).getConstructor().newInstance();
                    player1 = (PlayerStructure) object1;
                    int val = opp.remove(index);
                    player1.init(Integer.parseInt(item[1]), 3, item[2], opp);
                    opp.add(val);
                    player.add(player1);
                } else {
                    serverPlayer.add(Integer.parseInt(item[1]));
                }
                index++;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }


    }

    /* It returns the requested player from the list of player given the player id*/
    public PlayerStructure getPlayer(int id){
        for (PlayerStructure aplayer:player) {
            if (aplayer.getID() == id)
                return aplayer;
        }
        return null;
    }


    /* This method processes the "Invalid" json message*/
    public void processInvalidate(int id,int invalidId) {
        PlayerStructure aPlayerStructure = getPlayer(id);
        aPlayerStructure.playerInvalidated(invalidId);
    }

    /* This method processes the "LastMove" json message*/
    public void processLastMove(PlayerMove aPlayerMove, int sendTo){
        int id = aPlayerMove.getId();
        PlayerStructure aPlayerStructure = getPlayer(id);
        current = aPlayerStructure;
        int sendToId = sendTo;
        aPlayerStructure = getPlayer(sendToId);
        aPlayerStructure.lastMove(aPlayerMove);
        lastMove(aPlayerMove);
        System.out.println(aPlayerStructure);
        System.out.println(displayBoard(player));
    }

    /* This method processes the "request" json message*/
    public void processRequest(int playerId){
        for(PlayerStructure aplayer:player) {
            if (aplayer.getID() == playerId) {
                current = aplayer;
                aNetworkCommunication.sendHelper(client,"Move", aplayer,player,null);
            }
        }
    }

    /* This method processes the "Confirm" json message*/
    public void processConfirm(int gId){
        gameId=gId;
    }




    @Override
    /*Inbuilt mathod, it is called by the executor, if JSON is not Process
    * in a given time then timeout error is going to get thrown*/
    public String call() throws Exception {
        aNetworkCommunication.JSONprocess(json);
        return null;
    }

    /* This method prints the game board after converting it into from the player Id to
    * plaer piece*/
    public String displayBoard(ArrayList<PlayerStructure> player){
        String boardMark[][]=convertBoard(player);
        String printable = "  "+boardMark[0][0]+"   |  "+ boardMark[0][1]+"    |  "+boardMark[0][2]+"   \n"+
                "______|_______|______\n"+
                "      |       |     \n"+
                "  "+boardMark[1][0]+"   |  "+ boardMark[1][1]+"    |  "+boardMark[1][2]+"   \n"+
                "______|_______|______\n"+
                "      |       |     \n"+
                "  "+boardMark[2][0]+"   |  "+ boardMark[2][1]+"    |  "+boardMark[2][2]+"   \n";

        return printable;
    }

    /*This method updates the board based on the lastmove*/
    public void lastMove(PlayerMove aPlayerMove) {
        aTicTactoe.setBoard(aPlayerMove.getRow(),aPlayerMove.getColumn(),aPlayerMove.getId());
    }

    /* This method converts the game board from the player Id to Plaer piece*/
    private String[][] convertBoard(ArrayList<PlayerStructure> player) {
        int tableSize = 3;
        String[][] boardMark = new String[tableSize][tableSize];
        for (int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++){
                boardMark[iter][jiter]=" ";
                for (PlayerStructure aplayer :player) {
                    if (aTicTactoe.getBoard(iter,jiter) == aplayer.getID())
                        boardMark[iter][jiter] = aplayer.getMark();
                    if (serverPlayer!=null && serverPlayer.size()>0 &&
                            aTicTactoe.getBoard(iter,jiter)== serverPlayer.get(0))
                        boardMark[iter][jiter] = "O";
                }
            }
        }
        return boardMark;
    }




    /* This function provides a functionality to replay the already played game with the help of
    game id*/
    public  void replay(int primaryKey) {
        try {
            FileReader fr = new FileReader("GameLog"+primaryKey+".txt");
            BufferedReader br = new BufferedReader(fr);
            String next;
            int i=0;
            ArrayList<PlayerStructure> player = new ArrayList<>();
            while ((next=br.readLine())!=null){
                String[] stringList = next.split(" ");
                JSONParser parse = new JSONParser();
                JSONObject json = (JSONObject) parse.parse(stringList[1]);
                String command = (String)json.get("JSONCommand");
                if (command.equals("Initialize")){
                    int player2 = (int) (long)json.get("Player2");
                    int player1 = (int)(long) json.get("Player1");
                    PlayerStructure p1 = new PlayerA();
                    ArrayList<Integer> temp = new ArrayList<>();
                    temp.add(player2);
                    p1.init(player1,3,TicTacToe.CROSS,temp);
                    PlayerStructure p2 = new PlayerA();
                    ArrayList<Integer> temp1 = new ArrayList<>();
                    temp.add(player1);
                    p2.init(player2,3, model.TicTacToe.ROUND,temp1);
                    System.out.println(p1+" "+p2);
                    player.add(p1);player.add(p2);
                }
                if (command.equals("LastMove")) {
                    if(i%2==0) {
                        int col = (int) (long) json.get("Column");
                        int row = (int) (long) json.get("Row");
                        int id = (int) (long) json.get("Id");
                        PlayerMove aPlayerMove = new PlayerMove(row, col, id);
                        lastMove(aPlayerMove);
                        Thread.sleep(1000);
                        System.out.println(displayBoard(player));

                    }
                    i++;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}


