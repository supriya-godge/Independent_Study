package player;

/**
 * Created by sup33 on 3/13/2017.
 */
public interface PlayerStructure {

    /*
    Init method initializes the player using the player id
     */
     void init(int playerId, int tableSize, String mark);

    /*
    This method is used to update the last move played in the
    game. So that player has the knowledge about the recent move played
    in the game and it can calculate its move.
     */
     void lastMove(PlayerMove m);

    /*
    This function decides the next move that player wants to play.
    Player has the information of the current state of the game board.
     */
     PlayerMove move();

     /*
     This metod returns the mark of the player
      */
     String getMark();
    int getID();

    int getTableSize();

    public TicTacToe getaTicTactoe();




    /*
    This method is used to imform the player when another player
    is invalidated by the game server. The player has to play the game
    till the completion even if player is the only player in the game.
     */
    void playerInvalidated(int playerId);



    /*
    This method diaplyes the current state of the gme board
     */
    void displayBoard();

    /*
    This method request the server to replay the game player by player
    with the id primaryKey
     */
    void replay(int primaryKey);

}
