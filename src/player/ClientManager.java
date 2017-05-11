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
import java.util.concurrent.*;
import java.util.Random;


/**
 * This class is intermediatory between the server and a player.
 * It connects to the server and gets the messages then it invokes the player methods based on the
 * JSON commands. It uses the network communication class for the low level socket communication.
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */
public class ClientManager implements Callable<String> {
    ClientProxy aNetworkCommunication;
    TicTacToe aTicTactoe;
    Socket client;
    PlayerStructure[] player; //List of all players
    int gameId;
    static Random aRandom;
    JSONObject json;
    PlayerStructure current;

    public ClientManager(){
        aTicTactoe = new TicTacToe();
    }
    public ClientManager(PlayerStructure[] player){
        this.player = player;
        aNetworkCommunication = new ClientProxy(this);
        aTicTactoe=new TicTacToe();

    }

    public static void main(String[] str){
        ClientManager temp = new ClientManager();
        System.out.println(str[0]+" "+str[1]);
        temp.start(str[0],str[1]);


    }

    public  void start(String class1, String class2)  {
        aRandom = new Random();
        PlayerStructure player1=null,player2=null;
        int p1=aRandom.nextInt(300);
        int p2=aRandom.nextInt(300);
        try {
            Object object1 = Class.forName(class1).getConstructor().newInstance();
            player1 = (PlayerStructure) object1;
            player1.init(p1,3,TicTacToe.CROSS,p2);
            Object object2 = Class.forName(class2).getConstructor().newInstance();
            player2 = (PlayerStructure) object2;
            player2.init(p2,3,TicTacToe.ROUND,p1);
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

        //PlayerA player1 = new PlayerA( aRandom.nextInt(300),3,TicTacToe.CROSS);
        //PlayerA player2 = new PlayerA(999,3,TicTacToe.ROUND);
        PlayerStructure[] player={player1,player2};
        ClientManager aClientPlayer = new ClientManager(player);
        FutureTask<String> futureTask1 = new FutureTask<>(aClientPlayer);
        ExecutorService executor = Executors.newFixedThreadPool(1);

        try {
            aClientPlayer.json=aClientPlayer.aNetworkCommunication.receive(aClientPlayer.client);
            executor.execute(futureTask1);
            futureTask1.get(200L, TimeUnit.MILLISECONDS);

            aClientPlayer.aNetworkCommunication.sendHelper(aClientPlayer.client,JSONCommand.INITIALIZE,null,player);

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
                            aClientPlayer.current,null);
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

    private PlayerStructure createObject(String className,int id,String mark) {
        Class<?> clazz = null;
        PlayerStructure temp=null;
        try {
            clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor(Integer.class,Integer.class,String.class);
            Object instance = constructor.newInstance(id,3,mark);
            temp = (PlayerStructure) instance;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return temp;
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
                aNetworkCommunication.sendHelper(client,"Move", aplayer,player);
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
    public String displayBoard(PlayerStructure[] player){
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
    private String[][] convertBoard(PlayerStructure[] player) {
        int tableSize = player[0].getTableSize();
        String[][] boardMark = new String[tableSize][tableSize];
        for (int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++){
                boardMark[iter][jiter]=" ";
                for (PlayerStructure aplayer :player) {
                    if (aTicTactoe.getBoard(iter,jiter) == aplayer.getID())
                        boardMark[iter][jiter] = aplayer.getMark();
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
            PlayerStructure[] player = new PlayerStructure[2];
            while ((next=br.readLine())!=null){
                String[] stringList = next.split(" ");
                JSONParser parse = new JSONParser();
                JSONObject json = (JSONObject) parse.parse(stringList[1]);
                String command = (String)json.get("JSONCommand");
                if (command.equals("Initialize")){
                    int player2 = (int) (long)json.get("Player2");
                    int player1 = (int)(long) json.get("Player1");
                    PlayerStructure p1 = new PlayerA();
                    p1.init(player1,3,TicTacToe.CROSS,player2);
                    PlayerStructure p2 = new PlayerA();
                    p2.init(player2,3, model.TicTacToe.ROUND,player1);
                    System.out.println(p1+" "+p2);
                    player[0]=p1;player[1]=p2;
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


