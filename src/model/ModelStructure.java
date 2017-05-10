package model;
import ServerPlayer.PlayerMove;


/**
 * Created by sup33 on 3/13/2017.
 */
public interface ModelStructure {
    /*
    This method checks if a player who played last has won or not.
     */
    boolean isWin(PlayerMove aPlayerMove);

    /*
    This method checks if the game is draw.
    */
    boolean isDraw();

    /*
    This method updates the board, for the given move
     */
    void updateBoard(PlayerMove aPlayerMove);
    /*
    This method checks if the given move is valid.
    Move is valid if  0 >= row < totalRow and 0 >= column < totalColumn
     */
    boolean checkIfMoveValid(PlayerMove aPlayerMove);
    /*
    This method writes each move to the database for the game.
     */
    void storeGame(PlayerMove aPlayerMove);

    /*
    This method returns all the moves played in the specific game.
    It takes the game id as an input and returns all the moves stored on that ID.
     */
    String returnMoves(int gameId);
}
