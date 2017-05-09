package ServerPlayer;

/**
 * Created by sup33 on 3/13/2017.
 */
public class PlayerMove {
    private int row;
    private int column;
    private String mark;
    private int playerId;

    public PlayerMove(int row, int column, String mark,int playerId){
        this.row = row;
        this.column = column;
        this.mark = mark;
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

    public String getMark(){
        return mark;
    }

    public String toString(){
        return " id"+playerId+" row"+row+" column"+column;
    }

}

