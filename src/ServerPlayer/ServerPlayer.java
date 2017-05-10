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
    private int opponent;

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
        aTicTacToe.setBoard(aPlayerMove.getRow(),aPlayerMove.getColumn(),aPlayerMove.getId());
        
        //System.out.println(this);

    }

    @Override
    public PlayerMove move(){
        Random rand = new Random();
        PlayerMove me=new PlayerMove(0,0,playerId);
        PlayerMove opp = new PlayerMove(0,0,opponent);
        int[] result;
        result=maxD(0,me,opp);
        int r=result[1];
        int c=result[2];
        me=new PlayerMove(r,c,playerId);
        return  me;
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

    public boolean isWin(PlayerMove aPlayerMove ) {
        int id=aPlayerMove.getId();
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++) {
            boolean ans = checkrow(iter, id);
            if (ans) {
                //System.out.println(ans);
                return true;
            }
        }
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            boolean ans = checkcolumn(iter, id);

            if (ans) {
                //System.out.println("winner is  "+id);
                return true;
            }
        }
        boolean win = true;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            if (aTicTacToe.getBoard(iter,iter)!= 0 && id==aTicTacToe.getBoard(iter,iter)){
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
            if (aTicTacToe.getBoard(iter,jiter)!= 0 && id==aTicTacToe.getBoard(iter,jiter)){
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

    public boolean checkrow(int row, int id){
        boolean win = true;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){

            if (aTicTacToe.getBoard(iter,row)!= 0 && id==aTicTacToe.getBoard(iter,row)){
                continue;
            }
            else{
                win=false;
                break;
            }
        }
        return win;
    }

    private boolean checkcolumn(int row, int id) {
        boolean win = true;
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){

            if (aTicTacToe.getBoard(row,iter)!= 0&& id==aTicTacToe.getBoard(row, iter)){
                continue;
            }
            else{
                win=false;
                break;
            }
        }
        return win;

    }


    public int[] maxD(int count, PlayerMove  piece, PlayerMove opp) {
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
            aTicTacToe.setBoard(tern.getRow(), tern.getColumn(), piece.getId());
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

    public int[] minD(int count, PlayerMove piece,PlayerMove opp ){
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
        aTicTacToe.setBoard(tern.getRow(),tern.getColumn(),opp.getId());
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
        PlayerMove me=null;
        PlayerMove opp=null;
        while(!aServerPlayer.isWin(me) && !aServerPlayer.isWin(opp)){
            System.out.println("Enter row and column");
            int r = scan.nextInt();
            int c = scan.nextInt();
            aServerPlayer.aTicTacToe.setBoard(r,c,30);
            System.out.println(aServerPlayer);
            int[] result=aServerPlayer.maxD(0,me,opp);
            r=result[1];
            c=result[2];
            aServerPlayer.aTicTacToe.setBoard(r,c,aServerPlayer.getID());
            System.out.println("Selected:"+r+" "+c);
            System.out.println(aServerPlayer);
        }

   }
}
