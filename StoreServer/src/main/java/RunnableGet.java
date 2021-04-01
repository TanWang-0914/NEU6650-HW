import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class RunnableGet implements Runnable{

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private String QUEUE_NAME;
    StoreRecords storeRecords;
    Connection connection;

    public RunnableGet(StoreRecords storeRecords, Connection connection, String queueName){
        this.storeRecords = storeRecords;
        this.connection = connection;
        QUEUE_NAME = queueName;
    }

    @Override
    public void run() {
        try{
            final Channel channelForGet = connection.createChannel();
            channelForGet.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channelForGet.queuePurge(RPC_QUEUE_NAME);

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            Object monitor = new Object();

            DeliverCallback deliverCallbackOnGet = (consumerTag, delivery) -> {
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(delivery.getProperties().getCorrelationId())
                        .build();

                String response = "";

                try {
                    String requestMessage = new String(delivery.getBody(), "UTF-8");
                    JSONObject requestObj = new JSONObject(requestMessage);
                    String op = requestObj.getString("op");
                    int param = Integer.parseInt(requestObj.getString("param"));

                    if (op.equals("store")){
                        response = storeRecords.getStoreTop10Items(param);
                    }else if (op.equals("top10")){
                        response = storeRecords.getItemTop5Store(param);
                    }

                } catch (RuntimeException e) {
                    System.out.println(" [.] " + e.toString());
                } finally {
                    channelForGet.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channelForGet.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    // RabbitMq consumer worker thread notifies the RPC server owner thread
                    synchronized (monitor) {
                        monitor.notify();
                    }
                }
            };


            channelForGet.basicConsume(RPC_QUEUE_NAME, false, deliverCallbackOnGet, (consumerTag -> { }));

            // Wait and be prepared to consume the message from RPC client.
            while (true) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
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
            int itemID = Integer.parseInt(item.getString("ItemID"));
            int number = item.getInt("numberOfItems:");
            storeRecords.storeArrays[storeID].getAndAdd(itemID,number);
//            storeRecords.increase(storeID, itemID, number);
//             System.out.println("update success");
//             System.out.println("Increase store " + storeID + " item " + itemID + " by " + number);
        }

         System.out.println(" [x] Received '" + purchaseBody + "'");

    }
}
