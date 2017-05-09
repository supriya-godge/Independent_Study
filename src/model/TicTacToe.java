package model;
import ServerPlayer.PlayerMove;

import java.util.ArrayList;

/**
 * Created by sup33 on 3/13/2017.
 */
public class TicTacToe {
    private  int tableSize=3;
    private  String[][] board = new String[tableSize][tableSize];
    public final  static String CROSS = "X";
    public final  static String ROUND = "O";

    public TicTacToe(){
        for(int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++){
                board[iter][jiter]=" ";
            }
        }
    }

    public int getTableSize(){
        return tableSize;
    }

    public boolean isFree(PlayerMove aPlayerMove){
        if (board[aPlayerMove.getRow()][aPlayerMove.getColumn()].equals(" ")) {
            return true;
        }
        //System.out.println("not free"+board[aPlayerMove.getRow()][aPlayerMove.getColumn()]+"::");
        return false;
    }

    public boolean isAnyThigFree(){
        for(int iter=0;iter<board.length;iter++) {
            for(int jiter=0;jiter<board.length;jiter++) {

                if (board[iter][jiter].equals(" ")) {
                    return true;
                }
            }
        }
        //System.out.println("not free"+board[aPlayerMove.getRow()][aPlayerMove.getColumn()]+"::");
        return false;
    }



    public void lastMove(PlayerMove aPlayerMove) {
        board[aPlayerMove.getRow()][aPlayerMove.getColumn()] = aPlayerMove.getMark();

    }

    public void setBoard(int row, int column, String mark){
        board[row][column]=mark;
    }

    public String getBoard(int row, int column){
        return board[row][column];
    }

    public String toString(){
        String printable = "  "+board[0][0]+"   |  "+ board[0][1]+"    |  "+board[0][2]+"   \n"+
                "______|_______|______\n"+
                "      |       |     \n"+
                "  "+board[1][0]+"   |  "+ board[1][1]+"    |  "+board[1][2]+"   \n"+
                "______|_______|______\n"+
                "      |       |     \n"+
                "  "+board[2][0]+"   |  "+ board[2][1]+"    |  "+board[2][2]+"   \n";

        return printable;
    }


    public ArrayList<PlayerMove> getAllFreeSpaces() {
        ArrayList<PlayerMove> successors = new ArrayList<>();
        for(int iter=0;iter<board.length;iter++) {
            for(int jiter=0;jiter<board.length;jiter++) {
                if (board[iter][jiter].equals(" ")) {
                successors.add(new PlayerMove(iter,jiter,null,0));
                }
            }
        }
        return successors;

    }

    public void removePiece(int row, int column) {
        board[row][column]=" ";
    }
}


