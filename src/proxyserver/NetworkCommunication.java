package proxyserver;

import java.net.Socket;

import engine.PlayerInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * Created by sup33 on 3/13/2017.
 */
interface NetworkCommunication {

    /*
    This method implements the necessary code to send the
    data over the network using the socket.
     */
    boolean send(JSONObject data, PlayerInfo player);

    /*
    This method implements the necessary code to receive the
    data over the network using the socket.s
    */
    JSONObject receive();







}
