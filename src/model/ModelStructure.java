package model;


/**
 * This interface is for the structure of the model
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */

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

}
