package ServerPlayer;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import model.TicTacToe;

/**
 * Created by sup33 on 3/14/2017.
 */
public class ServerPlayer implements PlayerStructure {
    private int playerId;
    private String  mark;
    private TicTacToe aTicTacToe;
    private static int tableSize=3;

    public ServerPlayer(){

    }

    public ServerPlayer(int playerId, int tableSize, String mark) {
        this.playerId = playerId;
        this.mark = mark;
        this.tableSize = tableSize;
        this.aTicTacToe = new TicTacToe();
    }
    @Override
    public void init(int playerId, int tableSize, String mark) {
        this.playerId = playerId;
        this.mark = mark;
        this.tableSize = tableSize;
        this.aTicTacToe = new TicTacToe();
    }

    @Override
    public void lastMove(PlayerMove aPlayerMove) {
        aTicTacToe.setBoard(aPlayerMove.getRow(),aPlayerMove.getColumn(),aPlayerMove.getMark());
        
        //System.out.println(this);

    }

    @Override
    public PlayerMove move(){
        Random rand = new Random();
        PlayerMove aPlayerMove=null;
        int[] result;
        if (mark.equals("X"))
            result=maxD(0,mark,"O");
        else
            result=maxD(0,mark,"X");
        int r=result[1];
        int c=result[2];
        aPlayerMove=new PlayerMove(r,c,mark,playerId);
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
    public void replay(int primaryKey) {

    }

    public String toString(){

        return aTicTacToe.toString();
    }



    public boolean equal(PlayerA aPlayerA){
        return false;
    }

    public boolean isWin(String mark ) {
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++) {
            boolean ans = checkrow(iter, mark);
            if (ans) {
                System.out.println(ans);
                return ans;
            }
        }
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            boolean ans = checkcolumn(iter, mark);

            if (ans) {
                System.out.println("winner is  "+mark);
                return ans;
            }
        }
        boolean win = true;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            if (!aTicTacToe.getBoard(iter,iter).equals(" ") && mark.equals(aTicTacToe.getBoard(iter,iter))){
                continue;
            }
            else{
                win=false;
                break;
            }
        }
        if (win)
            return win;

        win = true;
        int jiter=aTicTacToe.getTableSize()-1;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            if (!aTicTacToe.getBoard(iter,jiter).equals(" ") && mark.equals(aTicTacToe.getBoard(iter,jiter))){
                jiter--;
                continue;
            }
            else{
                win=false;
                jiter--;
                break;
            }

        }
        if (win)
            return win;

        return false;
    }

    public boolean checkrow(int row, String mark){
        boolean win = true;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){

            if (!aTicTacToe.getBoard(iter,row).equals(" ") && mark.equals(aTicTacToe.getBoard(iter,row))){
                continue;
            }
            else{
                win=false;
                break;
            }
        }
        return win;
    }

    private boolean checkcolumn(int row, String mark) {
        boolean win = true;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){

            if (!aTicTacToe.getBoard(row,iter).equals(" ")&& mark.equals(aTicTacToe.getBoard(row, iter))){
                continue;
            }
            else{
                win=false;
                break;
            }
        }
        return win;

    }



    public int[] maxD(int count, String piece, String opp) {
        boolean end = false;
        int utility=0;
        if (isWin(piece)){
        utility = 1;
        end = true;
        }
        else if (isWin(opp)){
        utility = -1;
        end = true;
        }
        else if  (!aTicTacToe.isAnyThigFree()){
        utility = 0;
        end = true;
        }
        if (end) {
            int[] tuple = {utility,4, 4, count};
            return tuple ;
        }
        int value = -9999;
        int x = 0;
        int y = 0;
        ArrayList<PlayerMove> get_sucessor = aTicTacToe.getAllFreeSpaces();
        for (PlayerMove tern : get_sucessor){
            count += 1;
            aTicTacToe.setBoard(tern.getRow(), tern.getColumn(), piece);
            int[] returnVal = minD(count,piece,opp);
            int val1=returnVal[0]; int x1=returnVal[1]; int y1 = returnVal[2]; count = returnVal[3];
            if (value < val1){
            x = tern.getRow();
            y = tern.getColumn();
            value = val1;
            }
            aTicTacToe.removePiece(tern.getRow(), tern.getColumn());
          }
        int[] tuple = {value,x, y, count};
        return tuple;

    }

    public int[] minD(int count, String piece,String opp ){
    boolean end=false;
    int utility=0;
    if (isWin(piece)){
        utility = 1;
        end = true;
     }
    else if (isWin(opp)){
        utility = -1;
        end = true;
    }
    else if (!aTicTacToe.isAnyThigFree()){
        utility =0;
        end = true;
    }

    if (end){
        int[] tuple = {utility,4, 4, count};
        return tuple;
    }
    int value =9999;
    int x=0;
    int y=0;
    ArrayList<PlayerMove> get_sucessor = aTicTacToe.getAllFreeSpaces();
    for (PlayerMove tern : get_sucessor){
        count += 1;
        aTicTacToe.setBoard(tern.getRow(),tern.getColumn(),opp);
        int[]  returnVal= maxD(count,piece,opp);
        int val1=returnVal[0]; int x1=returnVal[1]; int y1 = returnVal[2]; count = returnVal[3];
            if (value > val1){
                x = tern.getRow();
                y = tern.getColumn();
                value =val1;
             }
            aTicTacToe.removePiece(tern.getRow(),tern.getColumn());
      }
        int[] tuple = {value,x, y, count};
      return tuple;
   }

   public static void main(String[] str){
        ServerPlayer aServerPlayer = new ServerPlayer();
       Scanner scan = new Scanner(System.in);
        aServerPlayer.init(3,123,"X");
        while(!aServerPlayer.isWin("O") && !aServerPlayer.isWin("X")){
            System.out.println("Enter row and column");
            int r = scan.nextInt();
            int c = scan.nextInt();
            aServerPlayer.aTicTacToe.setBoard(r,c,"O");
            System.out.println(aServerPlayer);
            int[] result=aServerPlayer.maxD(0,"X","O");
            r=result[1];
            c=result[2];
            aServerPlayer.aTicTacToe.setBoard(r,c,"X");
            System.out.println("Selected:"+r+" "+c);
            System.out.println(aServerPlayer);
        }

   }
}
