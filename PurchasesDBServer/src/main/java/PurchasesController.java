import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class PurchasesController {

    private static final String EXCHANGE_NAME = "purchaseLogs";

    public static void main(String[] argv) throws Exception {
        PurchaseDao purchaseDao = new PurchaseDao("Purchases");
//        PurchaseDao purchaseDao = null;
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername("PURCHASEDB_USER");
        factory.setPassword("PURCHASEDB_PASSWORD");

        factory.setHost("44.192.74.54");
        Connection connection = factory.newConnection();


        final Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // String queueName = channel.queueDeclare("purchase", true, false,false, null).getQueue();
        String queueName = channel.queueDeclare().getQueue();
        System.out.println(queueName);


        Thread[] threads = new Thread[50];
        for (int i = 0; i < threads.length; i++){
            threads[i] = new Thread(new RunnableSave(purchaseDao, connection, queueName));
            threads[i].start();
        }
    }
}