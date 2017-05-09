package player;

/**
 * Created by sup33 on 3/13/2017.
 */
public class TicTacToe {
    private static int tableSize=3;
    private static String[][] board = new String[tableSize][tableSize];
     final  static String CROSS = "X";
     final  static String ROUND = "O";

    static{
        for(int iter=0;iter<tableSize;iter++){
            for(int jiter=0;jiter<tableSize;jiter++){
                board[iter][jiter]=" ";
            }
        }
    }



    public boolean isFree(PlayerMove aPlayerMove){
        if (board[aPlayerMove.getRow()][aPlayerMove.getColumn()].equals(" ")) {
            return true;
        }
        return false;
    }

    public void lastMove(PlayerMove aPlayerMove) {
        board[aPlayerMove.getRow()][aPlayerMove.getColumn()] = aPlayerMove.getMark();

    }

    public void setBoard(int row, int column, String mark){
        board[row][column]=mark;
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

}


