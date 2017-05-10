package player;

import java.util.ArrayList;

public class TicTacToe {
    private  int tableSize=3;
    private  int[][] board = new int[tableSize][tableSize];
    public final  static String CROSS = "X";
    public final  static String ROUND = "O";

    public TicTacToe(){
    }

    public int getTableSize(){
        return tableSize;
    }

    public boolean isFree(PlayerMove aPlayerMove){
        if (board[aPlayerMove.getRow()][aPlayerMove.getColumn()]==0) {
            return true;
        }
        //System.out.println("not free"+board[aPlayerMove.getRow()][aPlayerMove.getColumn()]+"::");
        return false;
    }

    public boolean isAnyThigFree(){
        for(int iter=0;iter<board.length;iter++) {
            for(int jiter=0;jiter<board.length;jiter++) {

                if (board[iter][jiter]== 0) {
                    return true;
                }
            }
        }
        //System.out.println("not free"+board[aPlayerMove.getRow()][aPlayerMove.getColumn()]+"::");
        return false;
    }



    public void lastMove(PlayerMove aPlayerMove) {
        board[aPlayerMove.getRow()][aPlayerMove.getColumn()] = aPlayerMove.getId();

    }

    public void setBoard(int row, int column, int id){
        board[row][column]=id;
    }

    public int getBoard(int row, int column){
        return board[row][column];
    }


    /*
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
        String[][] boardMark = new String[tableSize][tableSize];
        for (int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++){
                boardMark[iter][jiter]=" ";
                for (PlayerInfo player1 :player) {
                    if (board[iter][jiter] == player1.getId())
                        boardMark[iter][jiter] = player1.getMark();
                }
            }
        }
        return boardMark;
    }
    */




    public ArrayList<PlayerMove> getAllFreeSpaces() {
        ArrayList<PlayerMove> successors = new ArrayList<>();
        for(int iter=0;iter<board.length;iter++) {
            for(int jiter=0;jiter<board.length;jiter++) {
                if (board[iter][jiter]== 0) {
                    successors.add(new PlayerMove(iter,jiter,0));
                }
            }
        }
        return successors;

    }

    public void removePiece(int row, int column) {
        board[row][column]=0;
    }
}


