package Trials;

/**
 * Created by sup33 on 4/12/2017.
 */
import java.util.concurrent.Callable;

public class MyCallable implements Callable<String> {

    private long waitTime;

    public MyCallable(int timeInMillis){
        this.waitTime=timeInMillis;
    }
    @Override
    public String call() throws Exception {
        String num="hi";
        char[] num1=num.toCharArray();
        for(char a:num1){

        }
        Thread.sleep(waitTime);
        //return the thread name executing this callable task
        return Thread.currentThread().getName();
    }

}