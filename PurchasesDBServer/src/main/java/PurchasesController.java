import java.util.concurrent.*;

public class PurchasesController {

    public static void main(String[] argv) {
        PurchaseDao purchaseDao = new PurchaseDao("Purchases");

        CountDownLatch saveThreadLatch = new CountDownLatch(10);
        RunnableSave[] mySaveRunnable = new RunnableSave[10];
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++){
            mySaveRunnable[i] = new RunnableSave(purchaseDao, saveThreadLatch);
            threads[i] = new Thread(mySaveRunnable[i]);
            threads[i].start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("shutting down threads");
            for(int i = 0; i < threads.length; i++){
                mySaveRunnable[i].shutdown();
            }
            try{
                saveThreadLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Application is closing");
            }
        }));
    }
}