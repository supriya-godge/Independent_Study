package Trials;

/**
 * Created by sup33 on 4/12/2017.
 */
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTaskExample {

    public static void main(String[] args) {
        MyCallable callable1 = new MyCallable(1000);
        MyCallable callable2 = new MyCallable(2000);

       // FutureTask<String> futureTask1 = new FutureTask<String>(callable1);
        FutureTask<String> futureTask2 = new FutureTask<String>(callable2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
       // executor.execute(futureTask1);
        executor.execute(futureTask2);
        System.out.println("Waiting for FutureTask2 to complete");
        String s = null;
        try {
            s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("Timeout ----");
        }
        if(s !=null){
            System.out.println("FutureTask2 output="+s);
        }

        while (true) {
            try {


                System.out.println("Waiting for FutureTask2 to complete");
                 s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
                if(s !=null){
                    System.out.println("FutureTask2 output="+s);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }catch(TimeoutException e){
                System.out.println("System timeouted");
            }
        }

    }

}