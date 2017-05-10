package player;

/**
 * Created by sup33 on 3/13/2017.
 */
public class PlayerMove {
    private int row;
    private int column;
    private int playerId;

    public PlayerMove(int row, int column,int playerId){
        this.row = row;
        this.column = column;
        this.playerId = playerId;
    }

    public int getRow(){
        return row;
    }

    public int getId(){
        return playerId;
    }

    public int getColumn(){
        return column;
    }

    public String toString(){
        return " id"+playerId+" row"+row+" column"+column;
    }

}