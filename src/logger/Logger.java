package logger;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by sup33 on 3/13/2017.
 */
public class Logger {
     String fileName;

    public Logger(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        fileName = "GameLog"+dateFormat;
    }

    public void write(String data){
        try {
            FileWriter fileWrite = new FileWriter(fileName, true); //the true will append the new data
            fileWrite.write(data+"\n");//appends the string to the file
            fileWrite.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
