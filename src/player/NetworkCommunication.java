package player;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.INITIALIZE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by sup33 on 3/13/2017.
 */


    public class NetworkCommunication {
        private static Socket clientSock;
        private BufferedReader din;
        private PrintWriter out;



        public NetworkCommunication()  {
            try {
                System.out.println("Connecting to server");
                clientSock = new Socket("localhost", 9001);
                //clientSock.setSoTimeout(10000);
                out = new PrintWriter(clientSock.getOutputStream(), true);
                din = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
        This method implements the necessary code to send the
        data over the network using the socket.
         */
        public boolean send(Socket aSocket, JSONObject data) {
            out.println(data.toJSONString());
            System.out.println("\nsent"+data.toJSONString());
            return true;
        }

        /*
        This method implements the necessary code to receive the
        data over the network using the socket.s
         */
        public JSONObject receive(Socket aSocket) throws IOException {
            JSONObject data = null;
            try {
                String newPort = din.readLine();
                JSONParser parse = new JSONParser();
                data = (JSONObject) parse.parse(newPort);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("\nreceived "+data.toJSONString());
            return data;
        }



}




