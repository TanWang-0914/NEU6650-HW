import java.util.concurrent.*;

public class StoreController {

//    static final ExecutorService servicePool;
    static StoreRecords storeRecords;



    public static void main(String[] argv) {

        storeRecords = new StoreRecords();

        CountDownLatch updateThreadLatch = new CountDownLatch(10);
        Thread[] threads = new Thread[10];
        RunnableUpdate[] myUpdateRunnable = new RunnableUpdate[10];
        for (int i = 0; i < threads.length; i++){
            myUpdateRunnable[i] = new RunnableUpdate(storeRecords, updateThreadLatch);
            threads[i] = new Thread(myUpdateRunnable[i]);
            threads[i].start();
        }

        CountDownLatch rpcThreadLatch = new CountDownLatch(1);
        RunnableGet myGetRunnable = new RunnableGet(storeRecords, rpcThreadLatch);
        Thread rpcThread = new Thread(myGetRunnable);
        rpcThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutting down threads");
            for(int i = 0; i < threads.length; i++){
                myUpdateRunnable[i].shutdown();
            }
            myGetRunnable.shutdown();
            try{
                updateThreadLatch.await();
                rpcThreadLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Application is closing");
            }
        }));

    }
}
