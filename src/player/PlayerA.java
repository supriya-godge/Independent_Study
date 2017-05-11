package player;


/**
 * This is a player class, it is responsible for making moves
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import  java.util.Random;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class PlayerA implements PlayerStructure{
    private int playerId;
    private String  mark;
    private TicTacToe aTicTactoe;
    private static int tableSize=3;
    private int opponent;

    public  int getTableSize() {
        return tableSize;
    }

    public TicTacToe getaTicTactoe() {
        return aTicTactoe;
    }

    public  void setTableSize(int tableSize) {
        PlayerA.tableSize = tableSize;
    }

    public PlayerA(){

    }

    public  void init(int playerId, int tableSize, String mark, int opp) {
        this.playerId = playerId;
        this.mark = mark;
        PlayerA.tableSize = tableSize;
        aTicTactoe = new TicTacToe();
        this.opponent = opp;
    }




    @Override
    /*This method updates the board based on the lastmove*/
    public void lastMove(PlayerMove aPlayerMove) {
        aTicTactoe.setBoard(aPlayerMove.getRow(),aPlayerMove.getColumn(),aPlayerMove.getId());
    }

    @Override
    /*This method tries to find the best move to play*/
    public PlayerMove move(){
        Random rand = new Random();
        PlayerMove aPlayerMove=null;
        boolean found = false;
        while(!found){
            aPlayerMove = new PlayerMove(rand.nextInt(3),rand.nextInt(3),playerId);
            if (aTicTactoe.isFree(aPlayerMove)) {
                found = true;
            }
        }
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  aPlayerMove;
    }

    @Override
    public int getID() {
        return playerId;
    }


    public String getMark() {
        return mark;
    }

    @Override
    public void playerInvalidated(int playerId) {

    }

    @Override
    public void displayBoard() {

    }

    @Override
    /* This function provides a functionality to replay the already played game with the help of
    game id*/
    public void replay(int primaryKey) {
        try {
            FileReader fr = new FileReader("GameLog"+primaryKey+".txt");
            BufferedReader br = new BufferedReader(fr);
            String next;
            int i=0;
            while ((next=br.readLine())!=null){
                String[] stringList = next.split(" ");
                JSONParser parse = new JSONParser();
                JSONObject json = (JSONObject) parse.parse(stringList[1]);
                String command = (String)json.get("JSONCommand");
                if (command.equals("LastMove")) {
                    if(i%2==0) {
                        int col = (int) (long) json.get("Column");
                        int row = (int) (long) json.get("Row");
                        int id = (int) (long) json.get("Id");
                        PlayerMove aPlayerMove = new PlayerMove(row, col, id);
                        lastMove(aPlayerMove);
                        Thread.sleep(1000);
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

    public String toString(){

        return "Id:"+playerId+" mark"+mark;
    }



    public boolean equal(PlayerA aPlayerA){
        return false;
    }


    public String toBoard(){
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
        String[][] boardMark = new String[tableSize][tableSize];
        for (int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++) {
                boardMark[iter][jiter] = " ";
                if (aTicTactoe.getBoard(iter, jiter) == playerId) {
                    boardMark[iter][jiter] = mark;
                }

                if (aTicTactoe.getBoard(iter, jiter) == opponent) {
                    boardMark[iter][jiter] = "S";
                }
            }

        }
        return boardMark;
    }
/*
    public static void main(String[] str){
        PlayerA aP = new PlayerA(283,3,"X",999);
        aP.replay(1003);

    }
    */


}






