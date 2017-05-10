package ServerPlayer;


import model.TicTacToe;

import java.util.Random;


public class PlayerA implements PlayerStructure {
    private int playerId;
    private String  mark;
    private TicTacToe aTicTactoe;
    private static int tableSize;
    private int opponent;





    public  PlayerA(int playerId, int tableSize, String mark) {
        this.playerId = playerId;
        this.mark = mark;
        PlayerA.tableSize = tableSize;
        aTicTactoe = new TicTacToe();
        //System.out.println(this);
    }


    @Override
    public void init(int playerId, int tableSize, String mark) {

    }




    @Override
    public void lastMove(PlayerMove aPlayerMove) {
        aTicTactoe.setBoard(aPlayerMove.getRow(),aPlayerMove.getColumn(),aPlayerMove.getId());
        //System.out.println(this);

    }

    @Override
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
    public void replay(int primaryKey) {

    }

    public String toString(){

        return aTicTactoe.toString();
    }



    public boolean equal(PlayerA aPlayerA){
        return false;
    }



}






