import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;

import java.io.IOException;

public class RunnableUpdate implements Runnable{

    private static final String EXCHANGE_NAME = "purchaseLogs";
    private String QUEUE_NAME;
    StoreRecords storeRecords;
    Connection connection;

    public RunnableUpdate(StoreRecords storeRecords, Connection connection, String queueName){
        this.storeRecords = storeRecords;
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
            channel.basicQos(20);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallbackOnUpdate = (consumerTag, delivery) -> {
                String purchaseMessage = new String(delivery.getBody(), "UTF-8");
                // System.out.println(purchaseMessage);
                update(purchaseMessage);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };

            channel.basicConsume(QUEUE_NAME, false, deliverCallbackOnUpdate, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void update(String purchaseMessage){

        JSONObject purchase = new JSONObject(purchaseMessage);

        int storeID = Integer.parseInt(purchase.getString("storeID"));

        String purchaseBody = purchase.getString("purchaseBody");

        JSONObject purchaseBodyJson = new JSONObject(purchaseBody);
        JSONArray purchaseItems = purchaseBodyJson.getJSONArray("items");
        for (int i = 0; i < purchaseItems.length(); i++){
            JSONObject item = purchaseItems.getJSONObject(i);
            int itemID = Integer.parseInt(item.getString("ItemID"))-1;
            int number = item.getInt("numberOfItems:");
//            storeRecords.storeArrays[storeID].getAndAdd(itemID,number);
            storeRecords.increase(storeID, itemID, number);
//             System.out.println("update success");
//             System.out.println("Increase store " + storeID + " item " + itemID + " by " + number);
        }

         System.out.println(" [x] Received '" + purchaseBody + "'");

    }
}
