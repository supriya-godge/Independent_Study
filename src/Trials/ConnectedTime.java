package Trials;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by sup33 on 4/20/2017.
 */
public class ConnectedTime {
    public static void main(String[] arg){
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\sup33\\workspace\\Independent Study Class digrame\\src\\Trials\\input.txt"));
            String line;
            HashMap<String,Integer> worrd = new HashMap<>();
            float start=0,end=0,connected=0,disconnected=0;
            while ((line=br.readLine())!=null){
                String date = line.substring(1,11);
                String[] date1 = date.split("/");
                String command = line.substring(25);
                String time_1 = line.substring(12,17);
                int hr = Integer.parseInt(time_1.substring(0,2));
                int min = Integer.parseInt(time_1.substring(3,5));
                int time = hr*60+min;
                System.out.println(time_1.substring(0,2)+" "+time_1.substring(3,5));
                System.out.println(" Comm:"+command+": "+time);
                switch (command){
                    case "START" : start = time;
                        System.out.println(start); break;
                    case "SHUTDOWN": end = time; break;
                    case "CONNECTED": connected = time;break;
                    case "DISCONNECTED": disconnected=time;break;
                }
            }
            System.out.println(start+" "+end+" "+connected+" "+disconnected);
            System.out.println("ans"+(disconnected-connected)/(end-start)*100);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
