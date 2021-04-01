import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RunnableSave implements Runnable{

    private static final String EXCHANGE_NAME = "purchaseLogs";
    private String QUEUE_NAME;

    PurchaseDao purchaseDao;
    Connection connection;



    public RunnableSave(PurchaseDao purchaseDao, Connection connection, String queueName){
        this.purchaseDao = purchaseDao;
        this.connection = connection;
        QUEUE_NAME = queueName;
    }

    @Override
    public void run() {
        try{
            final Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
//            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

            channel.basicQos(1);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String purchaseMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
//                System.out.println(purchaseMessage);
                save(purchaseMessage);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String purchaseMessage){
        JSONObject purchase = new JSONObject(purchaseMessage);

        String purhcaseID = purchase.getString("purchaseID");
        String storeID = purchase.getString("storeID");
        String custID = purchase.getString("custID");
        String date = purchase.getString("date");
        String purchaseBody = purchase.getString("purchaseBody");

        System.out.println(" [x] Received '" + purchaseBody + "'");
        purchaseDao.createPurchase(purhcaseID, storeID, custID, date, purchaseBody);
    }
}
