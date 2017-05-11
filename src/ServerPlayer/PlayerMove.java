package ServerPlayer;

/**
 * This is a player move class it stores the moves of the players
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
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

