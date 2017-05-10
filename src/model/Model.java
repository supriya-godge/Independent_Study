package model;

import ServerPlayer.PlayerMove;

//import PlayerMove;

import org.json.simple.JSONObject;

/**
 * Created by sup33 on 3/13/2017.
 */
public class Model implements ModelStructure{
    private TicTacToe aTicTacToe;
    public Model(){
        aTicTacToe = new TicTacToe();
    }

    @Override
    public boolean isWin(PlayerMove aPlayerMove ) {
        int id=aPlayerMove.getId();
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++) {
            boolean ans = checkrow(iter, id);
            if (ans) {
                System.out.println(ans);
                return true;
            }
        }
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            boolean ans = checkcolumn(iter, id);

            if (ans) {
                System.out.println("winner is  "+id);
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

    @Override
    public boolean isDraw() {
        for(int iter=0;iter<aTicTacToe.getTableSize();iter++){
            for(int jiter=0;jiter<aTicTacToe.getTableSize();jiter++){
                if (aTicTacToe.getBoard(iter , jiter)== 0)
                    return false;
            }
        }
        return true;
    }

    @Override
    public void updateBoard(PlayerMove aPlayerMove) {
         if(checkIfMoveValid(aPlayerMove)){
            aTicTacToe.setBoard(aPlayerMove.getRow(),aPlayerMove.getColumn(), aPlayerMove.getId());
        }
    }

    @Override
    public boolean checkIfMoveValid(PlayerMove aPlayerMove) {
        if (aPlayerMove.getRow() < aTicTacToe.getTableSize() &&
                aPlayerMove.getRow() < aTicTacToe.getTableSize() &&
                aTicTacToe.getBoard(aPlayerMove.getRow(),aPlayerMove.getColumn())== 0)
            return true;
        return false;
    }

    @Override
    public void storeGame(PlayerMove aPlayerMove) {

    }

    @Override
    public String returnMoves(int gameId) {
        return null;
    }

    public String toString(){
        return aTicTacToe.toString();
    }



}
