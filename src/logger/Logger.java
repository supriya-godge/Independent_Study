package logger;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This is the logging class, it creates the log files
 * Auther: Supriya Godge
 *         Sean Srout
 *         James Helliotis
 */
public class Logger {
     String fileName;

    public Logger(int gameId){
        fileName = "GameLog"+gameId+".txt";
    }

    public void write(String data){
        try {
            FileWriter fileWrite = new FileWriter(fileName, true); //true, will append the new data on existing file
            fileWrite.write(data+"\n");//appends the string to the file
            fileWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
